package com.eways.customer.order.activity.option

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.eways.customer.R
import com.eways.customer.core.baseactivity.BaseActivity
import com.eways.customer.order.adapter.option.ServicePacketOptionAdapter
import com.eways.customer.order.viewdto.InternetServiceOptionViewDTO
import com.eways.customer.utils.customitemdecoration.CustomDividerItemDecoration
import com.proyek.infrastructures.inventory.internetservice.usecases.GetInternetServiceList
import kotlinx.android.synthetic.main.activity_option.*


class ServicePacketOptionActivity : BaseActivity() {
    private lateinit var getInternetServiceList: GetInternetServiceList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_option)
        supportActionBar?.hide()
    }

    override fun onStart() {
        super.onStart()

        getInternetServiceList = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetInternetServiceList::class.java)

        this@ServicePacketOptionActivity.showProgress()

        getInternetServiceList.set()
        getInternetServiceList.get().observe(this, Observer {
            this@ServicePacketOptionActivity.dismissProgress()

            val dtoInternetService = ArrayList<InternetServiceOptionViewDTO>()
            it.data.forEach {
                dtoInternetService.add(InternetServiceOptionViewDTO(it.id!!, it.name!!, it.description!!))
            }
            setServicePacketData(dtoInternetService)
        })
    }

    private fun setServicePacketData(dtoInternetService: ArrayList<InternetServiceOptionViewDTO>){
        val optionAdapter =
            ServicePacketOptionAdapter(
                dtoInternetService
            )
        rvOption.apply {
            layoutManager = LinearLayoutManager(this@ServicePacketOptionActivity)
            addItemDecoration(CustomDividerItemDecoration(ContextCompat.getDrawable(this@ServicePacketOptionActivity, R.drawable.divider_line)!!))
            isNestedScrollingEnabled = false
            adapter = optionAdapter
        }

        svOption.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                optionAdapter.filter.filter(newText)
                return false
            }

        })
    }
}