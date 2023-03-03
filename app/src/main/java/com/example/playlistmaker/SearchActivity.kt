package com.example.playlistmaker

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {
    private val trackLibrary = ArrayList<Track>()
    private var searchText: String = ""
    private val ITunesApiBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder().baseUrl(ITunesApiBaseUrl)
        .addConverterFactory(GsonConverterFactory.create()).build()

    private val ITunesService = retrofit.create(ITunesApi::class.java)

    val trackAdapter = TrackAdapter(trackLibrary)

    private lateinit var errorImage: ImageView
    private lateinit var errorText: TextView
    private lateinit var updateButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var inputEditText: EditText

    companion object {
        const val SEARCH_ITEM = "SEARCH_ITEM"
        const val ERROR_ITEM = "ERROR_ITEM"
        const val TRACKS ="TRACKS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val backButton = findViewById<Button>(R.id.backButton)
        val clearButton = findViewById<ImageView>(R.id.clearButton)
        inputEditText = findViewById(R.id.inputEditText)
        errorImage = findViewById(R.id.errorImage)
        errorText = findViewById(R.id.errorText)
        updateButton = findViewById(R.id.buttonUpdate)

        backButton.setOnClickListener {
            this.finish()
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            hideKeyboard()
            errorImage.isVisible = false
            errorText.isVisible = false
            updateButton.isVisible = false
            trackLibrary.clear()
            trackAdapter.notifyDataSetChanged()
        }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.adapter = trackAdapter

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // ВЫПОЛНЯЙТЕ ПОИСКОВЫЙ ЗАПРОС ЗДЕСЬ
                search()
                true
            }
            false
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                clearButton.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }

        inputEditText.addTextChangedListener(simpleTextWatcher)

    }

    private fun search() {
        if (inputEditText.text.isNotEmpty()) {
            ITunesService.search(inputEditText.text.toString())
                .enqueue(object : Callback<TrackResponse> {
                    override fun onResponse(
                        call: Call<TrackResponse>, response: Response<TrackResponse>
                    ) {
                        if (response.code() == 200) {
                            trackLibrary.clear()
                            if (response.body()?.results?.isNotEmpty() == true) {
                                trackLibrary.addAll(response.body()?.results!!)
                                trackAdapter.notifyDataSetChanged()
                                recyclerView.isVisible = true
                            } else {
                                showMessage(getString(R.string.notFound))
                            }
                        } else {
                            showMessage(
                                getString(R.string.networkError)
                            )
                        }
                    }

                    override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                        showMessage(getString(R.string.networkError))
                    }

                })


        }
    }

    private fun showMessage(text: String) {
        if (text.isNotEmpty()) {
            errorImage.isVisible = true
            errorText.isVisible = true
            trackLibrary.clear()
            trackAdapter.notifyDataSetChanged()
            recyclerView.isVisible = false

            if (text == getString(R.string.notFound)) {
                updateButton.isVisible = false
                errorImage.setImageResource(R.drawable.not_found)
                errorText.text = text
            } else {
                updateButton.isVisible = true
                errorImage.setImageResource(R.drawable.network_error)
                errorText.text = text
                updateButton.setOnClickListener { search() }
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_ITEM, searchText)


    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(SEARCH_ITEM, "SEARCH_ITEM")


    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}
