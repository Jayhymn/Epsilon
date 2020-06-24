package com.triplecontrox.epsilon.db

import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.room.*
import com.triplecontrox.epsilon.dataClasses.DistinctOffice

@Entity(tableName = "office")
data class Office(
    @PrimaryKey(autoGenerate = true) val Id: Int,
    @ColumnInfo(name = "SiteName") val SiteName: String?,
    @ColumnInfo(name = "Assignee") val Assignee: String?,

    // coordinates of the site
    @ColumnInfo(name = "Latitude") val Latitude: Double,
    @ColumnInfo(name = "Longitude") val Longitude: Double,
    @ColumnInfo(name = "Clocked_In") val Clocked_In: String?,

    // tasks to b completed by app user
    @ColumnInfo(name = "DAILY_DEALS") val dailyDeals: String?,
    @ColumnInfo(name = "OTHER_DEALS") val otherDeals: String?,
    @ColumnInfo(name = "SUPPORT") val support: String?,

    @ColumnInfo(name = "BreakTime") var breakTime: Int = 0,
    @ColumnInfo(name = "verification_count") val verifyCount: Int = 1,
    @ColumnInfo(name = "should_verify") val shouldVerify: Int = 0
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(Id)
        parcel.writeString(Assignee)
        parcel.writeString(SiteName)
        parcel.writeDouble(Latitude)
        parcel.writeDouble(Longitude)
        parcel.writeString(Clocked_In)
        parcel.writeString(dailyDeals)
        parcel.writeString(otherDeals)
        parcel.writeString(support)
        parcel.writeInt(breakTime)
        parcel.writeInt(verifyCount)
        parcel.writeInt(shouldVerify)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Office> {
        override fun createFromParcel(parcel: Parcel): Office {
            return Office(parcel)
        }

        override fun newArray(size: Int): Array<Office?> {
            return arrayOfNulls(size)
        }
    }
}

data class Tasks(
    @ColumnInfo(name = "DAILY_DEALS") val dailyDeals: String?,
    @ColumnInfo(name = "OTHER_DEALS") val otherDeals: String?,
    @ColumnInfo(name = "SUPPORT") val support: String?
)

@Dao
interface OfficeDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOffice(office: Office)

    @Query("SELECT * FROM office WHERE Id = :uid")
    fun getOffices(uid: Int): LiveData<Office>

    @Query("SELECT DISTINCT Id, SiteName, Clocked_In, Assignee, verification_count, should_verify FROM office")
    fun getEachOffice(): LiveData<List<DistinctOffice>>

    @Query("SELECT COUNT(*) as Count_Entries, SiteName, (SELECT COUNT(*) FROM office WHERE(Clocked_In != \"00:00:00\")) as Count_Clocked_In FROM office")
    fun count(): CountModel

    @Query("SELECT verification_count from office WHERE (SiteName LIKE :siteName) LIMIT 1")
    fun verificationCount(siteName: String): Int

    @Query("UPDATE office SET BreakTime = :breakTime WHERE Id LIKE :uid")
    suspend fun addBreakTotal(breakTime: String?, uid: Int): Int

    @Query("DELETE FROM office WHERE Id LIKE :uid")
    suspend fun deleteOffice(uid: Int)

    @Query("UPDATE office SET DAILY_DEALS = \"YES\" WHERE Id = :uid")
    suspend fun updateDaily(uid: Int)

    @Query("UPDATE office SET OTHER_DEALS = \"YES\" WHERE Id = :uid")
    suspend fun updateOthers(uid: Int)

    @Query("UPDATE office SET SUPPORT = \"YES\" WHERE Id = :uid")
    suspend fun updateSupport(uid: Int)

    // re-verification queries
    @Query("UPDATE office SET verification_count = verification_count + 1, should_verify = 0")
    suspend fun verification()

    @Query("UPDATE office SET should_verify = 1 WHERE (SiteName LIKE :siteName)")
    fun shouldVerify(siteName: String)
}

