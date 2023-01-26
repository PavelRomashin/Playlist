package com.example.playlistmaker


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<Button>(R.id.backButton)
        val changeTheme = findViewById<SwitchCompat>(R.id.changeTheme)
        val shareButton = findViewById<Button>(R.id.shareButton)
        val supportButton = findViewById<Button>(R.id.supportButton)
        val userAgreementButton = findViewById<Button>(R.id.userAgreementButton)

        backButton.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }


        shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_VIEW)
            val shareLink = getString(R.string.shareLink)
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareLink)
            startActivity(shareIntent)
        }
        supportButton.setOnClickListener {
            val message = getString(R.string.supportMessage)
            val subject = getString(R.string.supportSubject)
            val email = getString(R.string.email)
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, email)
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            supportIntent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(supportIntent)
        }
        userAgreementButton.setOnClickListener {
            val browseIntent = Intent(Intent.ACTION_VIEW)
            val userAgreementPath = getString(R.string.userAgreementPath)
            browseIntent.data = Uri.parse(userAgreementPath)
            startActivity(browseIntent)
        }
        changeTheme.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                true -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

                false -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }


}
