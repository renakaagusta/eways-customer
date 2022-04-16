package com.eways.customer.order.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.eways.customer.R
import com.eways.customer.order.activity.gantipaket.GantiPaketOnProgressActivity
import com.eways.customer.order.activity.layananbebas.LayananBebasOnProgressActivity
import com.eways.customer.order.activity.pickupticket.PickupTicketOnProgressActivity
import com.eways.customer.order.activity.psb.PSBOnProgressActivity
import com.eways.customer.order.activity.sopp.SOPPOnProgressActivity
import com.eways.customer.order.activity.titipbelanja.TitipBelanjaOnProgressActivity
import com.eways.customer.order.activity.titippaket.TitipPaketOnProgressActivity
import com.eways.customer.order.const.OrderStatus
import com.eways.customer.order.const.OrderType
import com.eways.customer.order.viewdto.IOrderViewDTO
import com.eways.customer.order.viewdto.OrderBasicViewDTO
import com.eways.customer.order.viewdto.OrderPacketViewDTO
import com.proyek.infrastructures.user.customer.entities.UserCustomer
import kotlinx.android.synthetic.main.row_order_basic.view.*
import kotlinx.android.synthetic.main.row_order_basic.view.tvOrderTitle
import kotlinx.android.synthetic.main.row_order_packet.view.*

class OrderListAdapter(private val orders: List<IOrderViewDTO>, user: UserCustomer) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val user = user

    companion object{
        const val VIEW_TYPE_BASIC = 1
        const val VIEW_TYPE_PACKET = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_BASIC) {
            OrderBasicViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_order_basic, parent, false))
        } else OrderPacketViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_order_packet,parent,false))
    }

    override fun getItemCount(): Int =orders.size

    override fun getItemViewType(position: Int): Int {
        return if (orders[position] is OrderPacketViewDTO ) {
            VIEW_TYPE_PACKET
        } else VIEW_TYPE_BASIC
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (orders[position] is OrderPacketViewDTO) {
            (holder as OrderPacketViewHolder).bind(orders[position] as OrderPacketViewDTO)
        } else {
            (holder as OrderBasicViewHolder).bind(orders[position] as OrderBasicViewDTO, user)
        }
    }
    inner class OrderBasicViewHolder internal constructor(view: View): RecyclerView.ViewHolder(view){
        fun bind(orderBasicViewDTO: OrderBasicViewDTO, user: UserCustomer){
            itemView.apply {
                tvOrderTitle.text = orderBasicViewDTO.orderType.value
                tvOrderDescription.text = orderBasicViewDTO.orderDescription
                tvOrderTime.text = orderBasicViewDTO.orderTime.getLocalizeDateString()
                tvOrderStatus.text = orderBasicViewDTO.orderStatus.value
                if(orderBasicViewDTO.orderStatus == OrderStatus.Created){
                    tvOrderStatus.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                }else if(orderBasicViewDTO.orderStatus == OrderStatus.OnProgress){
                    tvOrderStatus.setTextColor(ContextCompat.getColor(context,R.color.blue))
                }

                setOnClickListener {
                    when (orderBasicViewDTO.orderType) {
                        OrderType.PSB -> {
                            val intent = Intent(context, PSBOnProgressActivity::class.java)
                            intent.putExtra("status", orderBasicViewDTO.orderStatus.value)
                            intent.putExtra("id", orderBasicViewDTO.id)
                            intent.putExtra("user", user)
                            context.startActivity(intent)
                        }
                        OrderType.PickupTicket -> {
                            val intent = Intent(context, PickupTicketOnProgressActivity::class.java)
                            intent.putExtra("status", orderBasicViewDTO.orderStatus.value)
                            intent.putExtra("id", orderBasicViewDTO.id)
                            intent.putExtra("user", user)
                            context.startActivity(intent)
                        }
                        OrderType.GantiPaket -> {
                            val intent = Intent(context, GantiPaketOnProgressActivity::class.java)
                            intent.putExtra("status", orderBasicViewDTO.orderStatus.value)
                            intent.putExtra("id", orderBasicViewDTO.id)
                            intent.putExtra("user", user)
                            context.startActivity(intent)
                        }
                        OrderType.TitipBelanja -> {
                            val intent = Intent(context, TitipBelanjaOnProgressActivity::class.java)
                            intent.putExtra("status", orderBasicViewDTO.orderStatus.value)
                            intent.putExtra("id", orderBasicViewDTO.id)
                            intent.putExtra("user", user)
                            context.startActivity(intent)
                        }
                        OrderType.SOPP -> {
                            val intent = Intent(context, SOPPOnProgressActivity::class.java)
                            intent.putExtra("status", orderBasicViewDTO.orderStatus.value)
                            intent.putExtra("id", orderBasicViewDTO.id)
                            intent.putExtra("user", user)
                            context.startActivity(intent)
                        }
                        OrderType.LayananBebas -> {
                            val intent = Intent(context, LayananBebasOnProgressActivity::class.java)
                            intent.putExtra("status", orderBasicViewDTO.orderStatus.value)
                            intent.putExtra("id", orderBasicViewDTO.id)
                            intent.putExtra("user", user)
                            context.startActivity(intent)
                        }
                        else -> {

                        }
                    }
                }
            }
        }
    }

    inner class OrderPacketViewHolder internal constructor(view:View): RecyclerView.ViewHolder(view){
        fun bind(orderPacketViewDTO: OrderPacketViewDTO){
            itemView.apply {
                tvOrderTitle.text = orderPacketViewDTO.orderType.value
                tvOrderSenderName.text = orderPacketViewDTO.orderSenderName
                tvOrderSenderAddress.text = orderPacketViewDTO.orderSenderAddress
                tvOrderReceiverName.text = orderPacketViewDTO.orderReceiverName
                tvOrderReceiverAddress.text = orderPacketViewDTO.orderReceiverAddress

                tvOrderStatusPacket.text = orderPacketViewDTO.orderStatus.value
                if(orderPacketViewDTO.orderStatus == OrderStatus.Created){
                    tvOrderStatusPacket.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                }else if(orderPacketViewDTO.orderStatus == OrderStatus.OnProgress){
                    tvOrderStatusPacket.setTextColor(ContextCompat.getColor(context,R.color.blue))
                }

                setOnClickListener {
                    val intent = Intent(context, TitipPaketOnProgressActivity::class.java)
                    intent.putExtra("status", orderPacketViewDTO.orderStatus.value)
                    intent.putExtra("id", orderPacketViewDTO.id)
                    intent.putExtra("user", user)
                    context.startActivity(intent)
                }
            }
        }
    }
}