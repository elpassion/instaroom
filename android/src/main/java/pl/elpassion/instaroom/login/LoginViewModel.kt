package pl.elpassion.instaroom.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel(loginRepository: LoginRepository) : ViewModel() {

    private val googleTokenLiveData = MutableLiveData<String>()

    init {
        googleTokenLiveData.value = loginRepository.googleToken
    }

    fun getGoogleToken(): LiveData<String> = googleTokenLiveData
}