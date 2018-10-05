package pl.elpassion.instaroom

import android.app.Application
import pl.elpassion.instaroom.login.LoginRepositoryImpl

class InstaRoomApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        DI.provideLoginRepository = { LoginRepositoryImpl() }
    }
}