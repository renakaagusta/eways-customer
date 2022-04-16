package com.eways.customer.dashboard.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eways.customer.R
import com.eways.customer.kabarcluster.activity.KabarClusterActivity
import com.eways.customer.kabarcluster.adapter.KabarClusterPostAdapter
import com.eways.customer.kabarcluster.viewdto.KabarClusterPostViewDTO
import com.eways.customer.notification.activity.NotificationActivity
import com.eways.customer.order.activity.gantipaket.GantiPaketFormActivity
import com.eways.customer.order.activity.layananbebas.LayananBebasFormActivity
import com.eways.customer.order.activity.pickupticket.PickupTicketFormActivity
import com.eways.customer.order.activity.psb.PSBFormActivity
import com.eways.customer.user.activity.ProfileActivity
import com.eways.customer.order.activity.sopp.SOPPFormActivity
import com.eways.customer.order.activity.titipbelanja.TitipBelanjaFormProductActivity
import com.eways.customer.order.activity.titippaket.TitipPaketFormActivity
import com.eways.customer.user.activity.AgentListActivity
import com.eways.customer.utils.customitemdecoration.CustomDividerItemDecoration
import com.eways.customer.utils.date.SLDate
import com.eways.customer.utils.firebase.firestore.Firestore
import com.google.firebase.firestore.ListenerRegistration
import com.proyek.infrastructures.kabarcluster.comment.usecases.GetCommentList
import com.proyek.infrastructures.kabarcluster.post.usecases.GetPostList
import com.proyek.infrastructures.notification.usecases.GetNotificationByUserId
import com.proyek.infrastructures.notification.usecases.GetUnreadNotificationByUserId
import com.proyek.infrastructures.order.order.usecases.GetOrderDetail
import com.proyek.infrastructures.user.customer.entities.UserCustomer
import com.proyek.infrastructures.user.user.usecases.GetUserDetail
import com.proyek.infrastructures.utils.Authenticated
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat


class HomeFragment(user: UserCustomer) : Fragment() {
    private var user = user
    private lateinit var rvPost: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvName: TextView
    private lateinit var imgProfile: ImageView

    private lateinit var getPostList: GetPostList
    private lateinit var getUserDetail: GetUserDetail
    private lateinit var getCommentList: GetCommentList
    private lateinit var getNotification: GetUnreadNotificationByUserId
    private lateinit var getOrderDetail: GetOrderDetail

    private lateinit var myView: View

    private var countRiwayat = 0
    private var countPesanan = 0

