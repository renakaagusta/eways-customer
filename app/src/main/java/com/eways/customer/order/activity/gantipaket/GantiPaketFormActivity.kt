package com.eways.customer.order.activity.gantipaket

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
import com.eways.customer.order.activity.psb.PSBFormConfirmationActivity
import com.eways.customer.utils.Utils
import com.eways.customer.utils.customsupportactionbar.CustomSupportActionBar
import com.proyek.infrastructures.inventory.internetservice.entities.InternetService
import com.proyek.infrastructures.inventory.internetservice.usecases.GetInternetServiceDetail
import com.proyek.infrastructures.order.order.network.body.GantiPaketBody
import com.proyek.infrastructures.order.order.network.body.PSBBody
import com.proyek.infrastructures.order.order.usecases.CreateGantiPaketOrder
import com.proyek.infrastructures.order.order.usecases.CreatePSBOrder
import com.proyek.infrastructures.user.agent.entities.UserAgent
import com.proyek.infrastructures.user.agent.usecases.GetAgentDetail
import com.proyek.infrastructures.user.customer.entities.UserCustomer
import kotlinx.android.synthetic.main.activity_gantipaket_form.*
import kotlinx.android.synthetic.main.activity_gantipaket_form.rlAgentOption
import kotlinx.android.synthetic.main.activity_gantipaket_form.tvAgentOption
import kotlinx.android.synthetic.main.activity_gantipaket_form.tvSubmit
import kotlinx.android.synthetic.main.activity_psb_form.rlServicePacketOption
import kotlinx.android.synthetic.main.activity_psb_form.tvServicePacketDescription
import kotlinx.android.synthetic.main.activity_psb_form.tvServicePacketOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GantiPaketFormActivity : BaseActivity() {
    private val requestCodeServicePacket = 1
    private val requestCodeCurrentServicePacket = 2
    private val requestCodeAgent = 3
    private lateinit var servicePacketOption:String
    private lateinit var currentServicePacketOption:String
    private lateinit var agentOption:String

    private lateinit var user: UserCustomer
    private lateinit var internetServiceId: String
    private lateinit var currentInternetServiceId: String
    private lateinit var agentId: String
    private lateinit var agentFee: String
    private lateinit var internetService: InternetService
    private lateinit var currentInternetService: InternetService
    private lateinit var agent: UserAgent

    private lateinit var getInternetServiceDetail: GetInternetServiceDetail
    private lateinit var getAgentDetail: GetAgentDetail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gantipaket_form)
        CustomSupportActionBar.setCustomActionBar(this,"Pesan Ganti Paket")
    }

    override fun onStart() {
        super.onStart()

        getInternetServiceDetail = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetInternetServiceDetail::class.java)
        getAgentDetail = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetAgentDetail::class.java)

        submissionButton()
        formCompleteness()
        setCurrentPacketService()
        setServicePacket()
        setAgent()

        user = intent.getParcelableExtra("user")
    }

    private fun isValid():Boolean{
        return tvServicePacketOption.text.toString() != getString(R.string.servicepacket_hint) &&
                tvCurrentServicePacketOption.text.toString() != getString(R.string.currentservicepaket_hint) &&
                tvAgentOption.text.toString() != getString(R.string.agent_hint)
    }

    private fun formCompleteness(){
        Utils.setOnTextChanged(tvServicePacketOption, object : Utils.Companion.OnTextChanged{
            override fun onChange(text: String) {
                servicePacketOption = text
            }
        }){submissionButton()}
        Utils.setOnTextChanged(tvCurrentServicePacketOption, object : Utils.Companion.OnTextChanged{
            override fun onChange(text: String) {
                currentServicePacketOption = text
            }
        }){submissionButton()}
        Utils.setOnTextChanged(tvAgentOption, object : Utils.Companion.OnTextChanged{
            override fun onChange(text: String) {
                agentOption = text
            }
        }){submissionButton()}
}

    private fun setCurrentPacketService(){
        rlCurrentServicePacketOption.setOnClickListener {
            val intent = Intent(this, ServicePacketOptionActivity::class.java)
            intent.putExtra("user", user)
            startActivityForResult(intent,requestCodeCurrentServicePacket)
        }

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

                GlobalScope.launch(Dispatchers.Main) {
                    getInternetServiceDetail.set(internetServiceId)
                    getInternetServiceDetail.get().observe(this@GantiPaketFormActivity, Observer {
                        internetService = it.data[0]
                        tvServicePacketOption.text = it.data[0].name
                        tvServicePacketDescription.text = it.data[0].description
                        tvServicePacketOption.setTextColor(
                            ContextCompat.getColor(
                                this@GantiPaketFormActivity,
                                R.color.darkText
                            )
                        )
                    })
                    delay(700)
                    getInternetServiceDetail.get().removeObservers(this@GantiPaketFormActivity)
                }
            }
            if(requestCode == requestCodeCurrentServicePacket){
                currentInternetServiceId =  data?.extras?.getSerializable("service_packet") as String

                GlobalScope.launch(Dispatchers.Main) {
                    getInternetServiceDetail.set(currentInternetServiceId)
                    getInternetServiceDetail.get().observe(this@GantiPaketFormActivity, Observer {
                        currentInternetService = it.data[0]
                        tvCurrentServicePacketOption.text = it.data[0].name
                        tvCurrentServicePacketDescription.text = it.data[0].description
                        tvCurrentServicePacketOption.setTextColor(
                            ContextCompat.getColor(
                                this@GantiPaketFormActivity,
                                R.color.darkText
                            )
                        )
                    })
                    delay(700)
                    getInternetServiceDetail.get().removeObservers(this@GantiPaketFormActivity)
                }
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
            tvSubmit.background = ContextCompat.getDrawable(this@GantiPaketFormActivity, R.drawable.rc_bglightgray)
        }else{
            tvSubmit.background = ContextCompat.getDrawable(this@GantiPaketFormActivity, R.drawable.rc_bgprimary)
            tvSubmit.isClickable = true
            moveToGantiPaketFormConfirmation()
        }
    }

    private fun moveToGantiPaketFormConfirmation(){
        tvSubmit.setOnClickListener {
            val gantiPaketOrderBody =
                GantiPaketBody(
                    currentInternetService,
                    internetService,
                    "1apRo3tPQuBvIvMsHvJp0VyFBiW",
                    user.ID!!,
                    agentId,
                    user.username!!,
                    "Membuat order ganti paket baru"
                )
                val intent = Intent(this, GantiPaketFormConfirmationActivity::class.java)
                intent.putExtra("body", gantiPaketOrderBody)
                intent.putExtra("agent", agent)
                intent.putExtra("user", user)
                startActivity(intent)
        }
    }
}