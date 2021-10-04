package com.example.spacephotos.ui.photos

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ActivityScenario.launch
import com.example.spacephotos.data.CameraInfoModel
import com.example.spacephotos.data.PhotoInfoModel
import com.example.spacephotos.data.RoverInfoModel
import junit.framework.TestCase
import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.*
import java.util.*

@ExperimentalCoroutinesApi
class PhotosViewModelTest: TestCase() {

    val viewModel = PhotosViewModel()
    val testDummyPhoto = PhotoInfoModel(
        123,
        111,
        CameraInfoModel(
            22,
            "MAST",
            5,
            "Mast Camera"
        ),
        Date(2012, 11, 28, 0, 0, 0),
        RoverInfoModel(
            5,
        "Curiosity",
            Date(2012, 8, 6, 0, 0, 0),
            Date(2012, 11, 26, 0, 0, 0),
            "active"
        ),
        "https://mars.nasa.gov/msl-raw-images/msss/00111/mcam/0111ML0006900050103668E01_DXXX.jpg",
    )

    @Test
    fun testSelectPhoto() {
        GlobalScope.launch {
            viewModel.selectPhoto(testDummyPhoto)
            delay(2500)
            assertNotNull(viewModel.selectedPhoto.value)
            assertEquals(viewModel.selectedPhoto.value, testDummyPhoto)
        }
    }

    @Test
    fun testLoadPhotos() {
        GlobalScope.launch {
            viewModel.launchDataLoad()
            delay(3000)
            assertNotNull(viewModel.photos.value)
            assert(viewModel.photos.value?.size ?: 0 > 0)
        }
    }

}
