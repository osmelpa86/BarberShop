package it.ssplus.barbershop.model.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import it.ssplus.barbershop.model.entity.Client
import it.ssplus.barbershop.model.entity.ExpenseCategory

@Dao
interface ExpenseCategoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(obj: ExpenseCategory): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(obj: ExpenseCategory)

    @Delete
    suspend fun delete(obj: ExpenseCategory)

    @Delete
    suspend fun delete(list: List<ExpenseCategory>)

    @Query("SELECT * FROM expense_category ORDER BY id_expense_category ASC")
    fun getAll(): LiveData<List<ExpenseCategory>>

    @Query(
        """ SELECT * FROM expense_category WHERE 
        name LIKE :query 
        OR description LIKE :query
        ORDER BY id_expense_category ASC """
    )
    fun search(query: String): LiveData<List<ExpenseCategory>>

    @Query("SELECT * FROM expense_category WHERE id_expense_category = :id_expense_category LIMIT 1")
    fun get(id_expense_category: Long): LiveData<ExpenseCategory>

    @Query("SELECT * FROM expense_category WHERE id_expense_category = (SELECT MAX(id_expense_category) FROM expense_category)")
    fun lastInserted(): LiveData<ExpenseCategory>

    @Query("SELECT name FROM expense_category ORDER BY id_expense_category ASC")
    fun getAllExpenseCategoryNames(): LiveData<List<String>>
}