package com.eways.customer.order.activity.pickupticket

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
import com.proyek.infrastructures.notification.usecases.CreateNotification
import com.proyek.infrastructures.notification.usecases.CreateOrderNotification
import com.proyek.infrastructures.order.order.network.body.LaporanKerusakanBody
import com.proyek.infrastructures.order.order.usecases.CreateLaporanKerusakanOrder
import com.proyek.infrastructures.user.agent.entities.UserAgent
import com.proyek.infrastructures.user.customer.entities.UserCustomer
import kotlinx.android.synthetic.main.activity_pickupticket_form_confirmation.*

class PickupTicketFormConfirmationActivity : BaseActivity(){
    private lateinit var createLaporanKerusakanOrder: CreateLaporanKerusakanOrder
    private lateinit var getServiceDetail: GetServiceDetail

    private lateinit var body: LaporanKerusakanBody
    private lateinit var agent: UserAgent
    private lateinit var user: UserCustomer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pickupticket_form_confirmation)
    }

    override fun onStart() {
        super.onStart()

        createLaporanKerusakanOrder = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(CreateLaporanKerusakanOrder::class.java)
        getServiceDetail = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetServiceDetail::class.java)

        body = intent.getParcelableExtra("body")
        agent = intent.getParcelableExtra("agent")
        user = intent.getParcelableExtra("user")

        CustomSupportActionBar.setCustomActionBar(this, "Detail Laporan Kerusakan")
        setData()
        onConfirmation()
    }

    private fun setData(){
        tvServicePacketName.text = body.internetService.name
        tvPickupTicketDescription.text = body.damageDescription
        tvAgentName.text = agent.username

        showProgress()
        getServiceDetail.set("1apPg8GAOei7bEsom7fxB0wBgxD")
        getServiceDetail.get().observe(this, Observer {
            dismissProgress()
            tvAgentFee.text = MoneyUtils.getAmountString(it[0].agentFee)
        })
    }

    private fun onConfirmation(){
        tvSubmit.setOnClickListener {
            this@PickupTicketFormConfirmationActivity.showProgress()
            createLaporanKerusakanOrder.set(body)
            createLaporanKerusakanOrder.get().observe(this, Observer{
                this@PickupTicketFormConfirmationActivity.dismissProgress()
                if(it.errors.message.isEmpty()) {
                    val intent = Intent(this@PickupTicketFormConfirmationActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.putExtra("user", user)
                    startActivity(intent)
                    finish()
                } else {
                    this@PickupTicketFormConfirmationActivity.showError(it.errors.message[0])
                }
            })
        }
    }
}