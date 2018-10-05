package pl.elpassion.instaroom

import pl.elpassion.instaroom.api.InstaRoomApi
import pl.elpassion.instaroom.login.LoginRepository

object DI {

    lateinit var provideLoginRepository: () -> LoginRepository
    lateinit var provideInstaRoomApi: () -> InstaRoomApi
}