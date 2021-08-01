package it.ssplus.barbershop.adapter

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import it.ssplus.barbershop.databinding.ItemExpenseBinding
import it.ssplus.barbershop.model.pojo.ExpensePojo
import it.ssplus.barbershop.utils.Constants
import it.ssplus.barbershop.utils.drawable
import it.ssplus.barbershop.utils.formatDate

class AdapterDetailsExpenseExpenseCategory(
    val activity: AppCompatActivity
) :
    RecyclerView.Adapter<AdapterDetailsExpenseExpenseCategory.ExpenseViewHolder>() {

    private var expenses = arrayListOf<ExpensePojo>()
    var multiSelect = false
    val selectedItems = arrayListOf<ExpensePojo>()

    fun setData(expenses: ArrayList<ExpensePojo>) {
        this.expenses.clear()
        this.expenses.addAll(expenses)
        notifyDataSetChanged()
    }

    inner class ExpenseViewHolder(val binding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpenseViewHolder(binding)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]

        holder.binding.tvNameExpense.text = expense.expenseCategory.name
        holder.binding.tvDescriptionExpense.text = expense.expense.description
        holder.binding.tvExpenseAmount.text = expense.expense.amount.toString()
        holder.binding.tvExpenseDate.text = formatDate(expense.expense.date)
        val bitmap = BitmapFactory.decodeByteArray(
            expense.expenseCategory.image,
            0,
            expense.expenseCategory.image!!.size
        )
        holder.binding.ivIconExpense.setImageBitmap(bitmap)
        holder.binding.clIconExpense.background =
            drawable(activity, Constants.roundIcons[expense.expenseCategory.color])
    }


    override fun getItemCount(): Int {
        return expenses.size
    }
}




