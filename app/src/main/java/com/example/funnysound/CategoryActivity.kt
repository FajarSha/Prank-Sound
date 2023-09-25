package com.example.funnysound

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.funnysound.Ad_Utils.InAppBilling
import com.example.funnysound.adapter.CategoryAdapter
import com.example.funnysound.ads.Banner
import com.example.funnysound.ads.Banner1
import com.example.funnysound.databinding.ActivityCategoryBinding
import com.example.funnysound.utils.DbConstants.ANIMAL
import com.example.funnysound.utils.DbConstants.HUMAN
import com.example.funnysound.utils.DbConstants.MAIN_CATEGORY_LIST_SIZE
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


class CategoryActivity : AppCompatActivity() {
    lateinit var binding: ActivityCategoryBinding
    private var categoryDrawableList = ArrayList<Drawable>()
    private lateinit var categoryAdapter: CategoryAdapter
    private var catName: String? = null
    private var listSize = 0
    var isInAppPurched = false
    private lateinit var inAppBilling: InAppBilling
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Paper.init(this)
        catName = intent.getData(MAIN_CATEGORY_NAME)
        listSize = intent.getIntExtra(MAIN_CATEGORY_LIST_SIZE, 0)


        inAppBilling = InAppBilling(this, this);
        isInAppPurched = inAppBilling.hasUserBoughtInApp()


        addListing(listSize)
        if (Paper.book().read("inapp", false)!!) {
//            binding.adView.visibility = View.GONE
        } else {
            val adRequest = AdRequest.Builder().build()
//            binding.adView.loadAd(adRequest)
        }

        if (catName.equals(HUMAN)) {
            val myArray = this.resources.getStringArray(R.array.humanSubCategory)
            categoryAdapter = CategoryAdapter(categoryDrawableList, myArray) { pos ->
                openActivity(SoundListActivity::class.java) {
                    putString(MAIN_CATEGORY_NAME, catName)
                    putString(SUB_CATEGORY_NAME, myArray[pos])
                    putString(SUB_CATEGORY_CLICK, (pos + 1).toString())

                }
            }

        } else if (catName.equals(ANIMAL)) {
            val myArray = this.resources.getStringArray(R.array.animalSubCategory)
            categoryAdapter = CategoryAdapter(categoryDrawableList, myArray) { pos ->
                openActivity(SoundListActivity::class.java) {
                    putString(MAIN_CATEGORY_NAME, catName)
                    putString(SUB_CATEGORY_NAME, myArray[pos])
                    putString(SUB_CATEGORY_CLICK, (pos + 1).toString())

                }
            }
        } else {
            val myArray = this.resources.getStringArray(R.array.machineSubCategory)
            categoryAdapter = CategoryAdapter(categoryDrawableList, myArray) { pos ->
                openActivity(SoundListActivity::class.java) {
                    putString(MAIN_CATEGORY_NAME, catName)
                    putString(SUB_CATEGORY_NAME, myArray[pos])
                    putString(SUB_CATEGORY_CLICK, (pos + 1).toString())

                }
            }
        }




        binding.apply {
            val upperString: String =
                catName!!.substring(0, 1).toUpperCase() + catName!!.substring(1).toLowerCase() + " "
            tvCategory.text = upperString + resources.getString(R.string.sounds)
            rcvCategory.adapter = categoryAdapter
            ivBack.setOnClickListener {
                finish()
            }
        }


        if (!isInAppPurched) {
            CoroutineScope(Dispatchers.IO).launch {
                binding?.adLayout?.let { Banner1.show(this@CategoryActivity, it, this@CategoryActivity) }
            }
        }
        else{
            binding.adLayout.visibility=View.GONE
        }
    }

    private fun addListing(limit: Int) {
        for (j in 1..limit) {
            val drawable = resources.getDrawable(
                resources.getIdentifier(
                    "${catName}_$j",
                    "drawable",
                    packageName
                )
            )
            categoryDrawableList.add(drawable)
        }
    }

    override fun onResume() {
        super.onResume()
        inAppBilling=InAppBilling(this
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