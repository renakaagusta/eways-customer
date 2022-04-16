package com.eways.customer.order.activity.psb

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.eways.customer.R
import com.eways.customer.core.baseactivity.BaseActivity
import com.eways.customer.dashboard.activity.MainActivity
import com.eways.customer.utils.MoneyUtils
import com.eways.customer.utils.customsupportactionbar.CustomSupportActionBar
import com.proyek.infrastructures.inventory.service.usecases.GetServiceDetail
import com.proyek.infrastructures.notification.usecases.CreateOrderNotification
import com.proyek.infrastructures.order.order.network.body.PSBBody
import com.proyek.infrastructures.order.order.usecases.CreatePSBOrder
import com.proyek.infrastructures.user.agent.entities.Agent
import com.proyek.infrastructures.user.agent.entities.UserAgent
import com.proyek.infrastructures.user.customer.entities.UserCustomer
import kotlinx.android.synthetic.main.activity_psb_form_confirmation.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PSBFormConfirmationActivity : BaseActivity() {
    private lateinit var createPSBOrder: CreatePSBOrder
    private lateinit var getServiceDetail: GetServiceDetail

    private lateinit var body: PSBBody
    private lateinit var agent: UserAgent
    private lateinit var user: UserCustomer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_psb_form_confirmation)
        CustomSupportActionBar.setCustomActionBar(this, "Detail Pasang Baru")
    }

    override fun onStart() {
        super.onStart()

        createPSBOrder = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(CreatePSBOrder::class.java)
        getServiceDetail = ViewModelProvider(this,ViewModelProvider.NewInstanceFactory()).get(GetServiceDetail::class.java)

        body = intent.getParcelableExtra("body")
        agent = intent.getParcelableExtra("agent")
        user = intent.getParcelableExtra("user")

        setData()
        onConfirmation()
    }

    private fun setData() {
        tvServicePacketName.text = body.internetService.name
        tvServicePacketDescription.text = body.internetService.description
        tvAgentName.text = agent.username

        showProgress()
        getServiceDetail.set("1apQwniIJo6WnAm2cIk7WEkzWt0")
        getServiceDetail.get().observe(this, Observer {
            dismissProgress()
            tvAgentFee.text = MoneyUtils.getAmountString(it[0].agentFee)
        })
    }

    private fun onConfirmation() {
        tvSubmit.setOnClickListener {
            this@PSBFormConfirmationActivity.showProgress()
            createPSBOrder.set(body)
            createPSBOrder.get().observe(this, Observer{
                this@PSBFormConfirmationActivity.dismissProgress()
                if(it.errors.message.isEmpty()) {
                    val intent = Intent(this@PSBFormConfirmationActivity, MainActivity::class.java)
                    intent.putExtra("user", user)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    this@PSBFormConfirmationActivity.showError(it.errors.message[0])
                }
            })
        }
    }
}