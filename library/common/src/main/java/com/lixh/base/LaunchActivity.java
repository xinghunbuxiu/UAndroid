package com.lixh.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Window;

import com.lixh.utils.PermissionUtils;
import com.lixh.utils.UIntent;

import java.util.List;

/**
 * @author lixh
 * @version V1.0
 * @Title: WelcomeActivity.java
 * @Package com.cihon.activities
 * @Description: 欢迎界面
 * @date 2015年5月12日 上午10:25:24
 */
public abstract class LaunchActivity extends AppCompatActivity {

    public final static int GO_HOME = 1;
    public final static int GO_GUIDE = 2;
    public final static long SPLASH_DELAY_MILLIS = 1000;
    public UIntent intent;

    public abstract boolean isFirst();

    public abstract int getLayoutId();

    public abstract Class toActivity(int what);

    /**
     * Handler:跳转到不同界面
     */
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            intent.go(toActivity(msg.what));
            finish();
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);//无标题
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        intent = new UIntent(this);
        initView();
    }

    public void initRxPermissions() {
        PermissionUtils.requestPermission(this, new PermissionUtils.RequestPermission() {
            @Override
            public void onRequestPermissionFailure(List<String> list) {
                mHandler.sendEmptyMessageDelayed(isFirst() ? GO_GUIDE : GO_HOME,
                        SPLASH_DELAY_MILLIS);

            }

            @Override
            public void onRequestPermissionSuccess() {
                mHandler.sendEmptyMessageDelayed(isFirst() ? GO_GUIDE : GO_HOME,
                        SPLASH_DELAY_MILLIS);
            }
        }, PermissionUtils.PERMISSIONS);

    }

    public void initView() {
        if (!this.isTaskRoot()) { // 判断该Activity是不是任务空间的源Activity，“非”也就是说是被系统重新实例化出来
            // 如果你就放在launcher Activity中话，这里可以直接return了
            Intent mainIntent = getIntent();
            String action = mainIntent.getAction();
            if (mainIntent.hasCategory(Intent.CATEGORY_LAUNCHER)
                    && action.equals(Intent.ACTION_MAIN)) {
                finish();
                return;// finish()之后该活动会继续执行后面的代码，你可以logCat验证，加return避免可能的exception
            }
        }
        initRxPermissions();
    }


}
