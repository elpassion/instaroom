package pl.elpassion.instaroom.dashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import org.jetbrains.anko.toast
import pl.elpassion.instaroom.DI
import pl.elpassion.instaroom.R

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_room)
        val model = ViewModelProviders.of(
            this,
            DashboardViewModelFactory(DI.provideInstaRoomApi(), DI.provideLoginRepository())
        ).get(DashboardViewModel::class.java)
        model.getRooms().observe(this, Observer { toast("$it") })
        model.getError().observe(this, Observer { toast(it) })
    }
}