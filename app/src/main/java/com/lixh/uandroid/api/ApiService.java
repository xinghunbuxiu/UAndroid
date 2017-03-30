package com.lixh.uandroid.api;


import com.lixh.base.BaseResPose;

import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by helin on 2016/10/9 17:09.
 */

public interface ApiService {
    @POST("login")
    Observable<BaseResPose<String>> login(@Query("username") String username, @Query("password") String password);
    @POST("Loan/GetSpeedBorrowedDetail")
    Observable<BaseResPose<String>> getBanner(@Query("id") int id);
}
