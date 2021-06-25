package it.ssplus.barbershop.model.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import it.ssplus.barbershop.model.entity.Expense

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(obj: Expense): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(obj: Expense)

    @Delete
    suspend fun delete(obj: Expense)

    @Delete
    suspend fun delete(list: List<Expense>)

    @Query("SELECT * FROM expense ORDER BY id_expense ASC")
    fun getAll(): LiveData<List<Expense>>

    @Query(
        """ SELECT * FROM expense WHERE 
        id_expense_category LIKE :query 
        OR amount LIKE :query
        OR date LIKE :query
        OR description LIKE :query
        ORDER BY id_expense ASC """
    )
    fun search(query: String): LiveData<List<Expense>>

    @Query("SELECT * FROM expense WHERE id_expense = :id_expense LIMIT 1")
    fun get(id_expense: Long): LiveData<Expense>

    @Query("SELECT * FROM expense WHERE id_expense = (SELECT MAX(id_expense) FROM expense)")
    fun lastInserted(): LiveData<Expense>
}