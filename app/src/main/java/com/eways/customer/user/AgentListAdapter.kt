package com.eways.customer.user

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.eways.customer.R
import com.eways.customer.order.viewdto.AgentOptionViewDTO
import kotlinx.android.synthetic.main.row_option_agent.view.*
import java.util.*
import kotlin.collections.ArrayList

class AgentListAdapter(private var itemList: MutableList<AgentOptionViewDTO>): RecyclerView.Adapter<AgentListAdapter.AgentViewHolder>(), Filterable {
    private var itemListFull: List<AgentOptionViewDTO> = itemList
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgentViewHolder {
        return AgentViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_option_agent,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: AgentViewHolder, position: Int) {
        holder.bindOption(itemList[position])
    }

    inner class AgentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindOption(agentOptionViewDTO: AgentOptionViewDTO) {
            itemView.apply {
                tvAgentName.text = agentOptionViewDTO.name
            }
        }
    }

    override fun getFilter(): Filter {
        return exampleFilter
    }

    private val exampleFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredList: ArrayList<AgentOptionViewDTO> = ArrayList()
            if (constraint.isEmpty()) {
                filteredList.addAll(itemListFull)
            } else {
                val filterPattern =
                    constraint.toString().toLowerCase(Locale.ROOT).trim { it <= ' ' }
                for (item in itemListFull) {
                    if (item.name.toLowerCase(Locale.ROOT).contains(filterPattern)) {
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(
            constraint: CharSequence,
            results: FilterResults
        ) {
            itemList.clear()
            itemList.addAll(results.values as java.util.ArrayList<AgentOptionViewDTO>)
            notifyDataSetChanged()
        }

    }

}