package com.eways.customer.order.activity.gantipaket

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
import com.proyek.infrastructures.order.order.network.body.GantiPaketBody
import com.proyek.infrastructures.order.order.usecases.CreateGantiPaketOrder
import com.proyek.infrastructures.user.agent.entities.UserAgent
import com.proyek.infrastructures.user.customer.entities.UserCustomer
import kotlinx.android.synthetic.main.activity_gantipaket_form_confirmation.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GantiPaketFormConfirmationActivity:BaseActivity() {
    private lateinit var createGantiPaketOrder: CreateGantiPaketOrder
    private lateinit var getServiceDetail: GetServiceDetail

    private lateinit var body: GantiPaketBody
    private lateinit var agent: UserAgent
    private lateinit var user: UserCustomer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gantipaket_form_confirmation)
        CustomSupportActionBar.setCustomActionBar(this, "Detail Ganti Paket")
    }

    override fun onStart() {
        super.onStart()

        getServiceDetail = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetServiceDetail::class.java)
        createGantiPaketOrder = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(CreateGantiPaketOrder::class.java)

        body = intent.getParcelableExtra("body")
        agent = intent.getParcelableExtra("agent")
        user = intent.getParcelableExtra("user")

        setData()
        onConfirmation()
    }

    private fun setData(){
        tvCurrentServicePacketName.text = body.oldInternetService.name
        tvCurrentServicePacketDescription.text = body.oldInternetService.description
        tvServicePacketName.text = body.newInternetService.name
        tvServicePacketDescription.text = body.newInternetService.description
        tvAgentName.text = agent.username

        showProgress()
        getServiceDetail.set("1apRo3tPQuBvIvMsHvJp0VyFBiW")
        getServiceDetail.get().observe(this, Observer {
            dismissProgress()
            tvAgentFee.text = MoneyUtils.getAmountString(it[0].agentFee)
        })
    }

    private fun moveToMainActivity() {
        val intent = Intent(this@GantiPaketFormConfirmationActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.putExtra("user", user)
        startActivity(intent)
        finish()
    }

    private fun onConfirmation() {
        tvSubmit.setOnClickListener {
            this@GantiPaketFormConfirmationActivity.showProgress()
            createGantiPaketOrder.set(body)
            createGantiPaketOrder.get().observe(this, Observer{
                this@GantiPaketFormConfirmationActivity.dismissProgress()
                if(it.errors.message.isEmpty()) {
                    showSuccess("Order berhasil dibuat")
                    setConfirm(this::moveToMainActivity)
                } else {
                    this@GantiPaketFormConfirmationActivity.showError(it.errors.message!![0])
                }
            })
        }
    }
}