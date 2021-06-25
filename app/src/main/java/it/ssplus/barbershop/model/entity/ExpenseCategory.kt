package it.ssplus.barbershop.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "expense_category",
    indices = [Index(value = ["id_expense_category"], unique = true)]
)
data class ExpenseCategory(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_expense_category")
    val id: Long = 0,
    val name: String,
    val description: String?,
    val image: ByteArray? = null,
    val color: Int = 0,
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExpenseCategory

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "Categoría de Gasto(id=$id, nombre='$name', descripción=$description)"
    }
}