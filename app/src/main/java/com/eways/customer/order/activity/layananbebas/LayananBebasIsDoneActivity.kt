package com.eways.customer.order.activity.layananbebas

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
import kotlinx.android.synthetic.main.activity_layananbebas_isdone.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class LayananBebasIsDoneActivity : BaseActivity() {

    private lateinit var orderId: String
    private lateinit var getOrderDetail: GetOrderDetail
    private lateinit var updateCustomerReview: UpdateCustomerReview

    private val reviewStatus = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layananbebas_isdone)
        CustomSupportActionBar.setCustomActionBar(this, "Detail Riwayat")
    }


    override fun onStart() {
        super.onStart()

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

        this@LayananBebasIsDoneActivity.showProgress()

        GlobalScope.launch(Dispatchers.Main) {
            getOrderDetail.set(orderId, this@LayananBebasIsDoneActivity)
            delay(500)
            getOrderDetail.get().forEach {
                val gson = Gson()

                val order: Orderable = gson.fromJson(it.order, Orderable::class.java)

                tvLayananBebasName.text = order.name
                tvLayananBebasDetail.text = order.description

                if(it.orderFee==null){
                    tvLayananBebasTransactionFee.text = "-"
                    tvTotal.text = "-"
                }
                else{
                    tvLayananBebasTransactionFee.text = MoneyUtils.getAmountString(it.orderFee)
                    tvTotal.text = MoneyUtils.getAmountString(it.orderFee)
                }

                setReviewView(it.customerReview != null)
                if (it.customerReview != null) tvReview.text = it.customerReview
            }

            this@LayananBebasIsDoneActivity.dismissProgress()
        }
    }

    private fun sendReview() {
        tvReviewSend.setOnClickListener {
            this@LayananBebasIsDoneActivity.showProgress()
            updateCustomerReview.set(orderId, etReview.text.toString())
            updateCustomerReview.get().observe(this, Observer {
                this@LayananBebasIsDoneActivity.dismissProgress()
                if (it.errors.message.isEmpty()) {
                    this@LayananBebasIsDoneActivity.showSuccess("Terima kasih atas review anda")
                    finish()
                } else
                    this@LayananBebasIsDoneActivity.showError(it.errors.message[0])
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