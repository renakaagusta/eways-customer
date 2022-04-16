package com.eways.customer.order.activity.titippaket

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.eways.customer.R
import com.eways.customer.core.baseactivity.BaseActivity
import com.eways.customer.order.activity.option.AgentOptionActivity
import com.eways.customer.utils.Utils
import com.eways.customer.utils.customsupportactionbar.CustomSupportActionBar
import com.proyek.infrastructures.inventory.internetservice.entities.InternetService
import com.proyek.infrastructures.inventory.internetservice.usecases.GetInternetServiceDetail
import com.proyek.infrastructures.user.agent.entities.UserAgent
import com.proyek.infrastructures.user.agent.usecases.GetAgentDetail
import com.proyek.infrastructures.user.customer.entities.UserCustomer
import kotlinx.android.synthetic.main.activity_psb_form.*
import kotlinx.android.synthetic.main.activity_titippaket_form.*
import kotlinx.android.synthetic.main.activity_titippaket_form.rlAgentOption
import kotlinx.android.synthetic.main.activity_titippaket_form.tvAgentOption
import kotlinx.android.synthetic.main.activity_titippaket_form.tvSubmit

class TitipPaketFormActivity :BaseActivity() {
    private val requestCodeAgent = 1
    private var itemName : String? =null
    private var itemDescription :String? = null
    private var senderName :String? = null
    private var senderAddress :String? = null
    private var senderPhone :String? = null
    private var receiverName :String? = null
    private var receiverAddress :String? = null
    private var receiverPhone :String? = null
    private lateinit var agentOption : String

    private lateinit var agent: UserAgent

    private lateinit var getAgentDetail: GetAgentDetail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_titippaket_form)
        CustomSupportActionBar.setCustomActionBar(this,"Titip Paket")
    }

    override fun onStart() {
        super.onStart()

        getAgentDetail = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetAgentDetail::class.java)

        submissionButton()
        formCompleteness()
        setAgent()
    }

    private fun isValid():Boolean{
        return Utils.isNotNullOrEmpty(itemName) &&
                Utils.isNotNullOrEmpty(itemDescription) &&
                Utils.isNotNullOrEmpty(senderName) &&
                Utils.isNotNullOrEmpty(senderPhone) &&
                Utils.isNotNullOrEmpty(senderAddress) &&
                Utils.isNotNullOrEmpty(receiverName) &&
                Utils.isNotNullOrEmpty(receiverAddress) &&
                Utils.isNotNullOrEmpty(receiverPhone) &&
                tvAgentOption.text.toString() != getString(R.string.agent_hint)
    }
    private fun submissionButton(){
        if(!isValid()){
            tvSubmit.isClickable = false
            tvSubmit.background = ContextCompat.getDrawable(this@TitipPaketFormActivity, R.drawable.rc_bglightgray)
        }else{
            tvSubmit.background = ContextCompat.getDrawable(this@TitipPaketFormActivity, R.drawable.rc_bgprimary)
            tvSubmit.isClickable = true
            moveToTitipPaketFormConfirmation()
        }
    }

    private fun formCompleteness(){
        Utils.setOnTextChanged(etItemName, object : Utils.Companion.OnTextChanged{
            override fun onChange(text: String) {
                itemName = text
            }
        }){this.submissionButton()}
        Utils.setOnTextChanged(etItemDescription, object : Utils.Companion.OnTextChanged{
            override fun onChange(text: String) {
                itemDescription = text
            }
        }){this.submissionButton()}
        Utils.setOnTextChanged(etSenderName, object : Utils.Companion.OnTextChanged{
            override fun onChange(text: String) {
                senderName = text
            }
        }){this.submissionButton()}
        Utils.setOnTextChanged(etSenderAddress, object : Utils.Companion.OnTextChanged{
            override fun onChange(text: String) {
                senderAddress = text
            }
        }){this.submissionButton()}
        Utils.setOnTextChanged(etSenderPhone, object : Utils.Companion.OnTextChanged{
            override fun onChange(text: String) {
                senderPhone = text
            }
        }){this.submissionButton()}
        Utils.setOnTextChanged(etReceiverName, object : Utils.Companion.OnTextChanged{
            override fun onChange(text: String) {
                receiverName = text
            }
        }){this.submissionButton()}
        Utils.setOnTextChanged(etReceiverAddress, object : Utils.Companion.OnTextChanged{
            override fun onChange(text: String) {
                receiverAddress = text
            }
        }){this.submissionButton()}
        Utils.setOnTextChanged(etReceiverPhone, object : Utils.Companion.OnTextChanged{
            override fun onChange(text: String) {
                receiverPhone = text
            }
        }){this.submissionButton()}
        Utils.setOnTextChanged(tvAgentOption, object : Utils.Companion.OnTextChanged{
            override fun onChange(text: String) {
                agentOption = text
            }
        }){this.submissionButton()}
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
            if (requestCode == requestCodeAgent){
                val agentId =  data?.extras?.getSerializable("agent") as String

                getAgentDetail.set(agentId)
                getAgentDetail.get().observe(this, Observer {
                    agent = it.data[0]
                    tvAgentOption.text = it.data[0].username
                    tvAgentOption.setTextColor(ContextCompat.getColor(this, R.color.darkText))
                })
            }
        }
    }

    private fun moveToTitipPaketFormConfirmation(){
        tvSubmit.setOnClickListener {
            val intent = Intent(this, TitipPaketFormConfirmationActivity::class.java)
            intent.putExtra("packageName", etItemName.text.toString())
            intent.putExtra("packageDescription", etItemDescription.text.toString())
            intent.putExtra("senderName", etSenderName.text.toString())
            intent.putExtra("senderPhoneNumber", etSenderPhone.text.toString())
            intent.putExtra("senderAddress", etSenderAddress.text.toString())
            intent.putExtra("receiverName", etReceiverName.text.toString())
            intent.putExtra("receiverPhoneNumber", etReceiverPhone.text.toString())
            intent.putExtra("receiverAddress", etReceiverAddress.text.toString())
            intent.putExtra("agent", agent)
            startActivity(intent)
        }
    }
}