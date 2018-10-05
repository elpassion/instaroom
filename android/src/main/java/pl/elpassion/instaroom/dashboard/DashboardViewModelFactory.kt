package pl.elpassion.instaroom.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.elpassion.instaroom.api.InstaRoomApi

class DashboardViewModelFactory(private val instaRoomApi: InstaRoomApi) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T = DashboardViewModel(instaRoomApi) as T
}