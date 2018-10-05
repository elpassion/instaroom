package pl.elpassion.instaroom.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import kotlinx.coroutines.experimental.Deferred
import retrofit2.Retrofit

object RetrofitInstaRoomApi : InstaRoomApi {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://instaroom.elpassion.pl")
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    private val service = retrofit.create(InstaRoomApi::class.java)

    override fun getRooms(): Deferred<List<Room>> = service.getRooms()
}