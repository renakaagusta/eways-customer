package com.eways.customer.order.activity.titipbelanja

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.eways.customer.R
import com.eways.customer.core.baseactivity.BaseActivity
import com.eways.customer.order.adapter.option.SubProductAdapter
import com.eways.customer.order.viewdto.SubProductViewDTO
import com.eways.customer.utils.customitemdecoration.CustomVerticalItemDecoration
import com.eways.customer.utils.customsupportactionbar.CustomSupportActionBar
import com.proyek.infrastructures.inventory.category.entities.Category
import com.proyek.infrastructures.inventory.item.entities.Grocery
import com.proyek.infrastructures.inventory.item.usecases.GetItemByCategory
import com.proyek.infrastructures.order.order.network.body.TitipBelanjaBody
import kotlinx.android.synthetic.main.activity_titipbelanja_form_subproduct.*

class TitipBelanjaFormSubProductActivity : BaseActivity(){
    private lateinit var category: Category
    private var groceries = ArrayList<Grocery>()

    private lateinit var getItemByCategory: GetItemByCategory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_titipbelanja_form_subproduct)
        val productType = intent.getStringExtra("product") ?: "Subproduct"
        CustomSupportActionBar.setCustomActionBar(this, productType)

        category = intent.getParcelableExtra("category")

        getItemByCategory = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GetItemByCategory::class.java)

        setSubProductData()
        moveToTitipBelanjaFormConfirmation()
    }

    private fun updateAmount(event: Int, index: Int) {
        if(event==1)
            groceries[index].quantity++
        else {
            if(groceries[index].quantity>0)
                groceries[index].quantity--
        }
    }

    private fun setSubProductData(){
        this@TitipBelanjaFormSubProductActivity.showProgress()
        getItemByCategory.set(category.id!!)
        getItemByCategory.get().observe(this, Observer {
          this@TitipBelanjaFormSubProductActivity.dismissProgress()
            val listSubproductViewDTO = ArrayList<SubProductViewDTO>()

            var index = 0

            it.data.forEach {
                groceries.add(index, Grocery(it, 0))
                listSubproductViewDTO.add(
                    SubProductViewDTO(
                        index,
                        it.id,
                        it.imgPath,
                        it.name!!,
                        it.description!!,
                        it.price!!
                    )
                )
                index++
            }

            val subproductAdapter = SubProductAdapter(listSubproductViewDTO) { event, index->
                updateAmount(event, index)
            }
            rvSubproduct.apply {
                layoutManager = LinearLayoutManager(this@TitipBelanjaFormSubProductActivity)
                addItemDecoration(CustomVerticalItemDecoration(15))
                isNestedScrollingEnabled = false
                adapter = subproductAdapter
            }
            svSubproduct.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if(!newText.isNullOrEmpty())
                        subproductAdapter.filter.filter(newText)
                    else
                        setSubProductData()
                    return false
                }

            })
        })

    }

    private fun moveToTitipBelanjaFormConfirmation(){
        rlCart.setOnClickListener {
            val intent = Intent(this, TitipBelanjaFormConfirmationActivity::class.java)
            intent.putExtra("category", category)
            intent.putExtra("groceries", groceries)
            startActivity(intent)
        }
    }
}