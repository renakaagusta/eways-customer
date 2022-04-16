package com.eways.customer.kabarcluster.activity

import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.eways.customer.R
import com.eways.customer.core.baseactivity.BaseActivity
import com.eways.customer.kabarcluster.adapter.KabarClusterCommentAdapter
import com.eways.customer.kabarcluster.adapter.KabarClusterPostAdapter
import com.eways.customer.kabarcluster.viewdto.KabarClusterCommentViewDTO
import com.eways.customer.kabarcluster.viewdto.KabarClusterPostViewDTO
import com.eways.customer.utils.customitemdecoration.CustomDividerItemDecoration
import com.eways.customer.utils.customsupportactionbar.CustomSupportActionBar
import com.eways.customer.utils.date.SLDate
import com.eways.customer.utils.firebase.firestore.Firestore
import com.google.firebase.firestore.ListenerRegistration
import com.proyek.infrastructures.kabarcluster.comment.usecases.CreateComment
import com.proyek.infrastructures.kabarcluster.comment.usecases.GetCommentList
import com.proyek.infrastructures.kabarcluster.post.usecases.GetPostList
import com.proyek.infrastructures.user.user.entities.User
import com.proyek.infrastructures.user.user.usecases.GetUserDetail
import kotlinx.android.synthetic.main.activity_kabarcluster_read.*
import kotlinx.android.synthetic.main.supportactionbar_post.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

class KabarClusterReadActivity : BaseActivity() {
    private lateinit var getCommentList: GetCommentList
    private lateinit var createComment: CreateComment
    private lateinit var getUserDetail: GetUserDetail

    private lateinit var postId: String
    private lateinit var userId: String
    private lateinit var clusterId: String

    private lateinit var commentListener: ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kabarcluster_read)
        CustomSupportActionBar.setCustomActionBarKabarCluster(this, R.layout.supportactionbar_post)

        getCommentList = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetCommentList::class.java)
        createComment = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(CreateComment::class.java)
        getUserDetail = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetUserDetail::class.java)

        postId = intent.getStringExtra("postId")!!
        userId = intent.getStringExtra("userId")!!
        clusterId = intent.getStringExtra("clusterId")!!

        commentListener = Firestore.CommentListener(clusterId, postId, this::setKabarClusterCommentData )

        tvPost.setOnClickListener {
            createComment()
        }
    }

    private fun createComment() {
        createComment.set(userId,postId, tietComment.text.toString())
        tietComment.setText("")
    }

    private fun setKabarClusterCommentData(dtoComment: ArrayList<KabarClusterCommentViewDTO>){
        this@KabarClusterReadActivity.showProgress()

        GlobalScope.launch(Dispatchers.Main) {
            val listUser = ArrayList<User>()
            dtoComment.forEach {
                val comment = it
                getUserDetail.set(it.creator, this@KabarClusterReadActivity)
                delay(500)
                getUserDetail.get().forEach {
                    Log.d(comment.creator, it.toString())
                    listUser.add(it)
                }
            }

            delay(3000)

            val kabarClusterAdapter = KabarClusterCommentAdapter(dtoComment,listUser, this@KabarClusterReadActivity, this@KabarClusterReadActivity)

            rvKabarCluster.apply {
                layoutManager = LinearLayoutManager(this@KabarClusterReadActivity)
                addItemDecoration(
                    CustomDividerItemDecoration(
                        ContextCompat.getDrawable(
                            this@KabarClusterReadActivity,
                            R.drawable.divider_line
                        )!!
                    )
                )
                adapter = kabarClusterAdapter
            }

            this@KabarClusterReadActivity.dismissProgress()
        }
    }
}