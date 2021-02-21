package com.charuniverse.kelasku.data.retrofit

import com.charuniverse.kelasku.util.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun get(): APIServices {
        return retrofit.create(APIServices::class.java)
    }

}