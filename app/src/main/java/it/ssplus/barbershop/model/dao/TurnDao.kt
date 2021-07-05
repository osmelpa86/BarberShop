package it.ssplus.barbershop.model.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import it.ssplus.barbershop.model.entity.Turn

@Dao
interface TurnDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(obj: Turn): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(obj: Turn)

    @Delete
    suspend fun delete(obj: Turn)

    @Delete
    suspend fun delete(list: List<Turn>)

    @Query("SELECT * FROM turn ORDER BY id_turn ASC")
    fun getAll(): LiveData<List<Turn>>

    @Query(
        """ SELECT * FROM turn WHERE 
        name LIKE :query 
        OR hour LIKE :query
        ORDER BY id_turn ASC """
    )
    fun search(query: String): LiveData<List<Turn>>

    @Query("SELECT * FROM turn WHERE id_turn = :id_turn LIMIT 1")
    fun get(id_turn: Long): LiveData<Turn>

    @Query("SELECT * FROM turn WHERE id_turn = (SELECT MAX(id_turn) FROM turn)")
    fun lastInserted(): LiveData<Turn>
}