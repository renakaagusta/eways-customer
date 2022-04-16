package com.eways.customer.kabarcluster.activity

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.eways.customer.R
import com.eways.customer.core.baseactivity.BaseActivity
import com.eways.customer.kabarcluster.adapter.KabarClusterPostAdapter
import com.eways.customer.kabarcluster.viewdto.KabarClusterPostViewDTO
import com.eways.customer.utils.customitemdecoration.CustomDividerItemDecoration
import com.eways.customer.utils.customsupportactionbar.CustomSupportActionBar
import com.eways.customer.utils.date.SLDate
import com.proyek.infrastructures.kabarcluster.comment.usecases.GetCommentList
import com.proyek.infrastructures.kabarcluster.post.usecases.GetPostList
import com.proyek.infrastructures.user.customer.entities.UserCustomer
import com.proyek.infrastructures.user.user.usecases.GetUserDetail
import com.proyek.infrastructures.utils.Authenticated
import kotlinx.android.synthetic.main.activity_kabarcluster.*
import kotlinx.android.synthetic.main.supportactionbar_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

class KabarClusterActivity : BaseActivity(){
    private lateinit var user: UserCustomer
    private lateinit var getPostList: GetPostList
    private lateinit var getUserDetail: GetUserDetail
    private lateinit var getCommentList: GetCommentList

    private lateinit var kabarClusterAdapter: KabarClusterPostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kabarcluster)

        CustomSupportActionBar.setCustomActionBarKabarCluster(this, R.layout.supportactionbar_main)
    }

    override fun onStart() {
        super.onStart()

        getPostList = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetPostList::class.java)
        getUserDetail = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetUserDetail::class.java)
        getCommentList = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetCommentList::class.java)

        user = Authenticated.getUserCustomer()

        if(user.imagePath!=null)
            Glide.with(this)
                .load("http://13.229.200.77:8001/storage/${user.imagePath}")
                .into(civUserImage)

        tvUserName.text = "Hai, ${user.username}!"

        moveToKabarClusterCreate()
        showKabarCluster()
    }

    override fun onRestart() {
        super.onRestart()
        kabarClusterAdapter.clear()
    }

    private fun showKabarCluster() {
        this@KabarClusterActivity.showProgress()

        GlobalScope.launch(context = Dispatchers.Main) {
            val dtoPost = ArrayList<KabarClusterPostViewDTO>()
            getPostList.set(user.cluster?.ID!!)
            getPostList.get().observe(this@KabarClusterActivity, Observer {
                it.data.apply {
                    it.data.forEach {

                        val post = it

                        GlobalScope.launch(context = Dispatchers.Main) {
                            val SLDate = SLDate()
                            SLDate.date = SimpleDateFormat("yyyy-MM-dd").parse(post.created_at)

                            var dto = KabarClusterPostViewDTO(
                                it.id!!,
                                it.user_id!!,
                                it.pinned == 1,
                                "",
                                it.content!!,
                                "",
                                SLDate,
                                0
                            )

                            dtoPost.add(
                                dto
                            )
                        }

                    }
                }
            })

            delay(500)

            var index = 0
            dtoPost.forEach {
                val dto = it
                getUserDetail.set(it.userId!!, this@KabarClusterActivity)
                delay(500)
                getUserDetail.get().apply {
                    dto.creator = this[0].username!!
                    if (this[0].imagePath != null) dto.imagePath = this[0].imagePath!!
                }
                dtoPost[index] = dto
                index++
            }

            index = 0

            delay(500)

            dtoPost.forEach {
                val dto = it
                getCommentList.set(it.id!!)
                delay(500)
                getCommentList.get().apply {
                    var commentCount = 0
                    this.data!!.forEach {
                        commentCount++
                    }
                    dto.commentCount = commentCount
                }
                dtoPost[index] = dto
                index++
            }

            delay(500)

            setKabarClusterData(dtoPost)

            getPostList.get().removeObservers(this@KabarClusterActivity)


            this@KabarClusterActivity.dismissProgress()
        }
    }

    private fun moveToKabarClusterCreate(){
        fabKabarClusterCreate.setOnClickListener {
            val intent = Intent(this, KabarClusterCreateActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("user", user)
            startActivity(intent)
            finish()
        }
    }


    private fun setKabarClusterData(dtoPost: ArrayList<KabarClusterPostViewDTO>){
        kabarClusterAdapter = KabarClusterPostAdapter(dtoPost, user.cluster?.ID!!, user.ID!!)

        this@KabarClusterActivity.dismissProgress()

        rvKabarCluster.apply {
            layoutManager = LinearLayoutManager(this@KabarClusterActivity)
            addItemDecoration(CustomDividerItemDecoration(ContextCompat.getDrawable(this@KabarClusterActivity, R.drawable.divider_line)!!))
            isNestedScrollingEnabled = false
            adapter = kabarClusterAdapter
        }
    }
}