package com.eways.customer.order.activity.psb

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.eways.customer.R
import com.eways.customer.core.baseactivity.BaseActivity
import com.eways.customer.order.activity.option.AgentOptionActivity
import com.eways.customer.order.activity.option.ServicePacketOptionActivity
import com.eways.customer.utils.Utils
import com.eways.customer.utils.customsupportactionbar.CustomSupportActionBar
import com.proyek.infrastructures.inventory.internetservice.entities.InternetService
import com.proyek.infrastructures.inventory.internetservice.usecases.GetInternetServiceDetail
import com.proyek.infrastructures.order.order.network.body.PSBBody
import com.proyek.infrastructures.order.order.usecases.CreatePSBOrder
import com.proyek.infrastructures.user.agent.entities.Agent
import com.proyek.infrastructures.user.agent.entities.UserAgent
import com.proyek.infrastructures.user.agent.usecases.GetAgentDetail
import com.proyek.infrastructures.user.customer.entities.UserCustomer
import kotlinx.android.synthetic.main.activity_psb_form.*

class PSBFormActivity : BaseActivity() {
    private val requestCodeServicePacket = 1
    private val requestCodeAgent = 2
    private lateinit var servicePacketOption : String
    private lateinit var agentOption : String
    private lateinit var user: UserCustomer
    private lateinit var internetServiceId: String
    private lateinit var agentId: String
    private lateinit var internetService: InternetService
    private lateinit var agent: UserAgent

    private lateinit var getInternetServiceDetail: GetInternetServiceDetail
    private lateinit var getAgentDetail: GetAgentDetail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_psb_form)
        CustomSupportActionBar.setCustomActionBar(this, "Pesan Pasang Baru")
    }

    override fun onStart() {
        super.onStart()
        getInternetServiceDetail = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetInternetServiceDetail::class.java)
        getAgentDetail = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetAgentDetail::class.java)

        submissionButton()
        formCompleteness()
        setServicePacket()
        setAgent()

        user = intent.getParcelableExtra("user")
    }

    private fun isValid():Boolean{
        return tvServicePacketOption.text.toString() != getString(R.string.servicepacket_hint) &&
                tvAgentOption.text.toString() != getString(R.string.agent_hint)
    }

    private fun formCompleteness(){
        Utils.setOnTextChanged(tvServicePacketOption, object : Utils.Companion.OnTextChanged{
            override fun onChange(text: String) {
                servicePacketOption = text
            }
        }){this.submissionButton()}
        Utils.setOnTextChanged(tvAgentOption, object : Utils.Companion.OnTextChanged{
            override fun onChange(text: String) {
                agentOption = text
            }
        }){submissionButton()}
    }

    private fun setServicePacket(){
        rlServicePacketOption.setOnClickListener {
            val intent = Intent(this, ServicePacketOptionActivity::class.java)
            intent.putExtra("user", user)
            startActivityForResult(intent,requestCodeServicePacket)
        }
    }

    private fun setAgent(){
        rlAgentOption.setOnClickListener {
            val intent = Intent(this, AgentOptionActivity::class.java)
            intent.putExtra("user", user)
            startActivityForResult(intent, requestCodeAgent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == requestCodeServicePacket){
                internetServiceId =  data?.extras?.getSerializable("service_packet") as String

                getInternetServiceDetail.set(internetServiceId)
                getInternetServiceDetail.get().observe(this, Observer {
                    internetService = it.data[0]
                    tvServicePacketOption.text = it.data[0].name
                    tvServicePacketDescription.text = it.data[0].description
                    tvServicePacketOption.setTextColor(ContextCompat.getColor(this, R.color.darkText))
                })
            }
            if (requestCode == requestCodeAgent){
                agentId =  data?.extras?.getSerializable("agent") as String

                getAgentDetail.set(agentId)
                getAgentDetail.get().observe(this, Observer {
                    agent = it.data[0]
                    tvAgentOption.text = it.data[0].username
                    tvAgentOption.setTextColor(ContextCompat.getColor(this, R.color.darkText))
                })
            }
        }
    }

    private fun submissionButton(){
        if(!isValid()){
            tvSubmit.isClickable = false
            tvSubmit.background = ContextCompat.getDrawable(this@PSBFormActivity, R.drawable.rc_bglightgray)
        }else{
            tvSubmit.background = ContextCompat.getDrawable(this@PSBFormActivity, R.drawable.rc_bgprimary)
            tvSubmit.isClickable = true
            moveToPSBConfirmation()
        }
    }

    private fun moveToPSBConfirmation(){
        tvSubmit.setOnClickListener {
            val PSBOrder =
                PSBBody(
                    internetService,
                    "1apQwniIJo6WnAm2cIk7WEkzWt0",
                    user.ID!!,
                    agentId,
                    user.username!!,
                    "Membuat order PSB baru"
                )
                val intent = Intent(this, PSBFormConfirmationActivity::class.java)
                intent.putExtra("body", PSBOrder)
                intent.putExtra("agent", agent)
                intent.putExtra("user", user)
                startActivity(intent)
        }
    }
}