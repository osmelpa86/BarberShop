package it.ssplus.barbershop.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.*
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import it.ssplus.barbershop.R
import it.ssplus.barbershop.model.entity.Expense
import it.ssplus.barbershop.model.entity.ExpenseCategory
import it.ssplus.barbershop.ui.expense_category.ExpenseCategoryFragment
import it.ssplus.barbershop.utils.Constants
import it.ssplus.barbershop.utils.ImageUtils
import java.util.*
import kotlin.collections.ArrayList

class AdapterExpenseCategorySelect(val activity: Activity, var selected: Int) :
    RecyclerView.Adapter<AdapterExpenseCategorySelect.ExpenseCategoryViewHolder>() {

    internal var expenseCategories = arrayListOf<ExpenseCategory>()
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
        internal val tvNameExpenseCategory: TextView
        internal val ivItemIconExpenseCategory: ImageView
        internal val clIconExpenseCategory: ConstraintLayout
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
                    activity.getDrawable(R.drawable.item_color_bg_transparent)
            } else {
                if (checkedPosition == adapterPosition) {
                    clExpenseCategoryContainer.background =
                        activity.getDrawable(R.drawable.item_color_bg_roud_shape)
                } else {
                    clExpenseCategoryContainer.background =
                        activity.getDrawable(R.drawable.item_color_bg_transparent)
                }
            }

            itemView.setOnClickListener {
                clExpenseCategoryContainer.background =
                    activity.getDrawable(R.drawable.item_color_bg_roud_shape)
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
            clIconExpenseCategory.background = activity.resources.getDrawable(
                Constants.roundIcons[expenseCategory.color],
                null
            )
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




