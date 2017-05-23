package com.lixh.uandroid.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.common.dialog.Alert;
import com.common.dialog.ImpAlert;
import com.lixh.base.BaseActivity;
import com.lixh.uandroid.R;
import com.lixh.utils.UToast;
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

    @Override
    public void init(Bundle savedInstanceState) {

    }

    @OnClick({R.id.b_1, R.id.b_2, R.id.b_3, R.id.b_4, R.id.b_5, R.id.b_6, R.id.b_7, R.id.b_8})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_1:
                Alert.displayAlertDialog(this, "nihao", "nidaye", null, null, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }, null);
                break;
            case R.id.b_2:
                Alert.displayEditDialog(this, "nihao", "nihao", "nidaye", new ImpAlert.OnOKDialogClickListener() {
                    @Override
                    public void okOnClick(String name) {
                        UToast.showShort(name);
                    }
                });
                break;
            case R.id.b_3:
                break;
            case R.id.b_4:
                break;
            case R.id.b_5:
                break;
            case R.id.b_6:
                break;
            case R.id.b_7:
                break;
            case R.id.b_8:
                break;
        }
    }
}
