package com.eways.customer.order.activity.option

import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.eways.customer.R
import com.eways.customer.core.baseactivity.BaseActivity
import com.eways.customer.order.adapter.option.AgentOptionAdapter
import com.eways.customer.order.viewdto.AgentOptionViewDTO
import com.eways.customer.utils.customitemdecoration.CustomDividerItemDecoration
import com.proyek.infrastructures.user.agent.entities.UserAgent
import com.proyek.infrastructures.user.agent.usecases.GetAgentByCluster
import com.proyek.infrastructures.user.agent.usecases.GetAgentList
import com.proyek.infrastructures.user.customer.entities.UserCustomer
import com.proyek.infrastructures.utils.Authenticated
import kotlinx.android.synthetic.main.activity_option.*

class AgentOptionActivity : BaseActivity() {
    private lateinit var user: UserCustomer
    private lateinit var getAgentList: GetAgentByCluster

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_option)
        supportActionBar?.hide()
        user = Authenticated.getUserCustomer()
     }

    override fun onStart() {
        super.onStart()

        getAgentList = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetAgentByCluster::class.java)

        this@AgentOptionActivity.showProgress()
        if(user.cluster!=null)
            getAgentList.set(user.cluster?.ID!!, this@AgentOptionActivity)
        else
            getAgentList.set("1", this@AgentOptionActivity)
        getAgentList.get().observe(this, Observer {
            this@AgentOptionActivity.dismissProgress()

            val dtoOptionAgent = ArrayList<AgentOptionViewDTO>()
            var i = 0
            it.data.forEach {
                if((it.verifStatus == 1)) {
                    dtoOptionAgent.add(i, AgentOptionViewDTO(it.ID!!, it.username!!))
                    i++
                }
            }

            setAgentData(dtoOptionAgent)
        })
    }

    private fun setAgentData( dtoOptionAgent: ArrayList<AgentOptionViewDTO>){
        val optionAdapter =
            AgentOptionAdapter(
                dtoOptionAgent
            )
        rvOption.apply {
            layoutManager = LinearLayoutManager(this@AgentOptionActivity)
            addItemDecoration(CustomDividerItemDecoration(ContextCompat.getDrawable(this@AgentOptionActivity, R.drawable.divider_line)!!))
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
    }
}