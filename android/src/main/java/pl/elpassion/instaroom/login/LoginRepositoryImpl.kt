package pl.elpassion.instaroom.login

import android.app.Application
import android.preference.PreferenceManager
import com.elpassion.android.commons.sharedpreferences.asProperty
import com.elpassion.android.commons.sharedpreferences.createSharedPrefs
import com.elpassion.sharedpreferences.moshiadapter.moshiConverterAdapter

class LoginRepositoryImpl(application: Application) : LoginRepository {

    private val sharedPreferencesProvider = { PreferenceManager.getDefaultSharedPreferences(application) }
    private val jsonAdapter = moshiConverterAdapter<String>()
    private val repository = createSharedPrefs(sharedPreferencesProvider, jsonAdapter)

    override var googleToken: String? by repository.asProperty(GOOGLE_TOKEN)

    companion object {
        private const val GOOGLE_TOKEN = "google token"
    }
}