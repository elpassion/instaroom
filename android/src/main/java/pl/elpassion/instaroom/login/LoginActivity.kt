package pl.elpassion.instaroom.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.login_activity.*
import pl.elpassion.instaroom.DI
import pl.elpassion.instaroom.R

class LoginActivity : AppCompatActivity() {

    private lateinit var model: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.server_client_id))
            .requestEmail()
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
        model.getGoogleToken().observe(this, Observer { toast("Token: $it") })
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
            Log.d(javaClass.simpleName, "Token: ${result?.idToken}")
            toast("Signed in", duration = LENGTH_LONG).show()
        } catch (e: ApiException) {
            e.printStackTrace()
        }
    }

    companion object {
        const val SIGN_IN_REQUEST_CODE = 627
    }
}
