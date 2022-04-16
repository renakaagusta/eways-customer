package com.eways.customer.notification.activity

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.eways.customer.R
import com.eways.customer.core.baseactivity.BaseActivity
import com.eways.customer.kabarcluster.viewdto.KabarClusterPostViewDTO
import com.eways.customer.notification.adapter.NotificationAdapter
import com.eways.customer.notification.viewdto.NotificationViewDTO
import com.eways.customer.order.const.OrderStatus
import com.eways.customer.order.const.OrderType
import com.eways.customer.order.const.PaymentStatus
import com.eways.customer.utils.customitemdecoration.CustomVerticalItemDecoration
import com.eways.customer.utils.customsupportactionbar.CustomSupportActionBar
import com.eways.customer.utils.date.SLDate
import com.google.firebase.firestore.core.View
import com.proyek.infrastructures.notification.usecases.GetNotificationByUserId
import com.proyek.infrastructures.order.order.entities.Service
import com.proyek.infrastructures.order.order.usecases.GetOrderDetail
import com.proyek.infrastructures.user.customer.entities.UserCustomer
import com.proyek.infrastructures.user.customer.network.CustomerResponse
import kotlinx.android.synthetic.main.activity_notification.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.proyek.infrastructures.notification.usecases.ReadNotification
import com.proyek.infrastructures.order.order.entities.Order
import com.proyek.infrastructures.order.order.entities.Orderable
import com.proyek.infrastructures.utils.Authenticated
import java.text.SimpleDateFormat

class NotificationActivity : BaseActivity(){
    private lateinit var user: UserCustomer
    private lateinit var getNotificationByUserId: GetNotificationByUserId
    private lateinit var getOrderDetail: GetOrderDetail
    private lateinit var readNotification: ReadNotification

    private val listNotificationId = ArrayList<String>()
    private lateinit var notificationAdapter : NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        CustomSupportActionBar.setCustomActionBar(this, "Notifikasi")
    }

    override fun onStart() {
        super.onStart()

        this@NotificationActivity.showProgress()

        user = Authenticated.getUserCustomer()
        getNotificationByUserId = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetNotificationByUserId::class.java)
        getOrderDetail = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetOrderDetail::class.java)
        readNotification = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(ReadNotification::class.java)

        GlobalScope.launch(context = Dispatchers.Main) {
            val gson  = Gson()
            val dtoNotification = ArrayList<NotificationViewDTO>()

            getNotificationByUserId.set(user.ID!!, this@NotificationActivity)
            delay(300)
            getNotificationByUserId.get().apply {
                Log.d("notificationData", this.data.toString())
                if (!this.data?.isNullOrEmpty()!!) {

                    for (i in 0 until this.data?.size!!) {
                        Log.d("notificationData2", this.data.toString())

                        Log.d("notif", this.data.toString())

                        if(this.data!![i].type==2)
                            continue

                        val it = this.data!![i]
                        var orderNotFound = false

                        val SLDate = SLDate()
                        SLDate.date = SimpleDateFormat("yyyy-MM-dd").parse(it.createdAt)

                        listNotificationId.add(this.data!![i].id!!)

                        Log.d("listNotificationId", listNotificationId.toString())

                        getOrderDetail.set(it.typeId!!, this@NotificationActivity)
                        delay(300)
                        for(j in 0 until getOrderDetail.get().size) {
                            if(getOrderDetail.get()[j]==null) {
                                orderNotFound = true
                                break
                            }

                            val thisOrder = getOrderDetail.get()[j]
                            Log.d("thisOrder", thisOrder.toString())
                            if (thisOrder != Order()) {
                                val order: Orderable = gson.fromJson(thisOrder.order, Orderable::class.java)

                                val orderType = orderType(order.service?.type!!)
                                val orderStatus = orderStatus(thisOrder.orderStatus!!)
                                val notificationType = notificationType(thisOrder.orderStatus!!, thisOrder.paymentStatus!!)
                                val fee = setFee(thisOrder)

                                dtoNotification.add(
                                    NotificationViewDTO(
                                        notificationType,
                                        SLDate,
                                        orderType?.value!!,
                                        fee,
                                        orderStatus.toString()
                                    )
                                )
                            }

                            delay(300)
                        }
                        getOrderDetail.get().clear()
                    }

                    setNotificationData(dtoNotification)

                    readNotification.set(listNotificationId)
                }
                this@NotificationActivity.dismissProgress()
            }
        }
    }

    override fun onRestart() {
        super.onRestart()

        getOrderDetail.get().clear()
        notificationAdapter.clear()
    }

    private fun setFee(order: Order): Int {
        val gson = Gson()
        val orderable = gson.fromJson(order.order, Orderable::class.java)
        orderable.apply {
            return when(service?.type) {
                1 -> {
                    service?.agentFee!!
                }
                2 -> {
                    service?.agentFee!!
                }
                3-> {
                    service?.agentFee!!
                }
                4 -> {
                    if(order.orderFee!=null)
                        service?.agentFee!!+order.orderFee!!
                    else
                        0
                }
                5 -> {
                    if(order.orderFee!=null)
                        service?.agentFee!!+order.orderFee!!
                    else
                        0
                }
                6 -> {
                    var bill = 0
                    groceries.forEach {
                        bill+=it.items.price!!*it.quantity
                    }
                    bill+service?.agentFee!!
                }
                7 -> {
                    if(order.orderFee==null)
                        0
                    else
                        order.orderFee!!
                }
                else -> {
                    0
                }
            }
        }
    }

    private fun notificationType(orderStatus: Int, paymentStatus: Int) : NotificationViewDTO.NotificationType {
        return when(orderStatus){
            0->NotificationViewDTO.NotificationType.created
            1->if(paymentStatus==1)NotificationViewDTO.NotificationType.onprogress else NotificationViewDTO.NotificationType.billing
            2->NotificationViewDTO.NotificationType.finished
            3->NotificationViewDTO.NotificationType.finished
            else->NotificationViewDTO.NotificationType.created
        }
    }


    private fun orderStatus(status: Int): OrderStatus? {
        return when(status){
            0-> OrderStatus.Created
            1-> OrderStatus.OnProgress
            2-> OrderStatus.CustomerFinished
            else-> OrderStatus.Created
        }
    }

    private fun orderType(type: Int): OrderType? {
        return when(type){
            1-> OrderType.PSB
            2-> OrderType.PickupTicket
            3-> OrderType.GantiPaket
            4-> OrderType.TitipPaket
            5-> OrderType.SOPP
            6-> OrderType.TitipBelanja
            7-> OrderType.LayananBebas
            else-> null
        }
    }

    private fun setNotificationData(dtoNotification: ArrayList<NotificationViewDTO>){
        notificationAdapter = NotificationAdapter(dtoNotification)
        rvNotification.apply {
            layoutManager = LinearLayoutManager(this@NotificationActivity)
            //addItemDecoration(CustomDividerItemDecoration(ContextCompat.getDrawable(this@KabarClusterActivity, R.drawable.divider_kabarcluster)!!))
            addItemDecoration(CustomVerticalItemDecoration(15))
            adapter = notificationAdapter
        }
    }
}