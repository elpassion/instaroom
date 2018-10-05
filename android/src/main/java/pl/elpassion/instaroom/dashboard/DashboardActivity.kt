package pl.elpassion.instaroom.dashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.elpassion.android.commons.recycler.adapters.basicAdapterWithLayoutAndBinder
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.item_room.view.*
import org.jetbrains.anko.toast
import pl.elpassion.instaroom.DI
import pl.elpassion.instaroom.R
import pl.elpassion.instaroom.api.Room

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

        setUpList()
    }

    private fun setUpList() {
        //TODO emptylist
        dashboard_recycler_view.adapter = basicAdapterWithLayoutAndBinder(emptyList<Room>(), R.layout.item_room) { holder, item ->
            holder.itemView.item_room_meeting_title_tv.text = item.name
            holder.itemView.setOnClickListener {
                onRoomClicked(item)
            }
        }
        dashboard_recycler_view.layoutManager = LinearLayoutManager(this)
    }

    fun onRoomClicked(room: Room) {

  }
}