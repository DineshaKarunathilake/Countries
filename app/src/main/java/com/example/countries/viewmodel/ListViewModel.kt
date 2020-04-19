package com.example.countries.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.countries.di.DaggerApiComponent
import com.example.countries.model.CountriesService
import com.example.countries.model.Country
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ListViewModel: ViewModel() {

    @Inject
    lateinit var countriesService: CountriesService

    init {
        DaggerApiComponent.create().inject(this)
    }

    private val disposable = CompositeDisposable()          //since vm uses rxjava to get info, when vm is closed this connection needs to be closed

    val countries = MutableLiveData<List<Country>>()    //anyone subscribed to this will know when it is updated
    val countryLoadingError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    fun refresh() {
        fetchCountries()
    }

    private fun fetchCountries() {

        loading.value = true
        disposable.add(
            countriesService.getCountries()
                .subscribeOn(Schedulers.newThread())            //Dont use current thread
                .observeOn(AndroidSchedulers.mainThread())          //user sees main thread response handled in that
                .subscribeWith(object: DisposableSingleObserver<List<Country>>() {
                    override fun onSuccess(value: List<Country>?) {
                        countries.value = value
                        countryLoadingError.value = false
                        loading.value = false
                    }

                    override fun onError(e: Throwable?) {
                        countryLoadingError.value = true
                        loading.value = false

                    }          //functinality we will do when info is received

                } )
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}