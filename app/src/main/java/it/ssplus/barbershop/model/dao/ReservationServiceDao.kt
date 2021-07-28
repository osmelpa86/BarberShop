package it.ssplus.barbershop.model.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import it.ssplus.barbershop.model.entity.ReservationService
import it.ssplus.barbershop.model.entity.Service

@Dao
interface ReservationServiceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(obj: ReservationService): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(obj: ReservationService)

    @Delete
    suspend fun delete(obj: ReservationService)

    @Delete
    suspend fun delete(list: List<ReservationService>)

    @Query("SELECT * FROM reservation_service ORDER BY id_reservation_service ASC")
    fun getAll(): LiveData<List<ReservationService>>

    @Query(
        """ SELECT * FROM reservation_service WHERE
        id_reservation LIKE :query
        OR id_service LIKE :query
    LIKE :query
        ORDER BY id_reservation_service ASC """
    )
    fun search(query: String): LiveData<List<ReservationService>>

    @Query("SELECT * FROM reservation_service WHERE id_reservation_service = :id_reservation_service LIMIT 1")
    fun get(id_reservation_service: Long): LiveData<ReservationService>

    @Query("SELECT * FROM reservation_service WHERE id_reservation = :id_reservation and id_service=:id_service LIMIT 1")
    fun get(id_reservation: Long,id_service:Long): ReservationService

    @Query("SELECT * FROM reservation_service WHERE id_reservation_service = (SELECT MAX(id_reservation_service) FROM reservation_service)")
    fun lastInserted(): LiveData<ReservationService>
}