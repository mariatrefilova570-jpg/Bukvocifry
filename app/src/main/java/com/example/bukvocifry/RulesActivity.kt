package com.example.bukvocifry

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class RulesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rules)

        findViewById<Button>(R.id.btnBack).setOnClickListener { finish() }
    }
}