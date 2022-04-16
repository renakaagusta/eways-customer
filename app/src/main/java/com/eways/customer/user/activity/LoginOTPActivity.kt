package com.eways.customer.user.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.eways.customer.R
import com.eways.customer.core.baseactivity.BaseActivity
import com.eways.customer.dashboard.activity.MainActivity
import com.eways.customer.utils.customsupportactionbar.CustomSupportActionBar
import com.proyek.infrastructures.user.customer.usecases.LoginCustomer
import com.proyek.infrastructures.user.customer.usecases.SendOTP
import com.proyek.infrastructures.utils.Authenticated
import kotlinx.android.synthetic.main.activity_login_otp.*
import kotlinx.android.synthetic.main.activity_register_otp.tvConfirmation


class LoginOTPActivity : BaseActivity(){
    private lateinit var loginCustomer: LoginCustomer
    private lateinit var sendOTP: SendOTP

    private lateinit var verificationId: String
    private lateinit var verificationNumber: String

    private lateinit var phoneNumber: String
    private lateinit var code: String
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_otp)
        CustomSupportActionBar.setCustomActionBar(this,"Login")

        phoneNumber = intent.getStringExtra("phoneNumber")
        code = intent.getStringExtra("code")
        token = intent.getStringExtra("token")


        loginCustomer = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(LoginCustomer::class.java)
        sendOTP = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(SendOTP::class.java)

        moveToMain()
    }

    private fun successLoginOTP() {
        this@LoginOTPActivity.showProgress()
        loginCustomer.set(phoneNumber, token, 1, this@LoginOTPActivity)
        loginCustomer.get().observe(this, Observer {
            this@LoginOTPActivity.dismissProgress()
            if(it.errors.message.isEmpty()) {
                Authenticated.setUserCustomer(it.data.user)
                val intent = Intent(this@LoginOTPActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                this@LoginOTPActivity.showError(it.errors.message[0])
            }
        })
    }

    fun authenticate (){
        if(verificationNumber == code) {
            successLoginOTP()
        } else {
            this@LoginOTPActivity.showError("Kode yang anda anda masukan salah")
        }
    }

    fun disableConfirmation(){
        tvConfirmation.isClickable = false
        tvConfirmation.setTextColor(ContextCompat.getColor(this@LoginOTPActivity, R.color.colorRegularText))
        tvConfirmation.setBackgroundColor(ContextCompat.getColor(this@LoginOTPActivity, R.color.lightGrey))
    }

    private fun moveToMain(){
        otpLogin.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                when(p0?.length){
                    6 -> {
                        tvConfirmation.setTextColor(ContextCompat.getColor(this@LoginOTPActivity, R.color.white))
                        tvConfirmation.setBackgroundColor(ContextCompat.getColor(this@LoginOTPActivity, R.color.colorPrimary))
                        tvConfirmation.isClickable
                        tvConfirmation.setOnClickListener {
                            verificationNumber = otpLogin.text.toString()
                            authenticate()
                        }
                    }
                    else ->{
                        disableConfirmation()
                    }
                }
            }
        })
    }
}