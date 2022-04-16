package com.eways.customer.dashboard.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cn.pedant.SweetAlert.SweetAlertDialog
import com.eways.customer.core.baseactivity.BaseActivity
import com.eways.customer.order.activity.HistoryFragment
import com.eways.customer.order.activity.OrderFragment
import com.eways.customer.R
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.proyek.infrastructures.notification.usecases.GetNotificationByUserId
import com.proyek.infrastructures.notification.usecases.GetUnreadNotificationByUserId
import com.proyek.infrastructures.notification.usecases.ReadNotification
import com.proyek.infrastructures.order.order.usecases.GetOrderDetail
import com.proyek.infrastructures.user.agent.entities.UserAgent
import com.proyek.infrastructures.user.customer.entities.UserCustomer
import com.proyek.infrastructures.user.customer.usecases.GetCustomerDetail
import com.proyek.infrastructures.utils.Authenticated
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.exitProcess


class MainActivity : BaseActivity() {
    private var selectedFragment: Fragment? = null
    private lateinit var user: UserCustomer

    private lateinit var getUnreadNotificationOrder: GetUnreadNotificationByUserId
    private lateinit var getOrderDetail: GetOrderDetail
    private lateinit var readNotification: ReadNotification

    private val listOrderNotificationId = ArrayList<String>()
    private val listHomeNotificationId = ArrayList<String>()
    private val listHistoryNotificationId = ArrayList<String>()

    private lateinit var getCustomerDetail: GetCustomerDetail
    
    private var bottomIndexItem = R.id.navHome

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        getUnreadNotificationOrder = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetUnreadNotificationByUserId::class.java)
        getOrderDetail = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetOrderDetail::class.java)
        readNotification = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(ReadNotification::class.java)

        if (Authenticated.isValidCacheMember()) {
            createFragment(savedInstanceState)
        } else {
            this.showProgress()
            getCustomerDetail.set(Authenticated.getUserCustomer().ID!!)
            getCustomerDetail.get().observe(this@MainActivity, Observer {
                this@MainActivity.dismissProgress()
                if (it.errors.message.isEmpty()){
                    Authenticated.setUserCustomer(it.data[0])
                    createFragment(savedInstanceState)
                } else {
                    this@MainActivity.showError(it.errors.message!![0])
                }
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBackPressed() {
        if(bottomIndexItem == R.id.navHome)
        SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Apakah anda yakin ingin keluar dari aplikasi?")
            .setConfirmButton("Ya",{
                System.exit(0)
                finishAndRemoveTask()
            })
            .setCancelText("Tidak")
            .show()
        else
            bottomNavigation.selectedItemId = R.id.navHome
    }

    private fun createFragment(savedInstanceState: Bundle?){
        user = Authenticated.getUserCustomer()

        bottomNavigation.setOnNavigationItemSelectedListener(navListener)
        bottomNavigation.selectedItemId = R.id.navHome

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(
                R.id.fragmentContainer,
                HomeFragment(user)
            ).commit()
        }

        setNotification()
    }

    override fun onResume() {
        super.onResume()

        bottomNavigation.selectedItemId = bottomIndexItem

        if (user != null){
            setNotification()
        }
    }

    override fun onRestart() {
        super.onRestart()

        listHistoryNotificationId.clear()
        listOrderNotificationId.clear()
    }

    private fun setNotification(){
        GlobalScope.launch ( Dispatchers.Main ) {
            val listOrderId = ArrayList<String>()
            getUnreadNotificationOrder.set(user.ID!!, this@MainActivity)
            delay(500)
            getUnreadNotificationOrder.get().data?.forEach {
                if(it.type==1) {
                    listOrderId.add(it.typeId!!)
                }
            }

            delay(500)

            Log.d("listOrderId", listOrderId.toString())

            listOrderId.forEach {
                getOrderDetail.set(it, this@MainActivity)
                delay(300)
                for(order in 0 until getOrderDetail.get().size){
                    Log.d("order", getOrderDetail.get().toString())
                    if((getOrderDetail.get()[order].orderStatus == 2)||(getOrderDetail.get()[order].orderStatus == 3))
                        listHistoryNotificationId.add(getOrderDetail.get()[order].id!!)
                    else
                        listOrderNotificationId.add(getOrderDetail.get()[order].id!!)
                    getOrderDetail.result.clear()
                    break
                }
                Log.d("listOrderId", it.toString())
            }

            delay(500)

            if(listHistoryNotificationId.isNotEmpty())setHistoryNotification()
            if(listOrderNotificationId.isNotEmpty())setOrderNotification()
        }
    }

    private fun setOrderNotification(){
        val badgeDrawableHistory : BadgeDrawable = bottomNavigation.getOrCreateBadge(R.id.navOrder)
        badgeDrawableHistory.number=listOrderNotificationId.size
        badgeDrawableHistory.backgroundColor = R.drawable.bottomnav_item
    }

    private fun setHistoryNotification() {
        val badgeDrawableOrder : BadgeDrawable = bottomNavigation.getOrCreateBadge(R.id.navHistory)
        badgeDrawableOrder.number=listHistoryNotificationId.size
        badgeDrawableOrder.backgroundColor = R.drawable.bottomnav_item
    }

    private val navListener: BottomNavigationView.OnNavigationItemSelectedListener =
        object : BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                selectedFragment = null
                when (item.itemId) {
                    R.id.navHome -> {
                        selectedFragment = HomeFragment(user)
                        bottomIndexItem = item.itemId
                    }
                    R.id.navHistory -> {
                        selectedFragment = HistoryFragment(user)
                        readNotification.set(listHistoryNotificationId)
                        bottomIndexItem = item.itemId
                    }
                    R.id.navOrder -> {
                        selectedFragment = OrderFragment(user)
                        readNotification.set(listOrderNotificationId)
                        bottomIndexItem = item.itemId
                    }
                }

                bottomNavigation.getBadge(item.itemId)?.let { badgeDrawable ->
                    if(badgeDrawable.isVisible) bottomNavigation.removeBadge(item.itemId)
                }
                supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, selectedFragment!!).commit()
                return true
            }
        }
}
