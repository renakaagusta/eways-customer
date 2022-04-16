package com.eways.customer.user.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.eways.customer.R
import com.eways.customer.core.baseactivity.BaseActivity
import com.eways.customer.dashboard.activity.MainActivity
import com.eways.customer.utils.customsupportactionbar.CustomSupportActionBar
import com.eways.customer.utils.firebase.firestore.Firestore
import com.eways.customer.utils.firebase.messaging.FirebaseCloudMessaging
import com.proyek.infrastructures.user.customer.usecases.LoginCustomer
import com.proyek.infrastructures.user.customer.usecases.SendOTP
import com.proyek.infrastructures.utils.Authenticated
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginActivity : BaseActivity() {

    private lateinit var loginCustomer: LoginCustomer
    private lateinit var sendOTP: SendOTP

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        CustomSupportActionBar.setCustomActionBar(this, "Login")

        sendOTP =
            ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(SendOTP::class.java)
        loginCustomer = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(LoginCustomer::class.java)

        FirebaseCloudMessaging.getToken()
        Login()
    }

    private fun moveToLoginOTP(phoneNumber: String, code: String,token: String) {
        val intent = Intent(this@LoginActivity, LoginOTPActivity::class.java)
        intent.putExtra("phoneNumber", phoneNumber)
        intent.putExtra("code", code)
        intent.putExtra("token", token)
        startActivity(intent)
    }

    private fun Login() {
        tvLogin.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                this@LoginActivity.showProgress()
                delay(500)
                loginCustomer.set(
                    "${tietPhone.text.toString()}",
                    Firestore.token,
                    0,
                    this@LoginActivity
                )
                delay(1000)
                loginCustomer.get().observe(this@LoginActivity, Observer {
                    this@LoginActivity.dismissProgress()
                    if (it.errors.message.isEmpty()) {
                        Authenticated.setUserCustomer(it.data.user)
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        if((it.errors.message!![0] == "OTP status false, please do OTP first") || (it.errors.message!![0] == "Agent OTP data not found")) {
                            val allowedChars = ('0'..'9')
                            var code = (1..6)
                                .map { allowedChars.random() }
                                .joinToString("")

                            var phoneNumber = "+62" + tietPhone.text.toString()

                            Log.d("phoneNumber", phoneNumber)

                            sendOTP.set(phoneNumber, code)
                            sendOTP.get().observe(this@LoginActivity, Observer {
                                moveToLoginOTP(tietPhone.text.toString(), code, Firestore.token)
                            })
                        } else {
                            this@LoginActivity.showError(it.errors.message!![0])
                        }
                    }
                })
            }
        }
    }
}