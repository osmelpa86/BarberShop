package it.ssplus.barbershop.model.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import it.ssplus.barbershop.model.entity.Client

@Dao
interface ClientDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(obj: Client): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(obj: Client)

    @Delete
    suspend fun delete(obj: Client)

    @Delete
    suspend fun delete(list: List<Client>)

    @Query("SELECT * FROM client ORDER BY id_client ASC")
    fun getAll(): LiveData<List<Client>>

    @Query(
        """ SELECT * FROM client WHERE 
        name LIKE :query 
        OR cell_phone LIKE :query
        OR phone_number LIKE :query
        ORDER BY id_client ASC """
    )
    fun search(query: String): LiveData<List<Client>>

    @Query("SELECT * FROM client WHERE id_client = :id_client LIMIT 1")
    fun get(id_client: Long): LiveData<Client>

    @Query("SELECT * FROM client WHERE id_client = (SELECT MAX(id_client) FROM client)")
    fun lastInserted(): LiveData<Client>
}