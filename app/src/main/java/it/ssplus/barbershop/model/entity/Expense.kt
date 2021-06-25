package it.ssplus.barbershop.model.entity

import androidx.room.*
import java.io.Serializable
import java.util.*

@Entity(
    tableName = "expense",
    indices = [
        Index(value = ["id_expense"], unique = true),
        Index(value = ["id_expense_category"]),
    ],
    foreignKeys = [
        ForeignKey(
            entity = ExpenseCategory::class,
            parentColumns = ["id_expense_category"],
            childColumns = ["id_expense_category"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class Expense(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_expense")
    val id: Long = 0,
    @ColumnInfo(name = "id_expense_category")
    val idExpenseCategory: Long,
    val amount: Double,
    val date: Date,
    val description: String? = null
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Expense

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "Gasto(id=$id, cantidad='$amount', fecha=$date, descripción=$description, categoría=$idExpenseCategory)"
    }
}
