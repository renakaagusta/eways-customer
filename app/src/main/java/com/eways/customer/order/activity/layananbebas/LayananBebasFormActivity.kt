package com.eways.customer.order.activity.layananbebas

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.eways.customer.R
import com.eways.customer.core.baseactivity.BaseActivity
import com.eways.customer.order.activity.option.AgentOptionActivity
import com.eways.customer.order.activity.psb.PSBFormConfirmationActivity
import com.eways.customer.utils.Utils
import com.eways.customer.utils.customsupportactionbar.CustomSupportActionBar
import com.proyek.infrastructures.inventory.internetservice.entities.InternetService
import com.proyek.infrastructures.user.agent.entities.UserAgent
import com.proyek.infrastructures.user.agent.usecases.GetAgentDetail
import com.proyek.infrastructures.user.customer.entities.UserCustomer
import kotlinx.android.synthetic.main.activity_layananbebas_form.*
import kotlinx.android.synthetic.main.activity_layananbebas_form.rlAgentOption
import kotlinx.android.synthetic.main.activity_layananbebas_form.tvAgentOption
import kotlinx.android.synthetic.main.activity_layananbebas_form.tvSubmit
import kotlinx.android.synthetic.main.activity_psb_form.*

class LayananBebasFormActivity : BaseActivity() {
    private val requestCodeAgent = 1
    private var layananBebasName : String? =null
    private var layananBebasDetail : String? =null
    private lateinit var agentOption : String
    private lateinit var user: UserCustomer
    private lateinit var agentId: String
    private lateinit var agent: UserAgent

    private lateinit var getAgentDetail: GetAgentDetail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layananbebas_form)
        CustomSupportActionBar.setCustomActionBar(this, "Layanan Bebas")
    }


    override fun onStart() {
        super.onStart()

        getAgentDetail = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetAgentDetail::class.java)

        submissionButton()
        formCompleteness()
        setAgent()

        user = intent.getParcelableExtra("user")
    }

    private fun isValid():Boolean{
        return Utils.isNotNullOrEmpty(layananBebasName) &&
                Utils.isNotNullOrEmpty(layananBebasDetail) &&
                tvAgentOption.text.toString() != getString(R.string.agent_hint)
    }

    private fun formCompleteness(){
        Utils.setOnTextChanged(etLayananBebasName, object : Utils.Companion.OnTextChanged{
            override fun onChange(text: String) {
                layananBebasName = text
            }
        }){this.submissionButton()}
        Utils.setOnTextChanged(tvAgentOption, object : Utils.Companion.OnTextChanged{
            override fun onChange(text: String) {
                agentOption = text
            }
        }){this.submissionButton()}
        Utils.setOnTextChanged(etLayananBebasDetail, object : Utils.Companion.OnTextChanged{
            override fun onChange(text: String) {
                layananBebasDetail = text
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
            tvSubmit.background = ContextCompat.getDrawable(this@LayananBebasFormActivity, R.drawable.rc_bglightgray)
        }else{
            tvSubmit.background = ContextCompat.getDrawable(this@LayananBebasFormActivity, R.drawable.rc_bgprimary)
            tvSubmit.isClickable = true
            moveToLayananBebasFormConfirmation()
        }
    }

    private fun moveToLayananBebasFormConfirmation(){
        tvSubmit.setOnClickListener {
            val intent = Intent(this, LayananBebasFormConfirmationActivity::class.java)
            intent.putExtra("name", etLayananBebasName.text.toString())
            intent.putExtra("description", etLayananBebasDetail.text.toString())
            intent.putExtra("user", user)
            intent.putExtra("agent", agent)
            startActivity(intent)
        }
    }
}