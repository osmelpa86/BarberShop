package it.ssplus.barbershop.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import it.ssplus.barbershop.R
import it.ssplus.barbershop.databinding.BottomSheetExpenseCategoryDetailsBinding
import it.ssplus.barbershop.databinding.ItemExpenseCategoryBinding
import it.ssplus.barbershop.model.entity.ExpenseCategory
import it.ssplus.barbershop.model.pojo.ExpensePojo
import it.ssplus.barbershop.ui.expense_category.ExpenseCategoryFragment
import it.ssplus.barbershop.utils.Constants
import it.ssplus.barbershop.utils.ImageUtils
import java.util.*
import kotlin.collections.ArrayList

class AdapterExpenseCategory(
    internal var expenseCategoryFragment: ExpenseCategoryFragment,
    val activity: AppCompatActivity
) :
    RecyclerView.Adapter<AdapterExpenseCategory.ExpenseCategoryViewHolder>() {

    internal var expenseCategories = arrayListOf<ExpenseCategory>()
    var multiSelect = false
    val selectedItems = arrayListOf<ExpenseCategory>()

    fun setData(expenseCategories: ArrayList<ExpenseCategory>) {
        this.expenseCategories.clear()
        this.expenseCategories.addAll(expenseCategories)
        notifyDataSetChanged()
    }

    inner class ExpenseCategoryViewHolder(val binding: ItemExpenseCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseCategoryViewHolder {
        val binding =
            ItemExpenseCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpenseCategoryViewHolder(binding)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ExpenseCategoryViewHolder, position: Int) {
        val expenseCategory = expenseCategories[position]

        if (selectedItems.contains(expenseCategory)) {
            holder.binding.root.setBackgroundColor(R.color.boxBackgroundDefault)
        } else {
            holder.binding.root.setBackgroundColor(android.R.color.transparent)
        }

        holder.binding.root.setOnLongClickListener {
            if (!multiSelect) {
                multiSelect = true
                LocalBroadcastManager.getInstance(activity)
                    .sendBroadcast(Intent(Constants.Actions.expense_category_item_selected))
                selectItem(holder, expenseCategory)
                true
            } else
                false
        }

        holder.binding.root.setOnClickListener {
            if (multiSelect) {
                selectItem(holder, expenseCategory)
                val toolbar = activity.findViewById<Toolbar>(R.id.toolbar)
                toolbar.title =
                    activity.resources.getString(R.string.title_selected) + " " + this.selectedItems.size.toString()
            } else {

                val sheetBinding = BottomSheetExpenseCategoryDetailsBinding.inflate(
                    LayoutInflater.from(activity),
                    null,
                    false
                )

                sheetBinding.textViewName.text = expenseCategories[position].name
                sheetBinding.textViewDescription.text = expenseCategories[position].description
                sheetBinding.toolbar.inflateMenu(R.menu.expense_category_details)

                val adapterDetailsExpenseExpenseCategory =
                    AdapterDetailsExpenseExpenseCategory(activity = activity)

                expenseCategoryFragment.expensesByCategory(expenseCategories[position].id)
                    .observe(activity, { items ->
                        adapterDetailsExpenseExpenseCategory.setData(items as ArrayList<ExpensePojo>)
                    })

                sheetBinding.rvExpensesExpenseCategory.adapter = adapterDetailsExpenseExpenseCategory
                sheetBinding.rvExpensesExpenseCategory.layoutManager = LinearLayoutManager(activity)

                sheetBinding.imageViewIcon.setBackgroundResource(
                    Constants.roundIcons[expenseCategories[position].color]
                )

                sheetBinding.imageViewIcon.setImageBitmap(expenseCategories[position].image?.let { it1 ->
                    ImageUtils.getImage(
                        it1
                    )
                })

                val dialog = BottomSheetDialog(activity, R.style.BottomSheetDialogTheme)
                dialog.setContentView(sheetBinding.root)

                sheetBinding.toolbar.setNavigationOnClickListener { dialog.dismiss() }
                sheetBinding.toolbar.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.expense_category_edit -> {
                            expenseCategoryFragment.expenseCategory =
                                expenseCategories[position]
                            expenseCategoryFragment.add()
                            dialog.dismiss()
                            true
                        }
                        else -> false
                    }
                }

                dialog.show()
            }
        }

        holder.binding.tvNameExpenseCategory.text = expenseCategory.name
        holder.binding.tvDescriptionExpenseCategory.text = expenseCategory.description
        val bitmap = BitmapFactory.decodeByteArray(
            expenseCategory.image,
            0,
            expenseCategory.image!!.size
        )
        holder.binding.ivIconExpenseCategory.setImageBitmap(bitmap)
        holder.binding.clIconExpenseCategory.background = ResourcesCompat.getDrawable(
            activity.resources,
            Constants.roundIcons[expenseCategory.color],
            null
        )
    }

    @SuppressLint("ResourceAsColor")
    private fun selectItem(holder: ExpenseCategoryViewHolder, image: ExpenseCategory) {
        if (selectedItems.contains(image)) {
            selectedItems.remove(image)
            holder.binding.root.setBackgroundColor(android.R.color.transparent)
        } else {
            selectedItems.add(image)
            holder.binding.root.setBackgroundColor(R.color.boxBackgroundDefault)
        }
    }

    override fun getItemCount(): Int {
        return expenseCategories.size
    }

    fun changeDataItem(position: Int, model: ExpenseCategory) {
        expenseCategories[position] = model
        notifyDataSetChanged()
    }

    var filter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = ArrayList<ExpenseCategory>()
            val filterPattern: String = constraint.toString().lowercase(Locale.ROOT).trim()
            if (filterPattern.isEmpty()) {
                filteredList.addAll(expenseCategoryFragment.listExpenseCategory)
            } else {
                for (category in expenseCategoryFragment.listExpenseCategory) {
                    if (category.name.lowercase(Locale.ROOT).contains(filterPattern)
                    ) {
                        filteredList.add(category)
                    }
                }
            }

            val results = FilterResults()
            results.values = filteredList

            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            expenseCategories.clear()
            expenseCategories.addAll(results.values as Collection<ExpenseCategory>)
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
                this.selectedItems.addAll(this.expenseCategories)
            }
            this.selectedItems.size != itemCount -> {
                this.selectedItems.clear()
                this.selectedItems.addAll(this.expenseCategories)
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




