package com.lixh.uandroid.model;

import com.lixh.bean.BaseModel;

/**
 * Created by LIXH on 2016/11/14.
 * email lixhVip9@163.com
 * des
 */
public class MainModel1 extends BaseModel {

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    String username;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    String password;
}
