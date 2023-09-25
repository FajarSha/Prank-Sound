//package com.example.funnysound.ads
//
//import android.app.Activity
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import androidx.activity.OnBackPressedCallback
//import androidx.appcompat.app.AppCompatActivity
//import com.example.funnysound.Ad_Utils.MyApplication
//import com.example.funnysound.R
//import com.example.funnysound.TutorialActivity
//
//import com.example.funnysound.databinding.ActivityInterstitialBinding
//import com.google.android.gms.ads.AdError
//import com.google.android.gms.ads.AdRequest
//import com.google.android.gms.ads.FullScreenContentCallback
//import com.google.android.gms.ads.LoadAdError
//import com.google.android.gms.ads.interstitial.InterstitialAd
//import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
//import java.util.Timer
//import java.util.TimerTask
//
//class InterstitialActivity : AppCompatActivity() {
//
//    private var binding: ActivityInterstitialBinding? = null
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityInterstitialBinding.inflate(layoutInflater)
//
//        setContentView(binding?.root)
//        loadAppOpen();
//      /*  Timer().schedule(object : TimerTask() {
//            override fun run() {
//                runOnUiThread {
//                    loadAppOpen();
////
//                }
//            }
//        }, 1500)*/
////        load()
//        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
//    }
//
//
//
//
//    private fun loadAppOpen() {
//        kotlin.runCatching {
//                val application = application
//
//                // If the application is not an instance of MyApplication, log an error message and
//                // start the MainActivity without showing the app open ad.
//                if (application !is MyApplication) {
//                    Log.e("LOG_TAG", "Failed to cast application to MyApplication.")
//                    setResult(Activity.RESULT_OK)
//                    finish()
//                    return
//                }
//
//                // Show the app open ad.
//                (application as MyApplication)
//                    .showAdIfAvailable(
//                        this@InterstitialActivity,
//                         MyApplication.OnShowAdCompleteListener {
//                             fun onShowAdComplete() {
//                                 Log.d("Result","activity result" );
//
//                                 setResult(Activity.RESULT_OK)
//                                finish()
//                            }
//                        })
//
//        }.onFailure {
//            setResult(Activity.RESULT_OK)
//            finish()
//        }
//    }
//
//
//  /*  private fun load() {
//        kotlin.runCatching {
//            val adRequest = AdRequest.Builder().build()
//            val unitId = getString(R.string.interstitial_high_id)
//            InterstitialAd.load(
//                this,
//                unitId,
//                adRequest,
//                object : InterstitialAdLoadCallback() {
//                    override fun onAdFailedToLoad(adError: LoadAdError) {
//                        Log.d("TAG", "onAdFailedToLoad: high  ")
//                        Mediumload();
//                    }
//
//                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
//                        kotlin.runCatching {
//                            interstitialAd.show(this@InterstitialActivity)
//                            interstitialAd.fullScreenContentCallback =
//                                object : FullScreenContentCallback() {
//                                    override fun onAdDismissedFullScreenContent() {
//                                        super.onAdDismissedFullScreenContent()
//                                        setResult(Activity.RESULT_OK)
//                                        finish()
//                                    }
//
//                                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
//                                        super.onAdFailedToShowFullScreenContent(p0)
//                                        setResult(Activity.RESULT_OK)
//                                        finish()
//                                    }
//                                }
//                        }.onFailure {
//                            setResult(Activity.RESULT_OK)
//                            finish()
//                        }
//                    }
//                })
//        }.onFailure {
//            setResult(Activity.RESULT_OK)
//            finish()
//        }
//    }
//
//    private fun Mediumload() {
//        kotlin.runCatching {
//            val adRequest = AdRequest.Builder().build()
//            val unitId = getString(R.string.interstitial_medium_id)
//            InterstitialAd.load(
//                this,
//                unitId,
//                adRequest,
//                object : InterstitialAdLoadCallback() {
//                    override fun onAdFailedToLoad(adError: LoadAdError) {
//                        Log.d("TAG", "onAdFailedToLoad: medium  ")
//                        lowload()
//                    }
//
//                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
//                        kotlin.runCatching {
//                            interstitialAd.show(this@InterstitialActivity)
//                            interstitialAd.fullScreenContentCallback =
//                                object : FullScreenContentCallback() {
//                                    override fun onAdDismissedFullScreenContent() {
//                                        super.onAdDismissedFullScreenContent()
//                                        setResult(Activity.RESULT_OK)
//                                        finish()
//                                    }
//
//                                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
//                                        super.onAdFailedToShowFullScreenContent(p0)
//                                        setResult(Activity.RESULT_OK)
//                                        finish()
//                                    }
//                                }
//                        }.onFailure {
//                            setResult(Activity.RESULT_OK)
//                            finish()
//                        }
//                    }
//                })
//        }.onFailure {
//            setResult(Activity.RESULT_OK)
//            finish()
//        }
//    }
//
//    private fun lowload() {
//        kotlin.runCatching {
//            val adRequest = AdRequest.Builder().build()
//            val unitId = getString(R.string.interstitial_low_id)
//            InterstitialAd.load(
//                this,
//                unitId,
//                adRequest,
//                object : InterstitialAdLoadCallback() {
//                    override fun onAdFailedToLoad(adError: LoadAdError) {
//                        setResult(Activity.RESULT_OK)
//                        finish()
//                    }
//
//                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
//                        kotlin.runCatching {
//                            interstitialAd.show(this@InterstitialActivity)
//                            interstitialAd.fullScreenContentCallback =
//                                object : FullScreenContentCallback() {
//                                    override fun onAdDismissedFullScreenContent() {
//                                        super.onAdDismissedFullScreenContent()
//                                        setResult(Activity.RESULT_OK)
//                                        finish()
//                                    }
//
//                                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
//                                        super.onAdFailedToShowFullScreenContent(p0)
//                                        setResult(Activity.RESULT_OK)
//                                        finish()
//                                    }
//                                }
//                        }.onFailure {
//                            setResult(Activity.RESULT_OK)
//                            finish()
//                        }
//                    }
//                })
//        }.onFailure {
//            setResult(Activity.RESULT_OK)
//            finish()
//        }
//    }
//*/
//    private val onBackPressedCallback: OnBackPressedCallback =
//        object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//
//            }
//        }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        finishAndRemoveTask()
//    }
//}