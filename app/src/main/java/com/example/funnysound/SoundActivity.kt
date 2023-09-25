package com.example.funnysound

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.bumptech.glide.Glide
import com.example.funnysound.Ad_Utils.InAppBilling
import com.example.funnysound.ads.Banner1
import com.example.funnysound.databinding.ActivitySoundBinding
import com.example.funnysound.utils.DbConstants
import com.example.funnysound.utils.getData
import com.example.funnysound.utils.onBackPressedCallback
import com.google.android.gms.ads.AdRequest
import io.paperdb.Paper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


class SoundActivity : AppCompatActivity(), OnCompletionListener,
    View.OnClickListener, OnTouchListener {
    lateinit var binding: ActivitySoundBinding
    var mediaPlayer: MediaPlayer? = null
    private var mainCatName: String? = null
    private var subCatName: String? = null
    private var isLoopEnabled: Boolean = false
    private var soundName: String? = null
    private var clickPosition: String? = null
    private var pictureName: String? = null
    lateinit var audioManager: AudioManager
    private val FORMAT = "%02d:%02d:%02d"

    var isInAppPurched = false
    private lateinit var inAppBilling: InAppBilling

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySoundBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Paper.init(this)

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        inAppBilling = InAppBilling(this, this);
        isInAppPurched = inAppBilling.hasUserBoughtInApp()

        mainCatName = intent.getData(DbConstants.MAIN_CATEGORY_NAME)
        subCatName = intent.getData(DbConstants.SUB_CATEGORY_NAME)
        clickPosition = intent.getData(DbConstants.SUB_CATEGORY_CLICK)
        pictureName = intent.getData(DbConstants.DETAIL_PICTURE)
        soundName = "${mainCatName}_${subCatName}_$clickPosition"
        val drawableResourceId: Int = resources.getIdentifier(pictureName, "drawable", packageName)
        val delayTimerList = resources.getStringArray(R.array.time_slabs)

        Glide.with(this).load(drawableResourceId).into(binding.ivMain)
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, delayTimerList
        )
        binding.spDelay.adapter = adapter
        binding.apply {
            if (Paper.book().read("inapp", false)!!) {
//                adView.visibility = View.GONE
            } else {
                val adRequest = AdRequest.Builder().build()
//                adView.loadAd(adRequest)
            }
            ivMain.setOnTouchListener(this@SoundActivity)
            ivPlay.setOnClickListener(this@SoundActivity)
            ivBack.setOnClickListener {
                stopSound()
                isLoopEnabled=false

                finish()
            }

            val upperString: String = subCatName!!.substring(0, 1).toUpperCase() + subCatName!!.substring(1).toLowerCase()+ " "
            tvSoundTitle.text=upperString+resources.getString(R.string.sounds)
            volumeSeekbar.max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            volumeSeekbar.progress = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            tvVolume.text = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toString()
            volumeSeekbar?.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)
                    tvVolume.text = progress.toString()


                }

                override fun onStartTrackingTouch(seek: SeekBar) {
                    // write custom code for progress is started
                }

                override fun onStopTrackingTouch(seek: SeekBar) {
                    // write custom code for progress is stopped

                }
            })

            loopSwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                isLoopEnabled = isChecked
            })
            spDelay.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?, position: Int, id: Long
                ) {
                    Log.d("selected", position.toString())
                    //   delayTimerList[position].substring(0, delayTimerList[position].length - 1)
                    if (position != 0) {
                        val delayTimeInMilli = delayTimerList[position].substring(
                            0,
                            delayTimerList[position].length - 1
                        ).toInt() * 1000
                        /*    Timer().schedule(delayTimeInMilli.toLong()) {
                            }*/

                        object : CountDownTimer(delayTimeInMilli.toLong(), 1000) {
                            override fun onTick(p0: Long) {
                                val remainTime = (p0 / 1000).toString()
                                //  binding.tvTimer.text=remainTime
                                binding.tvTimer.text = "" + String.format(
                                    FORMAT,
                                    TimeUnit.MILLISECONDS.toHours(p0),
                                    TimeUnit.MILLISECONDS.toMinutes(p0) - TimeUnit.HOURS.toMinutes(
                                        TimeUnit.MILLISECONDS.toHours(p0)
                                    ),
                                    TimeUnit.MILLISECONDS.toSeconds(p0) - TimeUnit.MINUTES.toSeconds(
                                        TimeUnit.MILLISECONDS.toMinutes(p0)
                                    )
                                )
                            }

                            override fun onFinish() {
                               if(!getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.DESTROYED)) {
                                   playSound(soundName)
                                   spDelay.setSelection(0);
                               }


                            }
                        }.start()
                    }

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // write code to perform some action
                }
            }
        }


        onBackPressedCallback(true) {
            stopSound()
            isLoopEnabled=false
            finish()
        }

        if (!isInAppPurched) {
            CoroutineScope(Dispatchers.IO).launch {
                binding?.adLayout?.let { Banner1.show(this@SoundActivity, it, this@SoundActivity) }
            }
        }
        else{
            binding.adLayout.visibility=View.GONE
        }
    }

    fun playSound(tuneName: String?) {
        if (mediaPlayer != null) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        }
        val id = resources.getIdentifier(tuneName, "raw", packageName)
        mediaPlayer = MediaPlayer.create(this, id)
        //  mediaPlayer?.isLooping = true
        binding.ivPlay.setImageResource(R.drawable.pause_icon)

        mediaPlayer?.setOnCompletionListener(this)
        mediaPlayer?.start()
    }

    override fun onCompletion(p0: MediaPlayer?) {
        if (!isLoopEnabled) {
            stopSound()
            binding.ivPlay.setImageResource(R.drawable.play_icon)
        } else {
            stopSound()
            playSound(soundName)


        }
    }

    fun stopSound() {
        if (mediaPlayer != null) {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }


    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.iv_play -> {
                playSound(soundName)
            }
        }
    }

    override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {

        when (view) {
            binding.ivMain -> {
                when (motionEvent?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        isLoopEnabled = true
                        playSound(soundName)
                    }
                    MotionEvent.ACTION_UP -> {
                        //   view.performClick()
                        isLoopEnabled = false

                        binding.ivPlay.setImageResource(R.drawable.play_icon)

                        stopSound()
                    }
                }
            }
            /*  //otherViewTouchListener
              previous -> {
              }*/
        }
        return true
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