package ro.pub.cs.systems.eim.practicaltest01

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {


    companion object {
        const val THRESHOLD = 10
    }
//    private lateinit var leftButton: Button
//    private lateinit var rightButton: Button
    private lateinit var leftView: TextView
    private lateinit var rightView: TextView
    private lateinit var openSecondaryActivityButton: Button
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        enableEdgeToEdge()

        openSecondaryActivityButton = findViewById(R.id.navigateButton)

        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            when (result.resultCode) {
                RESULT_OK -> Toast.makeText(this, "Result: Ok", Toast.LENGTH_SHORT).show()
                RESULT_CANCELED -> Toast.makeText(this, "Result: Cancel", Toast.LENGTH_SHORT).show()
            }
        }

        // Open Secondary Activity with press count
        openSecondaryActivityButton.setOnClickListener {
            val leftCount = leftView.text.toString().toInt()
            val rightCount = rightView.text.toString().toInt()
            val pressCount = leftCount + rightCount
            val intent = Intent(this, PracticalTest01SecondaryActivity::class.java).apply {
                putExtra("PRESS_COUNT", pressCount)
            }
            activityResultLauncher.launch(intent)
        }

        leftView = findViewById(R.id.leftTextView)
        rightView = findViewById(R.id.rightTextView)

        findViewById<Button>(R.id.leftButton).setOnClickListener {
            onLeftButtonClick(leftView)
            startServiceIfNeeded()
        }
        findViewById<Button>(R.id.rightButton).setOnClickListener {
            onRightButtonClick(rightView)
            startServiceIfNeeded()
        }

    }

    private fun onLeftButtonClick(leftView: TextView) {
//        val currentText = (leftView.text.toString().toInt() + 1).toString()
        (leftView.text.toString().toInt() + 1).toString().also { leftView.text = it }
    }

    private fun onRightButtonClick(rightView: TextView) {
//        val currentText = (rightView.text.toString().toInt() + 1).toString()
        (rightView.text.toString().toInt() + 1).toString().also { rightView.text = it }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the values of the TextViews to the Bundle
        outState.putInt("leftCount", leftView.text.toString().toInt())
        outState.putInt("rightCount", rightView.text.toString().toInt())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Restore the values from the Bundle
        val leftCount = savedInstanceState.getInt("leftCount", 0)
        val rightCount = savedInstanceState.getInt("rightCount", 0)
        leftView.text = leftCount.toString()
        rightView.text = rightCount.toString()
    }

    private fun startServiceIfNeeded() {
        val number1 = leftView.text.toString().toInt()
        val number2 = rightView.text.toString().toInt()
        val sum = number1 + number2

        if (sum > THRESHOLD) {
            val intent = Intent(this, PracticalTest01Service::class.java).apply {
                putExtra("NUMBER1", number1)
                putExtra("NUMBER2", number2)
            }
            startService(intent)
        } else {
            stopService(Intent(this, PracticalTest01Service::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, PracticalTest01Service::class.java))
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val timestamp = intent?.getStringExtra("TIMESTAMP")
            val arithmeticMean = intent?.getDoubleExtra("ARITHMETIC_MEAN", 0.0)
            val geometricMean = intent?.getDoubleExtra("GEOMETRIC_MEAN", 0.0)

            Toast.makeText(
                context,
                "Time: $timestamp, Arithmetic Mean: $arithmeticMean, Geometric Mean: $geometricMean",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        val filter = IntentFilter().apply {
            addAction(PracticalTest01Service.ACTION_ONE)
            addAction(PracticalTest01Service.ACTION_TWO)
            addAction(PracticalTest01Service.ACTION_THREE)
        }
        registerReceiver(broadcastReceiver, filter, RECEIVER_EXPORTED)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(broadcastReceiver)
    }

}