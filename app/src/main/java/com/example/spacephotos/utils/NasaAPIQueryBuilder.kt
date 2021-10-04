package com.example.spacephotos.utils

class NasaAPIQueryBuilder {

    //can add camera input when API adds functionality
    fun getMarsRoverPhotoListQueryURL(day: Int = 0, rover: String = "curiosity"): String {
        return "https://api.nasa.gov/mars-photos/api/v1/rovers/${rover}/photos?sol=${day}&api_key=AXVBRcGzFZijkOtgARBUwe1BIuBnCFfDc0YfipQd"
    }

}