package com.cradle.neptune.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.cradle.neptune.R
import com.cradle.neptune.view.ui.settings.SettingsActivity.Companion.makeLaunchIntent
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DashBoardActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)
        setupOnClickListner()
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true)
            actionBar.setDisplayUseLogoEnabled(true)
            actionBar.title = ""
        }
    }

    private fun setupOnClickListner() {
        val patientView =
            findViewById<View>(R.id.patientConstraintLayout)
        val patientCardView: CardView = patientView.findViewById(R.id.patientCardview)
        val patientImg = patientView.findViewById<ImageButton>(R.id.patientImg)
        val statView = findViewById<View>(R.id.statconstraintLayout)
        val statCardview: CardView = statView.findViewById(R.id.statCardView)
        val statImg = statView.findViewById<ImageButton>(R.id.statImg)
        val uploadCard =
            findViewById<View>(R.id.syncConstraintlayout)
        val syncCardview: CardView = uploadCard.findViewById(R.id.syncCardView)
        val syncImg = uploadCard.findViewById<ImageButton>(R.id.syncImg)
        val readingLayout =
            findViewById<View>(R.id.readingConstraintLayout)
        val readingCardView: CardView = readingLayout.findViewById(R.id.readingCardView)
        val readImg = readingLayout.findViewById<ImageButton>(R.id.readingImg)
        val helpButton =
            findViewById<FloatingActionButton>(R.id.fabHelpDashboard)
        readingCardView.setOnClickListener(this)
        readImg.setOnClickListener(this)
        syncCardview.setOnClickListener(this)
        syncImg.setOnClickListener(this)
        patientCardView.setOnClickListener(this)
        patientImg.setOnClickListener(this)
        statCardview.setOnClickListener(this)
        statImg.setOnClickListener(this)
        helpButton.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == R.id.action_settings) {
            val intent = makeLaunchIntent(this)
            startActivityForResult(
                intent,
                TabActivityBase.TAB_ACTIVITY_BASE_SETTINGS_DONE
            )
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.readingCardView, R.id.readingImg -> {
                val intent = ReadingActivity.makeIntentForNewReading(this@DashBoardActivity)
                startActivityForResult(intent, READING_ACTIVITY_DONE)
            }
            R.id.patientCardview, R.id.patientImg -> startActivity(PatientsActivity.makeIntent(this))
            R.id.syncCardView, R.id.syncImg -> startActivity(Intent(this, SyncActivity::class.java))
            R.id.fabHelpDashboard -> startActivity(Intent(this, EducationActivity::class.java))
            R.id.statCardView, R.id.statImg -> startActivity(
                Intent(
                    this,
                    StatsActivity::class.java
                )
            )
        }
    }

    companion object {
        const val READING_ACTIVITY_DONE = 12345
    }
}
