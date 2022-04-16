package com.eways.customer.utils.firebase.messaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.eways.customer.R
import com.eways.customer.order.activity.chat.ChatActivity
import com.eways.customer.order.activity.gantipaket.GantiPaketIsDoneActivity
import com.eways.customer.order.activity.gantipaket.GantiPaketOnProgressActivity
import com.eways.customer.order.activity.layananbebas.LayananBebasIsDoneActivity
import com.eways.customer.order.activity.layananbebas.LayananBebasOnProgressActivity
import com.eways.customer.order.activity.pickupticket.PickupTicketIsDoneActivity
import com.eways.customer.order.activity.pickupticket.PickupTicketOnProgressActivity
import com.eways.customer.order.activity.psb.PSBIsDoneActivity
import com.eways.customer.order.activity.psb.PSBOnProgressActivity
import com.eways.customer.order.activity.sopp.SOPPIsDoneActivity
import com.eways.customer.order.activity.sopp.SOPPOnProgressActivity
import com.eways.customer.order.activity.titipbelanja.TitipBelanjaIsDoneActivity
import com.eways.customer.order.activity.titipbelanja.TitipBelanjaOnProgressActivity
import com.eways.customer.order.activity.titippaket.TitipPaketIsDoneActivity
import com.eways.customer.order.activity.titippaket.TitipPaketOnProgressActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.proyek.infrastructures.inventory.item.entities.Grocery
import com.proyek.infrastructures.order.chat.entities.Chat
import com.proyek.infrastructures.order.order.entities.Order
import com.proyek.infrastructures.order.order.entities.Orderable
import kotlinx.android.synthetic.main.row_option_agent.view.*
import javax.annotation.meta.When

class FirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private val TAG = FirebaseMessagingService::class.java.simpleName
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.d(TAG, "Refreshed token: $p0")
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        p0?.notification?.let {
            sendNotification(it.title!!, it.body!!)
        }
    }

    private fun sendNotification(title: String, content: String) {
        val channelId = title
        val channelName = title

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(content)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_eways)
            .setColor(resources.getColor(R.color.colorPrimary))
            .setSound(defaultSoundUri)
            .setPriority(NotificationManager.IMPORTANCE_MAX)
            //.setContentIntent(pendingIntent)

        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)

            notificationBuilder.setChannelId(channelId)

            mNotificationManager.createNotificationChannel(channel)
        }

        val notification = notificationBuilder.build()

        mNotificationManager.notify(0, notification)
    }
}