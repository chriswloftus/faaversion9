/**
 * The ViewModel for the CatsFragment. Caches LiveData object
 * for our list of cats. Talks to the repository on behalf
 * of the UI.
 * @author Chris Loftus
 * @version 1
 */
package uk.ac.aber.dcs.cs31620.faa.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import uk.ac.aber.dcs.cs31620.faa.R
import uk.ac.aber.dcs.cs31620.faa.datasource.FaaRepository
import java.time.LocalDateTime
import java.util.*

class CatsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FaaRepository = FaaRepository(application)
    var catList: LiveData<List<Cat>> = repository.getAllCats()
        private set

    private val ageConstraints = application.resources.getStringArray(R.array.age_range_array)
    private val anyAge = ageConstraints[0]
    private var ageConstraint = anyAge
    private val anyGender = application.resources.getStringArray(R.array.gender_array)[0]
    private var gender = anyGender
    private val anyBreed = application.resources.getStringArray(R.array.breed_array)[0]
    private var breed = anyBreed

    fun getCats(breed: String, gender: String, ageConstraint: String): LiveData<List<Cat>> {
        var startDate = LocalDateTime.now()
        var endDate = LocalDateTime.now()
        var changed = false

        // Did anything change since last time?
        if (this.breed != breed) {
            this.breed = breed
            changed = true
        }
        if (this.gender != gender) {
            this.gender = gender
            changed = true
        }
        if (this.ageConstraint != ageConstraint) {
            this.ageConstraint = ageConstraint
            changed = true
        }

        if (changed) {
            // We load again based on the values in the ivars.
            // We look for values that are defaults: those are the ones that
            // need excluding from the request, and determine which method
            // Dao overload to call.
            if (breed != anyBreed && gender == anyGender && ageConstraint == anyAge) {
                catList = repository.getCatsByBreed(breed)
            }
            else if(breed == anyBreed && gender != anyGender && ageConstraint == anyAge){
                catList = repository.getCatsByGender(Gender.valueOf(gender.toUpperCase(Locale.ROOT)))
            }
            else if(breed == anyBreed && gender == anyGender && ageConstraint != anyAge){
                startDate = getStartDate(ageConstraint)
                endDate = getEndDate(ageConstraint)
                catList = repository.getCatsBornBetweenDates(startDate, endDate)
            }
            else if(breed != anyBreed && gender != anyGender && ageConstraint == anyAge){
                catList = repository.getCatsByBreedAndGender(breed, gender)
            }
            else if(breed != anyBreed && gender == anyGender && ageConstraint != anyAge){
                startDate = getStartDate(ageConstraint)
                endDate = getEndDate(ageConstraint)
                catList = repository.getCatsByBreedAndBornBetweenDates(breed, startDate, endDate)
            }
            else if (breed == anyBreed && gender != anyGender && ageConstraint != anyAge){
                startDate = getStartDate(ageConstraint)
                endDate = getEndDate(ageConstraint)
                catList = repository.getCatsByGenderAndBornBetweenDates(Gender.valueOf(gender.toUpperCase(Locale.ROOT)), startDate, endDate)
            }
            else if(breed != anyBreed &&  gender != anyGender && ageConstraint != anyAge){
                startDate = getStartDate(ageConstraint)
                endDate = getEndDate(ageConstraint)
                catList = repository.getCats(breed, Gender.valueOf(gender.toUpperCase(Locale.ROOT)), startDate, endDate)
            } else {
                catList = repository.getAllCats()
            }
        }
        return catList
    }

    private fun getEndDate(ageConstraint: String): LocalDateTime =
        when (ageConstraint) {
            ageConstraints[1] -> // 0 - 1 year
                // End date will be now. Fudge time: we add year here (into the future)
                // to solve the issue of LiveData queries showing up to 1 year cats
                // and a brand new cat is registered that was born on today's date
                // and has a time slightly later than the time used in the query and so
                // the LiveData update does not happen
                LocalDateTime.now().plusDays(365)
            ageConstraints[2] -> // 1 - 2 years
                // End date is 1 year ago
                LocalDateTime.now().minusDays(364)
            ageConstraints[3] -> // 2 - 5 years
                // End date is 2 years ago
                LocalDateTime.now().minusDays(365 * 2 - 1)
            else -> // Older
                // End date is 5 years ago
                LocalDateTime.now().minusDays(365 * 5 - 1)
        }

    private fun getStartDate(ageConstraint: String): LocalDateTime =
        when (ageConstraint) {
            ageConstraints[1] -> // 0 - 1 year
                // Start date is 1 year ago
                LocalDateTime.now().minusDays(365)
            ageConstraints[2] -> // 1 - 2 years
                // Start date is 2 year ago
                LocalDateTime.now().minusDays(365 * 2)
            ageConstraints[3] -> // 2 - 5 years
                // Start date is 5 years ago
                LocalDateTime.now().minusDays(365 * 5)
            else -> // Start date is > 5 years ago
                // Just use a very large number
                LocalDateTime.now().minusDays(365 * 40 - 1)
        }

    // This is called when the fragment using this ViewModel
    // is detached from its parent activity.
    // We don't have anything specific to tidy up.
    override fun onCleared() {
        super.onCleared()
    }

}