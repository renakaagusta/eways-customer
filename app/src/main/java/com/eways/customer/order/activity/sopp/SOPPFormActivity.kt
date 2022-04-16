package com.eways.customer.order.activity.sopp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.UserHandle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.eways.customer.R
import com.eways.customer.core.baseactivity.BaseActivity
import com.eways.customer.order.activity.option.AgentOptionActivity
import com.eways.customer.order.activity.option.SOPPOptionActivity
import com.eways.customer.utils.Utils
import com.eways.customer.utils.customsupportactionbar.CustomSupportActionBar
import com.proyek.infrastructures.inventory.internetservice.usecases.GetInternetServiceDetail
import com.proyek.infrastructures.inventory.invoice.entities.Invoice
import com.proyek.infrastructures.inventory.invoice.usecases.GetInvoiceDetail
import com.proyek.infrastructures.order.order.network.body.SOPPBody
import com.proyek.infrastructures.user.agent.entities.UserAgent
import com.proyek.infrastructures.user.agent.usecases.GetAgentDetail
import com.proyek.infrastructures.user.customer.entities.UserCustomer
import kotlinx.android.synthetic.main.activity_sopp_form.*

class SOPPFormActivity:BaseActivity() {
    private val requestCodeAgent = 1
    private val requestCodeSOPP = 2
    private var customerName : String? =null
    private var customerAddress : String? = null
    private var customerPhone : String? =null
    private lateinit var soppOption : String
    private lateinit var agentOption : String

    private lateinit var getAgentDetail: GetAgentDetail
    private lateinit var getInvoiceDetail: GetInvoiceDetail

    private lateinit var agent: UserAgent
    private lateinit var user: UserCustomer
    private lateinit var invoice: Invoice

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sopp_form)
        CustomSupportActionBar.setCustomActionBar(this,"SOPP")

        getAgentDetail = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetAgentDetail::class.java)
        getInvoiceDetail = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetInvoiceDetail::class.java)

        user = intent.getParcelableExtra("user")

        submissionButton()
        formCompleteness()
        setAgent()
        setSOPPOption()
    }

    private fun isValid():Boolean{
        return Utils.isNotNullOrEmpty(customerName) &&
                Utils.isNotNullOrEmpty(customerAddress) &&
                Utils.isNotNullOrEmpty(customerPhone) &&
                tvSOPPOption.text.toString() != getString(R.string.sopp_hint) &&
                tvAgentOption.text.toString() != getString(R.string.agent_hint)
    }

    private fun formCompleteness(){
        Utils.setOnTextChanged(etCustomerName, object : Utils.Companion.OnTextChanged{
            override fun onChange(text: String) {
                customerName = text
            }
        }){this.submissionButton()}
        Utils.setOnTextChanged(etCustomerAddress, object : Utils.Companion.OnTextChanged{
            override fun onChange(text: String) {
                customerAddress = text
            }
        }){this.submissionButton()}
        Utils.setOnTextChanged(etCustomerPhone, object : Utils.Companion.OnTextChanged{
            override fun onChange(text: String) {
                customerPhone = text
            }
        }){this.submissionButton()}
        Utils.setOnTextChanged(tvSOPPOption, object : Utils.Companion.OnTextChanged{
            override fun onChange(text: String) {
                soppOption = text
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

    private fun setSOPPOption(){
        rlSOPPOption.setOnClickListener {
            val intent = Intent(this, SOPPOptionActivity::class.java)
            startActivityForResult(intent, requestCodeSOPP)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if (requestCode == requestCodeSOPP){
                val invoiceId =  data?.extras?.getSerializable("invoice") as String

                this@SOPPFormActivity.showProgress()
                getInvoiceDetail.set(invoiceId)
                getInvoiceDetail.get().observe(this, Observer {
                    this@SOPPFormActivity.dismissProgress()
                    tvSOPPOption.text = it.data[0].name
                    tvSOPPOption.setTextColor(ContextCompat.getColor(this, R.color.darkText))
                    invoice = it.data[0]
                })
            }
            if (requestCode == requestCodeAgent){
                val agentId =  data?.extras?.getSerializable("agent") as String

                this@SOPPFormActivity.showProgress()
                getAgentDetail.set(agentId)
                getAgentDetail.get().observe(this, Observer {
                    this@SOPPFormActivity.dismissProgress()
                    agent = it.data[0]
                    tvAgentOption.text = it.data[0].username
                    tvAgentOption.setTextColor(ContextCompat.getColor(this, R.color.darkText))
                })
                tvAgentOption.setTextColor(ContextCompat.getColor(this, R.color.darkText))
            }
        }
    }

    private fun submissionButton(){
        if(!isValid()){
            tvSubmit.isClickable = false
            tvSubmit.background = ContextCompat.getDrawable(this@SOPPFormActivity, R.drawable.rc_bglightgray)
        }else{
            tvSubmit.background = ContextCompat.getDrawable(this@SOPPFormActivity, R.drawable.rc_bgprimary)
            tvSubmit.isClickable = true
            moveToSOPPConfirmation()
        }
    }

    private fun moveToSOPPConfirmation(){
        tvSubmit.setOnClickListener {
            val soppBody = SOPPBody(
                invoice,

                etCustomerName.text.toString(),
                etCustomerPhone.text.toString(),
                etCustomerAddress.text.toString(),
                etAdditionalInfo.text.toString(),
                "1apSlkvfNVxl5pp6bnzy1RUcZZh",
                user.ID!!,
                agent.ID!!,
                user.username!!,
                "Membuat order SOPP baru")
            val intent = Intent(this, SOPPFormConfirmationActivity::class.java)
            intent.putExtra("user", user)
            intent.putExtra("agent", agent)
            intent.putExtra("body", soppBody)
            startActivity(intent)
        }
    }
}