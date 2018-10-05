package pl.elpassion.instaroom.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val googleTokenLiveData = MutableLiveData<String>()

    init {
        googleTokenLiveData.value = loginRepository.googleToken
    }

    fun getGoogleToken(): LiveData<String> = googleTokenLiveData

    fun saveGoogleToken(token: String) {
        googleTokenLiveData.postValue(token)
        loginRepository.googleToken = token
    }
}