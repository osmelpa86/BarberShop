package it.ssplus.barbershop.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import it.ssplus.barbershop.R
import it.ssplus.barbershop.databinding.BottomSheetExpenseDetailsBinding
import it.ssplus.barbershop.databinding.ItemExpenseBinding
import it.ssplus.barbershop.model.pojo.ExpensePojo
import it.ssplus.barbershop.ui.expense.ExpenseFragment
import it.ssplus.barbershop.utils.Constants
import it.ssplus.barbershop.utils.drawable
import it.ssplus.barbershop.utils.formatDate
import it.ssplus.barbershop.utils.textMoney
import java.util.*
import kotlin.collections.ArrayList

class AdapterExpense(
    internal var expenseFragment: ExpenseFragment,
    val activity: AppCompatActivity
) :
    RecyclerView.Adapter<AdapterExpense.ExpenseViewHolder>() {

    internal var expenses = arrayListOf<ExpensePojo>()
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

        if (selectedItems.contains(expense)) {
            holder.binding.root.setBackgroundColor(R.color.boxBackgroundDefault)
        } else {
            holder.binding.root.setBackgroundColor(android.R.color.transparent)
        }

        holder.itemView.setOnLongClickListener {
            if (!multiSelect) {
                multiSelect = true
                LocalBroadcastManager.getInstance(activity)
                    .sendBroadcast(Intent(Constants.Actions.expense_item_selected))
                selectItem(holder, expense)
                true
            } else
                false
        }

        holder.binding.root.setOnClickListener {
            if (multiSelect) {
                selectItem(holder, expense)
                val toolbar = activity.findViewById<Toolbar>(R.id.toolbar)
                toolbar.title =
                    activity.resources.getString(R.string.title_selected) + " " + this.selectedItems.size.toString()
            } else {
                val sheetBinding = BottomSheetExpenseDetailsBinding.inflate(
                    LayoutInflater.from(activity),
                    null,
                    false
                )

                sheetBinding.clIconExpenseCategory.background = drawable(activity,Constants.roundIcons[expenses[position].expenseCategory.color])
                val bitmap = BitmapFactory.decodeByteArray(
                    expense.expenseCategory.image,
                    0,
                    expense.expenseCategory.image!!.size
                )
                sheetBinding.ivIconExpenseCategory.setImageBitmap(bitmap)
                sheetBinding.tvNameExpense.text = expenses[position].expenseCategory.name
                sheetBinding.tvDateExpense.text = formatDate(expenses[position].expense.date)
                sheetBinding.tvAmountExpense.text = expenses[position].expense.amount.toString().textMoney()
                sheetBinding.tvDescriptionExpense.text = expenses[position].expense.description
                sheetBinding.toolbar.inflateMenu(R.menu.expense_details)

                val dialog = BottomSheetDialog(activity, R.style.BottomSheetDialogTheme)
                dialog.setContentView(sheetBinding.root)

                sheetBinding.toolbar.setNavigationOnClickListener { dialog.dismiss() }
                sheetBinding.toolbar.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.expense_edit -> {
                            expenseFragment.expense = expenses[position]
                            expenseFragment.add()
                            dialog.dismiss()
                            true
                        }
                        else -> false
                    }
                }

                dialog.show()
            }
        }

        holder.binding.tvNameExpense.text = expense.expenseCategory.name
        holder.binding.tvDescriptionExpense.text = expense.expense.description
        holder.binding.tvExpenseAmount.text = expense.expense.amount.toString().textMoney()
        holder.binding.tvExpenseDate.text = formatDate(expense.expense.date)
        val bitmap = BitmapFactory.decodeByteArray(
            expense.expenseCategory.image,
            0,
            expense.expenseCategory.image!!.size
        )
        holder.binding.ivIconExpense.setImageBitmap(bitmap)
        holder.binding.clIconExpense.background = drawable(activity, Constants.roundIcons[expense.expenseCategory.color])
    }

    @SuppressLint("ResourceAsColor")
    private fun selectItem(holder: ExpenseViewHolder, image: ExpensePojo) {
        if (selectedItems.contains(image)) {
            selectedItems.remove(image)
            holder.binding.root.setBackgroundColor(android.R.color.transparent)
        } else {
            selectedItems.add(image)
            holder.binding.root.setBackgroundColor(R.color.boxBackgroundDefault)
        }
    }

    override fun getItemCount(): Int {
        return expenses.size
    }

    fun changeDataItem(position: Int, model: ExpensePojo) {
        expenses[position] = model
        notifyDataSetChanged()
    }

    var filter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = ArrayList<ExpensePojo>()
            val filterPattern: String = constraint.toString().lowercase(Locale.ROOT).trim()
            if (filterPattern.isEmpty()) {
                filteredList.addAll(expenseFragment.listExpense)
            } else {
                for (expense in expenseFragment.listExpense) {
                    if (expense.expenseCategory.name.lowercase(Locale.ROOT)
                            .contains(filterPattern)
                    ) {
                        filteredList.add(expense)
                    }
                }
            }

            val results = FilterResults()
            results.values = filteredList

            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            expenses.clear()
            expenses.addAll(results.values as Collection<ExpensePojo>)
            notifyDataSetChanged()
        }
    }

    @SuppressLint("ResourceAsColor")
    fun handleCancel() {
        multiSelect = false
        selectedItems.clear()
        notifyDataSetChanged()
    }

    fun handleSelectAll() {
        when {
            this.selectedItems.size == 0 -> {
                this.selectedItems.addAll(this.expenses)
            }
            this.selectedItems.size != itemCount -> {
                this.selectedItems.clear()
                this.selectedItems.addAll(this.expenses)
            }
            this.selectedItems.size == itemCount -> {
                this.selectedItems.clear()
            }
        }
        val toolbar = activity.findViewById<Toolbar>(R.id.toolbar)
        toolbar.title =
            activity.resources.getString(R.string.title_selected) + " " + this.selectedItems.size.toString()
        notifyDataSetChanged()
    }
}




