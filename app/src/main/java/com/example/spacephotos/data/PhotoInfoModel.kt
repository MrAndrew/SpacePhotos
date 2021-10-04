package com.example.spacephotos.data

import java.util.*

data class PhotoInfoModel(
    val id: Int,
    val missionDayNumber: Int, //sol in api
    val cameraInfo: CameraInfoModel?,
    val earthDate: Date,
    val roverInfo: RoverInfoModel?,
    val imageURL: String
)