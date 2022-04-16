package com.eways.customer.order.activity.gantipaket

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
import kotlinx.android.synthetic.main.activity_gantipaket_isdone.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GantiPaketIsDoneActivity : BaseActivity() {
    private lateinit var orderId : String
    private lateinit var getOrderDetail: GetOrderDetail
    private lateinit var updateCustomerReview: UpdateCustomerReview

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gantipaket_isdone)
        CustomSupportActionBar.setCustomActionBar(this, "Detail Riwayat")
    }

    override fun onStart() {
        super.onStart()

        orderId = intent.getStringExtra("orderId")

        getOrderDetail = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetOrderDetail::class.java)
        updateCustomerReview = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(UpdateCustomerReview::class.java)

        sendReview()

        this@GantiPaketIsDoneActivity.showProgress()

        GlobalScope.launch(Dispatchers.Main) {
            getOrderDetail.set(orderId, this@GantiPaketIsDoneActivity)
            delay(500)
            getOrderDetail.get().forEach {
                val gson = Gson()

                val orderable: Orderable = gson.fromJson(it.order, Orderable::class.java)

                tvServicePacketName.text = orderable.newInternetService.name
                tvCurrentServicePacketName.text = orderable.oldInternetService.name
                tvServicePacketDescription.text = orderable.newInternetService.description
                tvCurrentServicePacketDescription.text = orderable.oldInternetService.description

                txtServiceFee.text = MoneyUtils.getAmountString(orderable.service?.agentFee)
                txtTotalFee.text = MoneyUtils.getAmountString(orderable.service?.agentFee)

                setReviewView(it.customerReview!=null)
                if(it.customerReview!=null)tvReview.text = it.customerReview
            }

            this@GantiPaketIsDoneActivity.dismissProgress()
        }
    }


    private fun sendReview() {
        tvReviewSend.setOnClickListener {
            this@GantiPaketIsDoneActivity.showProgress()
            updateCustomerReview.set(orderId, etReview.text.toString())
            updateCustomerReview.get().observe(this, Observer {
                this@GantiPaketIsDoneActivity.dismissProgress()
                if(it.errors.message.isNullOrEmpty()) {
                    this@GantiPaketIsDoneActivity.showSuccess("Terima kasih atas review anda")
                    finish()
                } else {
                    this@GantiPaketIsDoneActivity.showError(it.errors.message!![0])
                }
            })
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

}