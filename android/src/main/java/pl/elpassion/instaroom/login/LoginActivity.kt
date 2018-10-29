package pl.elpassion.instaroom.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.login_activity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.startActivity
import org.koin.android.viewmodel.ext.android.viewModel
import pl.elpassion.instaroom.R
import pl.elpassion.instaroom.dashboard.DashboardActivity

class LoginActivity : AppCompatActivity() {

    private val model: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.server_client_id))
            .requestEmail()
            .requestScopes(Scope("https://www.googleapis.com/auth/calendar.events"))
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        signInButton.setOnClickListener {
            startActivityForResult(
                googleSignInClient.signInIntent,
                SIGN_IN_REQUEST_CODE
            )
        }

        model.getGoogleToken().observe(this, Observer { token ->
            if (token != null) showRoomsScreen()
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val result = task.getResult(ApiException::class.java)
            GlobalScope.launch(Dispatchers.IO) {
                val token = GoogleAuthUtil.getToken(
                    this@LoginActivity,
                    result!!.account,
                    "oauth2:https://www.googleapis.com/auth/calendar.events"
                )
                model.saveGoogleToken(token)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showRoomsScreen() = startActivity<DashboardActivity>()

    companion object {
        const val SIGN_IN_REQUEST_CODE = 627
    }
}
