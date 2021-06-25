package it.ssplus.barbershop.model.entity

import androidx.room.*
import java.io.Serializable
import java.util.*

@Entity(
    tableName = "service",
    indices = [Index(value = ["id_service"], unique = true),
        Index(value = ["id_client"])],

    foreignKeys = [
        ForeignKey(
            entity = Client::class,
            parentColumns = ["id_client"],
            childColumns = ["id_client"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class Service(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_service")
    val id: Long = 0,
    @ColumnInfo(name = "id_client")
    val idClient: Long = 0,
    @ColumnInfo(name = "additional_cost")
    val additionalCost: Double = 0.0,
    val date: Date
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Service

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "Servicio(id=$id, cliente='$idClient', costo=$additionalCost,date=$date)"
    }
}