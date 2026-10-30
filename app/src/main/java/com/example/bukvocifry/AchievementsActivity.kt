package com.example.bukvocifry

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AchievementsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievements)

        val prefs: SharedPreferences = getSharedPreferences("bukvocifry", MODE_PRIVATE)

        val bestScore = prefs.getInt("bestScore", 0)
        val totalStars = prefs.getInt("totalStars", 0)
        val gamesPlayed = prefs.getInt("gamesPlayed", 0)

        findViewById<TextView>(R.id.tvBestScore).text = "Лучший счёт: $bestScore"
        findViewById<TextView>(R.id.tvTotalStars).text = "Всего звёзд: $totalStars"
        findViewById<TextView>(R.id.tvGamesPlayed).text = "Сыграно игр: $gamesPlayed"

        // Достижения
        findViewById<TextView>(R.id.tvAch1).text =
            if (totalStars >= 1) "✅ ⭐ Первая звезда" else "🔒 ⭐ Первая звезда"
        findViewById<TextView>(R.id.tvAch2).text =
            if (bestScore >= 10) "✅ 🔥 10 правильных подряд" else "🔒 🔥 10 правильных подряд"
        findViewById<TextView>(R.id.tvAch3).text =
            if (totalStars >= 10) "✅ 🏆 10 звёзд" else "🔒 🏆 10 звёзд"

        findViewById<Button>(R.id.btnBack).setOnClickListener { finish() }
    }
}