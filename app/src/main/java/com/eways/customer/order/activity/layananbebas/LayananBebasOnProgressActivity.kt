package com.eways.customer.order.activity.layananbebas

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cn.pedant.SweetAlert.SweetAlertDialog
import com.eways.customer.R
import com.eways.customer.core.baseactivity.BaseActivity
import com.eways.customer.dashboard.activity.MainActivity
import com.eways.customer.order.activity.chat.ChatActivity
import com.eways.customer.order.const.OrderStatus
import com.eways.customer.order.const.PaymentStatus
import com.eways.customer.utils.MoneyUtils
import com.eways.customer.utils.customsupportactionbar.CustomSupportActionBar
import com.google.gson.Gson
import com.proyek.infrastructures.notification.usecases.CreateOrderNotification
import com.proyek.infrastructures.order.order.entities.Orderable
import com.proyek.infrastructures.order.order.usecases.CustomerFinishOrder
import com.proyek.infrastructures.order.order.usecases.DeleteOrder
import com.proyek.infrastructures.order.order.usecases.GetOrderDetail
import com.proyek.infrastructures.user.agent.usecases.GetAgentDetail
import com.proyek.infrastructures.user.customer.entities.UserCustomer
import kotlinx.android.synthetic.main.activity_layananbebas_onprogress.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LayananBebasOnProgressActivity :BaseActivity() {
    private lateinit var id: String
    private lateinit var status: String
    private lateinit var user : UserCustomer
    private var agentId = ""
    private var agentUserName = ""

    private lateinit var getOrderDetail: GetOrderDetail
    private lateinit var getAgentDetail: GetAgentDetail
    private lateinit var finishOrder: CustomerFinishOrder
    private lateinit var createNotification: CreateOrderNotification
    private lateinit var deleteOrder: DeleteOrder

    private var orderStatus = OrderStatus.OnProgress
    private var paymentStatus = PaymentStatus.Unpaid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layananbebas_onprogress)
        CustomSupportActionBar.setCustomActionBar(this, "Detail Pesanan Layanan Bebas")
    }

    override fun onStart() {
        super.onStart()

        id = intent.getStringExtra("id")
        status = intent.getStringExtra("status")
        user = intent.getParcelableExtra("user")

        getOrderDetail = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetOrderDetail::class.java)
        getAgentDetail = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetAgentDetail::class.java)
        finishOrder = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(CustomerFinishOrder::class.java)
        createNotification = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(CreateOrderNotification::class.java)
        deleteOrder = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(DeleteOrder::class.java)

        setData()
        setLayout()
        moveToChat()
        finishingOrder()
        cancelOrder()
    }

    private fun setData(){
        val lifecycleOwner = this

        txtStatus.text = status

        this@LayananBebasOnProgressActivity.showProgress()

        GlobalScope.launch(Dispatchers.Main) {
            getOrderDetail.set(id, this@LayananBebasOnProgressActivity)
            delay(1000)
            getOrderDetail.get().forEach {
                val gson = Gson()
                val order = gson.fromJson(it.order, Orderable::class.java)
                tvLayananBebasName.text = order.name

                if(it.orderFee==null){
                    tvLayananBebasTransactionFee.text = "-"
                    tvTotal.text = "-"
                }
                else{
                    tvLayananBebasTransactionFee.text = MoneyUtils.getAmountString(it.orderFee)
                    tvTotal.text = MoneyUtils.getAmountString(it.orderFee)
                }
                tvLayananBebasDetail.text = order.description

                agentId = it.agentId!!

                if(it.orderStatus!! > 0)
                    orderStatus = OrderStatus.OnProgress
                else
                    orderStatus = OrderStatus.Created
                if(it.paymentStatus==1)paymentStatus = PaymentStatus.Paid
            }

            delay (500)

            getAgentDetail.set(agentId)
            getAgentDetail.get().observe(lifecycleOwner, Observer {
                tvAgentName.text = it.data[0].username
            })

            setLayout()

            this@LayananBebasOnProgressActivity.dismissProgress()
        }
    }

    private fun setLayout(){
        if(orderStatus == OrderStatus.Created){
            statusOnRequest.isVisible = true
            otherStatus.isVisible = false
            llDone.isVisible = false
            llCancelOrder.isVisible = true
        }else if(orderStatus == OrderStatus.OnProgress){
            statusOnRequest.isVisible = false
            otherStatus.isVisible =true

            when (paymentStatus) {
                PaymentStatus.Paid -> {
                    llDone.background = ContextCompat.getDrawable(
                        this@LayananBebasOnProgressActivity,
                        R.drawable.rc_bgprimary
                    )
                }
                PaymentStatus.Unpaid -> {
                    llDone.background = ContextCompat.getDrawable(
                        this@LayananBebasOnProgressActivity,
                        R.drawable.rc_bglightgray
                    )
                }
            }
        }
    }

    private fun moveToChat(){
        llChat.setOnClickListener {
            val intent = Intent(this@LayananBebasOnProgressActivity, ChatActivity::class.java)
            intent.putExtra("customerId", user.ID)
            intent.putExtra("customerUserName", user.username)
            intent.putExtra("agentId", agentId)
            intent.putExtra("agentUserName", tvAgentName.text.toString())
            intent.putExtra("orderId", id)
            startActivity(intent)
        }
        llChatNotReady.setOnClickListener{
            this@LayananBebasOnProgressActivity.showError("Agen belum menerima pesanan")
        }
    }

    private fun finishingOrder(){
        llDone.setOnClickListener {
            this@LayananBebasOnProgressActivity.showProgress()
            finishOrder.set(id)
            finishOrder.get().observe(this, Observer {
                if(it.errors.message.isEmpty()){
                    GlobalScope.launch(Dispatchers.Main) {
                        createNotification.set(
                            agentId,
                            id,
                            "Order pemasangan barumu telah diselesaikan user",
                            ""
                        )
                        delay(300)
                        createNotification.set(
                            user.ID!!,
                            id,
                            "Eways",
                            "Order berhasil diselesaikan"
                        )
                        delay(300)
                        this@LayananBebasOnProgressActivity.dismissProgress()
                        val intent = Intent(this@LayananBebasOnProgressActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    this@LayananBebasOnProgressActivity.dismissProgress()
                    this@LayananBebasOnProgressActivity.showError(it.errors.message[0])
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