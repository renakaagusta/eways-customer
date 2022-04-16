package com.eways.customer.user.activity

import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.eways.customer.R
import com.eways.customer.core.baseactivity.BaseActivity
import com.eways.customer.order.viewdto.AgentOptionViewDTO
import com.eways.customer.user.AgentListAdapter
import com.eways.customer.utils.customitemdecoration.CustomDividerItemDecoration
import com.eways.customer.utils.customsupportactionbar.CustomSupportActionBar
import com.proyek.infrastructures.user.agent.usecases.GetAgentByCluster
import com.proyek.infrastructures.user.customer.entities.UserCustomer
import com.proyek.infrastructures.utils.Authenticated
import kotlinx.android.synthetic.main.activity_option.*

class AgentListActivity : BaseActivity(){
    private lateinit var getAgentByCluster: GetAgentByCluster

    private lateinit var user: UserCustomer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_option)
        CustomSupportActionBar.setCustomActionBar(this, "List Agen")

        getAgentByCluster = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetAgentByCluster::class.java)

        user = Authenticated.getUserCustomer()

        setAgentData()
    }

    private fun setAgentData(){
        val listAgent : MutableList<AgentOptionViewDTO> = arrayListOf()
        if(user.cluster!=null)
            getAgentByCluster.set(user.cluster?.ID!!, this@AgentListActivity)
        else
            getAgentByCluster.set("1", this@AgentListActivity)
        getAgentByCluster.get().observe(this, Observer {
            it.data.forEach {
                listAgent.add(
                    AgentOptionViewDTO(
                        it.ID!!,
                        it.username!!
                    )
                )
            }

            val optionAdapter =
                AgentListAdapter(
                    listAgent
                )
            rvOption.apply {
                layoutManager = LinearLayoutManager(this@AgentListActivity)
                addItemDecoration(CustomDividerItemDecoration(ContextCompat.getDrawable(this@AgentListActivity, R.drawable.divider_line)!!))
                isNestedScrollingEnabled = false
                adapter = optionAdapter
            }
            svOption.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    optionAdapter.filter.filter(newText)
                    return false
                }

            })
        })
    }
}