package it.ssplus.barbershop.model.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import it.ssplus.barbershop.model.entity.Reservation

@Dao
interface ReservationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(obj: Reservation): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(obj: Reservation)

    @Delete
    suspend fun delete(obj: Reservation)

    @Delete
    suspend fun delete(list: List<Reservation>)

    @Query("SELECT * FROM reservation ORDER BY id_reservation ASC")
    fun getAll(): LiveData<List<Reservation>>

    @Query(
        """ SELECT * FROM reservation WHERE
        id_turn LIKE :query
        OR id_service LIKE :query
        OR status LIKE :query
        ORDER BY id_reservation ASC """
    )
    fun search(query: String): LiveData<List<Reservation>>

    @Query("SELECT * FROM reservation WHERE id_reservation = :id_reservation LIMIT 1")
    fun get(id_reservation: Long): LiveData<Reservation>

    @Query("SELECT * FROM reservation WHERE id_reservation = (SELECT MAX(id_reservation) FROM reservation)")
    fun lastInserted(): LiveData<Reservation>
}