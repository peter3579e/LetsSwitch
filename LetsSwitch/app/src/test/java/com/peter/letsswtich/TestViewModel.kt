package com.peter.letsswtich

import android.content.Context
import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.net.toUri
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth
import com.peter.letsswtich.data.source.DefaultLetsSwitchRepository
import com.peter.letsswtich.data.source.DefaultLetsSwitchRepositoryTest
import com.peter.letsswtich.data.source.LetsSwitchRepository
import com.peter.letsswtich.map.event.EditEventViewModel
import getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class TestViewModel {


    @get:Rule
    var instantExecutor = InstantTaskExecutorRule()

    @Mock
    private lateinit var letsSwitchRepository: DefaultLetsSwitchRepositoryTest
    private lateinit var editEventViewModel: EditEventViewModel
//    lateinit var mockApplication: LetsSwtichApplication
    private val mockString = "1231231234"
//    private val testDispatcher = TestCoroutineDispatcher()
//    private val testScope = TestCoroutineScope(testDispatcher)

    @Before
    fun setUp(){
//        MockitoAnnotations.initMocks(this)
//        Dispatchers.setMain(testDispatcher)
//        LetsSwtichApplication.instance = mockApplication
        editEventViewModel = EditEventViewModel(letsSwitchRepository)
    }

//    @After
//    fun tearDown() {
//        testDispatcher.cleanupTestCoroutines()
//        testScope.cleanupTestCoroutines()
//    }

    @Test
    fun setPhoto(){
        editEventViewModel.setDate(mockString)
        val value = editEventViewModel.selectedDate.getOrAwaitValue()
        Truth.assertThat(value).isNotNull()
    }
}