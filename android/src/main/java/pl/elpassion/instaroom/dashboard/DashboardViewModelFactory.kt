package pl.elpassion.instaroom.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.elpassion.instaroom.api.InstaRoomApi
import pl.elpassion.instaroom.login.LoginRepository

class DashboardViewModelFactory(
    private val instaRoomApi: InstaRoomApi,
    private val loginRepository: LoginRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        DashboardViewModel(instaRoomApi, loginRepository) as T
}