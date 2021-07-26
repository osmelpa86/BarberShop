package it.ssplus.barbershop.adapter

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import it.ssplus.barbershop.databinding.ItemExpenseBinding
import it.ssplus.barbershop.model.pojo.ExpensePojo
import it.ssplus.barbershop.utils.Constants
import java.text.SimpleDateFormat

class AdapterDetailsExpenseExpenseCategory(
    val activity: AppCompatActivity
) :
    RecyclerView.Adapter<AdapterDetailsExpenseExpenseCategory.ExpenseViewHolder>() {

    private var expenses = arrayListOf<ExpensePojo>()
    var multiSelect = false
    val selectedItems = arrayListOf<ExpensePojo>()
    @SuppressLint("SimpleDateFormat")
    private val df = SimpleDateFormat("d/M/yyyy")

    fun setData(expenses: ArrayList<ExpensePojo>) {
        this.expenses.clear()
        this.expenses.addAll(expenses)
        notifyDataSetChanged()
    }

    inner class ExpenseViewHolder(val binding: ItemExpenseBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding= ItemExpenseBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ExpenseViewHolder(binding)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]

        holder.binding.tvNameExpense.text = expense.expenseCategory.name
        holder.binding.tvDescriptionExpense.text = expense.expense.description
        holder.binding.tvExpenseAmount.text = expense.expense.amount.toString()
        holder.binding.tvExpenseDate.text = df.format(expense.expense.date)
        val bitmap = BitmapFactory.decodeByteArray(
            expense.expenseCategory.image,
            0,
            expense.expenseCategory.image!!.size
        )
        holder.binding.ivIconExpense.setImageBitmap(bitmap)
        holder.binding.clIconExpense.background = ResourcesCompat.getDrawable(activity.resources,
            Constants.roundIcons[expense.expenseCategory.color],
            null
        )
    }



    override fun getItemCount(): Int {
        return expenses.size
    }
}




