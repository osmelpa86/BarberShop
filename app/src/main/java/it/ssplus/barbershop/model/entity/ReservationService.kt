package it.ssplus.barbershop.model.entity

import androidx.room.*
import java.io.Serializable

@Entity(
    tableName = "reservation_service",
    indices = [
        Index(value = ["id_reservation_service"], unique = true),
        Index(value = ["id_reservation"]),
        Index(value = ["id_service"])],
    foreignKeys = [
        ForeignKey(
            entity = Reservation::class,
            parentColumns = ["id_reservation"],
            childColumns = ["id_reservation"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Service::class,
            parentColumns = ["id_service"],
            childColumns = ["id_service"],
            onDelete = ForeignKey.RESTRICT //can't delete a product if has asks
        )
    ]
)

data class ReservationService(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_reservation_service") var id: Long? = 0,
    @ColumnInfo(name = "id_reservation") var idReservation: Long,
    @ColumnInfo(name = "id_service") var idService: Long
) : Serializable {
    constructor(idReservation: Long, idService: Long) : this(null, idReservation, idService)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReservationService

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "ReservaciónServicio(id=$id, reservación=$idReservation ,ervicio='$idService')"
    }
}

