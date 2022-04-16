package com.eways.customer.order.activity.titippaket

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.eways.customer.R
import com.eways.customer.core.baseactivity.BaseActivity
import com.eways.customer.utils.MoneyUtils
import com.eways.customer.utils.customsupportactionbar.CustomSupportActionBar
import com.google.gson.Gson
import com.proyek.infrastructures.order.order.entities.Orderable
import com.proyek.infrastructures.order.order.usecases.GetOrderDetail
import com.proyek.infrastructures.order.order.usecases.UpdateCustomerReview
import com.proyek.infrastructures.user.agent.usecases.GetAgentDetail
import kotlinx.android.synthetic.main.activity_titippaket_isdone.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TitipPaketIsDoneActivity : BaseActivity() {

    private lateinit var orderId : String
    private lateinit var agentId : String

    private lateinit var getOrderDetail: GetOrderDetail
    private lateinit var getAgentDetail: GetAgentDetail
    private lateinit var updateCustomerReview: UpdateCustomerReview

    private val reviewStatus = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_titippaket_isdone)
        CustomSupportActionBar.setCustomActionBar(this,"Detail Riwayat")
    }

    override fun onStart() {
        super.onStart()

        orderId = intent.getStringExtra("orderId")

        getOrderDetail = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetOrderDetail::class.java)
        getAgentDetail = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetAgentDetail::class.java)
        updateCustomerReview = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(UpdateCustomerReview::class.java)

        setReviewView(reviewStatus)
        setData()
        sendReview()
    }

    private fun setData(){

        this@TitipPaketIsDoneActivity.showProgress()

        GlobalScope.launch(Dispatchers.Main) {
            getOrderDetail.set(orderId, this@TitipPaketIsDoneActivity)
            delay(500)
            getOrderDetail.get().forEach {
                val gson = Gson()

                val order: Orderable = gson.fromJson(it.order, Orderable::class.java)

                tvReceiverName.text = order.receiverName
                tvReceiverAddress.text = order.receiverAddress
                tvReceiverPhone.text = order.receiverPhoneNumber

                tvSenderName.text = order.senderName
                tvSenderAddress.text = order.senderAddress
                tvSenderPhone.text= order.senderPhoneNumber

                tvItemName.text = order.packetName
                tvItemDescription.text = order.packetDescription

                tvAgentFee.text = MoneyUtils.getAmountString(order.service?.agentFee)
                if(it.orderFee!=null) {
                    tvTitipPaketFee.text = MoneyUtils.getAmountString(it.orderFee)
                    tvTotal.text = MoneyUtils.getAmountString(order.service?.agentFee?.plus(it.orderFee!!))
                } else {
                    tvTitipPaketFee.text = "-"
                    tvTotal.text = "-"
                }

                agentId = it.agentId!!

                setReviewView(it.customerReview!=null)
                if(it.customerReview!=null)tvReview.text = it.customerReview
            }

            this@TitipPaketIsDoneActivity.dismissProgress()
        }

    }

    private fun setReviewView(reviewStatus: Boolean){
        if(reviewStatus){
            tvReview.isVisible = true
            etReview.isVisible = false
            tvReviewSend.isVisible = false
        }else{
            tvReview.isVisible = false
            etReview.isVisible = true
            tvReviewSend.isVisible = true
        }
    }

    private fun sendReview() {
        tvReviewSend.setOnClickListener {
            this@TitipPaketIsDoneActivity.showProgress()
            updateCustomerReview.set(orderId, etReview.text.toString())
            updateCustomerReview.get().observe(this, Observer {
                this@TitipPaketIsDoneActivity.dismissProgress()
                if(it.errors.message.isEmpty()) {
                    this@TitipPaketIsDoneActivity.showSuccess("Terima kasih atas review anda")
                    finish()
                } else
                    this@TitipPaketIsDoneActivity.showError(it.errors.message[0])
            })
        }
    }
}