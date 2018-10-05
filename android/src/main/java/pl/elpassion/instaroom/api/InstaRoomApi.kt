package pl.elpassion.instaroom.api

import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.GET

interface InstaRoomApi {

    @GET("/rooms")
    fun getRooms(): Deferred<List<Room>>
}