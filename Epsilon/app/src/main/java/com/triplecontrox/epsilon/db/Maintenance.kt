package com.triplecontrox.epsilon.db

import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(tableName = "maintenance")
data class Maintenance(
    @PrimaryKey (autoGenerate = true) val Id: Int = 0,

    @ColumnInfo(name = "Assignee") val Assignee: String?,
    @ColumnInfo(name = "SiteName") val SiteName: String?,
    @ColumnInfo(name = "Clocked_In") val Clocked_In: String?,

    // defines the equipment name
    @ColumnInfo(name = "Equipment_Code") val Equipment_Code: String?,
    @ColumnInfo(name = "Capacity") val Capacity: String?,
    @ColumnInfo(name = "Equipment_Id") val Equipment_Id: String?,

    // coordinates of the site
    @ColumnInfo(name = "Longitude") val Longitude: Double,
    @ColumnInfo(name = "Latitude") val Latitude: Double,

    // last three conditions of the equipment
    @ColumnInfo(name = "Cond1") val Cond1: String?,
    @ColumnInfo(name = "Cond2") val Cond2: String?,
    @ColumnInfo(name = "Cond3") val Cond3: String?,

    @ColumnInfo(name = "Status") val Status: String?,
    @ColumnInfo(name = "uploaded") val uploaded: String?,
    val eqpCode: String? = "$SiteName-$Equipment_Code-$Capacity-$Equipment_Id"
):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(Id)
        parcel.writeString(Assignee)
        parcel.writeString(SiteName)
        parcel.writeString(Clocked_In)
        parcel.writeString(Equipment_Code)
        parcel.writeString(Capacity)
        parcel.writeString(Equipment_Id)
        parcel.writeDouble(Longitude)
        parcel.writeDouble(Latitude)
        parcel.writeString(Cond1)
        parcel.writeString(Cond2)
        parcel.writeString(Cond3)
        parcel.writeString(Status)
        parcel.writeString(uploaded)
        parcel.writeString(eqpCode)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Maintenance> {
        override fun createFromParcel(parcel: Parcel): Maintenance {
            return Maintenance(parcel)
        }

        override fun newArray(size: Int): Array<Maintenance?> {
            return arrayOfNulls(size)
        }
    }

}

data class CountModel(
    @ColumnInfo(name = "Count_Entries") val hasNoEntries: Int?,
    @ColumnInfo(name = "SiteName") val SiteName: String?,
    @ColumnInfo(name = "Count_Clocked_In") val Count_Clocked_In: Int?
)

data class DistinctSite(
    @ColumnInfo(name = "Id") val Id: Int = 0,
    @ColumnInfo(name = "SiteName") val siteName: String,
    @ColumnInfo(name = "Clocked_In") val clockedIn: String,
    @ColumnInfo(name = "Assignee") val assignee: String
)

@Dao
interface MaintenanceDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMaintenance(maintenance: Array<Maintenance>)

    @Query("SELECT * FROM maintenance WHERE SiteName LIKE :siteName")
    fun getSites(siteName: String): LiveData<List<Maintenance>>

    @Query("SELECT DISTINCT Id, SiteName, Clocked_In, Assignee FROM maintenance")
    fun getEachSites(): LiveData<List<DistinctSite>>

    @Query("SELECT COUNT(*) as Count_Entries, SiteName, (SELECT COUNT(*) FROM maintenance WHERE(Clocked_In != \"00:00:00\")) as Count_Clocked_In FROM maintenance")
    fun count(): CountModel

    @Query("DELETE FROM maintenance WHERE SiteName LIKE :siteName")
    suspend fun deleteSite(siteName: String)

    @Query("UPDATE maintenance SET Clocked_In = '10:00:00' WHERE SiteName LIKE :siteName")
    suspend fun clockIn(siteName: String)

    @Query("UPDATE maintenance SET uploaded = 'YES' WHERE Id = :entry")
    suspend fun updateSiteForm(entry: Int?)

    @Query("UPDATE maintenance SET Status = 'YES' WHERE siteName LIKE :site AND Equipment_Code LIKE :code AND Capacity LIKE :capacity AND Equipment_Id = :id")
    suspend fun updateStatus(site: String?, code: String?, capacity: String?, id: String?)

}