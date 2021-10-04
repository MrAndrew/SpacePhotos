package com.example.spacephotos.data

import java.util.*

data class RoverInfoModel (
    val roverID: Int,
    val roverName: String,
    val earthLandingDate: Date,
    val earthLaunchDate: Date,
    val status: String
)
