package com.eways.customer.order.activity.titipbelanja

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.eways.customer.R
import com.eways.customer.core.baseactivity.BaseActivity
import com.eways.customer.dashboard.activity.MainActivity
import com.eways.customer.order.activity.chat.ChatActivity
import com.eways.customer.order.adapter.TitipBelanjaTransactionAdapter
import com.eways.customer.order.const.OrderStatus
import com.eways.customer.order.const.PaymentStatus
import com.eways.customer.order.viewdto.TransactionViewDTO
import com.eways.customer.utils.MoneyUtils
import com.eways.customer.utils.customitemdecoration.CustomDividerItemDecoration
import com.eways.customer.utils.customsupportactionbar.CustomSupportActionBar
import com.google.gson.Gson
import com.proyek.infrastructures.inventory.item.usecases.GetItemDetail
import com.proyek.infrastructures.notification.usecases.CreateOrderNotification
import com.proyek.infrastructures.order.order.entities.Orderable
import com.proyek.infrastructures.order.order.usecases.CustomerFinishOrder
import com.proyek.infrastructures.order.order.usecases.DeleteOrder
import com.proyek.infrastructures.order.order.usecases.GetOrderDetail
import com.proyek.infrastructures.user.agent.usecases.GetAgentDetail
import com.proyek.infrastructures.user.customer.entities.UserCustomer
import com.proyek.infrastructures.user.customer.usecases.GetCustomerDetail
import kotlinx.android.synthetic.main.activity_titipbelanja_onprogress.*
import kotlinx.android.synthetic.main.activity_titipbelanja_onprogress.llCancelOrder
import kotlinx.android.synthetic.main.activity_titipbelanja_onprogress.llChat
import kotlinx.android.synthetic.main.activity_titipbelanja_onprogress.llChatNotReady
import kotlinx.android.synthetic.main.activity_titipbelanja_onprogress.llDone
import kotlinx.android.synthetic.main.activity_titipbelanja_onprogress.otherStatus
import kotlinx.android.synthetic.main.activity_titipbelanja_onprogress.statusOnRequest
import kotlinx.android.synthetic.main.activity_titipbelanja_onprogress.tvAgentFee
import kotlinx.android.synthetic.main.activity_titipbelanja_onprogress.tvAgentName
import kotlinx.android.synthetic.main.activity_titipbelanja_onprogress.txtStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TitipBelanjaOnProgressActivity : BaseActivity() {
    private lateinit var id: String
    private lateinit var status: String
    private lateinit var user : UserCustomer
    private var agentId = ""
    private var agentUserName = ""
    private var agentFee = 0

    private lateinit var getOrderDetail: GetOrderDetail
    private lateinit var getAgentDetail: GetAgentDetail
    private lateinit var finishOrder: CustomerFinishOrder
    private lateinit var createNotification: CreateOrderNotification
    private lateinit var getCustomerDetail: GetCustomerDetail
    private lateinit var getItemDetail: GetItemDetail
    private lateinit var deleteOrder: DeleteOrder

    private var orderStatus = OrderStatus.OnProgress
    private var paymentStatus = PaymentStatus.Unpaid
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_titipbelanja_onprogress)
        CustomSupportActionBar.setCustomActionBar(this, "Detail Pesanan Titip Belanja")

        id = intent.getStringExtra("id")
        status = intent.getStringExtra("status")
        user = intent.getParcelableExtra("user")

        getOrderDetail = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetOrderDetail::class.java)
        getAgentDetail = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetAgentDetail::class.java)
        finishOrder = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(CustomerFinishOrder::class.java)
        createNotification = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(CreateOrderNotification::class.java)
        getCustomerDetail = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetCustomerDetail::class.java)
        getItemDetail = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetItemDetail::class.java)
        deleteOrder = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(DeleteOrder::class.java)


        setData()
        moveToChat()
        finishingOrder()
        cancelOrder()
    }

    private fun setData(){
        val lifecycleOwner = this

        txtStatus.text = status

        this@TitipBelanjaOnProgressActivity.showProgress()

        GlobalScope.launch(Dispatchers.Main) {
            val listTransactionViewDTO = ArrayList<TransactionViewDTO>()
            var itemId = ""

            getOrderDetail.set(id, this@TitipBelanjaOnProgressActivity)
            delay(500)
            getOrderDetail.get().forEach {
                val gson = Gson()

                val order = gson.fromJson(it.order, Orderable::class.java)

                order.groceries.forEach {
                    itemId = it.items.id!!
                    if (it.quantity > 0) {
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

                tvProductName.text = "Pesan produk " + order.groceries[0].items.category?.name

                agentId = it.agentId!!
                agentFee = order.service?.agentFee!!


                if (it.orderStatus!! > 0)
                    orderStatus = OrderStatus.OnProgress
                else
                    orderStatus = OrderStatus.Created
                if (it.paymentStatus == 1)
                    paymentStatus = PaymentStatus.Paid

                Log.d("orderStatus", it.orderStatus.toString())
                Log.d("orderStatus", orderStatus.toString())
            }

            delay(500)

            getAgentDetail.set(agentId)
            getAgentDetail.get().observe(lifecycleOwner, Observer {
                agentId = it.data[0].ID!!
                agentUserName = it.data[0].username!!
                tvAgentName.text = it.data[0].username
            })

            delay(300)

            val titipBelanjaTransactionAdapter =
                TitipBelanjaTransactionAdapter(
                    listTransactionViewDTO
                )
            rvTransaction.apply {
                layoutManager = LinearLayoutManager(this@TitipBelanjaOnProgressActivity)
                addItemDecoration(
                    CustomDividerItemDecoration(
                        ContextCompat.getDrawable(
                            this@TitipBelanjaOnProgressActivity,
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

            this@TitipBelanjaOnProgressActivity.dismissProgress()

            setLayout()

        }
    }

    private fun setLayout(){
        Log.d("orderStatus", orderStatus.toString())

        if(orderStatus == OrderStatus.Created){
            statusOnRequest.isVisible = true
            otherStatus.isVisible = false
            llDone.isVisible = false
            llCancelOrder.isVisible = true
        }else if(orderStatus == OrderStatus.OnProgress){
            statusOnRequest.isVisible = false
            otherStatus.isVisible =true
            if(paymentStatus == PaymentStatus.Paid){
                llDone.background = ContextCompat.getDrawable(this@TitipBelanjaOnProgressActivity, R.drawable.rc_bgprimary)
            }else{
                llDone.background = ContextCompat.getDrawable(this@TitipBelanjaOnProgressActivity, R.drawable.rc_bglightgray)
            }
        }
    }

    private fun moveToChat(){
        llChat.setOnClickListener {
            val intent = Intent(this@TitipBelanjaOnProgressActivity, ChatActivity::class.java)
            intent.putExtra("customerId", user.ID)
            intent.putExtra("customerUserName", user.username)
            intent.putExtra("agentId", agentId)
            intent.putExtra("agentUserName", tvAgentName.text.toString())
            intent.putExtra("orderId", id)
            startActivity(intent)
        }
        llChatNotReady.setOnClickListener{
            this@TitipBelanjaOnProgressActivity.showError("Agen belum menerima pesanan")
        }
    }

    private fun finishingOrder(){
        llDone.setOnClickListener {
            this@TitipBelanjaOnProgressActivity.showProgress()
            finishOrder.set(id)
            finishOrder.get().observe(this, Observer {
                if(it.errors.message.isEmpty()) {
                    GlobalScope.launch(Dispatchers.Main) {
                        createNotification.set(
                            agentId,
                            id,
                            "Eways",
                            "Order titip belanja mu telah diselesaikan user"
                        )
                        delay(300)
                        createNotification.set(
                            user.ID!!,
                            id,
                            "Eways",
                            "Order berhasil diselesaikan"
                        )
                        delay(300)
                        this@TitipBelanjaOnProgressActivity.dismissProgress()
                        val intent = Intent(this@TitipBelanjaOnProgressActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    this@TitipBelanjaOnProgressActivity.dismissProgress()
                    this@TitipBelanjaOnProgressActivity.showError(it.errors.message[0])
                }
            })
        }
    }

    private fun cancelOrder(){
        llCancelOrder.setOnClickListener {
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Apakah anda yakin ingin membatalkan pesanan")
                .setConfirmButton("Ya",{
                    deleteOrder.set(id)
                    deleteOrder.get().observe(this, Observer {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    })
                })
                .setCancelText("Tidak")
                .show()

        }
    }
}