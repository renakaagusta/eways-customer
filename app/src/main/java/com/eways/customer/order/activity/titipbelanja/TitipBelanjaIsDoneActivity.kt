package com.eways.customer.order.activity.titipbelanja

import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.eways.customer.R
import com.eways.customer.core.baseactivity.BaseActivity
import com.eways.customer.order.adapter.TitipBelanjaTransactionAdapter
import com.eways.customer.order.viewdto.TransactionViewDTO
import com.eways.customer.utils.MoneyUtils
import com.eways.customer.utils.customitemdecoration.CustomDividerItemDecoration
import com.eways.customer.utils.customsupportactionbar.CustomSupportActionBar
import com.google.gson.Gson
import com.proyek.infrastructures.order.order.entities.Orderable
import com.proyek.infrastructures.order.order.usecases.GetOrderDetail
import com.proyek.infrastructures.order.order.usecases.UpdateCustomerReview
import kotlinx.android.synthetic.main.activity_titipbelanja_isdone.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TitipBelanjaIsDoneActivity : BaseActivity() {

    private lateinit var orderId : String
    private var agentFee = 0

    private lateinit var getOrderDetail: GetOrderDetail
    private lateinit var updateCustomerReview: UpdateCustomerReview

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_titipbelanja_isdone)
        CustomSupportActionBar.setCustomActionBar(this,"Detail Riwayat")

        CustomSupportActionBar.setCustomActionBar(this, "Detail Riwayat")
        sendReview()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("orderId", orderId)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        orderId = savedInstanceState.getString("orderId")!!
    }

    override fun onStart() {
        super.onStart()

        orderId = intent.getStringExtra("orderId")

        getOrderDetail = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetOrderDetail::class.java)
        updateCustomerReview = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(UpdateCustomerReview::class.java)

        this@TitipBelanjaIsDoneActivity.showProgress()

        GlobalScope.launch(Dispatchers.Main) {
            getOrderDetail.set(orderId, this@TitipBelanjaIsDoneActivity)
            delay(200)
            for(it in getOrderDetail.get()){

                getOrderDetail.get().clear()

                val listTransactionViewDTO = ArrayList<TransactionViewDTO>()

                getOrderDetail.set(orderId, this@TitipBelanjaIsDoneActivity)
                delay(500)
                getOrderDetail.get().forEach {
                    val gson = Gson()
                    val order = gson.fromJson(it.order, Orderable::class.java)

                    order.groceries.forEach {
                        if(it.quantity>0) {
                            listTransactionViewDTO.add(
                                TransactionViewDTO(
                                    it.items.imgPath,
                                    it.items.name!!,
                                    it.quantity,
                                    it.items.price!!
                                )
                            )
                        }
                    }

                    tvProductName.text = "Pesan produk "+order.groceries[0].items.category?.name
                    agentFee = order.service?.agentFee!!

                    if(it.customerReview!=null) {
                        tvReview.text = it.customerReview
                        setReviewView(true)
                    }
                }

                delay(300)

                val titipBelanjaTransactionAdapter =
                    TitipBelanjaTransactionAdapter(
                        listTransactionViewDTO
                    )
                rvTransaction.apply {
                    layoutManager = LinearLayoutManager(this@TitipBelanjaIsDoneActivity)
                    addItemDecoration(
                        CustomDividerItemDecoration(
                            ContextCompat.getDrawable(
                                this@TitipBelanjaIsDoneActivity,
                                R.drawable.divider_line
                            )!!
                        )
                    )
                    isNestedScrollingEnabled = false
                    adapter = titipBelanjaTransactionAdapter
                }
                var totalPrice = 0
                for (i in 0 until listTransactionViewDTO.size) {
                    totalPrice += listTransactionViewDTO[i].subproductPrice * listTransactionViewDTO[i].suProductAmount
                }

                tvTotalBelanja.text = totalPrice.toString()
                tvAgentFee.text = MoneyUtils.getAmountString(agentFee)
                tvTotalTransaction.text = MoneyUtils.getAmountString(totalPrice + agentFee)
            }

            getOrderDetail.get().clear()

            this@TitipBelanjaIsDoneActivity.dismissProgress()
        }
    }

    private fun sendReview() {
        tvReviewSend.setOnClickListener {
            this@TitipBelanjaIsDoneActivity.showProgress()
            updateCustomerReview.set(orderId, etReview.text.toString())
            updateCustomerReview.get().observe(this, Observer {
                this@TitipBelanjaIsDoneActivity.dismissProgress()
                if(it.errors.message.isEmpty()) {
                    this@TitipBelanjaIsDoneActivity.showSuccess("Terima kasih atas review anda")
                    finish()
                } else
                    this@TitipBelanjaIsDoneActivity.showError(it.errors.message[0])
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