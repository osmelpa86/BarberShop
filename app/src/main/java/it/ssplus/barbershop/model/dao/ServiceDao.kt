package it.ssplus.barbershop.model.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import it.ssplus.barbershop.model.entity.Service

@Dao
interface ServiceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(obj: Service): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(obj: Service)

    @Delete
    suspend fun delete(obj: Service)

    @Delete
    suspend fun delete(list: List<Service>)

    @Query("SELECT * FROM Service ORDER BY id_service ASC")
    fun getAll(): LiveData<List<Service>>

    @Query(
        """ SELECT * FROM Service WHERE 
        name LIKE :query 
        OR cost LIKE :query
        OR description LIKE :query
        ORDER BY id_service ASC """
    )
    fun search(query: String): LiveData<List<Service>>

    @Query("SELECT * FROM Service WHERE id_service = :id_service LIMIT 1")
    fun get(id_service: Long): LiveData<Service>

    @Query("SELECT * FROM Service WHERE id_service = (SELECT MAX(id_service) FROM Service)")
    fun lastInserted(): LiveData<Service>
}