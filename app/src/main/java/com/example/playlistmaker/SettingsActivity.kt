package com.example.playlistmaker


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<Button>(R.id.backButton)
        val changeTheme = findViewById<SwitchCompat>(R.id.changeTheme)
        val shareButton = findViewById<View>(R.id.shareButton)
        val supportButton = findViewById<View>(R.id.supportButton)
        val userAgreementButton = findViewById<View>(R.id.userAgreementButton)

        backButton.setOnClickListener {
            this.finish()
        }


        shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_VIEW)
            val shareLink = getString(R.string.shareLink)
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareLink)
            startActivity(shareIntent)
        }
        supportButton.setOnClickListener {
            Intent(Intent.ACTION_SENDTO).apply {
                val message = getString(R.string.supportMessage)
                val subject = getString(R.string.supportSubject)
                val email = getString(R.string.email)
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, email)
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, message)
                startActivity(this)
            }
        }
        userAgreementButton.setOnClickListener {
            val browseIntent = Intent(Intent.ACTION_VIEW)
            val userAgreementPath = getString(R.string.userAgreementPath)
            browseIntent.data = Uri.parse(userAgreementPath)
            startActivity(browseIntent)
        }

        val sharedPrefs = getSharedPreferences(SETTING_PREFERENCES, MODE_PRIVATE)
        changeTheme.isChecked = sharedPrefs.getBoolean(DARK_THEME_KEY, false)
        changeTheme.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
            sharedPrefs.edit().putBoolean(DARK_THEME_KEY, checked).apply()
        }
    }


}
