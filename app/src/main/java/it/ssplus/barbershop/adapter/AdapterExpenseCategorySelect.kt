package it.ssplus.barbershop.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import it.ssplus.barbershop.R
import it.ssplus.barbershop.model.entity.ExpenseCategory
import it.ssplus.barbershop.utils.Constants

class AdapterExpenseCategorySelect(val activity: Activity, selected: Int) :
    RecyclerView.Adapter<AdapterExpenseCategorySelect.ExpenseCategoryViewHolder>() {

    private var expenseCategories = arrayListOf<ExpenseCategory>()
    private var checkedPosition = selected

    fun setData(expenseCategories: ArrayList<ExpenseCategory>) {
        this.expenseCategories.clear()
        this.expenseCategories.addAll(expenseCategories)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseCategoryViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.item_expense_category_select,
            parent,
            false
        )
        return ExpenseCategoryViewHolder(v)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ExpenseCategoryViewHolder, position: Int) {
        val expenseCategory = expenseCategories[position]
        holder.bind(expenseCategory)
    }

    inner class ExpenseCategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNameExpenseCategory: TextView
        private val ivItemIconExpenseCategory: ImageView
        private val clIconExpenseCategory: ConstraintLayout
        internal val clExpenseCategoryContainer: ConstraintLayout

        init {
            tvNameExpenseCategory =
                itemView.findViewById<View>(R.id.tvNameExpenseCategory) as TextView
            ivItemIconExpenseCategory =
                itemView.findViewById<View>(R.id.ivIconExpenseCategory) as ImageView
            clIconExpenseCategory =
                itemView.findViewById<View>(R.id.clIconExpenseCategory) as ConstraintLayout
            clExpenseCategoryContainer =
                itemView.findViewById<View>(R.id.clExpenseCategoryContainer) as ConstraintLayout
        }

        fun bind(expenseCategory: ExpenseCategory) {
            if (checkedPosition == -1) {
                clExpenseCategoryContainer.background =
                    ContextCompat.getDrawable(activity,R.drawable.item_color_bg_transparent)
            } else {
                if (checkedPosition == adapterPosition) {
                    clExpenseCategoryContainer.background = ContextCompat.getDrawable(activity,R.drawable.item_color_bg_roud_shape)
                } else {
                    clExpenseCategoryContainer.background = ContextCompat.getDrawable(activity,R.drawable.item_color_bg_transparent)
                }
            }

            itemView.setOnClickListener {
                clExpenseCategoryContainer.background =
                    ContextCompat.getDrawable(activity,R.drawable.item_color_bg_roud_shape)
                if (checkedPosition != adapterPosition) {
                    notifyItemChanged(checkedPosition)
                    checkedPosition = adapterPosition
                }
            }

            tvNameExpenseCategory.text = expenseCategory.name
            val bitmap = BitmapFactory.decodeByteArray(
                expenseCategory.image,
                0,
                expenseCategory.image!!.size
            )
            ivItemIconExpenseCategory.setImageBitmap(bitmap)
            clIconExpenseCategory.background = ContextCompat.getDrawable(activity,Constants.roundIcons[expenseCategory.color])
        }
    }

    override fun getItemCount(): Int {
        return expenseCategories.size
    }

    fun changeDataItem(position: Int, model: ExpenseCategory) {
        expenseCategories[position] = model
        notifyDataSetChanged()
    }

    fun getSelected(): Int? {
        return if (checkedPosition != -1) {
            checkedPosition
        } else null
    }

    var filter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults? {
//            val filteredList = ArrayList<ExpenseCategory>()
//            val filterPattern: String = constraint.toString().toLowerCase(Locale.ROOT).trim()
//            if (filterPattern.isEmpty()) {
//                filteredList.addAll(activity.listExpenseCategory)
//            } else {
//                for (category in activity.listExpenseCategory) {
//                    if (category.name.toLowerCase(Locale.ROOT).contains(filterPattern)
//                    ) {
//                        filteredList.add(category)
//                    }
//                }
//            }
//
//            val results = FilterResults()
//            results.values = filteredList
//
//            return results
            return null
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {

        }
    }
}




