package com.example.countries.model

import com.google.gson.annotations.SerializedName

data class Country(
    @SerializedName("name")     //Name in json
    val countryName: String?,
    @SerializedName("capital")
    val capital: String?,
    @SerializedName("flagPNG")
    val flag: String?
    ) {
}