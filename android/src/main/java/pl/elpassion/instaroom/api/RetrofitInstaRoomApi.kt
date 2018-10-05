package pl.elpassion.instaroom.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import retrofit2.Retrofit

class RetrofitInstaRoomApi {

    private val client = Retrofit.Builder()
        .baseUrl("https://instaroom.elpassion.pl")
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()
}