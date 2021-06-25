package it.ssplus.barbershop.model.entity

import androidx.room.*
import java.io.Serializable

@Entity(
    tableName = "type_service",
    indices = [Index(value = ["id_type_service"], unique = true)]
)
data class TypeService(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_type_service")
    val id: Long = 0,
    val name: String,
    val description: String,
    val price: Double? = 0.0,
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TypeService

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "Tipo Servicio(id=$id, nombre='$name', descripcción=$description, precio=$price)"
    }
}
