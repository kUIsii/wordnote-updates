package com.wordnote.app.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.wordnote.app.R
import com.wordnote.app.util.compatOverridePendingTransition
import com.wordnote.app.util.compatOverridePendingTransitionClose

class MoreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more)

        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
            compatOverridePendingTransitionClose(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        findViewById<android.view.View>(R.id.calendarButton).setOnClickListener {
            startActivity(Intent(this, CalendarViewActivity::class.java))
            compatOverridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        findViewById<android.view.View>(R.id.statisticsButton).setOnClickListener {
            startActivity(Intent(this, StatisticsActivity::class.java))
            compatOverridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        findViewById<android.view.View>(R.id.quizButton).setOnClickListener {
            startActivity(Intent(this, QuizSetupActivity::class.java))
            compatOverridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        findViewById<android.view.View>(R.id.sentenceButton).setOnClickListener {
            startActivity(Intent(this, SentenceListActivity::class.java))
            compatOverridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        findViewById<android.view.View>(R.id.categoryButton).setOnClickListener {
            startActivity(Intent(this, CategoryActivity::class.java))
            compatOverridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }
}
