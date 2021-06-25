package it.ssplus.barbershop.model.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import it.ssplus.barbershop.model.entity.Client
import it.ssplus.barbershop.model.entity.TypeService

@Dao
interface TypeServiceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(obj: TypeService): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(obj: TypeService)

    @Delete
    suspend fun delete(obj: TypeService)

    @Delete
    suspend fun delete(list: List<TypeService>)

    @Query("SELECT * FROM type_service ORDER BY id_type_service ASC")
    fun getAll(): LiveData<List<TypeService>>

    @Query(
        """ SELECT * FROM type_service WHERE 
        name LIKE :query 
        OR description LIKE :query
        OR price LIKE :query
        ORDER BY id_type_service ASC """
    )
    fun search(query: String): LiveData<List<TypeService>>

    @Query("SELECT * FROM type_service WHERE id_type_service = :id_type_service LIMIT 1")
    fun get(id_type_service: Long): LiveData<TypeService>

    @Query("SELECT * FROM type_service WHERE id_type_service = (SELECT MAX(id_type_service) FROM type_service)")
    fun lastInserted(): LiveData<TypeService>
}