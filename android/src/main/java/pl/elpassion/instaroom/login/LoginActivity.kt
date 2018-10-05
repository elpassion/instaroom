package pl.elpassion.instaroom.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.login_activity.*
import org.jetbrains.anko.startActivity
import pl.elpassion.instaroom.DI
import pl.elpassion.instaroom.R
import pl.elpassion.instaroom.dashboard.DashboardActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var model: LoginViewModel

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

        model = ViewModelProviders.of(this, LoginViewModelFactory(DI.provideLoginRepository()))
            .get(LoginViewModel::class.java)
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
            task.getResult(ApiException::class.java)?.idToken?.let(model::saveGoogleToken)
        } catch (e: ApiException) {
            e.printStackTrace()
        }
    }

    private fun showRoomsScreen() = startActivity<DashboardActivity>()

    companion object {
        const val SIGN_IN_REQUEST_CODE = 627
    }
}
