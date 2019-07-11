package com.lixh.rxhttp;

import android.app.Activity;
import androidx.collection.ArrayMap;

import com.lixh.base.BaseResPose;
import com.lixh.rxhttp.exception.ApiException;
import com.lixh.rxlife.LifeEvent;
import com.lixh.utils.ULog;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;


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
    private ArrayMap<String, BehaviorSubject<LifeEvent>> lifecycleSubject = new ArrayMap<>();

    private RxHelper(Activity activity) {
        this.c = activity;
    }

    public RxHelper bindLifeCycle(BehaviorSubject<LifeEvent> lifeEvent) {
        lifecycleSubject.put(c.getClass().getName(), lifeEvent);
        return this;
    }

    /**
     * 对服务器返回数据进行预处理
     *
     * @param <T>
     * @return
     */
    public <T> ObservableTransformer<BaseResPose<T>, T> handleResult(final LifeEvent event, final BehaviorSubject<LifeEvent> lifecycleSubject) {
        return tObservable -> {
            Observable<LifeEvent> compareLifecycleObservable =
                    lifecycleSubject.filter(activityLifeCycleEvent -> activityLifeCycleEvent.equals(event));
            return tObservable.flatMap((Function<BaseResPose<T>, ObservableSource<T>>) result -> {
                if (result.toString().length() < 300) {
                    ULog.e(result.toString());
                }
                return createData(result);
            }).takeUntil(compareLifecycleObservable).subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        };

    }

    /**
     * T
     *
     * @param fromNetwork
     */
    public <T> void createSubscriber(Observable fromNetwork, final RxSubscriber result) {
        //数据预处理
        BehaviorSubject<LifeEvent> lifeEvent = lifecycleSubject.get(c.getClass().getName());
        Observable observable = fromNetwork.compose(handleResult(getEvent(), lifeEvent)).compose(RxSchedulers.io_main());
        RxCache.load(c, caChe, 1000 * 60 * 30, observable, isForceRefresh)
                .subscribeWith(result);

    }

    public <T> void createZipSubscriber(Observable o1, Observable o2, BiFunction zipFunction, final RxSubscriber result) {
        BehaviorSubject<LifeEvent> lifeEvent = lifecycleSubject.get(c.getClass().getName());
        Observable observable = o1.compose(handleResult(getEvent(), lifeEvent));
        Observable observable1 = o2.compose(handleResult(getEvent(), lifeEvent));
        Observable.zip(RxCache.load(c, caChe, 1000 * 60 * 30, observable, isForceRefresh), RxCache.load(c, caChe, 1000 * 60 * 30, observable1, isForceRefresh), zipFunction).subscribe(result);
    }

    /**
     * 创建成功的数据
     *
     * @param <T>
     * @return
     */

    private <T> Observable createData(final BaseResPose<T> result) {
        return Observable.create((ObservableOnSubscribe<T>) subscriber -> {
            try {
                if (result.success()) {
                    subscriber.onNext(result.data);
                } else {
                    subscriber.onError(new ApiException(result.code, result.message));
                }
                subscriber.onComplete();
            } catch (Exception e) {
                subscriber.onError(e);
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


    public void clearSubject() {
        lifecycleSubject.clear();
    }
}
