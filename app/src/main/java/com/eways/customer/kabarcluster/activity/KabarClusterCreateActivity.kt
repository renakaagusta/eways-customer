package com.eways.customer.kabarcluster.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.eways.customer.R
import com.eways.customer.core.baseactivity.BaseActivity
import com.eways.customer.utils.customsupportactionbar.CustomSupportActionBar
import com.proyek.infrastructures.kabarcluster.post.usecases.CreatePost
import com.proyek.infrastructures.user.customer.entities.UserCustomer
import kotlinx.android.synthetic.main.activity_kabarcluster_create.*
import kotlinx.android.synthetic.main.supportactionbar_post.*
import com.bumptech.glide.Glide

class KabarClusterCreateActivity :BaseActivity() {
    private lateinit var createPost: CreatePost
    private lateinit var user: UserCustomer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kabarcluster_create)
        CustomSupportActionBar.setCustomActionBarKabarCluster(this, R.layout.supportactionbar_post)

        user = intent.getParcelableExtra("user")

        if(user.imagePath!=null)
            Glide.with(this)
                .load("http://13.229.200.77:8001/storage/${user.imagePath}")
                .into(civUserImage)

        createPost = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(CreatePost::class.java)

        moveToKabarClusterRead()
    }

    private fun moveToKabarClusterRead(){
        tvPost.setOnClickListener {
            this@KabarClusterCreateActivity.showProgress()
            createPost.set(user.ID!!, user.cluster?.ID!!, tietPost.text.toString())
            createPost.get().observe(this, Observer {
                this@KabarClusterCreateActivity.dismissProgress()
                startActivity(Intent(this@KabarClusterCreateActivity, KabarClusterActivity::class.java))
                finish()
            })
        }
    }
}