package it.ssplus.barbershop.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity(
    tableName = "turn",
    indices = [Index(value = ["id_turn"], unique = true)]
)
data class Turn(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_turn")
    val id: Long = 0,
    val name: String,
    val hour: String
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Turn

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "Turno(id=$id, nombre='$name', hora=$hour)"
    }
}