package com.lixh.rxhttp.exception;

public interface ApiErrorCode {

    /**
     * 客户端错误
     */
    String ERROR_CLIENT_AUTHORIZED = "1";
    /**
     * 用户授权失败
     */
    String ERROR_USER_AUTHORIZED = "2";
    /**
     * 请求参数错误
     */
    String ERROR_REQUEST_PARAM = "3";
    /**
     * 参数检验不通过
     */
    String ERROR_PARAM_CHECK = "4";
    /**
     * 自定义错误
     */
    String ERROR_OTHER = "10";
    /**
     * 无网络连接
     */
    String ERROR_NO_INTERNET = "11";

}