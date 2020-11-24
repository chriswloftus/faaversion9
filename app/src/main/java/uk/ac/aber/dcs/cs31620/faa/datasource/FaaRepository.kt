/**
 * The Repository class acting as a facade to the
 * underlying Room database.
 * @author Chris Loftus
 * @version 1
 */
package uk.ac.aber.dcs.cs31620.faa.datasource

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.cs31620.faa.model.Cat
import uk.ac.aber.dcs.cs31620.faa.model.Gender
import java.time.LocalDateTime

class FaaRepository(application: Application) {
    private val catDao = Injection.getDatabase(application).catDao()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun insert(cat: Cat){
        coroutineScope.launch(Dispatchers.IO) {
            catDao.insertSingleCat(cat)
        }
    }

    fun insertMultipleCats(cats: List<Cat>){
        coroutineScope.launch(Dispatchers.IO) {
            catDao.insertMultipleCats(cats)
        }
    }

    fun getAllCats() = catDao.getAllCats()

    fun getCats(breed: String, gender: Gender, startDate: LocalDateTime, endDate: LocalDateTime)=
        catDao.getCats(breed, gender, startDate, endDate)

    fun getCatsByBreed(breed: String)=
        catDao.getCatsByBreed(breed)

    fun getCatsByGender(gender: Gender)=
        catDao.getCatsByGender(gender)

    fun getRecentCats(startDate: LocalDateTime, endDate: LocalDateTime)=
        catDao.getCatsAdmittedBetweenDates(startDate, endDate)

    fun getRecentCatsSync(startDate: LocalDateTime, endDate: LocalDateTime)=
        catDao.getCatsAdmittedBetweenDatesSync(startDate, endDate)

    fun getCatsBornBetweenDates(startDate: LocalDateTime, endDate: LocalDateTime)=
        catDao.getCatsBornBetweenDates(startDate, endDate)

    fun getCatsByBreedAndGender(breed: String, gender: String)=
        catDao.getCatsByBreedAndGender(breed, gender)

    fun getCatsByBreedAndBornBetweenDates(breed: String, startDate: LocalDateTime, endDate: LocalDateTime)=
        catDao.getCatsByBreedAndBornBetweenDates(breed, startDate, endDate)

    fun getCatsByGenderAndBornBetweenDates(gender: Gender, startDate: LocalDateTime, endDate: LocalDateTime)=
        catDao.getCatsByGenderAndBornBetweenDates(gender, startDate, endDate)

}