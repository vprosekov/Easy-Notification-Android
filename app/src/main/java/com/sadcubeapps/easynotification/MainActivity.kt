package com.sadcubeapps.easynotification

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.text.Editable
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.sadcubeapps.easynotification.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var mInterstitialAd: InterstitialAd

    private lateinit var binding: ActivityMainBinding

    lateinit var notificationManager : NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private var title = ""
    private var text = ""
    private var description = "userNotification"
    private val CHANNEL_ID = "EasyNotificationSadCubeApps"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        MobileAds.initialize(this, getString(R.string.admob_app_id))

        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = getString(R.string.interstial_ad_id)
        mInterstitialAd.loadAd(AdRequest.Builder().build())

        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdLoaded() {
                mInterstitialAd.show()
                super.onAdLoaded()
            }
        }

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        binding.button.setOnClickListener {
            binding.titleEditText.text = binding.titleEditText.text.toString().trim().toEditable()
            binding.textEditText.text = binding.textEditText.text.toString().trim().toEditable()
            if(binding.titleEditText.text.toString().isEmpty()) {
                vibrate(300)
                Toast.makeText(this, "Enter title of notification", Toast.LENGTH_LONG).show()
            }
            else if(binding.textEditText.text.toString().isEmpty()) {
                vibrate(300)
                Toast.makeText(this, "Enter text of notification", Toast.LENGTH_LONG).show()
            }
            else {
                title = binding.titleEditText.text.toString()
                text = binding.textEditText.text.toString()
                val intent = Intent(this, LauncherActivity::class.java)
                val pendingIntent =
                    PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    notificationChannel = NotificationChannel(
                        CHANNEL_ID,
                        description,
                        NotificationManager.IMPORTANCE_HIGH
                    )
                    notificationChannel.enableLights(true)
                    notificationChannel.lightColor = Color.GREEN
                    notificationChannel.enableVibration(false)
                    notificationManager.createNotificationChannel(notificationChannel)

                    builder = Notification.Builder(this, CHANNEL_ID)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setSmallIcon(R.drawable.icon)
                        .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.icon512))
                        .setContentIntent(pendingIntent)
                        .setStyle(Notification.BigTextStyle().bigText(text))
                } else {
                    builder = Notification.Builder(this)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setSmallIcon(R.drawable.icon)
                        .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.icon512))
                        .setContentIntent(pendingIntent)
                        .setStyle(Notification.BigTextStyle().bigText(text))
                }
                notificationManager.notify(Random.nextInt(1, 999999), builder.build())
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {

        outState.putString("title", binding.titleEditText.text.toString())
        outState.putString("text", binding.textEditText.text.toString())
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.titleEditText.text = savedInstanceState.getString("title").toString().toEditable()
        binding.textEditText.text = savedInstanceState.getString("text").toString().toEditable()
    }

    fun vibrate(duration: Int) {
        val vibs = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibs.vibrate(duration.toLong())
    }

    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)
}