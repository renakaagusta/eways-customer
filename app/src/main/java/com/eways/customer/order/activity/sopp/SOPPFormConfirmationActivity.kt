package com.eways.customer.order.activity.sopp

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.eways.customer.R
import com.eways.customer.core.baseactivity.BaseActivity
import com.eways.customer.dashboard.activity.MainActivity
import com.eways.customer.utils.customsupportactionbar.CustomSupportActionBar
import com.proyek.infrastructures.notification.usecases.CreateOrderNotification
import com.proyek.infrastructures.order.order.network.body.SOPPBody
import com.proyek.infrastructures.order.order.usecases.CreateSOPPOrder
import com.proyek.infrastructures.user.agent.entities.UserAgent
import com.proyek.infrastructures.user.customer.entities.UserCustomer
import kotlinx.android.synthetic.main.activity_sopp_form_confirmation.*

class SOPPFormConfirmationActivity : BaseActivity(){
    private lateinit var createSOPPOrder: CreateSOPPOrder

    private lateinit var body: SOPPBody
    private lateinit var agent: UserAgent
    private lateinit var user: UserCustomer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sopp_form_confirmation)
        CustomSupportActionBar.setCustomActionBar(this, "Detail SOPP")

        createSOPPOrder = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(CreateSOPPOrder::class.java)

        body = intent.getParcelableExtra("body")
        agent = intent.getParcelableExtra("agent")
        user = intent.getParcelableExtra("user")

        setData()
        onConfirmation()
    }

    private fun setData(){
        tvSOPPName.text = body.invoice.name
        tvCustomerName.text = body.name
        tvCustomerPhone.text = body.phoneNumber
        tvCustomerAddress.text = body.address
        tvAdditionalInfo.text = body.description
        tvAgentName.text = agent.username
    }

    private fun onConfirmation(){
        tvSubmit.setOnClickListener {
            this@SOPPFormConfirmationActivity.showProgress()
            createSOPPOrder.set(body)
            createSOPPOrder.get().observe(this, Observer{
                this@SOPPFormConfirmationActivity.dismissProgress()
                if(it.errors.message.isEmpty()) {
                    val intent = Intent(this@SOPPFormConfirmationActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    this@SOPPFormConfirmationActivity.showError(it.errors.message!![0])
                }
            })
        }
    }
}