    private lateinit var postListener: ListenerRegistration

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_home,container,false)
        tvName = view.findViewById(R.id.tvName)
        imgProfile = view.findViewById(R.id.imgProfile)
        rvPost = view.findViewById(R.id.rvKabarCluster)
        progressBar = view.findViewById(R.id.progressBar)

        getUserDetail = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetUserDetail::class.java)
        getPostList = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetPostList::class.java)
        getPostList = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetPostList::class.java)
        getCommentList = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetCommentList::class.java)
        getNotification = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetUnreadNotificationByUserId::class.java)
        getOrderDetail = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetOrderDetail::class.java)

        if(user.cluster?.ID!=null)
            postListener = Firestore.PostListener(user.cluster?.ID!!, view, this::showKabarCluster)
        else
            postListener = Firestore.PostListener("1", view, this::showKabarCluster)

        moveToProfile(view)
        moveToNotification(view)
        moveToPSBCreate(view)
        moveToGantiPaketCreate(view)
        moveToPickupTicketCreate(view)
        moveToTitipPaketCreate(view)
        moveToTitipBelanjaCreate(view)
        moveToSOPPCreate(view)
        moveToLayananBebasCreate(view)
        moveToKabarCluster(view)
        moveToAgentList(view)
        setNotification(view)

        return view
    }

    private fun setProfileInfo(){
        user = Authenticated.getUserCustomer()
        tvName.text = user.fullname.toString()
        if(user.imagePath!=null)
            Glide.with(view!!)
                .load("http://13.229.200.77:8001/storage/${user.imagePath}")
                .into(imgProfile)
    }

    override fun onStart() {
        super.onStart()
        setProfileInfo()
    }

    private fun showKabarCluster(view: View?, dtoPost: ArrayList<KabarClusterPostViewDTO>) {
        countPesanan = 0
        countRiwayat = 0

        progressBar.visibility = View.VISIBLE
        rvPost.visibility = View.GONE

        GlobalScope.launch(Dispatchers.Main) {
            val size = dtoPost.size
            for(index in 0 until size) {
                val dto = dtoPost[index]
                getUserDetail.set(dtoPost[index].userId!!, view?.context!!)
                delay(700)
                getUserDetail.get().apply {
                    if((this!=null)&&(this.isNotEmpty())) {
                        dto.creator = this[0].username!!
                        if (this[0].imagePath != null) dto.imagePath = this[0].imagePath!!
                    } else {
                        dto.creator = "-"
                    }
                }
                dtoPost[index] = dto
            }

            delay(700)

            for(index in 0 until size) {
                val dto = dtoPost[index]
                getCommentList.set(dtoPost[index].id!!)
                delay(700)
                getCommentList.get().apply {
                    var commentCount = 0
                    this.data!!.forEach {
                        commentCount++
                    }
                    dto.commentCount = commentCount
                }
                dtoPost[index] = dto
            }
            delay(700)

            progressBar.visibility = View.GONE
            rvPost.visibility = View.VISIBLE

            setKabarClusterData(view!!, dtoPost)
        }
    }

    private fun setNotification(view: View){
        GlobalScope.launch(Dispatchers.Main) {
            var unreadNotification = 0
            getNotification.set(user.ID!!, view.context)
            delay(200)
            if(!getNotification.get().data.isNullOrEmpty()) {
                for(i in 0 until getNotification.get().data?.size!!) {
                    if(getNotification.get().data!![i].readAt==null)
                        unreadNotification++
                }

            }

            delay(200)

            view.ivNotificationAlert.isVisible = unreadNotification>0
        }
    }

    private fun moveToProfile(view: View){
        view.llUser.setOnClickListener {
            val intent = Intent(view.context, ProfileActivity::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
        }
    }

    private fun moveToNotification(view: View){
        view.ivNotification.setOnClickListener {
            val intent = Intent(view.context, NotificationActivity::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
        }
    }

    private fun moveToPSBCreate(view: View){
        view.llPSB.setOnClickListener {
            val intent = Intent(view.context, PSBFormActivity::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
        }
    }

    private fun moveToGantiPaketCreate(view: View){
        view.llGantiPaket.setOnClickListener {
            val intent = Intent(view.context, GantiPaketFormActivity::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
        }
    }

    private fun moveToPickupTicketCreate(view: View){
        view.llPickupTicket.setOnClickListener {
            val intent = Intent(view.context, PickupTicketFormActivity::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
        }
    }

    private fun moveToTitipPaketCreate(view: View){
        view.llTitipPaket.setOnClickListener {
            val intent = Intent(view.context, TitipPaketFormActivity::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
        }
    }

    private fun moveToTitipBelanjaCreate(view: View){
        view.llTitipBelanja.setOnClickListener {
            val intent = Intent(view.context, TitipBelanjaFormProductActivity::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
        }
    }

    private fun moveToSOPPCreate(view: View){
        view.llSOPP.setOnClickListener {
            val intent = Intent(view.context, SOPPFormActivity::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
        }
    }

    private fun moveToLayananBebasCreate(view: View){
        view.llLayananBebas.setOnClickListener {
            val intent = Intent(view.context, LayananBebasFormActivity::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
        }
    }

    private fun moveToKabarCluster(view: View){
        view.tvKabarClusterLainnya.setOnClickListener {
            val intent = Intent(view.context, KabarClusterActivity::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
        }
    }

    private fun moveToAgentList(view: View){
        view.llAgent.setOnClickListener {
            val intent = Intent(view.context, AgentListActivity::class.java)
            startActivity(intent)
        }
    }
    private fun setKabarClusterData(view: View,dto: ArrayList<KabarClusterPostViewDTO>){

        lateinit var kabarClusterAdapter: KabarClusterPostAdapter

        if(user.cluster!=null)
            kabarClusterAdapter = KabarClusterPostAdapter(dto, user.cluster?.ID!!, user.ID!!)
        else
            kabarClusterAdapter = KabarClusterPostAdapter(dto, "1", user.ID!!)

        val decorator = DividerItemDecoration(view.context, LinearLayoutManager.VERTICAL)
        decorator.setDrawable(ContextCompat.getDrawable(view.context, R.drawable.divider_line)!!)
        rvPost.rvKabarCluster.apply {
            layoutManager = LinearLayoutManager(view.context)
            addItemDecoration(CustomDividerItemDecoration(ContextCompat.getDrawable(view.context, R.drawable.divider_line)!!))
            isNestedScrollingEnabled = false
            adapter = kabarClusterAdapter
        }
    }
}
