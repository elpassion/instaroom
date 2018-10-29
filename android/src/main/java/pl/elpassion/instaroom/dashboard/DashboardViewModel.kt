package pl.elpassion.instaroom.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import pl.elpassion.instaroom.api.InstaRoomApi
import pl.elpassion.instaroom.api.Room
import pl.elpassion.instaroom.login.LoginRepository
import retrofit2.HttpException
import kotlin.coroutines.CoroutineContext

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
        launch(Dispatchers.IO) {
            try {
                val accessToken = loginRepository.googleToken!!
                val response = instaRoomApi.getRooms(accessToken).await()
                roomsLiveData.postValue(response.rooms)
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