package com.example.bukvocifry

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    // UI элементы
    private lateinit var tvTask: TextView
    private lateinit var tvResult: TextView
    private lateinit var tvScore: TextView
    private lateinit var tvStars: TextView
    private lateinit var tvStreak: TextView
    private lateinit var btnOption1: Button
    private lateinit var btnOption2: Button
    private lateinit var btnOption3: Button
    private lateinit var progressTimer: ProgressBar

    // Логика
    private var score = 0
    private var stars = 0
    private var streak = 0
    private var correctAnswer = ""
    private var speedMode = false
    private var timer: CountDownTimer? = null

    // Данные
    private val letters = listOf("А","Б","В","Г","Д","Е","Ж","З","И","К")
    private val digits = listOf("1","2","3","4","5","6","7","8","9","10")
    private val learnedPairs = mutableSetOf<String>()

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences("bukvocifry", MODE_PRIVATE)

        // Инициализация View
        tvTask = findViewById(R.id.tvTask)
        tvResult = findViewById(R.id.tvResult)
        tvScore = findViewById(R.id.tvScore)
        tvStars = findViewById(R.id.tvStars)
        tvStreak = findViewById(R.id.tvStreak)
        btnOption1 = findViewById(R.id.btnOption1)
        btnOption2 = findViewById(R.id.btnOption2)
        btnOption3 = findViewById(R.id.btnOption3)
        progressTimer = findViewById(R.id.progressTimer)

        // Загрузка сохранённого прогресса
        stars = prefs.getInt("totalStars", 0)
        learnedPairs.addAll(prefs.getStringSet("learnedPairs", emptySet()) ?: emptySet())
        updateStarsUI()

        // Обработчики кнопок
        btnOption1.setOnClickListener { checkAnswer(btnOption1.text.toString()) }
        btnOption2.setOnClickListener { checkAnswer(btnOption2.text.toString()) }
        btnOption3.setOnClickListener { checkAnswer(btnOption3.text.toString()) }

        findViewById<Button>(R.id.btnRules).setOnClickListener {
            startActivity(Intent(this, RulesActivity::class.java))
        }
        findViewById<Button>(R.id.btnAbout).setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
        findViewById<Button>(R.id.btnAchievements).setOnClickListener {
            startActivity(Intent(this, AchievementsActivity::class.java))
        }

        // Режим на скорость
        findViewById<Button>(R.id.btnStartSpeed).setOnClickListener {
            speedMode = true
            score = 0; stars = 0; streak = 0
            updateStarsUI()
            tvScore.text = "Счёт: 0"
            tvStreak.text = "🔥 0"
            btnOption1.isEnabled = true
            btnOption2.isEnabled = true
            btnOption3.isEnabled = true
            generateTask()
        }

        generateTask()
    }

    // ---------- Генерация задания ----------
    private fun generateTask() {
        val isLetterToDigit = Random.nextBoolean()
        val options = mutableListOf<String>()

        if (isLetterToDigit) {
            val idx = Random.nextInt(letters.size)
            val letter = letters[idx]
            val digit = digits[idx]
            correctAnswer = digit
            tvTask.text = "Найди цифру: $letter"
            learnedPairs.add("$letter=$digit")
            options.add(digit)
            while (options.size < 3) {
                val d = digits.random()
                if (d !in options) options.add(d)
            }
        } else {
            val idx = Random.nextInt(digits.size)
            val letter = letters[idx]
            val digit = digits[idx]
            correctAnswer = letter
            tvTask.text = "Найди букву: $digit"
            learnedPairs.add("$letter=$digit")
            options.add(letter)
            while (options.size < 3) {
                val l = letters.random()
                if (l !in options) options.add(l)
            }
        }

        options.shuffle()
        btnOption1.text = options[0]
        btnOption2.text = options[1]
        btnOption3.text = options[2]

        tvResult.text = ""

        if (speedMode) startTimer()
    }

    // ---------- Проверка ответа ----------
    private fun checkAnswer(selected: String) {
        timer?.cancel()

        if (selected == correctAnswer) {
            tvResult.text = getString(R.string.result_correct)
            tvResult.setTextColor(ContextCompat.getColor(this, R.color.accent_green))
            score++
            streak++

            if (streak % 3 == 0) {
                stars++
                saveProgress()
            }
            tvScore.text = "Счёт: $score"
            updateStarsUI()
            tvStreak.text = "🔥 $streak"
        } else {
            tvResult.text = getString(R.string.result_wrong)
            tvResult.setTextColor(ContextCompat.getColor(this, R.color.accent_red))
            streak = 0
            tvStreak.text = "🔥 0"
        }

        generateTask()
    }

    // ---------- Таймер ----------
    private fun startTimer() {
        timer?.cancel()
        progressTimer.progress = 100
        progressTimer.progressTintList = ContextCompat.getColorStateList(this, R.color.timer_normal)

        timer = object : CountDownTimer(10000, 100) {
            override fun onTick(millisUntilFinished: Long) {
                val progress = (millisUntilFinished / 100).toInt()
                progressTimer.progress = progress
                if (progress < 30) {
                    progressTimer.progressTintList =
                        ContextCompat.getColorStateList(this@MainActivity, R.color.timer_warning)
                }
            }

            override fun onFinish() {
                speedMode = false
                tvResult.text = getString(R.string.result_timeout)
                tvResult.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.accent_red))
                showGameOverDialog()
            }
        }.start()
    }

    // ---------- Окно итогов игры ----------
    private fun showGameOverDialog() {
        btnOption1.isEnabled = false
        btnOption2.isEnabled = false
        btnOption3.isEnabled = false

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Время вышло! ⏰")
        builder.setMessage("Игра окончена.\n\nВаш счёт: $score\nЗвёзд получено: $stars\n\nХотите сыграть ещё раз?")
        builder.setCancelable(false)

        builder.setPositiveButton("Заново") { _, _ ->
            speedMode = true
            score = 0
            stars = 0
            streak = 0
            updateStarsUI()
            tvScore.text = "Счёт: 0"
            tvStreak.text = "🔥 0"
            btnOption1.isEnabled = true
            btnOption2.isEnabled = true
            btnOption3.isEnabled = true
            generateTask()
        }

        builder.setNegativeButton("В меню") { _, _ ->
            btnOption1.isEnabled = true
            btnOption2.isEnabled = true
            btnOption3.isEnabled = true
            speedMode = false
            score = 0
            stars = 0
            streak = 0
            updateStarsUI()
            tvScore.text = "Счёт: 0"
            tvStreak.text = "🔥 0"
            progressTimer.progress = 100
            generateTask()
        }

        builder.show()
    }

    // ---------- Сохранение ----------
    private fun saveProgress() {
        val editor = prefs.edit()
        val best = prefs.getInt("bestScore", 0)
        if (score > best) editor.putInt("bestScore", score)
        editor.putInt("totalStars", stars)
        editor.putStringSet("learnedPairs", learnedPairs)
        editor.putInt("gamesPlayed", prefs.getInt("gamesPlayed", 0) + 1)
        editor.apply()
    }

    private fun updateStarsUI() {
        tvStars.text = "⭐ $stars"
    }

    override fun onPause() {
        super.onPause()
        saveProgress()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}