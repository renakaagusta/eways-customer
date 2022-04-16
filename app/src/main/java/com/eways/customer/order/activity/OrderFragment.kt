package com.eways.customer.order.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.eways.customer.R
import com.eways.customer.core.baseactivity.BaseFragment
import com.eways.customer.order.adapter.OrderListAdapter
import com.eways.customer.order.const.OrderStatus
import com.eways.customer.order.const.OrderType
import com.eways.customer.order.viewdto.IOrderViewDTO
import com.eways.customer.order.viewdto.OrderBasicViewDTO
import com.eways.customer.order.viewdto.OrderPacketViewDTO
import com.eways.customer.utils.customitemdecoration.CustomVerticalItemDecoration
import com.eways.customer.utils.date.SLDate
import com.google.gson.Gson
import com.proyek.infrastructures.order.order.entities.Order
import com.proyek.infrastructures.order.order.entities.Orderable
import com.proyek.infrastructures.order.order.usecases.GetCustomerUnfinishedOrder
import com.proyek.infrastructures.user.customer.entities.UserCustomer
import kotlinx.android.synthetic.main.fragment_order.*
import kotlinx.android.synthetic.main.fragment_order.view.*
import java.text.SimpleDateFormat

class OrderFragment(user: UserCustomer): BaseFragment() {
    private lateinit var listCreatedOrderBasicViewDTO : ArrayList<IOrderViewDTO>
    private lateinit var listOnProgressOrderBasicViewDTO : ArrayList<IOrderViewDTO>
    private lateinit var getCustomerUnfinishedOrder: GetCustomerUnfinishedOrder
    private val user = user

    private var countDividerItemDecoratorCreated = 0
    private var countDividerItemDecoratorOnProgress = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_order, container, false)

        getCustomerUnfinishedOrder = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetCustomerUnfinishedOrder::class.java)

        showListCreatedOrder(view)
        showListOnProgressOrder(view)
        setData()
        return view
    }

    fun setData() {

        this@OrderFragment.showProgress()

        val orderDTO = ArrayList<IOrderViewDTO>()
        val listorder = ArrayList<Order>()

        getCustomerUnfinishedOrder.set(user.ID!!)
        getCustomerUnfinishedOrder.get().observe(this, Observer {
            it.data.forEach {
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
                    orderDTO.add(
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
                    orderDTO.add(
                        OrderBasicViewDTO(
                            it.id!!,
                            orderType(order.service?.type!!)!!,
                            orderDescription,
                            SLDate,
                            orderStatus(it.orderStatus!!)!!
                        )
                    )
                }

                listorder.add(it)
            }
            orderDTO.reverse()
            setOrderListData(view!!, orderDTO)
            initialState(requireContext(),view!!)

            this@OrderFragment.dismissProgress()
        })
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

    private fun setOrderListData(view: View, orderDTO: ArrayList<IOrderViewDTO>){
        listCreatedOrderBasicViewDTO = ArrayList<IOrderViewDTO>()
        listOnProgressOrderBasicViewDTO = ArrayList<IOrderViewDTO>()
        for (orderViewDTO in orderDTO) {
            if(orderViewDTO.orderStatus.value == OrderStatus.Created.value) {
                listCreatedOrderBasicViewDTO.add(orderViewDTO)
            }else if(orderViewDTO.orderStatus.value == OrderStatus.OnProgress.value){
                listOnProgressOrderBasicViewDTO.add(orderViewDTO)
            }
        }

        val createdOrderAdapter = OrderListAdapter(listCreatedOrderBasicViewDTO, user)

        view.rvOrderOnCreate.apply {
            layoutManager = LinearLayoutManager(activity)
            if(countDividerItemDecoratorCreated == 0){
                addItemDecoration(CustomVerticalItemDecoration(15))
                countDividerItemDecoratorCreated+=1
            }
            isNestedScrollingEnabled = false
            adapter = createdOrderAdapter
        }

        val onProgressOrderAdapter = OrderListAdapter(listOnProgressOrderBasicViewDTO, user)
        view.rvOrderOnProgress.apply {
            layoutManager = LinearLayoutManager(activity)
            if(countDividerItemDecoratorOnProgress ==0){
                addItemDecoration(CustomVerticalItemDecoration(15))
                countDividerItemDecoratorOnProgress+=1
            }
            isNestedScrollingEnabled = false
            adapter = onProgressOrderAdapter
        }
    }

    private fun initialState(context : Context,view: View){
        view.apply {
            if(listCreatedOrderBasicViewDTO.isEmpty() or !::listCreatedOrderBasicViewDTO.isInitialized) {
                rvOrderOnCreate.isVisible = false
                tvOrderOnCreateNoData.isVisible = true
            }else {
                rvOrderOnCreate.isVisible = true
                tvOrderOnCreateNoData.isVisible = false
            }
            rvOrderOnProgress.isVisible = false
            tvOrderOnProgressNoData.isVisible = false
            tvOnCreate.setTextColor(ContextCompat.getColor(context, R.color.white))
            tvOnCreate.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
            tvOnProgress.setTextColor(ContextCompat.getColor(context, R.color.colorRegularText))
            tvOnProgress.setBackgroundColor(ContextCompat.getColor(context,R.color.white))
        }
    }

    private fun showListCreatedOrder(view: View){
        view.tvOnCreate.setOnClickListener {
            initialState(it.context,view)
        }
    }

    private fun showListOnProgressOrder(view: View){
        view.tvOnProgress.setOnClickListener {
            if(listOnProgressOrderBasicViewDTO.isEmpty()){
                rvOrderOnProgress.isVisible = false
                tvOrderOnProgressNoData.isVisible =true
            }else{
                rvOrderOnProgress.isVisible = true
                tvOrderOnProgressNoData.isVisible =false
            }
            rvOrderOnCreate.isVisible = false
            tvOrderOnCreateNoData.isVisible = false
            tvOnProgress.setTextColor(ContextCompat.getColor(it.context, R.color.white))
            tvOnProgress.setBackgroundColor(ContextCompat.getColor(it.context, R.color.colorPrimary))
            tvOnCreate.setTextColor(ContextCompat.getColor(it.context, R.color.colorRegularText))
            tvOnCreate.setBackgroundColor(ContextCompat.getColor(it.context,R.color.white))
        }
    }
}