package com.eways.customer.user.activity

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.eways.customer.R
import com.eways.customer.core.baseactivity.BaseActivity
import com.eways.customer.utils.customsupportactionbar.CustomSupportActionBar
import com.proyek.infrastructures.user.customer.entities.UserCustomer
import com.proyek.infrastructures.user.customer.usecases.UpdateCustomer
import com.proyek.infrastructures.utils.Authenticated
import kotlinx.android.synthetic.main.activity_profile_update.*

class ProfileUpdateActivity : BaseActivity() {
    private lateinit var user: UserCustomer
    private lateinit var updateCustomer: UpdateCustomer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_update)
        CustomSupportActionBar.setCustomActionBar(this, "Edit Profil")

        user = Authenticated.getUserCustomer()
        updateCustomer = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(UpdateCustomer::class.java)

        checkUserName()
    }

    override fun onStart() {
        super.onStart()
        tietFullname.setText(user.fullname)
        tietEmail.setText(user.email)
        tietAddress.setText(user.address)
        tietPhone.setText(user.phoneNumber)
    }

    private fun checkUserName() {
        tvSubmit.setOnClickListener {
            if(tietFullname.text.toString().isEmpty()) {
                showError("Masukan nama lengkap baru")
                return@setOnClickListener
            }

            if (tietFullname.text.toString().length < 8)
                showError("Panjang nama kurang dari 8 karakter")
            else
                checkEmail()
        }
    }

    private fun checkEmail() {
        if(tietEmail.text.toString().isEmpty()) {
            showError("Masukan email baru")
            return
        }

        if((tietEmail.text.toString().contains("@")==false) || (tietEmail.text.toString().contains(".")==false))
            showError("Format email tidak sesuai")
        else
            checkPhoneNumber()
    }

    private fun checkPhoneNumber() {
        if(tietPhone.text.toString().isEmpty()) {
            showError("Masukan nomor hp baru")
            return
        } else
            checkAddress()
    }

    private fun checkAddress() {
        if(tietAddress.text.toString().isEmpty()) {
            showError("Masukan alamat baru")
            return
        } else
            moveToPreviousActivity()
    }

    private fun moveToPreviousActivity(){
        tvSubmit.setOnClickListener {
            user.fullname = tietFullname.text.toString()
            user.email = tietEmail.text.toString()
            user.phoneNumber = tietPhone.text.toString()
            user.address = tietAddress.text.toString()
            updateCustomer.set(user.ID!!,
                user.username!!,
                user.fullname!!,
                user.phoneNumber!!,
                user.email!!,
                user.password!!,
                user.address!!,
                if(user.customer?.addressLat!=null)user.customer?.addressLat!!else 0f,
                if(user.customer?.addressLng!=null)user.customer?.addressLng!!else 0f)
            updateCustomer.get().observe(this, Observer {
                Authenticated.invalidateCache()
                finish()
            })
        }
    }
}