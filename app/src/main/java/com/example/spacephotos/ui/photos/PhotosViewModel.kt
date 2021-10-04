package com.example.spacephotos.ui.photos

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spacephotos.data.CameraInfoModel
import com.example.spacephotos.data.PhotoInfoModel
import com.example.spacephotos.data.RoverInfoModel
import com.example.spacephotos.utils.ApiKeys
import com.example.spacephotos.utils.PhotoCollections
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.lang.StringBuilder
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList
import java.io.*

class PhotosViewModel(): ViewModel() {

    val TAG = this::class.simpleName

    var photos = MutableLiveData<ArrayList<PhotoInfoModel>>()
    var selectedPhoto = MutableLiveData<PhotoInfoModel>()

    //for all coroutines started by this ViewModel
    private val viewModelJob = SupervisorJob()
    //main scope for all coroutines launched by this MainViewModel
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    //Heavy operation that cannot be done in the Main Thread
    fun launchDataLoad() {
        Log.d(TAG, "launchDataLoad()")
        uiScope.launch {
            val photoObjectList = loadNasaPhotos(PhotoCollections.MARS_ROVER.type)
            // Modify UI
            photos.value = photoObjectList
        }
    }

    private fun loadMediaListFromUrlString(sUrl: String): JSONArray? {
        Log.d(TAG, "url: $sUrl")
        var dlJsonString: String? = null
        try {

            val url = URL(sUrl)
            val `in` = BufferedReader(
                InputStreamReader(url.openStream())
            )
            val responseStrBuilder = StringBuilder()
            var inputLine: String?
            while (`in`.readLine().also { inputLine = it } != null) responseStrBuilder.append(
                inputLine
            )
            `in`.close()
            dlJsonString = responseStrBuilder.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val resultsObj = JSONObject(dlJsonString ?: "")
        if (!resultsObj.has("photos")) {
            return null
        }
        val photosJsonArray = resultsObj.getJSONArray("photos")
        return photosJsonArray
    }

    private fun jsonArrayToObjectArrayList(jsonArray: JSONArray?): ArrayList<PhotoInfoModel> {
        val onlinePhotos = ArrayList<PhotoInfoModel>()
        if(jsonArray != null) {
            try {
                for(a in 0 until  jsonArray.length()) {
                    //this for loop iterates through the retrieved arraylist
                    val obj = jsonArray.getJSONObject(a)

                    //retrieve specific variables
                    val photoID = obj.getInt("id")
                    val missionDayNumber = obj.getInt("sol")
                    val cameraInfoJsonObj = obj.getJSONObject("camera")
                    val earthDateString = obj.getString("earth_date") //todo set to calendar object from string like "2021-01-13"
                    val earthDate = Date(yearMonthDayStringToCalendar(earthDateString).timeInMillis)
                    val roverInfoJsonObj = obj.getJSONObject("rover")
                    val imageURL = obj.getString("img_src")

                    Log.d(TAG, "fetchOnlineData(), photoID: $photoID")
                    val cameraInfo = extractCameraInfo(cameraInfoJsonObj)
                    val roverInfo = extractRoverInfo(roverInfoJsonObj)
                    val photo = PhotoInfoModel(photoID, missionDayNumber, cameraInfo, earthDate, roverInfo, imageURL)
                    onlinePhotos.add(photo) //adding Post objects to an arraylist
                }
            } catch (error: Error) {
                Log.d(TAG, "error: $error")
            }
        }
        return onlinePhotos
    }

    private fun yearMonthDayStringToCalendar(dateString: String): Calendar {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
        val year = dateString.split("-")[0].toInt()
        val month = dateString.split("-")[1].toInt()
        val date = dateString.split("-")[2].toInt()
        cal.set(year, month, date)
        return cal
    }

    private fun extractRoverInfo(roverJson: JSONObject): RoverInfoModel {
        val roverID = roverJson.getInt("id")
        val roverName = roverJson.getString("name")
        val earthLandingDate = Date(yearMonthDayStringToCalendar(roverJson.getString("landing_date")).timeInMillis)
        val earthLaunchDate = Date(yearMonthDayStringToCalendar(roverJson.getString("launch_date")).timeInMillis)
        val status = roverJson.getString("status")
        return RoverInfoModel(roverID, roverName, earthLandingDate, earthLaunchDate, status)
    }

    private fun extractCameraInfo(cameraJson: JSONObject): CameraInfoModel {
        val cameraID = cameraJson.getInt("id")
        val shortName = cameraJson.getString("name")
        val roverID = cameraJson.getInt("rover_id")
        val fullName = cameraJson.getString("full_name")
        return CameraInfoModel(cameraID, shortName, roverID, fullName)
    }

    fun selectPhoto(photoInfoModel: PhotoInfoModel) {
        Log.d(TAG, "selectPhoto() photo: ${photoInfoModel}")
        selectedPhoto.value = photoInfoModel
    }

    suspend fun loadNasaPhotos(type: String) = withContext(Dispatchers.Default) {
        Log.d(TAG, "loadNasaPhoto2s()")
        val queryUrlString = when (type) {
            PhotoCollections.MARS_ROVER.type -> "https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?sol=111&api_key=${ApiKeys.nasa_api_key}"
            else -> "https://api.nasa.gov/planetary/apod?api_key=${ApiKeys.nasa_api_key} "
        }
        val jsonArray = loadMediaListFromUrlString(queryUrlString)
        return@withContext jsonArrayToObjectArrayList(jsonArray)
    }

    //Cancel all coroutines when the ViewModel is cleared
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}