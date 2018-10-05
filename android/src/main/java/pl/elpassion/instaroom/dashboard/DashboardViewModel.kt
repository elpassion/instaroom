package pl.elpassion.instaroom.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import pl.elpassion.instaroom.api.InstaRoomApi
import pl.elpassion.instaroom.api.Room
import kotlin.coroutines.experimental.CoroutineContext

class DashboardViewModel(private val instaRoomApi: InstaRoomApi) : ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job

    private val roomsLiveData = MutableLiveData<List<Room>>()

    private val job: Job

    init {
        job = launch {
            roomsLiveData.postValue(instaRoomApi.getRooms().await())
        }
    }

    override fun onCleared() {
        job.cancel()
    }
}