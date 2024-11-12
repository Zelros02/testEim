package ro.pub.cs.systems.eim.practicaltest01

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.sqrt

class PracticalTest01Service : Service() {

    private val handler = Handler(Looper.getMainLooper())
    private val broadcastInterval = 100000L // 10 seconds
    private lateinit var broadcastRunnable: Runnable

    companion object {
        const val ACTION_ONE = "ro.pub.cs.systems.eim.practicaltest01.ACTION_ONE"
        const val ACTION_TWO = "ro.pub.cs.systems.eim.practicaltest01.ACTION_TWO"
        const val ACTION_THREE = "ro.pub.cs.systems.eim.practicaltest01.ACTION_THREE"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val number1 = intent?.getIntExtra("NUMBER1", 0) ?: 0
        val number2 = intent?.getIntExtra("NUMBER2", 0) ?: 0

        broadcastRunnable = object : Runnable {
            override fun run() {
                broadcastMessage(number1, number2)
                handler.postDelayed(this, broadcastInterval)
            }
        }
        handler.post(broadcastRunnable)
        return START_STICKY
    }

    private fun broadcastMessage(number1: Int, number2: Int) {
        // Calculate the arithmetic and geometric means
        val arithmeticMean = (number1 + number2) / 2.0
        val geometricMean = sqrt((number1 * number2).toDouble())
        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

        // Randomly select one of the three actions
        val actions = listOf(ACTION_ONE, ACTION_TWO, ACTION_THREE)
        val action = actions.random()

        // Create and send the broadcast
        val broadcastIntent = Intent(action).apply {
            putExtra("TIMESTAMP", timestamp)
            putExtra("ARITHMETIC_MEAN", arithmeticMean)
            putExtra("GEOMETRIC_MEAN", geometricMean)
        }
        sendBroadcast(broadcastIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop broadcasting when the service is destroyed
        handler.removeCallbacks(broadcastRunnable)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
