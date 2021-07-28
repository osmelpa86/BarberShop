package it.ssplus.barbershop.model.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import it.ssplus.barbershop.model.entity.Reservation
import it.ssplus.barbershop.model.entity.ReservationService
import it.ssplus.barbershop.model.entity.Service
import it.ssplus.barbershop.model.pojo.ReservationPojo

@Dao
interface ReservationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(obj: Reservation): Long

    @Transaction
    suspend fun insert(
        reservation: Reservation,
        services: MutableList<Service>,
        reservationServiceDao: ReservationServiceDao
    ) {
        val id = insert(reservation)
        if (services.isNotEmpty()) {
            for (service in services) {
                val reservationService = ReservationService(id, service.id)
                reservationServiceDao.insert(reservationService)
            }
        }
    }

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(obj: Reservation)

    @Transaction
    suspend fun update(
        reservation: Reservation,
        services: MutableList<Service>,
        servicesDelete: MutableList<Service>,
        reservationServiceDao: ReservationServiceDao
    ) {
        if (servicesDelete.isNotEmpty()) {
            for (service in servicesDelete) {
                val reservationService: ReservationService =
                    reservationServiceDao.get(reservation.id, service.id)
                reservationServiceDao.delete(reservationService)
            }
        }

        if (services.isNotEmpty()) {
            for (service in services) {
                val reservationService = ReservationService(reservation.id, service.id)
                reservationServiceDao.insert(reservationService)
            }
        }

        update(reservation)
    }

    @Delete
    suspend fun delete(obj: Reservation)

    @Delete
    suspend fun delete(list: List<Reservation>)

    @Transaction
    @Query("SELECT * FROM reservation ORDER BY id_reservation ASC")
    fun getAll(): LiveData<List<ReservationPojo>>

    @Transaction
    @Query(
        """ SELECT * FROM reservation WHERE
        id_turn LIKE :query
        OR id_client LIKE :query
        OR status LIKE :query
        ORDER BY id_reservation ASC """
    )
    fun search(query: String): LiveData<List<ReservationPojo>>

    @Transaction
    @Query("SELECT * FROM reservation WHERE id_reservation = :id_reservation LIMIT 1")
    fun get(id_reservation: Long): LiveData<ReservationPojo>

    @Query("SELECT * FROM reservation WHERE id_reservation = (SELECT MAX(id_reservation) FROM reservation)")
    fun lastInserted(): LiveData<Reservation>
}