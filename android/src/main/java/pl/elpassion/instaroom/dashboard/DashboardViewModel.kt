package pl.elpassion.instaroom.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import pl.elpassion.instaroom.api.InstaRoomApi
import pl.elpassion.instaroom.api.Room
import pl.elpassion.instaroom.login.LoginRepository
import retrofit2.HttpException
import kotlin.coroutines.experimental.CoroutineContext

class DashboardViewModel(
    private val instaRoomApi: InstaRoomApi,
    private val loginRepository: LoginRepository
) : ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job

    private val roomsLiveData = MutableLiveData<List<Room>>()
    private val errorLiveData = MutableLiveData<String>()

    private val job = Job()

    init {
        launch {
            try {
                val accessToken = loginRepository.googleToken!!
                val rooms = instaRoomApi.getRooms(accessToken).await()
                roomsLiveData.postValue(rooms)
            } catch (e: HttpException) {
                errorLiveData.postValue(e.message())
            }
        }
    }

    fun getRooms(): LiveData<List<Room>> = roomsLiveData

    fun getError(): LiveData<String> = errorLiveData

    override fun onCleared() {
        job.cancel()
    }
}