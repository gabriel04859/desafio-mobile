package com.gabrielribeiro.desafio_mobile.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.gabrielribeiro.desafio_mobile.repositories.MovieRepositoryImplement
import com.gabrielribeiro.desafio_mobile.R
import com.gabrielribeiro.desafio_mobile.data.remote.model.MovieResponse
import com.gabrielribeiro.desafio_mobile.singletons.RetrofitInstance
import com.gabrielribeiro.desafio_mobile.utils.Resource
import com.gabrielribeiro.desafio_mobile.ui.viewmodels.SearchMovieViewModel
import kotlinx.android.synthetic.main.activity_search_movie.*
import kotlinx.android.synthetic.main.include_progress_layout.view.*

class SearchMovieActivity : AppCompatActivity() {
    private lateinit var searchAdapter : SearchAdapter
    private val listLock = Any()
    private var movieArray = mutableListOf<MovieResponse>()
    private var filteredMovieArray = mutableListOf<MovieResponse>()
    private lateinit var viewModel : SearchMovieViewModel

    companion object {
        fun newIntent(context: Context) = Intent(context, SearchMovieActivity::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_movie)




        searchAdapter = SearchAdapter()
        recycler_view_search.adapter = searchAdapter
        edit_text_search.requestFocus()
        setupHooks()
        //observerViewModel()

    }

    /*private fun observerViewModel() {
        viewModel.moviesResponse.observe(this, {
            when (it) {
                is Resource.Loading -> {
                    include_progress_indicator.progress_indicator.visibility = View.VISIBLE
                    Log.d("FeedFragment", "Loading:")
                }
                is Resource.Failure -> {
                    Log.d("FeedFragment", "Failure: ${it.message}")
                    include_progress_indicator.progress_indicator.visibility = View.GONE

                }
                is Resource.Success -> {
                    if (it.data != null) {
                        val premiereDateFiltered =
                            it.data.movieResponses.filter { movie -> movie.premiereDate != null }
                        setMovieList(premiereDateFiltered.sortedBy { movie -> movie.dateMillis })
                        include_progress_indicator.progress_indicator.visibility = View.GONE
                    }
                }
            }

        })
    } */

    private fun setMovieList(movieResponses: List<MovieResponse>) {
        synchronized(listLock) {
            movieArray = movieResponses as MutableList<MovieResponse>
        }
    }

    private fun setupHooks() {
        edit_text_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (char != null) {
                    if (char.isEmpty()) {
                        image_clear.visibility = View.INVISIBLE
                    } else {
                        image_clear.visibility = View.VISIBLE
                    }
                }
                updateMovieList()
            }

            override fun afterTextChanged(p0: Editable?) { }

        })

        image_clear.setOnClickListener {
            edit_text_search.setText("")
            filteredMovieArray.clear()
        }

        image_back.setOnClickListener {
            finish()
        }
    }

    private fun updateMovieList() {
        synchronized(listLock) {
            filteredMovieArray = movieArray.filter {
                it.title.contains(edit_text_search.text.toString(), ignoreCase = true)
            } as MutableList<MovieResponse>
           searchAdapter.submitList(filteredMovieArray)
        }
    }

}