package com.eways.customer.order.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eways.customer.R
import com.eways.customer.order.activity.titipbelanja.TitipBelanjaFormSubProductActivity
import com.eways.customer.order.viewdto.ProductViewDTO
import com.proyek.infrastructures.inventory.category.entities.Category
import kotlinx.android.synthetic.main.row_product.view.*

class ProductAdapter(private val products : List<ProductViewDTO>, listCategory: ArrayList<Category>) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    private var listCategory = listCategory

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_product, parent, false))
    }

    override fun getItemCount(): Int =products.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bindProduct(products[position], listCategory[position])
    }

    inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view){
        fun bindProduct(productViewDTO: ProductViewDTO, category: Category){
            itemView.apply {
                tvProduct.text = productViewDTO.productName
                ivProduct.setImageResource(R.drawable.ic_product)

                setOnClickListener {
                    val intent = Intent(this.context, TitipBelanjaFormSubProductActivity::class.java)
                    intent.putExtra("category", category)
                    this.context.startActivity(intent)
                }
            }
        }
    }
}