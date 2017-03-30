package com.lixh.rxhttp;

import android.app.Activity;

import com.lixh.base.BaseResPose;
import com.lixh.rxhttp.exception.ApiException;
import com.lixh.rxlife.LifeEvent;
import com.lixh.utils.ULog;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

/**
 * des:对服务器返回数据成功和失败处理
 * Created by xsf
 * on 2016.09.9:59
 */
public class RxHelper {
    public static Activity c;
    static RxHelper rxHelper;
    private boolean isShow = true;
    private String caChe;
    boolean isForceRefresh = true;
    private LifeEvent event;

    public BehaviorSubject<LifeEvent> lifeEvent;

    private RxHelper(Activity activity) {
        this.c = activity;
    }

    public RxHelper bindLifeCycle(BehaviorSubject<LifeEvent> lifeEvent) {
        this.lifeEvent = lifeEvent;
        return this;
    }


    /**
     * 对服务器返回数据进行预处理
     *
     * @param <T>
     * @return
     */
    public <T> Observable.Transformer<BaseResPose<T>, T> handleResult(final LifeEvent event, final BehaviorSubject<LifeEvent> lifecycleSubject) {
        return new Observable.Transformer<BaseResPose<T>, T>() {
            @Override
            public Observable<T> call(Observable<BaseResPose<T>> tObservable) {
                Observable<LifeEvent> compareLifecycleObservable =
                        lifecycleSubject.takeFirst(new Func1<LifeEvent, Boolean>() {
                            @Override
                            public Boolean call(LifeEvent activityLifeCycleEvent) {
                                return activityLifeCycleEvent.equals(event);
                            }
                        });
                return tObservable.flatMap(new Func1<BaseResPose<T>, Observable<T>>() {
                    @Override
                    public Observable<T> call(BaseResPose<T> result) {
                        ULog.e(result.toString());
                        return createData(result);

                    }
                }).takeUntil(compareLifecycleObservable).subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        };

    }


    /**
     * T
     *
     * @param fromNetwork
     */
    public <T> void createSubscriber(Observable fromNetwork, final RxSubscriber result) {
            //数据预处理
            Observable observable = fromNetwork.compose(handleResult(getEvent(), lifeEvent)).compose(RxSchedulers.io_main());
            RxCache.load(c, caChe, 1000 * 60 * 30, observable, isForceRefresh)
                    .subscribe(result);

    }

    public <T> void createSubscriber(Observable fromNetwork1, Observable fromNetwork2, final RxSubscriber result) {
        Observable observable = fromNetwork1.compose(handleResult(getEvent(), lifeEvent));
        Observable observable1 = fromNetwork2.compose(handleResult(getEvent(), lifeEvent));
        ;
        RxCache.load(c, caChe, 1000 * 60 * 30, observable1, isForceRefresh);
        Observable.zip(RxCache.load(c, caChe, 1000 * 60 * 30, observable, isForceRefresh), RxCache.load(c, caChe, 1000 * 60 * 30, observable1, isForceRefresh), new Func2<BaseResPose<T>, BaseResPose<T>,Boolean>() {

            @Override
            public Boolean call(BaseResPose<T> tBaseResPose, BaseResPose<T> tBaseResPose2) {
                return null;
            }
        }) .subscribe(result);;
    }

    /**
     * 创建成功的数据
     *
     * @param <T>
     * @return
     */

    private <T> Observable createData(final BaseResPose<T> result) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    if (result.success()) {
                        subscriber.onNext(result.data);
                    } else {
                        subscriber.onError(new ApiException(result.message));
                    }
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });

    }


    public LifeEvent getEvent() {
        return event;
    }

    public void setEvent(LifeEvent event) {
        this.event = event;
    }

    public RxHelper setForceRefresh(boolean forceRefresh) {
        isForceRefresh = forceRefresh;
        return this;
    }


    public RxHelper setCaChe(String caChe) {
        this.caChe = caChe;
        return this;
    }

    public static RxHelper build(Activity context) {
        if (rxHelper == null) {
            rxHelper = new RxHelper(context);
        }
        return
                rxHelper;
    }


}
