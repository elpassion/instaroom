package pl.elpassion.instaroom.api

import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Header

interface InstaRoomApi {

    @GET("/rooms")
    fun getRooms(@Header("AccessToken") accessToken: String): Deferred<RoomsResponse>
}