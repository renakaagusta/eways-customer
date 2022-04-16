package com.eways.customer.dashboard.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.eways.customer.core.baseactivity.BaseActivity
import com.eways.customer.user.activity.LoginActivity
import com.eways.customer.user.activity.RegisterActivity
import com.eways.customer.R
import com.eways.customer.utils.firebase.firestore.Firestore
import com.proyek.infrastructures.utils.Authenticated
import com.eways.customer.utils.firebase.messaging.FirebaseCloudMessaging
import com.proyek.infrastructures.user.customer.entities.UserCustomer
import com.proyek.infrastructures.user.customer.usecases.SendOTP
import com.proyek.infrastructures.utils.retrofit.MyRetrofit
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_welcome.*
import kotlinx.android.synthetic.main.activity_welcome.tvLogin
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

class WelcomeActivity : BaseActivity() {
    private lateinit var user: UserCustomer
    private lateinit var sendOTP: SendOTP

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        supportActionBar?.hide()
        sendOTP =
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(SendOTP::class.java)

        MyRetrofit.context = this
        Authenticated.init(this)
        FirebaseCloudMessaging.getToken()

        if (Authenticated.isValidCacheMember()){
            startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "Membutuhkan Izin Lokasi", Toast.LENGTH_SHORT).show()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    1
                )
            }
        }

        moveToRegister()
        moveToLogin()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBackPressed() {
        finishAndRemoveTask()
        this.finishAffinity()
        System.exit(0)
    }

    private fun moveToRegister(){
        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun moveToLogin(){
        tvLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
