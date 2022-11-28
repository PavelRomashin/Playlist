package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val search = findViewById<Button>(R.id.search)
        val library = findViewById<Button>(R.id.library)
        val settings = findViewById<Button>(R.id.settings)
// Поиск
        val searchListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                val searchIntent = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(searchIntent)
            }
        }
        search.setOnClickListener(searchListener)
// Медиатека
        val libraryListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                val libraryIntent = Intent(this@MainActivity, LibraryActivity::class.java)
                startActivity(libraryIntent)
            }
        }
        library.setOnClickListener(libraryListener)
// Настройки
        val settingsListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                val settingsIntent = Intent(this@MainActivity, SettingsActivity::class.java)
                startActivity(settingsIntent)
            }
        }
        settings.setOnClickListener(settingsListener)

        search.setOnClickListener {
            val searchIntent = Intent(this, SearchActivity::class.java)
            startActivity(searchIntent)
        }
        library.setOnClickListener {
            val libraryIntent = Intent(this, LibraryActivity::class.java)
            startActivity(libraryIntent)
        }
        settings.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
    }
}