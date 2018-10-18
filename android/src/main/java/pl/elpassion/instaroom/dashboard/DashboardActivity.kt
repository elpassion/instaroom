package pl.elpassion.instaroom.dashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.elpassion.android.commons.recycler.adapters.basicAdapterWithLayoutAndBinder
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.item_room.view.*
import org.koin.android.viewmodel.ext.android.viewModel
import pl.elpassion.instaroom.R
import pl.elpassion.instaroom.api.Room

class DashboardActivity : AppCompatActivity() {

    private val model: DashboardViewModel by viewModel()
    private val rooms = mutableListOf<Room>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        model.getRooms().observe(this, Observer(::updateRooms))
        model.getError().observe(this, Observer { toast(it) })

        setUpList()
    }

    private fun setUpList() {
        dashboard_recycler_view.adapter = basicAdapterWithLayoutAndBinder(rooms, R.layout.item_room) { holder, item ->
            holder.itemView.item_room_name_tv.text = item.name
            holder.itemView.item_room_meeting_title_tv.text = item.events.firstOrNull()?.name
            holder.itemView.setOnClickListener {
                onRoomClicked(item)
            }
        }
        dashboard_recycler_view.layoutManager = LinearLayoutManager(this)
    }

    private fun updateRooms(rooms: List<Room>) {
        this.rooms.run { clear(); addAll(rooms) }
        dashboard_recycler_view.adapter?.notifyDataSetChanged()
    }

    fun onRoomClicked(room: Room) {

    }
}