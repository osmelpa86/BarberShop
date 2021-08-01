package it.ssplus.barbershop.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import it.ssplus.barbershop.R
import it.ssplus.barbershop.databinding.ItemExpenseCategorySelectBinding
import it.ssplus.barbershop.model.entity.ExpenseCategory
import it.ssplus.barbershop.utils.Constants
import it.ssplus.barbershop.utils.drawable

class AdapterExpenseCategorySelect(val activity: Activity, selected: Int) :
    RecyclerView.Adapter<AdapterExpenseCategorySelect.ExpenseCategoryViewHolder>() {

    private var expenseCategories = arrayListOf<ExpenseCategory>()
    private var checkedPosition = selected

    fun setData(expenseCategories: ArrayList<ExpenseCategory>) {
        this.expenseCategories.clear()
        this.expenseCategories.addAll(expenseCategories)
        notifyDataSetChanged()
    }

    inner class ExpenseCategoryViewHolder(val binding: ItemExpenseCategorySelectBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(expenseCategory: ExpenseCategory) {
            if (checkedPosition == -1) {
                binding.root.background = drawable(activity, R.drawable.item_color_bg_transparent)
            } else {
                if (checkedPosition == adapterPosition) {
                    binding.root.background =
                        drawable(activity, R.drawable.item_color_bg_roud_shape_linear)
                } else {
                    binding.root.background =
                        drawable(activity, R.drawable.item_color_bg_transparent)
                }
            }

            binding.root.setOnClickListener {
                binding.root.background =
                    drawable(activity, R.drawable.item_color_bg_roud_shape_linear)
                if (checkedPosition != adapterPosition) {
                    notifyItemChanged(checkedPosition)
                    checkedPosition = adapterPosition
                }
                LocalBroadcastManager.getInstance(activity)
                    .sendBroadcast(Intent(Constants.Actions.expense_category_expense_item_selected))
            }

            binding.tvNameExpenseCategory.text = expenseCategory.name
            val bitmap = BitmapFactory.decodeByteArray(
                expenseCategory.image,
                0,
                expenseCategory.image!!.size
            )
            binding.ivIconExpenseCategory.setImageBitmap(bitmap)
            binding.clIconExpenseCategory.background =
                drawable(activity, Constants.roundIcons[expenseCategory.color])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseCategoryViewHolder {
        val binding = ItemExpenseCategorySelectBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExpenseCategoryViewHolder(binding)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ExpenseCategoryViewHolder, position: Int) {
        val expenseCategory = expenseCategories[position]
        holder.bind(expenseCategory)
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




