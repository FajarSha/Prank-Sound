package com.example.funnysound.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.funnysound.R

class SoundListAdapter (  private val categoryList: ArrayList<String>,
                          private val catName: String,
                          private val posValue: String,
                          var onClick: (pos:Int,picName:String) -> Unit,
): RecyclerView.Adapter<SoundListAdapter.SoundViewHolder>() {

    private var mContext: Context?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoundViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.sound_list_item,
            parent, false
        )
        mContext=parent.context
        // at last we are returning our view holder
        // class with our item View File.
        return SoundViewHolder(itemView)
    }



    override fun getItemCount(): Int {
        return categoryList.size
    }

    class SoundViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // on below line we are initializing our course name text view and our image view.
        val soundImageView: ImageView = itemView.findViewById(R.id.iv_sound)
        val soundNameText: TextView = itemView.findViewById(R.id.tv_sound_name)

    }

    override fun onBindViewHolder(holder: SoundViewHolder, position: Int) {
        //holder.courseIV.setImageResource(categoryList[position])
        val drawableNameSelected= "${catName}_icon_$posValue"
        Log.d("drawableLabling",drawableNameSelected)

          val drawableResourceId: Int = mContext?.resources!!.getIdentifier(drawableNameSelected, "drawable", mContext?.packageName)
        mContext?.let {
            Glide
                .with(it)
                .load(drawableResourceId)
                .into(holder.soundImageView)
        };
        holder.apply {
            itemView.setOnClickListener {
                onClick(position,drawableNameSelected)
            }
            soundNameText.text=mContext?.resources!!.getString(R.string.sound)+" "+position

        }

    }
}
