package com.example.funnysound

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.billingclient.api.*
import com.example.funnysound.Ad_Utils.InAppBilling
import com.example.funnysound.ads.Banner
import com.example.funnysound.databinding.ActivityMainBinding
import com.example.funnysound.utils.DbConstants.MAIN_CATEGORY_LIST_SIZE
import com.example.funnysound.utils.DbConstants.MAIN_CATEGORY_NAME
import com.example.funnysound.utils.onBackPressedCallback
import com.example.funnysound.utils.openActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import io.paperdb.Paper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var mInterstitialAd: InterstitialAd? = null
    private var mCurrentClickNumber = 0
    private var billingClient: BillingClient? = null
    var slideUpAnimation: Animation? = null
    var slideDownAnimation: Animation? = null
    var isInAppPurched = false
    private lateinit var inAppBilling: InAppBilling


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Paper.init(this)
        val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.button_scale)
        animation.repeatCount = Animation.INFINITE
        animation.repeatMode = Animation.REVERSE
        binding.subscribeNow.startAnimation(animation)

        // define inapp billing here
        inAppBilling = InAppBilling(this, this);
        isInAppPurched = inAppBilling.hasUserBoughtInApp()


        setupBillingClient()
        slideUpAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_above)
        slideDownAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_down)
        binding.apply {
            if (Paper.book().read("inapp", false)!!) {
//                adView2.visibility = View.GONE
            } else {
                val adRequest = AdRequest.Builder().build()
//                adView2.loadAd(adRequest)
//                exitAdview.loadAd(adRequest)
                loadInterstitialAd()

            }
            ivHuman.setOnClickListener {
                mCurrentClickNumber = 1

                if (checkTurn()) {
                    openActivity(CategoryActivity::class.java) {
                        putString(MAIN_CATEGORY_NAME, getString(R.string.human))
                        putInt(MAIN_CATEGORY_LIST_SIZE, 10)

                    }
                }
            }
            ivAnimal.setOnClickListener {
                mCurrentClickNumber = 2

                if (checkTurn()) {
                    openActivity(CategoryActivity::class.java) {
                        putString(MAIN_CATEGORY_NAME, getString(R.string.animal))
                        putInt(MAIN_CATEGORY_LIST_SIZE, 8)

                    }
                }
            }
            ivMachine.setOnClickListener {
                mCurrentClickNumber = 3

                if (checkTurn()) {
                    openActivity(CategoryActivity::class.java) {
                        putString(MAIN_CATEGORY_NAME, getString(R.string.machine))
                        putInt(MAIN_CATEGORY_LIST_SIZE, 8)

                    }
                }


            }

            inappBtn.setOnClickListener {
                if (Paper.book().read("inapp", false)!!) {
                    Toast.makeText(
                        this@MainActivity,
                        "Already Purchased....",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    val params: SkuDetailsParams = SkuDetailsParams.newBuilder()
                        .setSkusList(Arrays.asList("prank_remove_ads"))
                        .setType(BillingClient.SkuType.INAPP)
                        .build()
                    billingClient?.querySkuDetailsAsync(
                        params, object : SkuDetailsResponseListener {


                            override fun onSkuDetailsResponse(
                                billingResult: BillingResult,
                                p1: List<SkuDetails>?
                            ) {
                                if (billingResult.getResponseCode() === BillingClient.BillingResponseCode.OK) {
                                    //                            Toast.makeText(MenuActivity.this, "" + billingResult.getResponseCode(), Toast.LENGTH_SHORT).show();
                                    val billingFlowParams: BillingFlowParams =
                                        BillingFlowParams.newBuilder()
                                            .setSkuDetails(p1!![0])
                                            .build()
                                    billingClient?.launchBillingFlow(
                                        this@MainActivity,
                                        billingFlowParams
                                    )
                                } else {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Cannot Remove ads",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }


                        })
                }
            }


        }
        binding.crossbtn.setOnClickListener {
            binding.inApp.visibility = View.GONE
        }
        binding.inappBtn.setOnClickListener {
            binding.inApp.bringToFront();
            binding.inApp.visibility = View.VISIBLE
        }
        if (!isInAppPurched) {
            CoroutineScope(Dispatchers.IO).launch {
                binding?.adView?.let { Banner.show(this@MainActivity, it, this@MainActivity) }
            }
        }
        if (isInAppPurched) {
            binding.inApp.visibility = View.GONE
            binding.inappBtn.visibility = View.GONE

        } else {
            binding.inApp.visibility = View.VISIBLE
            binding.inappBtn.visibility = View.VISIBLE
        }
        binding.subscribeNow.setOnClickListener {
            inAppBilling.purchase()
        }

        onBackPressedCallback(true) {
            if (binding.inApp.visibility == View.VISIBLE) {
                binding
                    .inApp.visibility = View.GONE
            } else {
                if (binding.exitLayout.visibility != View.VISIBLE) {
                    showExitDailog()
                }
            }

            binding.yesBtn.setOnClickListener {
                finishAffinity();
            }
            binding.Nobtn.setOnClickListener {
                binding.exitLayout.visibility = View.GONE
            }

//            } else {
//                hideExitDailog()
//            }

        }
    }


    private fun incrementInAdCounter() {
        Paper.book().write("adcounter", Paper.book().read("adcounter", 2)!!.plus(1))

    }

    private fun checkTurn(): Boolean {
        incrementInAdCounter()
        if (Paper.book().read("adcounter", 3)!! % 2 == 0 && !(Paper.book()
                .read("inapp", false)!!) && mInterstitialAd != null && !isInAppPurched
        ) {

            showAdLayout()
            return false
        }
        return true

    }

    fun showAdLayout() {
        binding.apply {
            adLayout.visibility = View.VISIBLE
            val delay = 2000L // 3 seconds
            adLayout.postDelayed({
                showInterstitial()
                adLayout.visibility = View.GONE
            }, delay)
        }

    }

    fun showInterstitial() {


        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    super.onAdFailedToShowFullScreenContent(p0)
                }

                override fun onAdShowedFullScreenContent() {
                    //Input your code here
                    super.onAdShowedFullScreenContent()
                }

                // When you exit the ad using the cancel button, the next activity is displayed.

                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    loadInterstitialAd()
                    when (mCurrentClickNumber) {
                        1 -> {
                            openActivity(CategoryActivity::class.java) {
                                putString(MAIN_CATEGORY_NAME, getString(R.string.human))
                                putInt(MAIN_CATEGORY_LIST_SIZE, 10)

                            }
                        }

                        2 -> {
                            openActivity(CategoryActivity::class.java) {
                                putString(MAIN_CATEGORY_NAME, getString(R.string.animal))
                                putInt(MAIN_CATEGORY_LIST_SIZE, 8)

                            }
                        }

                        3 -> {
                            openActivity(CategoryActivity::class.java) {
                                putString(MAIN_CATEGORY_NAME, getString(R.string.machine))
                                putInt(MAIN_CATEGORY_LIST_SIZE, 8)

                            }
                        }


                    }
                }

                override fun onAdImpression() {
                    // input your code here
                    super.onAdImpression()
                }

                // What will happen when the ad is clicked

                override fun onAdClicked() {
                    //Input your code here
                    super.onAdClicked()
                }
            }
            mInterstitialAd?.show(this)
        } else {

            when (mCurrentClickNumber) {
                1 -> {
                    openActivity(CategoryActivity::class.java) {
                        putString(MAIN_CATEGORY_NAME, getString(R.string.human))
                        putInt(MAIN_CATEGORY_LIST_SIZE, 10)

                    }
                }

                2 -> {
                    openActivity(CategoryActivity::class.java) {
                        putString(MAIN_CATEGORY_NAME, getString(R.string.animal))
                        putInt(MAIN_CATEGORY_LIST_SIZE, 8)

                    }
                }

                3 -> {
                    openActivity(CategoryActivity::class.java) {
                        putString(MAIN_CATEGORY_NAME, getString(R.string.machine))
                        putInt(MAIN_CATEGORY_LIST_SIZE, 8)

                    }
                }


            }
        }


    }

    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this, getString(R.string.interstitial_high_id), adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                    loadMediumeInterstitialAd();
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                }
            })


    }

    private fun loadMediumeInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this, getString(R.string.interstitial_medium_id), adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                    loadlowInterstitialAd()
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                }
            })


    }

    private fun loadlowInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this, getString(R.string.interstitial_low_id), adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                }
            })


    }

    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(this@MainActivity).enablePendingPurchases()
            .setListener { billingResult, list ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Toast.makeText(
                        this@MainActivity,
                        "Successfully Remove ads",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    Paper.book().write("inapp", true)
                    //    ShowRestarDailog()
                } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                    if (Paper.book().read("inapp", false)!!) {
                        Toast.makeText(
                            this@MainActivity,
                            "Already Purchased...",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Purchases Restored...",
                            Toast.LENGTH_LONG
                        ).show()
                        Paper.book().write("inapp", true)
                        // ShowRestarDailog()
                    }
                } else if (billingResult.responseCode == BillingClient.BillingResponseCode.BILLING_UNAVAILABLE) {
                    Toast.makeText(
                        this@MainActivity,
                        "Billing Unavailable..!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ERROR) {
                    Toast.makeText(this@MainActivity, "Billing Error..!", Toast.LENGTH_SHORT)
                        .show()
                } else if (billingResult.responseCode == BillingClient.BillingResponseCode.SERVICE_DISCONNECTED) {
                    Toast.makeText(
                        this@MainActivity,
                        "Service Disconnected..!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else if (billingResult.responseCode == BillingClient.BillingResponseCode.SERVICE_TIMEOUT) {
                    Toast.makeText(
                        this@MainActivity,
                        "Service Timeout..!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else if (billingResult.responseCode == BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE) {
                    Toast.makeText(
                        this@MainActivity,
                        "Service Unavailable..!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                    Toast.makeText(
                        this@MainActivity,
                        "Billing not Completed..!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(this@MainActivity, "Billing Error..!", Toast.LENGTH_SHORT)
                        .show()
                }
            }.build()
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                    //     Toast.makeText(MenuActivity.this, "" + billingResult.getResponseCode(), Toast.LENGTH_SHORT).show();
                }
            }

            override fun onBillingServiceDisconnected() {
                // this toast is showing when app closed!
                /*  Toast.makeText(
                    this@MenuActivity,
                    "You are disconnected from billing",
                    Toast.LENGTH_SHORT
                ).show()*/
            }
        })
    }

    private fun hideExitDailog() {
        binding.exitLayout.visibility = View.GONE
        binding.exitLayout.startAnimation(slideDownAnimation)
    }

    private fun showExitDailog() {
        if (!isInAppPurched) {
            binding.adLayout1.visibility = View.VISIBLE
            showBannerAd()
        } else {
            binding.adLayout1.visibility = View.GONE
        }
        showBannerAd()
        binding.exitLayout.bringToFront()
        binding.exitLayout.visibility = View.VISIBLE
        binding.exitLayout.startAnimation(slideUpAnimation)


    }

    fun exitApp(v: View?) {
        finish()
    }

    private fun showBannerAd() {
        val adRequest = AdRequest.Builder().build()
        val adView = AdView(this@MainActivity)
        adView.adUnitId = getString(R.string.banner_low_id)
//        adView.adUnitId = "ca-app-pub-3940256099942544/6300978111"
        adView.setAdSize(AdSize.MEDIUM_RECTANGLE)
        adView.loadAd(adRequest)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        binding.adLayout1.addView(adView, params)
        adView.visibility = View.VISIBLE
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.d("TAG", "onAdFailedToLoad: exit  ")

            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                Log.d("TAG", "onAdFailedToLoad: exit  ")
            }
        }


    }

    override fun onResume() {
        super.onResume()
        inAppBilling = InAppBilling(this, this);
        var isPurchase = inAppBilling.isCurrentpurchased();
        if (isPurchase) {
            val intent = Intent(this@MainActivity, SplashActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            isInAppPurched = inAppBilling.hasUserBoughtInApp();
            if (isInAppPurched) {
                binding?.adView?.visibility = View.GONE
                binding?.adLayout1?.visibility = View.GONE
                binding.inApp.visibility = View.GONE
                binding.inappBtn.visibility = View.GONE
            } else {

                binding.inappBtn.visibility = View.VISIBLE
                binding?.adView?.visibility = View.VISIBLE
                CoroutineScope(Dispatchers.IO).launch {
                    binding?.adView?.let { Banner.show(this@MainActivity, it, this@MainActivity) }
                }
            }
        }

    }


}