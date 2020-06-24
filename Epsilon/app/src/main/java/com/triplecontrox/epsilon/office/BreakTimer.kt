package com.triplecontrox.epsilon.office

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.*
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.triplecontrox.epsilon.AuthScreen
import com.triplecontrox.epsilon.R
import com.triplecontrox.epsilon.db.AppDatabase

class BreakTimer: Service() {
    private val channelID = 1300
    private lateinit var db: AppDatabase
    private lateinit var mNM: NotificationManager

    override fun onCreate() {
        startForeground(channelID, getNotification("Count Down").build())
        db = AppDatabase.getInstance(this)
    }
    override fun onBind(intent: Intent?): IBinder? { return null }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mNM = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val ticker = intent?.extras?.getLong("ticker")!!
        val office = intent.extras?.getString("office")!!
        var countText: String

        val countDownTimer = object : CountDownTimer(ticker,1000) {
            override fun onTick(timeLeft: Long) {
                val timeRemaining = "${(timeLeft / 1000) / 60} m : ${(timeLeft / 1000) % 60} s"
                countText = resources.getString(R.string.time_left).plus(timeRemaining)

                broadCastData(countText)
                showNotification(timeRemaining)
            }

            override fun onFinish() {
                countText = getString(R.string.break_over)
                broadCastData(countText)
                showNotification("your ${ticker / 60000} minutes break is over",
                    true)
                Thread{
                    db.officeDao().shouldVerify(office)
                }.start()
                stopSelf()
            }
        }
        countDownTimer.start()
        return START_NOT_STICKY
    }

    private fun broadCastData(data: String){
        Intent("changed time").putExtra("ticking", data).also {
            LocalBroadcastManager.getInstance(this).sendBroadcast(it)
        }
    }
    private fun showNotification(content: String, isFinished: Boolean = false){
        val notifyBuilder = getNotification(content)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel("break time", "Epsilon",
                NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "notifies user of break"
                mNM.createNotificationChannel(this)
            }
        }

        if (isFinished) {
            val notification =
                notifyBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND).build()

            notification.flags = Notification.FLAG_INSISTENT
            mNM.notify(channelID, notification)

            Handler(Looper.getMainLooper()).postDelayed({
                mNM.cancel(channelID)
                notification.flags = Notification.FLAG_ONLY_ALERT_ONCE
                mNM.notify(channelID, notification)
            }, 15000)
            return
        }

        mNM.notify(channelID, notifyBuilder.build())
    }
    private fun getNotification(content: String): NotificationCompat.Builder {
        val intent = Intent(this, AuthScreen::class.java)
        return NotificationCompat.Builder(this, "officeService")
            .setContentTitle("Break Time")
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_baseline_access_alarm_24)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(
                PendingIntent.getActivity(this, 0, intent, 0)
            )
    }
}