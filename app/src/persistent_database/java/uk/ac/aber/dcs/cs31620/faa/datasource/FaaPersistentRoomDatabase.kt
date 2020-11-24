/**
 * The Room database implementation. We link to the Entity
 * class to use to define the cats table.
 * We also say which converters to use when converting to and from
 * database types.
 * We also populate a new database using code.
 * @author Chris Loftus
 * @version 1
 */
package uk.ac.aber.dcs.cs31620.faa.datasource

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.cs31620.faa.model.Cat
import uk.ac.aber.dcs.cs31620.faa.model.CatDao
import uk.ac.aber.dcs.cs31620.faa.model.Gender
import uk.ac.aber.dcs.cs31620.faa.datasource.util.DateTimeConverter
import uk.ac.aber.dcs.cs31620.faa.datasource.util.GenderConverter
import java.time.LocalDateTime

@Database(entities = [Cat::class], version = 1)
@TypeConverters(DateTimeConverter::class, GenderConverter::class)
abstract class FaaPersistentRoomDatabase : RoomDatabase(), RoomDatabaseI {

    abstract override fun catDao(): CatDao

    override fun closeDb() {
        instance?.close()
        instance = null
    }

    companion object {
        private var instance: FaaPersistentRoomDatabase? = null
        private val coroutineScope = CoroutineScope(Dispatchers.Main)

        fun getDatabase(context: Context): FaaPersistentRoomDatabase? {
            synchronized(this) {
                if (instance == null) {
                    instance =
                        Room.databaseBuilder<FaaPersistentRoomDatabase>(
                            context.applicationContext,
                            FaaPersistentRoomDatabase::class.java,
                            "faa_database"
                        )
                            .allowMainThreadQueries() // Normally you would't but for testing
                            .addCallback(roomDatabaseCallback(context))
                            //.addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                            .build()
                }
                return instance!!
            }
        }

        private fun roomDatabaseCallback(context: Context): Callback {
            return object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // Run in background thread
                    coroutineScope.launch(Dispatchers.IO) {
                        populateDatabase(context, getDatabase(context)!!)
                    }
                }
            }
        }

        val MIGRATION_1_2 = object : Migration(1,2){
            override fun migrate(database: SupportSQLiteDatabase) {
                Log.d("migrate", "Doing a migrate from version 1 to 2")
                // This is where we make relevant database data changes,
                // or copy data from old table to a new table.
                // Deals with the migration from version 1 to version 2
            }
        }

        // This is one way to do this. Another is to provide a pre-built
        // database. See the reference slides. Another is to read DDL
        // and query command from a resource file. SupportSQLiteDatabase
        // has methods e.g. exeSql that can be used to run SQL and DDL
        // read as strings from resource files.
        private fun populateDatabase(context: Context, instance: FaaPersistentRoomDatabase) {
            val upToOneYear = LocalDateTime.now().minusDays(365 / 2) // Half year old
            val from1to2Years = LocalDateTime.now().minusDays(365 + (36 / 2)) // 1.5 years old
            val from2to5Years = LocalDateTime.now().minusDays(365 * 3) // 3 years old
            val over5Years = LocalDateTime.now().minusDays(365 * 10) // 10 years old
            val admissionsDate = LocalDateTime.now().minusDays(60) // Two months ago
            val veryRecentAdmission = LocalDateTime.now()

            val upToOneYearCat = Cat(
                0,
                "Tibs",
                Gender.MALE,
                "Moggie",
                "Lorem ipsum dolor sit amet, consectetur...",
                upToOneYear,
                veryRecentAdmission,
                "cat1.png"
            )

            val from1to2YearsCat = Cat(
                0,
                "Tibs",
                Gender.MALE,
                "Moggie",
                "Lorem ipsum dolor sit amet, consectetur...",
                from1to2Years,
                admissionsDate,
                "cat1.png"
            )

            val from2to5YearsCat = Cat(
                0,
                "Tibs",
                Gender.MALE,
                "Moggie",
                "Lorem ipsum dolor sit amet, consectetur...",
                from2to5Years,
                admissionsDate,
                "cat1.png"
            )

            val over5YearsCat = Cat(
                0,
                "Tibs",
                Gender.MALE,
                "Moggie",
                "Lorem ipsum dolor sit amet, consectetur...",
                over5Years,
                admissionsDate,
                "cat1.png"
            )

            val catList = mutableListOf(
                upToOneYearCat,
                upToOneYearCat,
                from1to2YearsCat,
                from1to2YearsCat,
                from2to5YearsCat,
                from2to5YearsCat,
                over5YearsCat,
                over5YearsCat,
                over5YearsCat
            )

            val dao = instance.catDao()
            dao.insertMultipleCats(catList)

        }
    }

}