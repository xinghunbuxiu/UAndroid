package com.lixh.uandroid.ui;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.lixh.base.BaseActivity;
import com.lixh.uandroid.R;
import com.lixh.utils.Alert;
import com.lixh.view.UToolBar;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by LIXH on 2017/4/11.
 * email lixhVip9@163.com
 * des
 */

public class DialogActivity extends BaseActivity {

    @Bind(R.id.b_1)
    Button b1;
    @Bind(R.id.b_2)
    Button b2;
    @Bind(R.id.b_3)
    Button b3;
    @Bind(R.id.b_4)
    Button b4;
    @Bind(R.id.b_5)
    Button b5;
    @Bind(R.id.b_6)
    Button b6;
    @Bind(R.id.b_7)
    Button b7;
    @Bind(R.id.b_8)
    Button b8;
    private View pushView;
    ArrayList<String> array = new ArrayList<String>() {
        {
            add("nihaooo");
            add("nihaooo");
            add("nihaooo");
            add("nihaooo");
            add("nihaooo");
            add("nihaooo");
            add("nihaooo");
            add("nihaooo");


        }
    };
    ArrayList<ArrayList<String>> array2 = new ArrayList<ArrayList<String>>() {
        {
            add(array);
            add(array);
            add(array);
            add(array);
            add(array);
            add(array);
            add(array);
            add(array);
            add(array);
            add(array);
            add(array);
            add(array);
            add(array);
            add(array);
            add(array);
            add(array);
            add(array);
            add(array);
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_dialog;
    }


    @Override
    public void initTitle(UToolBar toolBar) {

    }

    private WindowManager windowManager;

    @Override
    public void init(Bundle savedInstanceState) {
        pushView = View.inflate(this, R.layout.item_ciew, null);
        windowManager = getWindowManager();
    }

    @OnClick({R.id.b_1, R.id.b_2, R.id.b_3, R.id.b_4, R.id.b_5, R.id.b_6, R.id.b_7, R.id.b_8})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_1://Edit
//                WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//                        WindowManager.LayoutParams.MATCH_PARENT,
//                        WindowManager.LayoutParams.WRAP_CONTENT,
//                        WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
//                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
//                                | WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                        PixelFormat.TRANSLUCENT);
//                params.gravity = Gravity.TOP | Gravity.LEFT;
//                params.y = 0;
//                params.x = 0;
//                windowManager.addView(pushView, params);
                break;
            case R.id.b_2://warn
                Alert.displayAlertDialog(this, "warn", "nihao", "cancel", "ok", new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {

                    }
                }, null);
                break;
            case R.id.b_3://Selected
                final String[] stringItems = {"版本更新", "帮助与反馈", "退出QQ"};
                Alert.displayAlertSelectedDialog(this, stringItems, new OnOperItemClickL() {
                    @Override
                    public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                });
                break;
            case R.id.b_4://SingleList

                break;
            case R.id.b_5://MultipleList

                break;
            case R.id.b_6://Time
                break;
            case R.id.b_7://City
                break;
            case R.id.b_8://Custom
                break;
        }
    }
}
