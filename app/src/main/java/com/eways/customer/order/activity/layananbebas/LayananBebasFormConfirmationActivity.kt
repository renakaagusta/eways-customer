package com.eways.customer.order.activity.layananbebas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.eways.customer.R
import com.eways.customer.core.baseactivity.BaseActivity
import com.eways.customer.dashboard.activity.MainActivity
import com.eways.customer.utils.customsupportactionbar.CustomSupportActionBar
import com.proyek.infrastructures.notification.usecases.CreateOrderNotification
import com.proyek.infrastructures.order.order.usecases.CreateLayananBebasOrder
import com.proyek.infrastructures.user.agent.entities.UserAgent
import com.proyek.infrastructures.user.customer.entities.UserCustomer
import kotlinx.android.synthetic.main.activity_layananbebas_form.tvSubmit
import kotlinx.android.synthetic.main.activity_layananbebas_form_confirmation.*

class LayananBebasFormConfirmationActivity : BaseActivity() {
    private lateinit var createLayananBebasOrder: CreateLayananBebasOrder

    private lateinit var name: String
    private lateinit var description: String
    private lateinit var agent: UserAgent
    private lateinit var user: UserCustomer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layananbebas_form_confirmation)
        CustomSupportActionBar.setCustomActionBar(this, "Detail Layanan Bebas")
    }

    override fun onStart() {
        super.onStart()

        createLayananBebasOrder = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(CreateLayananBebasOrder::class.java)

        name = intent.getStringExtra("name")
        description = intent.getStringExtra("description")
        agent = intent.getParcelableExtra("agent")
        user = intent.getParcelableExtra("user")

        setData()
        onConfirmation()
    }

    private fun setData() {
        tvLayananBebasName.text = name
        tvLayananBebasDetail.text = description
        tvAgentName.text = agent.username
    }

    private fun onConfirmation() {
        tvSubmit.setOnClickListener {
            this@LayananBebasFormConfirmationActivity.showProgress()
            createLayananBebasOrder.set(name,
                description,
                "1apSvFBkIq0ScrIeQWX8cGl3T8X",
                user.ID!!,
                agent.ID!!,
                user.username!!,
                "Membuat order layanan bebas baru"
                )
            createLayananBebasOrder.get().observe(this, Observer{
                Log.d("layananbebas",it.toString())
                this@LayananBebasFormConfirmationActivity.dismissProgress()
                if(it.errors.message.isEmpty()) {
                    val intent = Intent(this@LayananBebasFormConfirmationActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    this@LayananBebasFormConfirmationActivity.showError(it.errors.message[0])
                }
            })
        }
    }
}