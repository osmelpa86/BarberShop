package it.ssplus.barbershop.model.entity

import androidx.room.*
import java.io.Serializable
import java.util.*

@Entity(
    tableName = "service",
    indices = [Index(value = ["id_service"], unique = true)]
)
data class Service(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_service")
    val id: Long = 0,
    val name: String,
    val cost: Double = 0.0,
    val description: String?
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
        return "Servicio(id=$id, nombre='$name', costo=$cost,descripción=$description)"
    }
}