package pl.elpassion.instaroom

import android.app.Application
import org.koin.android.ext.android.startKoin

class InstaRoomApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(appModule))
    }
}