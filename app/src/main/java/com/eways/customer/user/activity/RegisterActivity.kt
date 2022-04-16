package com.eways.customer.user.activity

import android.content.Intent
import android.os.Bundle
import com.eways.customer.core.baseactivity.BaseActivity
import com.eways.customer.utils.customsupportactionbar.CustomSupportActionBar
import com.eways.customer.R
import com.eways.customer.dashboard.activity.WelcomeActivity
import kotlinx.android.synthetic.main.activity_profile_update.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.tietEmail
import kotlinx.android.synthetic.main.activity_register.tietFullname
import kotlinx.android.synthetic.main.activity_register.tietPhone
class RegisterActivity : BaseActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        CustomSupportActionBar.setCustomActionBar(this,"Registrasi")

        checkUserNameLength()
    }

    private fun checkUserNameLength() {
        tvRegister.setOnClickListener {
            if (tietFullname.text.toString().length < 8)
                showError("Panjang nama kurang dari 8 karakter")
            else
                checkEmailFormat()
        }
    }

    private fun checkEmailFormat() {
        if((tietEmail.text.toString().contains("@")==false) || (tietEmail.text.toString().contains(".")==false))
            showError("Format email tidak sesuai")
        else
            moveToSetAddress()
    }

    private fun moveToSetAddress(){
        tvRegister.setOnClickListener {
            val intent = Intent(this, SetAddressActivity::class.java)
            intent.putExtra("userName", tietFullname.text.toString())
            intent.putExtra("email", tietEmail.text.toString())
            intent.putExtra("phoneNumber", "+62"+tietPhone.text.toString())
            startActivity(intent)
        }
    }
}