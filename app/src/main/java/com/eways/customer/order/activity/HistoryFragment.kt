package com.eways.customer.order.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.eways.customer.R
import com.eways.customer.core.baseactivity.BaseFragment
import com.eways.customer.order.adapter.HistoryOrderAdapter
import com.eways.customer.order.const.OrderStatus
import com.eways.customer.order.const.OrderType
import com.eways.customer.order.viewdto.IOrderViewDTO
import com.eways.customer.order.viewdto.OrderBasicViewDTO
import com.eways.customer.order.viewdto.OrderPacketViewDTO
import com.eways.customer.utils.customitemdecoration.CustomDividerItemDecoration
import com.eways.customer.utils.date.SLDate
import com.google.gson.Gson
import com.proyek.infrastructures.order.order.entities.Orderable
import com.proyek.infrastructures.order.order.network.OrderResponse
import com.proyek.infrastructures.order.order.usecases.GetCustomerFinishedOrder
import com.proyek.infrastructures.user.customer.entities.UserCustomer
import kotlinx.android.synthetic.main.fragment_history.view.*
import java.text.SimpleDateFormat


class HistoryFragment(user: UserCustomer) : BaseFragment() {
    private lateinit var getOrderFinishedOrder: GetCustomerFinishedOrder
    private val user: UserCustomer = user

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        getOrderFinishedOrder = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetCustomerFinishedOrder::class.java)

        setData()

        return view
    }

    private fun setData() {

        this@HistoryFragment.showProgress()

        val historyDTO = ArrayList<IOrderViewDTO>()

        getOrderFinishedOrder.set(user.ID!!)
        getOrderFinishedOrder.get().observe(this, Observer { response->
            for(it in response.data) {
                val gson = Gson()
                val order: Orderable = gson.fromJson(it.order, Orderable::class.java)

                val SLDate = SLDate()
                SLDate.date = SimpleDateFormat("yyyy-MM-dd").parse(it.createdAt)

                var orderDescription = ""

                when(order.service?.type){
                    1 -> orderDescription = order.internetService?.name!!
                    2 -> orderDescription = order.damageDescription!!
                    3 -> orderDescription =  "${order.oldInternetService.name}  -  ${order.newInternetService.name}"
                    5 -> orderDescription = order.invoice?.name!!
                    6 -> orderDescription = if(order.groceries[0].items.category?.name!= null) order.groceries[0].items.category?.name!! else ""
                    7 -> orderDescription = order.name!!
                }

                if(order.service?.type==4){
                    historyDTO.add(
                        OrderPacketViewDTO(
                            it.id!!,
                            orderType(order.service?.type!!)!!,
                            order.senderName!!,
                            order.senderAddress!!,
                            order.receiverName!!,
                            order.receiverAddress!!,
                            SLDate,
                            orderStatus(it.orderStatus!!)!!
                        )
                    )
                } else {
                    historyDTO.add(
                        OrderBasicViewDTO(
                            it.id!!,
                            orderType(order.service?.type!!)!!,
                            orderDescription,
                            SLDate,
                            orderStatus(it.orderStatus!!)!!
                        )
                    )
                }
            }
            historyDTO.reverse()
            setHistoryOrderData(view!!, historyDTO)

            this@HistoryFragment.dismissProgress()
        })
    }

    private fun orderStatus(status: Int): OrderStatus? {
        return when(status){
            1-> OrderStatus.Created
            2-> OrderStatus.OnProgress
            3-> OrderStatus.CustomerFinished
            else-> null
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

    private fun setHistoryOrderData(view: View, listDTO: ArrayList<IOrderViewDTO>){
        if (listDTO.isEmpty()){
            view.apply {
                rvHistory.isVisible = false
                tvHistoryNoData.isVisible = true
            }
        }else{
            view.tvHistoryNoData.isVisible = false
            val historyOrderAdapter = HistoryOrderAdapter(listDTO)
            view.rvHistory.apply {
                isVisible =true
                layoutManager = LinearLayoutManager(activity)
                addItemDecoration(CustomDividerItemDecoration(ContextCompat.getDrawable(activity!!.applicationContext, R.drawable.divider_line)!!))
                isNestedScrollingEnabled = false
                adapter = historyOrderAdapter
            }
        }
    }
}