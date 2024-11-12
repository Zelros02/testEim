package ro.pub.cs.systems.eim.practicaltest01

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PracticalTest01SecondaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practical_test01_secondary)

        val pressCountTextView = findViewById<TextView>(R.id.pressCountTextView)
        val pressCount = intent.getIntExtra("PRESS_COUNT", 0)
        pressCountTextView.text = "Button Press Count: $pressCount"

        // Ok button returns RESULT_OK
        findViewById<Button>(R.id.okButton).setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }

        // Cancel button returns RESULT_CANCELED
        findViewById<Button>(R.id.cancelButton).setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }
}