package com.example.countries.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.countries.R
import com.example.countries.viewmodel.ListViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.io.LineNumberReader

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: ListViewModel
    private val countryListAdapter = CountryListAdapter(arrayListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this).get(ListViewModel::class.java)
        viewModel.refresh()

        countriesList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = countryListAdapter
        }

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
            viewModel.refresh()

        }

        observeViewModel()
    }


    private fun observeViewModel() {

        //Observes for changes in list
        viewModel.countries.observe(this, Observer { countries ->
            countries?.let {
                countriesList.visibility = View.VISIBLE
                countryListAdapter.updateCountries(it) }
        })

        viewModel.countryLoadingError.observe(this, Observer { isError ->
            isError?.let { list_error.visibility = if(it) View.VISIBLE else View.GONE }
        })

        viewModel.loading.observe(this, Observer { isLoading ->
            isLoading?.let { loading_view.visibility = if(it) View.VISIBLE else View.GONE
                if (it) {
                    list_error.visibility = View.GONE
                    countriesList.visibility = View.GONE
                }
            }

        })
    }


}
