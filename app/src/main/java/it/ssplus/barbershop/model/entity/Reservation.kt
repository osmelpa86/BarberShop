package it.ssplus.barbershop.model.entity

import androidx.room.*
import java.io.Serializable
import java.util.*

@Entity(
    tableName = "reservation",
    indices = [Index(value = ["id_reservation"], unique = true),
        Index(value = ["id_turn"]),
        Index(value = ["id_client"])],
    foreignKeys = [
        ForeignKey(
            entity = Turn::class,
            parentColumns = ["id_turn"],
            childColumns = ["id_turn"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Client::class,
            parentColumns = ["id_client"],
            childColumns = ["id_client"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class Reservation(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_reservation")
    val id: Long = 0,
    @ColumnInfo(name = "id_turn")
    val idTurn: Long,
    @ColumnInfo(name = "id_client")
    val idClient: Long,
    val status: Int,
    @ColumnInfo(name = "additional_cost")
    val additionalCost: Double = 0.0,
    val date: Date,
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Reservation

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "Reservación(id=$id, cliente='$idClient', turno=$idTurn, estado=$status)"
    }
}

