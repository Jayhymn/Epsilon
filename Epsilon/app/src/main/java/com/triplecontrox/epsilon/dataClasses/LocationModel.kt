package com.triplecontrox.epsilon.dataClasses

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class LocationModel(val time: MutableMap<String, String>, val latitude: Double, val longitude: Double, val address: String)
data class BreakFirebase(val time: MutableMap<String, String>, val breakLength: Int)
//val long: Long, val lat: Long,