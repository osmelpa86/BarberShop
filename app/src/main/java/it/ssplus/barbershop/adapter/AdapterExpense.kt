package it.ssplus.barbershop.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import it.ssplus.barbershop.R
import it.ssplus.barbershop.model.pojo.ExpensePojo
import it.ssplus.barbershop.ui.expense.ExpenseFragment
import it.ssplus.barbershop.utils.Constants
import java.text.SimpleDateFormat
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
    @SuppressLint("SimpleDateFormat")
    private val df = SimpleDateFormat("d/M/yyyy")

    fun setData(expenses: ArrayList<ExpensePojo>) {
        this.expenses.clear()
        this.expenses.addAll(expenses)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.item_expense,
            parent,
            false
        )
        return ExpenseViewHolder(v)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]

        if (selectedItems.contains(expense)) {
            holder.itemView.setBackgroundColor(R.color.boxBackgroundDefault)
        } else {
            holder.itemView.setBackgroundColor(android.R.color.transparent)
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

        holder.itemView.setOnClickListener {
            if (multiSelect) {
                selectItem(holder, expense)
                val toolbar = activity.findViewById<Toolbar>(R.id.toolbar)
                toolbar.title =
                    activity.resources.getString(R.string.title_selected) + " " + this.selectedItems.size.toString()
            } else {
                val view = LayoutInflater.from(activity)
                    .inflate(R.layout.bottom_sheet_expense_details, null, false)

                val toolbar: Toolbar = view.findViewById(R.id.toolbar)

                val clIconExpenseCategory: ConstraintLayout =
                    view.findViewById(R.id.clIconExpenseCategory)
                val ivIconExpenseCategory: ImageView = view.findViewById(R.id.ivIconExpenseCategory)
                val tvNameExpense: TextView = view.findViewById(R.id.tvNameExpense)
                val tvDateExpense: TextView = view.findViewById(R.id.tvDateExpense)
                val tvAmountExpense: TextView = view.findViewById(R.id.tvAmountExpense)
                val tvDescriptionExpense: TextView = view.findViewById(R.id.tvDescriptionExpense)

                clIconExpenseCategory.background = ResourcesCompat.getDrawable(
                    activity.resources,
                    Constants.roundIcons[expenses[position].expenseCategory.color], null
                )
                val bitmap = BitmapFactory.decodeByteArray(
                    expense.expenseCategory.image,
                    0,
                    expense.expenseCategory.image!!.size
                )
                ivIconExpenseCategory.setImageBitmap(bitmap)
                tvNameExpense.text = expenses[position].expenseCategory.name
                tvDateExpense.text = df.format(expenses[position].expense.date)
                tvAmountExpense.text = expenses[position].expense.amount.toString()
                tvDescriptionExpense.text = expenses[position].expense.description
                toolbar.inflateMenu(R.menu.expense_details)

                val dialog = BottomSheetDialog(activity, R.style.BottomSheetDialogTheme)
                dialog.setContentView(view)

                toolbar.setNavigationOnClickListener { dialog.dismiss() }
                toolbar.setOnMenuItemClickListener {
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

        holder.tvNameExpense.text = expense.expenseCategory.name
        holder.tvDescriptionExpense.text = expense.expense.description
        holder.tvExpenseAmount.text = expense.expense.amount.toString()
        holder.tvExpenseDate.text = df.format(expense.expense.date)
        val bitmap = BitmapFactory.decodeByteArray(
            expense.expenseCategory.image,
            0,
            expense.expenseCategory.image!!.size
        )
        holder.ivItemIconExpense.setImageBitmap(bitmap)
        holder.clIconExpense.background = ResourcesCompat.getDrawable(activity.resources,
            Constants.roundIcons[expense.expenseCategory.color],
            null
        )
    }

    @SuppressLint("ResourceAsColor")
    private fun selectItem(holder: ExpenseViewHolder, image: ExpensePojo) {
        if (selectedItems.contains(image)) {
            selectedItems.remove(image)
            holder.itemView.setBackgroundColor(android.R.color.transparent)
        } else {
            selectedItems.add(image)
            holder.itemView.setBackgroundColor(R.color.boxBackgroundDefault)
        }
    }

    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val tvNameExpense: TextView
        internal val ivItemIconExpense: ImageView
        internal val tvDescriptionExpense: TextView
        internal val clIconExpense: ConstraintLayout
        internal val tvExpenseAmount: TextView
        internal val tvExpenseDate: TextView

        init {
            tvNameExpense =
                itemView.findViewById<View>(R.id.tvNameExpense) as TextView
            ivItemIconExpense =
                itemView.findViewById<View>(R.id.ivIconExpense) as ImageView
            tvDescriptionExpense =
                itemView.findViewById<View>(R.id.tvDescriptionExpense) as TextView
            clIconExpense =
                itemView.findViewById<View>(R.id.clIconExpense) as ConstraintLayout
            tvExpenseAmount =
                itemView.findViewById<View>(R.id.tvExpenseAmount) as TextView
            tvExpenseDate =
                itemView.findViewById<View>(R.id.tvExpenseDate) as TextView
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




