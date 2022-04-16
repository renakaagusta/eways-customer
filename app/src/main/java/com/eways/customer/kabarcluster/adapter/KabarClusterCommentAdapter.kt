package com.eways.customer.kabarcluster.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eways.customer.kabarcluster.viewdto.KabarClusterCommentViewDTO
import com.eways.customer.R
import com.proyek.infrastructures.user.user.entities.User
import com.proyek.infrastructures.user.user.usecases.GetUserDetail
import kotlinx.android.synthetic.main.row_kabarcluster.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class KabarClusterCommentAdapter(val kabarClusters : List<KabarClusterCommentViewDTO>, val listUser: ArrayList<User>, val storeOwner: ViewModelStoreOwner, val owner: LifecycleOwner) : RecyclerView.Adapter<KabarClusterCommentAdapter.KabarClusterCommentViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KabarClusterCommentViewHolder {
        return KabarClusterCommentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_kabarcluster, parent, false), storeOwner, owner)
    }

    override fun getItemCount(): Int =kabarClusters.size

    override fun onBindViewHolder(holder: KabarClusterCommentViewHolder, position: Int) {
        holder.bindKabarCluster(kabarClusters[position], listUser[position])
    }

    inner class KabarClusterCommentViewHolder(view: View, storeOwner: ViewModelStoreOwner, owner: LifecycleOwner) : RecyclerView.ViewHolder(view){
        val getUserDetail: GetUserDetail = ViewModelProvider(storeOwner, ViewModelProvider.NewInstanceFactory()).get(GetUserDetail::class.java)
        val view = view

        fun bindKabarCluster(kabarClusterCommentViewDTO: KabarClusterCommentViewDTO, user: User){

            itemView.tvCreator.text = user.username
            if (user.imagePath != null) Glide.with(itemView)
                .load("http://13.229.200.77:8001/storage/${user.imagePath}")
                .into(itemView.imgProfile)

            itemView.apply {
                tvContent.text = kabarClusterCommentViewDTO.content
                tvCreatedAt.text = kabarClusterCommentViewDTO.createdAt.getLocalizeDateString()
                llCommentCount.isVisible = false
            }
        }
    }
}
