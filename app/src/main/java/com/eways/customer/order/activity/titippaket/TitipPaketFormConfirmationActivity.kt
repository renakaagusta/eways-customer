package com.eways.customer.order.activity.titippaket

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.eways.customer.R
import com.eways.customer.core.baseactivity.BaseActivity
import com.eways.customer.dashboard.activity.MainActivity
import com.eways.customer.utils.customsupportactionbar.CustomSupportActionBar
import com.proyek.infrastructures.order.order.usecases.CreateTitipPaketOrder
import com.proyek.infrastructures.user.agent.entities.UserAgent
import com.proyek.infrastructures.user.customer.entities.UserCustomer
import com.proyek.infrastructures.utils.Authenticated
import kotlinx.android.synthetic.main.activity_titippaket_form_confirmation.*

class TitipPaketFormConfirmationActivity :BaseActivity(){

    private lateinit var createTitipPaketOrder: CreateTitipPaketOrder

    private lateinit var agent: UserAgent
    private lateinit var user: UserCustomer

    private lateinit var itemName: String
    private lateinit var itemDescription: String
    private lateinit var senderName: String
    private lateinit var senderPhoneNumber: String
    private lateinit var senderAddress: String
    private lateinit var receiverName: String
    private lateinit var receiverPhoneNumber: String
    private lateinit var receiverAddress: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_titippaket_form_confirmation)
        CustomSupportActionBar.setCustomActionBar(this, "Detail Titip Paket")
    }

    override fun onStart() {
        super.onStart()

        createTitipPaketOrder = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(CreateTitipPaketOrder::class.java)

        agent = intent.getParcelableExtra("agent")
        user = Authenticated.getUserCustomer()

        itemName = intent.getStringExtra("packageName")
        itemDescription = intent.getStringExtra("packageDescription")

        senderName = intent.getStringExtra("senderName")
        senderPhoneNumber = intent.getStringExtra("senderPhoneNumber")
        senderAddress = intent.getStringExtra("senderAddress")

        receiverName = intent.getStringExtra("receiverName")
        receiverPhoneNumber = intent.getStringExtra("receiverPhoneNumber")
        receiverAddress = intent.getStringExtra("receiverAddress")

        setData()
        onConfirmation()
    }

    private fun setData(){
        tvReceiverName.text = receiverName
        tvReceiverAddress.text = receiverAddress
        tvReceiverPhone.text = receiverAddress

        tvSenderName.text = senderName
        tvSenderAddress.text = senderAddress
        tvSenderPhone.text= senderPhoneNumber

        tvItemName.text = itemName
        tvItemDescription.text = itemDescription

        tvAgentName.text = agent.username
    }

    private fun onConfirmation(){
        tvSubmit.setOnClickListener {
            this@TitipPaketFormConfirmationActivity.showProgress()
            createTitipPaketOrder.set(
                senderName, senderPhoneNumber, senderAddress,
                receiverName, receiverPhoneNumber, receiverAddress,
                itemName, itemDescription,
                "1apLWI7YYVugJAKSsrhvcOHDyHZ", user.ID!!, agent.ID!!,
                user.username!!, "Membuat order titip paket baru"
            )
            createTitipPaketOrder.get().observe(this, Observer{
                this@TitipPaketFormConfirmationActivity.dismissProgress()
                if(it.errors.message.isEmpty()) {
                    val intent = Intent(this@TitipPaketFormConfirmationActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    this@TitipPaketFormConfirmationActivity.showError(it.errors.message[0])
                }
            })
        }
    }
}