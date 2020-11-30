package uk.ac.aber.dcs.cs31620.faa.util

import uk.ac.aber.dcs.cs31620.faa.model.Cat
import uk.ac.aber.dcs.cs31620.faa.model.Gender
import java.time.LocalDateTime

class TestUtil {
    var veryRecentAdmission: LocalDateTime = LocalDateTime.now()

    fun createMaleRecent(num: Int, days: Int):MutableList<Cat>{
        var cats = mutableListOf<Cat>()
        for (i in 0 until num){
            cats.add(Cat(
                0,
                "Tibs",
                Gender.MALE,
                "Moggie",
                "Lorem ipsum dolor sit amet, consectetur...",
                veryRecentAdmission.minusDays(days.toLong()),
                veryRecentAdmission,
                "cat1.png"
            ))
        }
        return cats
    }
}