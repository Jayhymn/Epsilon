package com.triplecontrox.epsilon.dataClasses

import androidx.room.ColumnInfo

data class DistinctOffice(
    @ColumnInfo(name = "Id") val Id: Int,
    @ColumnInfo(name = "SiteName") val siteName: String,
    @ColumnInfo(name = "Clocked_In") val clockedIn: String,
    @ColumnInfo(name = "Assignee") val assignee: String,
    @ColumnInfo(name = "verification_count") val verifyCount: Int = 0,
    @ColumnInfo(name = "should_verify") val shouldVerify: Int = 0
)