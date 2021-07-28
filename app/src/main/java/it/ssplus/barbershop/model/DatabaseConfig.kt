package it.ssplus.barbershop.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import it.ssplus.barbershop.model.dao.*
import it.ssplus.barbershop.model.entity.*
import it.ssplus.barbershop.utils.Converters

@Database(
    entities =
    [
        Client::class,
        Expense::class,
        ExpenseCategory::class,
        Reservation::class,
        Service::class,
        ReservationService::class,
        Turn::class
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
    abstract fun reservationServiceDao(): ReservationServiceDao
    abstract fun turnDao(): TurnDao

    companion object {
        @Volatile
        private var INSTANCE: DatabaseConfig? = null

        fun getDatabase(
            context: Context,
        ): DatabaseConfig {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DatabaseConfig::class.java,
                    "barber"
                )
                    .fallbackToDestructiveMigration()
//                        .addCallback(DatabaseConfigCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

    }
}