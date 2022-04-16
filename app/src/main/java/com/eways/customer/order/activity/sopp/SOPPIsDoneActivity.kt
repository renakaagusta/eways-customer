package com.eways.customer.order.activity.sopp

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
import kotlinx.android.synthetic.main.activity_sopp_isdone.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SOPPIsDoneActivity : BaseActivity() {

    private lateinit var orderId: String
    private lateinit var getOrderDetail: GetOrderDetail
    private lateinit var updateCustomerReview: UpdateCustomerReview

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sopp_isdone)
        CustomSupportActionBar.setCustomActionBar(this, "Detail Riwayat")

        orderId = intent.getStringExtra("orderId")

        getOrderDetail = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(GetOrderDetail::class.java)
        updateCustomerReview = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(UpdateCustomerReview::class.java)

        sendReview()
    }

    override fun onStart() {
        super.onStart()
        GlobalScope.launch(Dispatchers.Main) {
            getOrderDetail.set(orderId, this@SOPPIsDoneActivity)
            delay(500)
            getOrderDetail.get().forEach {
                val gson = Gson()

                val order: Orderable = gson.fromJson(it.order, Orderable::class.java)

                tvSOPPName.text = order.invoice?.name
                tvSOPPTransactionName.text = order.invoice?.name
                tvCustomerName.text = order.name
                tvCustomerPhone.text = order.phoneNumber
                tvCustomerAddress.text = order.address
                tvAdditionalInfo.text = order.description

                tvAgentFee.text = MoneyUtils.getAmountString(order.service?.agentFee)

                if(it.orderFee!=null) {
                    tvSOPPFee.text = MoneyUtils.getAmountString(it.orderFee)
                    tvTotal.text = MoneyUtils.getAmountString(order.service?.agentFee?.plus(it.orderFee!!))
                } else {
                    tvSOPPFee.text = "-"
                    tvTotal.text = "-"
                }

                setReviewView(it.customerReview != null)
                if (it.customerReview != null) tvReview.text = it.customerReview
            }
        }
    }

    private fun sendReview() {
        tvReviewSend.setOnClickListener {
            this@SOPPIsDoneActivity.showProgress()
            updateCustomerReview.set(orderId, etReview.text.toString())
            updateCustomerReview.get().observe(this, Observer {
                this@SOPPIsDoneActivity.dismissProgress()
                if (it.errors.message.isEmpty()) {
                    this@SOPPIsDoneActivity.showSuccess("Terima kasih atas review anda")
                    finish()
                } else
                    this@SOPPIsDoneActivity.showError(it.errors.message[0])
            })
        }
    }

    private fun setReviewView(reviewStatus: Boolean) {
        if (reviewStatus) {
            tvReview.isVisible = true
            etReview.isVisible = false
            tvReviewSend.isVisible = false
        } else {
            tvReview.isVisible = false
            etReview.isVisible = true
            tvReviewSend.isVisible = true
        }
    }
}