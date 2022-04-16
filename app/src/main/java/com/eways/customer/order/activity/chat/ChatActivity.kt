package com.eways.customer.order.activity.chat

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.eways.customer.R
import com.eways.customer.core.baseactivity.BaseActivity
import com.eways.customer.order.adapter.ChatAdapter
import com.eways.customer.order.viewdto.ChatViewDTO
import com.eways.customer.utils.customsupportactionbar.CustomSupportActionBar
import com.eways.customer.utils.date.SLDate
import com.eways.customer.utils.firebase.firestore.Firestore
import com.google.firebase.firestore.ListenerRegistration
import com.proyek.infrastructures.order.chat.entities.Chat
import com.proyek.infrastructures.order.chat.usecases.CreateChat
import com.proyek.infrastructures.order.order.usecases.GetOrderDetail
import com.proyek.infrastructures.user.agent.usecases.GetAgentDetail
import com.proyek.infrastructures.user.customer.usecases.GetCustomerDetail
import com.proyek.infrastructures.user.user.usecases.GetUserDetail
import kotlinx.android.synthetic.main.activity_chat.*
import java.text.SimpleDateFormat

class ChatActivity : BaseActivity() {
    private lateinit var getUserDetail: GetUserDetail
    private lateinit var chatListener: ListenerRegistration

    private lateinit var createChat: CreateChat
    private lateinit var getOrderDetail: GetOrderDetail
    private lateinit var getCustomerDetail: GetCustomerDetail
    private lateinit var getAgentDetail: GetAgentDetail

    private lateinit var agentId: String
    private lateinit var agentUserName: String
    private lateinit var customerId: String
    private lateinit var customerUserName: String
    private lateinit var orderId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        createChat = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(CreateChat::class.java)
        getUserDetail = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetUserDetail::class.java)
        getOrderDetail =  ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetOrderDetail::class.java)
        getCustomerDetail = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetCustomerDetail::class.java)

        orderId = intent.getStringExtra("orderId")
        agentId = intent.getStringExtra("agentId")
        agentUserName = intent.getStringExtra("agentUserName")
        customerId = intent.getStringExtra("customerId")
        customerUserName = intent.getStringExtra("customerUserName")

        CustomSupportActionBar.setCustomActionBar(this, customerUserName)
        chatListener = Firestore.ChatListener(orderId, this::setChatData)

        sendChat()

        Log.d("agentId", agentId)
        Log.d("customerId", customerId)

    }

    private fun sendChat() {
        ivSend.setOnClickListener {
            createChat.set(customerId, agentId, etChat.text.toString(), orderId, customerUserName, etChat.text.toString())
            etChat.setText("")
        }
    }


    private fun setChatData(chats: ArrayList<Chat>) {
        val listChatViewDTO = ArrayList<ChatViewDTO>()

        chats.forEach {
            val SLDate = SLDate()
            SLDate.date = SimpleDateFormat("yyyy-MM-dd").parse(it.created_at)

            listChatViewDTO.add(
                ChatViewDTO(
                    it.sender_id == customerId,
                    it.content!!,
                    SLDate
                )
            )
        }

        val charAdapter = ChatAdapter(listChatViewDTO)
        rvChat.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = charAdapter
        }
    }
}