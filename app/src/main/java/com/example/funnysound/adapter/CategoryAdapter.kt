package com.example.funnysound.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.funnysound.R

class CategoryAdapter(
    private val categoryList: ArrayList<Drawable>,
    private val categoryNameList: Array<String>,

    var onClick: (pos:Int) -> Unit,
    ): RecyclerView.Adapter<CategoryAdapter.CourseViewHolder>() {

private var mContext: Context?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.category_list_item,
            parent, false
        )
        mContext=parent.context
        // at last we are returning our view holder
        // class with our item View File.
        return CourseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        //holder.courseIV.setImageResource(categoryList[position])
     //   val drawableResourceId: Int = mContext?.resources!!.getIdentifier(categoryList[position].toString(), "drawable", mContext?.packageName)

       // holder.categoryImage.setImageDrawable(categoryList[position])
        mContext?.let {
            Glide
                .with(it)
                .load(categoryList[position])
                .into(holder.categoryImage)
        };
        holder.itemView.setOnClickListener {
            onClick(position)
        }

    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // on below line we are initializing our course name text view and our image view.
        val categoryImage: ImageView = itemView.findViewById(R.id.iv_category)
    }
}