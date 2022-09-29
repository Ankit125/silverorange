package com.silverorange.videoplayer.retrofit

import com.silverorange.videoplayer.ApiResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

interface VideoApi {

    @GET("/videos")
     fun getVideo() : Call<ArrayList<ApiResponse>>
}