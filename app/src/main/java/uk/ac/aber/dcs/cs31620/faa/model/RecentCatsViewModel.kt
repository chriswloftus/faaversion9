/**
 * The ViewModel for the HomeFragment. Caches LiveData object
 * for our recent cats list. Talks to the repository on behalf
 * of the UI.
 * @author Chris Loftus
 * @version 1
 */
package uk.ac.aber.dcs.cs31620.faa.model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import uk.ac.aber.dcs.cs31620.faa.datasource.FaaRepository
import java.time.LocalDateTime

const val NUM_DAYS_RECENT:Long = 30

class RecentCatsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FaaRepository = FaaRepository(application)
    var recentCats: LiveData<List<Cat>> = loadRecentCats()
        private set

    private fun loadRecentCats(): LiveData<List<Cat>> {
        // We actually make the present the future. This is a fudge to
        // make sure the LiveData query remains relevant to the admission
        // of new cats after the query has been made. If we don't do this
        // the LiveData will not emit onChange requests to its Observers.
        // Bug: we should force re-query when the real current date
        // changes to a new day, otherwise the recent cats period for
        // the LiveData query will stretch!
        val endDate = LocalDateTime.now().plusDays(365)
        val pastDate =  LocalDateTime.now().minusDays(NUM_DAYS_RECENT)

        val catList: LiveData<List<Cat>> = repository.getAllCats()
        Log.i("FAA", "Before forEach")
        catList.value?.forEach {
            Log.i("FAA", it.name)
        }

        return repository.getRecentCats(pastDate, endDate)
    }

    // This is called when the fragment using this ViewModel
    // is detached from its parent activity.
    // We don't have anything specific to tidy up.
    override fun onCleared() {
        super.onCleared()
    }
}