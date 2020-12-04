package uk.ac.aber.dcs.cs31620.faa.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import uk.ac.aber.dcs.cs31620.faa.datasource.FaaRepository

class AddCatViewModel(application: Application) : AndroidViewModel(application)  {
    private val repository: FaaRepository = FaaRepository(application)

    fun insertCat(cat: Cat){
        repository.insert(cat)
    }
}