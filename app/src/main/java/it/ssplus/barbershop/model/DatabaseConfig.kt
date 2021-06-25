package it.ssplus.barbershop.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import it.ssplus.barbershop.model.dao.*
import it.ssplus.barbershop.model.entity.*
import it.ssplus.barbershop.utils.Converters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities =
    [
        Client::class,
        Expense::class,
        ExpenseCategory::class,
        Reservation::class,
        Service::class,
        ServiceTypeService::class,
        Turn::class,
        TypeService::class
    ],
    version = 1, exportSchema = false
)
@TypeConverters(Converters::class)
abstract class DatabaseConfig : RoomDatabase() {

    //Imported Dao
    abstract fun clientDao(): ClientDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun expenseCategoryDao(): ExpenseCategoryDao
    abstract fun reservationDao(): ReservationDao
    abstract fun serviceDao(): ServiceDao
    abstract fun serviceTypeServiceDao(): ServiceTypeServiceDao
    abstract fun turnDao(): TurnDao
    abstract fun typeServiceDao(): TypeServiceDao

    companion object {
        @Volatile
        private var INSTANCE: DatabaseConfig? = null

        fun getDatabase(
            context: Context,
//            scope: CoroutineScope
        ): DatabaseConfig {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DatabaseConfig::class.java,
                    "barber"
                )
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    // Migration is not part of this codelab.
                    .fallbackToDestructiveMigration()
//                        .addCallback(DatabaseConfigCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        //Se usa para insertar por primera vez un juego de datos
        private class DatabaseConfigCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            /**
             * Override the onCreate method to populate the database.
             */
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // If you want to keep the data through app restarts,
                // comment out the following line.
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
//                        populateDatabase(database.wordDao())
                    }
                }
            }
        }
    }
}