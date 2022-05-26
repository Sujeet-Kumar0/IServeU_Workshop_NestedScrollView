package com.example.bluetoothandnestedscrollview

import retrofit2.Call
import retrofit2.http.GET

interface APIservice {

    @GET("quotes")
    fun getPosts(): Call<MutableList<PostModel>>

}