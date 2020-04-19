package com.example.countries

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.countries.model.CountriesService
import com.example.countries.model.Country
import com.example.countries.viewmodel.ListViewModel
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.Disposable
import io.reactivex.internal.schedulers.ExecutorScheduler
import io.reactivex.plugins.RxJavaPlugins
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

class ListViewModelTest {

    @get:Rule
    var rule =  InstantTaskExecutorRule()

    @Mock
    lateinit var countriesService: CountriesService

    @InjectMocks
    var listViewModel = ListViewModel()

    private var testSingle: Single<List<Country>>? = null

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }


    @Before
    fun setUpRxSchedulers() {
        val immediate = object : Scheduler() {
            override fun scheduleDirect(run: Runnable?, delay: Long, unit: TimeUnit?): Disposable {
                return super.scheduleDirect(run, 0, unit)
            }

            override fun createWorker(): Worker {
                return ExecutorScheduler.ExecutorWorker(Executor { it.run() })
            }
        }

        RxJavaPlugins.setInitIoSchedulerHandler { scheduler -> immediate}
        RxJavaPlugins.setInitComputationSchedulerHandler { scheduler -> immediate}
        RxJavaPlugins.setInitNewThreadSchedulerHandler { scheduler -> immediate}
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler -> immediate}
    }

    @Test
    fun getCountriesSuccess() {
        val country = Country("CountryA", "capitol", "url")
        val list = arrayListOf(country)
        testSingle = Single.just(list)
        `when`(countriesService.getCountries()).thenReturn(testSingle)

        listViewModel.refresh()

        Assert.assertEquals (1, listViewModel.countries.value?.size)
        Assert.assertEquals(false, listViewModel.countryLoadingError.value)
        Assert.assertEquals(false, listViewModel.loading.value)

    }

    @Test
    fun getCountriesFailed() {

        testSingle = Single.error(Throwable())
        `when`(countriesService.getCountries()).thenReturn(testSingle)

        listViewModel.refresh()

        Assert.assertEquals(true, listViewModel.countryLoadingError.value)
        Assert.assertEquals(false, listViewModel.loading.value)
    }




}