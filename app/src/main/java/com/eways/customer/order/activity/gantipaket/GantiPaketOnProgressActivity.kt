package com.eways.customer.order.activity.gantipaket

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
import com.proyek.infrastructures.utils.Authenticated
import kotlinx.android.synthetic.main.activity_gantipaket_onprogress.*
import kotlinx.android.synthetic.main.activity_gantipaket_onprogress.llCancelOrder
import kotlinx.android.synthetic.main.activity_gantipaket_onprogress.llChat
import kotlinx.android.synthetic.main.activity_gantipaket_onprogress.llDone
import kotlinx.android.synthetic.main.activity_gantipaket_onprogress.otherStatus
import kotlinx.android.synthetic.main.activity_gantipaket_onprogress.statusOnRequest
import kotlinx.android.synthetic.main.activity_gantipaket_onprogress.tvAgentFee
import kotlinx.android.synthetic.main.activity_gantipaket_onprogress.tvAgentName
import kotlinx.android.synthetic.main.activity_gantipaket_onprogress.tvServicePacketName
import kotlinx.android.synthetic.main.activity_gantipaket_onprogress.tvTotal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GantiPaketOnProgressActivity : BaseActivity() {
    private lateinit var id: String
    private var agentId = ""
    private lateinit var status: String

    private lateinit var getOrderDetail: GetOrderDetail
    private lateinit var finishOrder: CustomerFinishOrder
    private lateinit var getAgentDetail: GetAgentDetail
    private lateinit var createNotification: CreateOrderNotification
    private lateinit var deleteOrder: DeleteOrder

    private lateinit var user: UserCustomer

    //3 types: hasbeenpaid, onrequest, onprogress
    private var orderStatus = OrderStatus.OnProgress
    private var paymentStatus = PaymentStatus.Unpaid
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gantipaket_onprogress)
        CustomSupportActionBar.setCustomActionBar(this, "Detail Ganti Paket")

        id = intent.getStringExtra("id")
        status = intent.getStringExtra("status")

        user = Authenticated.getUserCustomer()

        getOrderDetail = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetOrderDetail::class.java)
        getAgentDetail = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetAgentDetail::class.java)
        finishOrder = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(CustomerFinishOrder::class.java)
        createNotification = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(CreateOrderNotification::class.java)
        deleteOrder = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(DeleteOrder::class.java)

        setData()
        moveToChat()
        finishingOrder()
        cancelOrder()
    }

    private fun setData(){
        val lifecycleOwner = this

        txtStatus.text = status

        this@GantiPaketOnProgressActivity.showProgress()

        GlobalScope.launch(Dispatchers.Main) {
            getOrderDetail.set(id, this@GantiPaketOnProgressActivity)
            delay(1000)
            getOrderDetail.get().forEach {
                val gson = Gson()
                val order = gson.fromJson(it.order, Orderable::class.java)
                tvCurrentServicePacketName.text = order.oldInternetService.name
                tvCurrentServicePacketDescription.text = order.oldInternetService.description
                tvServicePacketName.text = order.newInternetService.name
                tvServicePacketDescription.text = order.newInternetService.description
                tvAgentFee.text = MoneyUtils.getAmountString(order.service?.agentFee)
                tvTotal.text = MoneyUtils.getAmountString(order.service?.agentFee)
                agentId = it.agentId!!
                if(it.orderStatus!! > 0)
                    orderStatus = OrderStatus.OnProgress
                else
                    orderStatus = OrderStatus.Created
                if(it.orderStatus==2)orderStatus = OrderStatus.CustomerFinished
                if(it.paymentStatus==1)paymentStatus = PaymentStatus.Paid
            }

            delay (500)

            getAgentDetail.set(agentId)
            getAgentDetail.get().observe(lifecycleOwner, Observer {
                tvAgentName.text = it.data[0].username
            })

            setLayout()

            this@GantiPaketOnProgressActivity.dismissProgress()
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
            if(paymentStatus == PaymentStatus.Paid){
                llDone.background = ContextCompat.getDrawable(this@GantiPaketOnProgressActivity, R.drawable.rc_bgprimary)
            }else{
                llDone.background = ContextCompat.getDrawable(this@GantiPaketOnProgressActivity, R.drawable.rc_bglightgray)
            }
        }
    }

    private fun moveToChat(){
        llChat.setOnClickListener {
            val intent = Intent(this@GantiPaketOnProgressActivity, ChatActivity::class.java)
            intent.putExtra("customerId", user.ID)
            intent.putExtra("customerUserName", user.username)
            intent.putExtra("agentId", agentId)
            intent.putExtra("agentUserName", tvAgentName.text.toString())
            intent.putExtra("orderId", id)
            startActivity(intent)
        }
        llChatNotReady.setOnClickListener{
            this@GantiPaketOnProgressActivity.showError("Agen belum menerima pesanan")
        }
    }

    private fun finishingOrder(){
        llDone.setOnClickListener {
            this@GantiPaketOnProgressActivity.showProgress()
            finishOrder.set(id)
            finishOrder.get().observe(this, Observer {
                if(it.errors.message.isEmpty()) {
                    GlobalScope.launch(Dispatchers.Main) {
                        createNotification.set(
                            agentId,
                            id,
                            "Eways",
                            "Order Ganti Paket telah diselesaikan oleh User"
                        )
                        delay(300)
                        createNotification.set(
                            user.ID!!,
                            id,
                            "Eways",
                            "Order berhasil diselesaikan"
                        )
                        delay(300)
                        this@GantiPaketOnProgressActivity.dismissProgress()
                        val intent = Intent(this@GantiPaketOnProgressActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    this@GantiPaketOnProgressActivity.dismissProgress()
                    this@GantiPaketOnProgressActivity.showError(it.errors.message[0])
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