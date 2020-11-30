package uk.ac.aber.dcs.cs31620.faa

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import uk.ac.aber.dcs.cs31620.faa.datasource.Injection
import uk.ac.aber.dcs.cs31620.faa.datasource.RoomDatabaseI
import uk.ac.aber.dcs.cs31620.faa.model.CatDao
import uk.ac.aber.dcs.cs31620.faa.util.LiveDataTestUtil
import uk.ac.aber.dcs.cs31620.faa.util.TestUtil
import java.lang.Exception
import java.time.LocalDateTime
import kotlin.jvm.Throws

@RunWith(AndroidJUnit4::class)
class InsertCatTest {
    // This is a JUnit Test Rules that swaps the background executor used by the Architecture
    // Components with one that executes synchronously instead.
    @JvmField @Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var catDao: CatDao
    private lateinit var db: RoomDatabaseI
    private val testUtil = TestUtil()

    @Before
    @Throws(Exception::class)
    fun createDb() {
        val context = InstrumentationRegistry.getTargetContext()
        db = Injection.getDatabase(context)
        catDao = db.catDao()
    }

    @After
    fun closeDb() {
        db.closeDb()
    }

    @Test
    fun onInsertingACat_checkThat_catWasInserted() {
        val cats = testUtil.createMaleRecent(1, 365)

        catDao.insertSingleCat(cats[0])

        val foundCats = catDao.getAllCats()
        assertEquals(1, LiveDataTestUtil.getValue(foundCats).size)
    }

    @Test
    fun onInsertingTwoCatsWithDifferentBreeds_checkThat_weCanFindByBreed() {
        val cats = testUtil.createMaleRecent(2, 365)
        val cat2 = cats[0]
        cats[1].breed = "TEST-BREED"

        catDao.insertMultipleCats(cats)

        val foundCats = catDao.getCatsByBreed("TEST-BREED")
        assertEquals(1, LiveDataTestUtil.getValue(foundCats).size)
    }

    @Test
    fun onInsertCatsOfDifferentAges_checkThat_weCanFindForDifferentAgeDateRanges() {
        val halfYearCats = testUtil.createMaleRecent(2, 365 / 2)
        val oneYearCats = testUtil.createMaleRecent(2, 365)
        val fiveYearCats = testUtil.createMaleRecent(2, 365 * 5)

        catDao.insertMultipleCats(halfYearCats)
        catDao.insertMultipleCats(oneYearCats)
        catDao.insertMultipleCats(fiveYearCats)

        // Up to one year cats (startDate, endDate)
        var foundCats = catDao.getCatsBornBetweenDates(
            LocalDateTime.now().minusDays(365 - 1),
            LocalDateTime.now()
        )
        assertEquals("Up to one year cats: failed", 2, LiveDataTestUtil.getValue(foundCats).size)

        // From one to two year cats
        foundCats = catDao.getCatsBornBetweenDates(
            LocalDateTime.now().minusDays(365*2 - 1),
            LocalDateTime.now().minusDays(365)
        )
        assertEquals("From one to two year cats: failed", 2, LiveDataTestUtil.getValue(foundCats).size)

        // Over 5 year cats
        foundCats = catDao.getCatsBornBetweenDates(
            LocalDateTime.now().minusDays(365*10),
            LocalDateTime.now().minusDays(365*5)
        )
        assertEquals("Over five year cats: failed", 2, LiveDataTestUtil.getValue(foundCats).size)

        // Make start and end dates exactly same as cat dob
        foundCats = catDao.getCatsBornBetweenDates(
            LocalDateTime.now().minusDays(365*5),
            LocalDateTime.now().minusDays(365*5)
        )
        assertEquals("Exactly five year cats: failed", 0, LiveDataTestUtil.getValue(foundCats).size)
    }

    // Add more database tests here....

}