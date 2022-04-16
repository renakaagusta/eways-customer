package com.eways.customer.order.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eways.customer.R
import com.eways.customer.order.activity.gantipaket.GantiPaketIsDoneActivity
import com.eways.customer.order.activity.layananbebas.LayananBebasIsDoneActivity
import com.eways.customer.order.activity.pickupticket.PickupTicketIsDoneActivity
import com.eways.customer.order.activity.psb.PSBIsDoneActivity
import com.eways.customer.order.activity.sopp.SOPPIsDoneActivity
import com.eways.customer.order.activity.titipbelanja.TitipBelanjaIsDoneActivity
import com.eways.customer.order.activity.titippaket.TitipPaketIsDoneActivity
import com.eways.customer.order.const.OrderType
import com.eways.customer.order.viewdto.IOrderViewDTO
import com.eways.customer.order.viewdto.OrderBasicViewDTO
import com.eways.customer.order.viewdto.OrderPacketViewDTO
import kotlinx.android.synthetic.main.row_history.view.*

class HistoryOrderAdapter(private val orders: List<IOrderViewDTO>)  : RecyclerView.Adapter<HistoryOrderAdapter.HistoryOrderViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryOrderViewHolder{
        return HistoryOrderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_history,parent,false))
    }

    override fun getItemCount(): Int =orders.size

    override fun onBindViewHolder(holder: HistoryOrderViewHolder, position: Int){
        holder.bindHistory(orders[position])
    }

    inner class HistoryOrderViewHolder(view: View) : RecyclerView.ViewHolder(view){
        fun bindHistory(iOrderViewDTO: IOrderViewDTO){
            itemView.apply {
                tvOrderTitle.text = iOrderViewDTO.orderType.value
                if(iOrderViewDTO.orderType == OrderType.TitipPaket){
                    iOrderViewDTO as OrderPacketViewDTO
                    tvOrderDescription.text = context.getString(R.string.titipPaketHistoryDescription, iOrderViewDTO.orderSenderName, iOrderViewDTO.orderReceiverName)
                    tvOrderTime.text = iOrderViewDTO.orderTime.getLocalizeDateString()
                }else{
                    iOrderViewDTO as OrderBasicViewDTO
                    tvOrderDescription.text = iOrderViewDTO.orderDescription
                    tvOrderTime.text = iOrderViewDTO.orderTime.getLocalizeDateString()
                }
                setOnClickListener {
                    when(iOrderViewDTO.orderType){
                        OrderType.PSB -> {
                            val intent = Intent(this.context, PSBIsDoneActivity::class.java)
                            intent.putExtra("orderId",(iOrderViewDTO as OrderBasicViewDTO).id)
                            this.context.startActivity(intent)
                        }
                        OrderType.PickupTicket -> {
                            val intent = Intent(this.context, PickupTicketIsDoneActivity::class.java)
                            intent.putExtra("orderId",(iOrderViewDTO as OrderBasicViewDTO).id)
                            this.context.startActivity(intent)
                        }
                        OrderType.GantiPaket -> {
                            val intent = Intent(this.context, GantiPaketIsDoneActivity::class.java)
                            intent.putExtra("orderId",(iOrderViewDTO as OrderBasicViewDTO).id)
                            this.context.startActivity(intent)
                        }
                        OrderType.TitipBelanja -> {
                            val intent = Intent(this.context, TitipBelanjaIsDoneActivity::class.java)
                            intent.putExtra("orderId",(iOrderViewDTO as OrderBasicViewDTO).id)
                            this.context.startActivity(intent)
                        }
                        OrderType.SOPP -> {
                            val intent = Intent(this.context, SOPPIsDoneActivity::class.java)
                            intent.putExtra("orderId",(iOrderViewDTO as OrderBasicViewDTO).id)
                            this.context.startActivity(intent)
                        }
                        OrderType.TitipPaket ->{
                            val intent = Intent(this.context, TitipPaketIsDoneActivity::class.java)
                            intent.putExtra("orderId",(iOrderViewDTO as OrderPacketViewDTO).id)
                            this.context.startActivity(intent)
                        }
                        OrderType.LayananBebas ->{
                            val intent = Intent(this.context, LayananBebasIsDoneActivity::class.java)
                            intent.putExtra("orderId",(iOrderViewDTO as OrderBasicViewDTO).id)
                            this.context.startActivity(intent)
                        }
                    }
                }
            }
        }
    }

}