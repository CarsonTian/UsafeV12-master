package com.example.greyson.test1.net;

import com.example.greyson.test1.entity.RouteRes;
import com.example.greyson.test1.entity.SafePlaceRes;
import com.example.greyson.test1.entity.TrackerRes;

import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;


/**
 * This class is interface
 *
 * @author Greyson, Carson
 * @version 1.0
 */

public interface WSNetService {
    /**
     * SafePlace Request
     */
    @GET("?format=json&")
    Observable<SafePlaceRes> getSafePlaceData(@QueryMap Map<String, String> params);

    @GET("json?")
    Observable<RouteRes> getSafePlaceRoute(@QueryMap Map<String, String> params);

    @GET("json?")
    Observable<TrackerRes> getDuration(@Url String url, @QueryMap Map<String, String> params);
}
