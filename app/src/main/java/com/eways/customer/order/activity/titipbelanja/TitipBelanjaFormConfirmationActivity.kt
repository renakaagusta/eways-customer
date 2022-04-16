package com.eways.customer.order.activity.titipbelanja

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.eways.customer.R
import com.eways.customer.core.baseactivity.BaseActivity
import com.eways.customer.dashboard.activity.MainActivity
import com.eways.customer.order.activity.option.AgentOptionActivity
import com.eways.customer.order.adapter.TitipBelanjaTransactionAdapter
import com.eways.customer.order.viewdto.TransactionViewDTO
import com.eways.customer.utils.MoneyUtils
import com.eways.customer.utils.Utils
import com.eways.customer.utils.customitemdecoration.CustomDividerItemDecoration
import com.eways.customer.utils.customsupportactionbar.CustomSupportActionBar
import com.proyek.infrastructures.inventory.category.entities.Category
import com.proyek.infrastructures.inventory.item.entities.Grocery
import com.proyek.infrastructures.inventory.service.usecases.GetServiceDetail
import com.proyek.infrastructures.order.order.network.body.TitipBelanjaBody
import com.proyek.infrastructures.order.order.usecases.CreateTitipBelanjaOrder
import com.proyek.infrastructures.user.agent.entities.UserAgent
import com.proyek.infrastructures.user.agent.usecases.GetAgentDetail
import com.proyek.infrastructures.user.customer.entities.UserCustomer
import com.proyek.infrastructures.utils.Authenticated
import kotlinx.android.synthetic.main.activity_psb_form_confirmation.*
import kotlinx.android.synthetic.main.activity_titipbelanja_form_confirmation.*
import kotlinx.android.synthetic.main.activity_titipbelanja_form_confirmation.tvAgentFee
import kotlinx.android.synthetic.main.activity_titipbelanja_form_confirmation.tvSubmit
import kotlin.collections.ArrayList

class TitipBelanjaFormConfirmationActivity:BaseActivity(){
    private val requestCodeAgent = 1
    private var isEmpty = true
    private lateinit var agentOption : String
    private lateinit var user: UserCustomer
    private lateinit var agent: UserAgent
    private lateinit var groceries: ArrayList<Grocery>
    private lateinit var category: Category

    private lateinit var createTitipBelanjaOrder: CreateTitipBelanjaOrder
    private lateinit var getAgentDetail: GetAgentDetail
    private lateinit var getServiceDetail: GetServiceDetail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_titipbelanja_form_confirmation)
        CustomSupportActionBar.setCustomActionBar(this, "Detail Pesanan Titip Belanja")

        user = Authenticated.getUserCustomer()
        groceries = intent.getParcelableArrayListExtra("groceries")!!
        category = intent.getParcelableExtra("category")

        groceries.forEach {
            if(it.quantity>0)
                isEmpty = false
        }

        createTitipBelanjaOrder = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(CreateTitipBelanjaOrder::class.java)
        getAgentDetail = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetAgentDetail::class.java)
        getServiceDetail = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetServiceDetail::class.java)

        setData()
        setAgent()
        submissionButton()
        formCompleteness()
    }

    private fun isValid():Boolean{
        return tvAgentOption.text.toString() != getString(R.string.agent_hint)
    }

    private fun setData(){
        val listTransactionViewDTO = ArrayList<TransactionViewDTO>()

        groceries.forEach {
            if(it.quantity>0)
            listTransactionViewDTO.add(
                TransactionViewDTO(
                    it.items.imgPath,
                    it.items.name!!,
                    it.quantity,
                    it.items.price!!
                )
            )
        }

        val sizeOrder = groceries.size
        for(i in 0 until sizeOrder)
            groceries[i].items.category = category

        val titipBelanjaTransactionAdapter = TitipBelanjaTransactionAdapter(listTransactionViewDTO)
        rvTransaction.apply {
            layoutManager = LinearLayoutManager(this@TitipBelanjaFormConfirmationActivity)
            addItemDecoration(CustomDividerItemDecoration(ContextCompat.getDrawable(this@TitipBelanjaFormConfirmationActivity, R.drawable.divider_line)!!))
            isNestedScrollingEnabled = false
            adapter = titipBelanjaTransactionAdapter
        }

        var totalPrice = 0
        for (i in 0 until listTransactionViewDTO.size) {
            totalPrice += listTransactionViewDTO[i].subproductPrice * listTransactionViewDTO[i].suProductAmount
        }

        showProgress()
        getServiceDetail.set("1apNPAk6sYE5SoBxfvKT7tlmlqI")
        getServiceDetail.get().observe(this, Observer {
            dismissProgress()
            tvAgentFee.text = MoneyUtils.getAmountString(it[0].agentFee)
            tvTotalBelanja.text = MoneyUtils.getAmountString(totalPrice)
            tvTotalTransaction.text = (totalPrice+it[0].agentFee!!).toString()
        })
    }

    private fun setAgent(){
        rlAgentOption.setOnClickListener {
            val intent = Intent(this, AgentOptionActivity::class.java)
            startActivityForResult(intent, requestCodeAgent)
        }
    }

    private fun formCompleteness(){
        Utils.setOnTextChanged(tvAgentOption, object : Utils.Companion.OnTextChanged{
            override fun onChange(text: String) {
                agentOption = text
            }
        }){this.submissionButton()}
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if (requestCode == requestCodeAgent){
                val agentId =  data?.extras?.getSerializable("agent") as String

                this@TitipBelanjaFormConfirmationActivity.showProgress()
                getAgentDetail.set(agentId)
                getAgentDetail.get().observe(this, Observer {
                    this@TitipBelanjaFormConfirmationActivity.dismissProgress()
                    agent = it.data[0]
                    tvAgentOption.text = it.data[0].username
                    tvAgentOption.setTextColor(ContextCompat.getColor(this, R.color.darkText))
                })
                tvAgentOption.setTextColor(ContextCompat.getColor(this, R.color.darkText))

            }
        }
    }

    private fun submissionButton(){
        if(!isValid() || isEmpty){
            tvSubmit.isClickable = false
            tvSubmit.background = ContextCompat.getDrawable(this@TitipBelanjaFormConfirmationActivity, R.drawable.rc_bglightgray)
        }else{
            tvSubmit.background = ContextCompat.getDrawable(this@TitipBelanjaFormConfirmationActivity, R.drawable.rc_bgprimary)
            tvSubmit.isClickable = true
            onConfirmation()
        }
    }

    private fun onConfirmation(){
        tvSubmit.setOnClickListener {
            this@TitipBelanjaFormConfirmationActivity.showProgress()
            val body = TitipBelanjaBody(
                groceries,
                "1apNPAk6sYE5SoBxfvKT7tlmlqI",
                user.ID!!,
                agent.ID!!,
                user.username!!,
                "Membuat order titip belanja baru"
            )
            createTitipBelanjaOrder.set(body)
            createTitipBelanjaOrder.get().observe(this, Observer{
                this@TitipBelanjaFormConfirmationActivity.dismissProgress()
                if(it.errors.message.isEmpty()) {
                    val intent = Intent(this@TitipBelanjaFormConfirmationActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    this@TitipBelanjaFormConfirmationActivity.showError(it.errors.message[0])
                }
            })
        }
    }
}