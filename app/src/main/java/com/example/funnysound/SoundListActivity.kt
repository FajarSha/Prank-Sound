package com.example.funnysound

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.example.funnysound.Ad_Utils.InAppBilling
import com.example.funnysound.adapter.CategoryAdapter
import com.example.funnysound.adapter.SoundListAdapter
import com.example.funnysound.ads.Banner1
import com.example.funnysound.databinding.ActivityCategoryBinding
import com.example.funnysound.databinding.ActivitySoundListBinding
import com.example.funnysound.utils.DbConstants.DETAIL_PICTURE
import com.example.funnysound.utils.DbConstants.MAIN_CATEGORY_NAME
import com.example.funnysound.utils.DbConstants.SUB_CATEGORY_CLICK
import com.example.funnysound.utils.DbConstants.SUB_CATEGORY_NAME
import com.example.funnysound.utils.getData
import com.example.funnysound.utils.openActivity
import com.google.android.gms.ads.AdRequest
import io.paperdb.Paper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SoundListActivity : AppCompatActivity() {
    lateinit var binding: ActivitySoundListBinding
    private var categoryList = ArrayList<String>()
    private lateinit var soundListAdapter: SoundListAdapter
    private var mainCategoryName: String? = null
    private var subCategoryName: String? = null
    private var clickPosition: String? = null


    var isInAppPurched = false
    private lateinit var inAppBilling: InAppBilling

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySoundListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Paper.init(this)


        inAppBilling = InAppBilling(this, this);
        isInAppPurched = inAppBilling.hasUserBoughtInApp()

        mainCategoryName = intent.getData(MAIN_CATEGORY_NAME)
        subCategoryName = intent.getData(SUB_CATEGORY_NAME)
        clickPosition = intent.getData(SUB_CATEGORY_CLICK)
        addListing();

        soundListAdapter = SoundListAdapter(
            categoryList,
            mainCategoryName!!,
            clickPosition!!
        ) { pos, pictureName ->
            //  startActivity(Intent(this,SoundActivity::class.java))
            openActivity(SoundActivity::class.java) {
                putString(MAIN_CATEGORY_NAME, mainCategoryName)
                putString(SUB_CATEGORY_NAME, subCategoryName)
                putString(SUB_CATEGORY_CLICK, (pos + 1).toString())
                putString(DETAIL_PICTURE, pictureName)

            }
        }





        binding.apply {
            val upperString: String = subCategoryName!!.substring(0, 1).toUpperCase() + subCategoryName!!.substring(1).toLowerCase()+ " "
            tvSoundCategory.text=upperString+resources.getString(R.string.sounds)

            if (Paper.book().read("inapp", false)!!) {
//                adView.visibility = View.GONE
            } else {
                val adRequest = AdRequest.Builder().build()
//                adView.loadAd(adRequest)
            }
            rcvCategory.adapter = soundListAdapter
            val packNameMaking = "${mainCategoryName}_pack_$clickPosition"
            val drawableResourceId: Int =
                resources!!.getIdentifier(packNameMaking, "drawable", packageName)
            Glide
                .with(this@SoundListActivity)
                .load(drawableResourceId)
                .into(binding.ivPack)

            ivBack.setOnClickListener {
                finish()
            }

        }
        if (!isInAppPurched) {
            CoroutineScope(Dispatchers.IO).launch {
                binding?.adLayout?.let { Banner1.show(this@SoundListActivity, it, this@SoundListActivity) }
            }
        }
        else{
            binding.adLayout.visibility=View.GONE
        }
    }

    fun addListing() {
        for (j in 1..5) {
            categoryList.add("Sound $j")
        }
    }
    override fun onResume() {
        super.onResume()
        inAppBilling= InAppBilling(this
            ,this)
        isInAppPurched=inAppBilling.hasUserBoughtInApp()
        if (!isInAppPurched) {
            binding.adLayout.visibility=View.VISIBLE
        }
        else{
            binding.adLayout.visibility=View.GONE
        }
    }


}