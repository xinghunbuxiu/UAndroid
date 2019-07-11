package com.lixh.bean;/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
**/

import androidx.annotation.NonNull;

public class Message {
    public int what;
    public int arg1;
    public int arg2;
    public Object obj;

    public int getArg1() {
        return arg1;
    }

    public Message setArg1(int arg1) {
        this.arg1 = arg1;
        return this;
    }

    public int getArg2() {
        return arg2;
    }

    public Message setArg2(int arg2) {
        this.arg2 = arg2;
        return this;
    }

    public Object getObj() {
        return obj;
    }

    public Message setObj(Object obj) {
        this.obj = obj;
        return this;
    }

    public int getWhat() {
        return what;
    }

    public Message setWhat(int what) {
        this.what = what;
        return this;
    }

    @Override
    public String toString() {
        return "Message{" +
                "what=" + what +
                ", arg1=" + arg1 +
                ", arg2=" + arg2 +
                ", obj=" + obj +
                '}';
    }
}
