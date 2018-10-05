package pl.elpassion.instaroom

import pl.elpassion.instaroom.login.LoginRepository

object DI {

    lateinit var provideLoginRepository: () -> LoginRepository
}