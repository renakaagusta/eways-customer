package com.eways.customer.order.activity.pickupticket

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.eways.customer.R
import com.eways.customer.core.baseactivity.BaseActivity
import com.eways.customer.order.activity.gantipaket.GantiPaketFormConfirmationActivity
import com.eways.customer.order.activity.option.AgentOptionActivity
import com.eways.customer.order.activity.option.ServicePacketOptionActivity
import com.eways.customer.utils.Utils
import com.eways.customer.utils.customsupportactionbar.CustomSupportActionBar
import com.proyek.infrastructures.inventory.internetservice.entities.InternetService
import com.proyek.infrastructures.inventory.internetservice.usecases.GetInternetServiceDetail
import com.proyek.infrastructures.order.order.network.body.GantiPaketBody
import com.proyek.infrastructures.order.order.network.body.LaporanKerusakanBody
import com.proyek.infrastructures.order.order.usecases.CreateLaporanKerusakanOrder
import com.proyek.infrastructures.user.agent.entities.UserAgent
import com.proyek.infrastructures.user.agent.usecases.GetAgentDetail
import com.proyek.infrastructures.user.customer.entities.UserCustomer
import kotlinx.android.synthetic.main.activity_gantipaket_form.*
import kotlinx.android.synthetic.main.activity_pickupticket_form.*
import kotlinx.android.synthetic.main.activity_pickupticket_form.rlAgentOption
import kotlinx.android.synthetic.main.activity_pickupticket_form.rlCurrentServicePacketOption
import kotlinx.android.synthetic.main.activity_pickupticket_form.tvAgentOption
import kotlinx.android.synthetic.main.activity_pickupticket_form.tvCurrentServicePacketOption
import kotlinx.android.synthetic.main.activity_pickupticket_form.tvSubmit
import kotlinx.android.synthetic.main.activity_psb_form.*
import java.util.*

class PickupTicketFormActivity : BaseActivity(){
    private val requestCodeCurrentServicePacket = 1
    private val requestCodeAgent = 2
    private lateinit var currentServicePacketOption : String
    private lateinit var agentOption : String
    private var pickupTicketDescription : String? =null

    private lateinit var user: UserCustomer
    private lateinit var internetServiceId: String
    private lateinit var agentId: String
    private lateinit var internetService: InternetService
    private lateinit var agent: UserAgent

    private lateinit var getInternetServiceDetail: GetInternetServiceDetail
    private lateinit var getAgentDetail: GetAgentDetail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pickupticket_form)
        CustomSupportActionBar.setCustomActionBar(this,"Laporan Kerusakan")
    }

    override fun onStart() {
        super.onStart()

        getInternetServiceDetail = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetInternetServiceDetail::class.java)
        getAgentDetail = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetAgentDetail::class.java)

        submissionButton()
        formCompleteness()
        setCurrentPacketService()
        setAgent()

        user = intent.getParcelableExtra("user")
    }

    private fun isValid():Boolean{
        return tvCurrentServicePacketOption.text.toString() != getString(R.string.currentservicepaket_hint) &&
                Utils.isNotNullOrEmpty(pickupTicketDescription) &&
                tvAgentOption.text.toString() != getString(R.string.agent_hint)
    }

    private fun formCompleteness(){
        Utils.setOnTextChanged(tvCurrentServicePacketOption, object : Utils.Companion.OnTextChanged{
            override fun onChange(text: String) {
                currentServicePacketOption = text
            }
        }){this.submissionButton()}
        Utils.setOnTextChanged(tvAgentOption, object : Utils.Companion.OnTextChanged{
            override fun onChange(text: String) {
                agentOption = text
            }
        }){this.submissionButton()}
        Utils.setOnTextChanged(etPickupTicketDescription, object : Utils.Companion.OnTextChanged{
            override fun onChange(text: String) {
                pickupTicketDescription = text
            }
        }){this.submissionButton()}
    }

    private fun setCurrentPacketService(){
        rlCurrentServicePacketOption.setOnClickListener {
            val intent = Intent(this, ServicePacketOptionActivity::class.java)
            startActivityForResult(intent,requestCodeCurrentServicePacket)
        }
    }

    private fun setAgent(){
        rlAgentOption.setOnClickListener {
            val intent = Intent(this, AgentOptionActivity::class.java)
            startActivityForResult(intent, requestCodeAgent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == requestCodeCurrentServicePacket){
                internetServiceId =  data?.extras?.getSerializable("service_packet") as String

                getInternetServiceDetail.set(internetServiceId)
                getInternetServiceDetail.get().observe(this, Observer {
                    internetService = it.data[0]
                    tvCurrentServicePacketOption.text = it.data[0].name
                    tvCurrentServicePacketOption.setTextColor(ContextCompat.getColor(this, R.color.darkText))

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
            tvSubmit.background = ContextCompat.getDrawable(this@PickupTicketFormActivity, R.drawable.rc_bglightgray)
        }else{
            tvSubmit.background = ContextCompat.getDrawable(this@PickupTicketFormActivity, R.drawable.rc_bgprimary)
            tvSubmit.isClickable = true
            moveToPickupTicketFormConfirmation()
        }
    }

    private fun moveToPickupTicketFormConfirmation(){
        tvSubmit.setOnClickListener {
            val laporanKerusakanOrderBody =
                LaporanKerusakanBody(
                    internetService,
                    etPickupTicketDescription.text.toString(),
                    "1apPg8GAOei7bEsom7fxB0wBgxD",
                    user.ID!!,
                    agentId,
                    user.username!!,
                    "Membuat order laporan kerusakan baru"
                )
                val intent = Intent(this, PickupTicketFormConfirmationActivity::class.java)
                intent.putExtra("body", laporanKerusakanOrderBody)
                intent.putExtra("agent", agent)
                intent.putExtra("user", user)
                startActivity(intent)
        }
    }
}