/**
 * A DAO interface to allow access to the underlying Room
 * database
 * @author Chris Loftus
 * @version 1
 */
package uk.ac.aber.dcs.cs31620.faa.model

import androidx.lifecycle.LiveData
import androidx.room.*
import java.time.LocalDateTime

@Dao
interface CatDao {
    @Insert
    suspend fun insertSingleCat(cat: Cat)

    @Insert
    suspend fun insertMultipleCats(catsList: List<Cat>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCat(cat: Cat)

    @Delete
    suspend fun deleteCat(cat: Cat)

    @Query("DELETE FROM cats")
    suspend fun deleteAll()

    @Query("SELECT * FROM cats")
    fun getAllCats(): LiveData<List<Cat>>

    @Query(
        """SELECT * FROM cats WHERE breed = :breed AND 
              gender = :gender AND dob BETWEEN :startDate AND :endDate"""
    )
    fun getCats(
        breed: String, gender: Gender, startDate: LocalDateTime,
        endDate: LocalDateTime
    ): LiveData<List<Cat>>

    @Query(
        "SELECT * FROM cats WHERE breed = :breed"
    )
    fun getCatsByBreed(breed: String): LiveData<List<Cat>>

    @Query(
        "SELECT * FROM cats WHERE gender = :gender"
    )
    fun getCatsByGender(gender: Gender): LiveData<List<Cat>>

    @Query("SELECT * FROM cats WHERE dob BETWEEN :startDate AND :endDate")
    fun getCatsBornBetweenDates(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): LiveData<List<Cat>>

    @Query("SELECT * FROM cats WHERE breed = :breed AND gender = :gender")
    fun getCatsByBreedAndGender(breed: String, gender: String): LiveData<List<Cat>>

    @Query(
        """SELECT * FROM cats WHERE breed = :breed 
              AND dob BETWEEN :startDate AND :endDate"""
    )
    fun getCatsByBreedAndBornBetweenDates(
        breed: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): LiveData<List<Cat>>

    @Query(
        """SELECT * FROM cats WHERE gender = :gender 
              AND dob BETWEEN :startDate AND :endDate"""
    )
    fun getCatsByGenderAndBornBetweenDates(
        gender: Gender,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): LiveData<List<Cat>>

    @Query(
        """SELECT * FROM cats WHERE admission_date 
           BETWEEN :startDate AND :endDate"""
    )
    fun getCatsAdmittedBetweenDates(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): LiveData<List<Cat>>

    @Query(
        """SELECT * FROM cats WHERE admission_date 
           BETWEEN :startDate AND :endDate"""
    )
    fun getCatsAdmittedBetweenDatesSync(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Cat>


}