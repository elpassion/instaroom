package pl.elpassion.instaroom.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitInstaRoomApi : InstaRoomApi {

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://instaroom.app.elpassion.com")
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    private val service = retrofit.create(InstaRoomApi::class.java)

    override fun getRooms(accessToken: String): Deferred<RoomsResponse> = service.getRooms(accessToken)
}