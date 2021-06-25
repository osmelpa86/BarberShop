package it.ssplus.barbershop.model.pojo

import androidx.room.Embedded
import androidx.room.Relation
import it.ssplus.barbershop.model.entity.Expense
import it.ssplus.barbershop.model.entity.ExpenseCategory
import java.io.Serializable

data class ExpensePojo(
    @Embedded val expense: Expense,
    @Relation(parentColumn = "id_expense_category", entityColumn = "id_expense_category")
    val expenseCategory: ExpenseCategory
) : Serializable