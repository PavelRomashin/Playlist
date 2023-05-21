package com.example.playlistmaker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {


    private val trackLibrary = ArrayList<Track>()
    private val trackLibraryHistory = ArrayList<Track>()
    private var searchText: String = ""
    private val ITunesApiBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder().baseUrl(ITunesApiBaseUrl)
        .addConverterFactory(GsonConverterFactory.create()).build()

    private val ITunesService = retrofit.create(ITunesApi::class.java)


    val trackAdapter = TrackAdapter(trackLibrary)
    val trackAdapterHistory = TrackAdapter(trackLibraryHistory)

    private lateinit var errorImage: ImageView
    private lateinit var errorText: TextView
    private lateinit var updateButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewHistory: RecyclerView
    private lateinit var inputEditText: EditText
    private lateinit var searchHistory: SearchHistory
    private lateinit var sharedPrefsHistory: SharedPreferences
    private lateinit var clearSearchButton: Button
    private lateinit var layoutForHistory: View
    private lateinit var errorLayout: View
    private lateinit var progressBar: ProgressBar
    private lateinit var progressBarContainer: ViewGroup

    private var isClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())

    private val searchRunnable = Runnable { search() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val backButton = findViewById<Button>(R.id.backButton)
        val clearButton = findViewById<ImageView>(R.id.clearButton)
        announce()



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



        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            layoutForHistory.visibility =
                if (hasFocus && inputEditText.text.isEmpty() && searchHistory.read()
                        ?.isNotEmpty() == true
                ) View.VISIBLE else View.GONE
            clearSearchButton.visibility =
                if (hasFocus && inputEditText.text.isEmpty() && searchHistory.read()
                        ?.isNotEmpty() == true
                ) View.VISIBLE else View.GONE
            errorLayout.visibility =
                if (hasFocus && inputEditText.text.isEmpty()) View.GONE else View.VISIBLE
            recyclerView.visibility =
                if (hasFocus && inputEditText.text.isEmpty()) View.GONE else View.VISIBLE
        }


        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                searchDebounce()

                clearButton.visibility = clearButtonVisibility(s)
                layoutForHistory.visibility =
                    if (inputEditText.hasFocus() && s?.isEmpty() == true && searchHistory.read()
                            ?.isNotEmpty() == true
                    ) View.VISIBLE else View.GONE
                clearSearchButton.visibility =
                    if (inputEditText.hasFocus() && s?.isEmpty() == true && searchHistory.read()
                            ?.isNotEmpty() == true
                    ) View.VISIBLE else View.GONE
                errorLayout.visibility =
                    if (inputEditText.hasFocus() && s?.isEmpty() == true) View.GONE else View.VISIBLE
                recyclerView.visibility =
                    if (inputEditText.hasFocus() && s?.isEmpty() == true) View.GONE else View.VISIBLE

            }

            override fun afterTextChanged(s: Editable?) {

            }
        }

        inputEditText.addTextChangedListener(simpleTextWatcher)

        searchHistory.read()?.let { trackLibraryHistory.addAll(it) }
        recyclerViewHistory.adapter = trackAdapterHistory
        trackAdapterHistory.notifyDataSetChanged()

        trackAdapterHistory.itemClickListener = { track ->
            intentCreation(track)
        }


        trackAdapter.itemClickListener = { track ->
            if (trackLibraryHistory.contains(track)) {
                trackLibraryHistory.remove(track)
            }
            trackLibraryHistory.add(0, track)
            if (trackLibraryHistory.size == 10) {
                trackLibraryHistory.removeAt(9)
            }
            searchHistory.write(trackLibraryHistory)
            trackAdapterHistory.notifyDataSetChanged()

            intentCreation(track)
        }


        clearSearchButton.setOnClickListener {
            searchHistory.clearHistory()
            trackLibraryHistory.clear()
            layoutForHistory.visibility = View.GONE
            clearSearchButton.visibility = View.GONE
        }

    }

    private fun announce() {

        inputEditText = findViewById(R.id.inputEditText)
        errorImage = findViewById(R.id.errorImage)
        errorText = findViewById(R.id.errorText)
        updateButton = findViewById(R.id.buttonUpdate)
        recyclerViewHistory = findViewById(R.id.search_history_recycler_view)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.adapter = trackAdapter
        sharedPrefsHistory = getSharedPreferences(HISTORY_PREFERENCES, MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPrefsHistory)
        clearSearchButton = findViewById(R.id.clear_search_history)
        layoutForHistory = findViewById(R.id.search_history)
        errorLayout = findViewById(R.id.errorLayout)
        progressBar = findViewById(R.id.progress_Bar)
        progressBarContainer = findViewById(R.id.progress_Bar_Frame)
    }

    private fun intentCreation(track: Track) {
        if (clickDebounce()) {
            val playerIntent = Intent(this, PlayerActivity::class.java)
            playerIntent.putExtra(TRACK, Gson().toJson(track))
            startActivity(playerIntent)
        }
    }

    private fun search() {
        if (inputEditText.text.isNotEmpty()) {
            progressBarContainer.isVisible = true
            ITunesService.search(inputEditText.text.toString())
                .enqueue(object : Callback<TrackResponse> {
                    override fun onResponse(
                        call: Call<TrackResponse>, response: Response<TrackResponse>
                    ) {
                        progressBarContainer.isVisible = false
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
                        progressBarContainer.isVisible = false
                        showMessage(getString(R.string.networkError))
                    }

                })


        }
    }

    private fun showMessage(text: String) {
        if (text.isNotEmpty()) {
            errorImage.isVisible = true
            errorText.isVisible = true
            progressBarContainer.isVisible = false
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

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    companion object {
        const val SEARCH_ITEM = "SEARCH_ITEM"
        const val HISTORY_PREFERENCES = "HISTORY_PREFERENCES"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}




