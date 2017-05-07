package com.example.greyson.test1.net;

import android.content.Context;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.PersistentCookieStore;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
/**
 * This class is net server
 *
 * @author Greyson, Carson
 * @version 1.0
 */

public class NetWorkApi2 {
    private static volatile NetWorkApi2 instance;
    private OkHttpClient mOkHttpClient;
    private Retrofit mRetrofit2;

    public static NetWorkApi2 getInstance() {
        if (instance == null) {
            synchronized (NetWorkApi2.class) {
                if (instance == null) {
                    instance = new NetWorkApi2();
                }
            }
        }
        return instance;
    }

    /**
     * http
     */
    public OkHttpClient gradleOkHttp(Context context) {
        if (mOkHttpClient == null) {
            CookieJarImpl cookieJar = new CookieJarImpl(new PersistentCookieStore(context));
            mOkHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LoggerInterceptor("TAG"))
                    .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                    .readTimeout(10000L, TimeUnit.MILLISECONDS)
                    .cookieJar(cookieJar)
                    .build();
            OkHttpUtils.initClient(mOkHttpClient);
        }
        return mOkHttpClient;
    }

    /**
     * Retrofit
     *
     * @return
     */
    public Retrofit gradleRetrofit(Context context) {
        if (mRetrofit2 == null) {
            OkHttpClient okHttpClient = gradleOkHttp(context);
            mRetrofit2 = new Retrofit.Builder()
                    .baseUrl(ServerUrl.routeUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())////
                    .client(okHttpClient)
                    .build();
        }
        return mRetrofit2;
    }
}
