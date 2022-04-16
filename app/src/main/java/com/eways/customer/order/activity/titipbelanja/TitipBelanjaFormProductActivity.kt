package com.eways.customer.order.activity.titipbelanja

import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.eways.customer.R
import com.eways.customer.core.baseactivity.BaseActivity
import com.eways.customer.order.adapter.ProductAdapter
import com.eways.customer.order.viewdto.ProductViewDTO
import com.eways.customer.utils.customitemdecoration.CustomDividerItemDecoration
import com.eways.customer.utils.customsupportactionbar.CustomSupportActionBar
import com.proyek.infrastructures.inventory.category.entities.Category
import com.proyek.infrastructures.inventory.category.usecases.GetCategoryList
import com.proyek.infrastructures.user.customer.entities.UserCustomer
import kotlinx.android.synthetic.main.activity_titipbelanja_form_product.*
import java.util.ArrayList

class TitipBelanjaFormProductActivity:BaseActivity(){
    private lateinit var getCategoryList: GetCategoryList
    private var listCategory = ArrayList<Category>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_titipbelanja_form_product)
        CustomSupportActionBar.setCustomActionBar(this, "Titip Belanja")

        getCategoryList = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetCategoryList::class.java)

        setProductData()
    }

    private fun setProductData(){
        getCategoryList.set()
        getCategoryList.get().observe(this, Observer {
            val listProductViewDTO = ArrayList<ProductViewDTO>()

            listCategory.addAll(it.data)

            it.data.forEach {
                listProductViewDTO.add(
                    ProductViewDTO(
                        it.id!!,
                        it.name!!,
                        ""
                    )
                )
            }

            val productAdapter = ProductAdapter(listProductViewDTO, listCategory)
            rvProduct.apply {
                layoutManager = LinearLayoutManager(this@TitipBelanjaFormProductActivity)
                addItemDecoration(CustomDividerItemDecoration(ContextCompat.getDrawable(this@TitipBelanjaFormProductActivity, R.drawable.divider_line)!!))
                isNestedScrollingEnabled = false
                adapter = productAdapter
            }
        })
    }
}