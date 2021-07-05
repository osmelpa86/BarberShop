package it.ssplus.barbershop.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.*
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
import it.ssplus.barbershop.model.entity.ExpenseCategory
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseCategoryViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.item_expense_category,
            parent,
            false
        )
        return ExpenseCategoryViewHolder(v)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ExpenseCategoryViewHolder, position: Int) {
        val expenseCategory = expenseCategories[position]

        if (selectedItems.contains(expenseCategory)) {
            holder.itemView.setBackgroundColor(R.color.boxBackgroundDefault)
        } else {
            holder.itemView.setBackgroundColor(android.R.color.transparent)
        }

        holder.itemView.setOnLongClickListener {
            if (!multiSelect) {
                multiSelect = true
                LocalBroadcastManager.getInstance(activity)
                    .sendBroadcast(Intent(Constants.Actions.expense_category_item_selected))
                selectItem(holder, expenseCategory)
                true
            } else
                false
        }

        holder.itemView.setOnClickListener {
            if (multiSelect) {
                selectItem(holder, expenseCategory)
                val toolbar = activity.findViewById<Toolbar>(R.id.toolbar)
                toolbar.title =
                    activity.resources.getString(R.string.title_selected) + " " + this.selectedItems.size.toString()
            } else {
                val view = LayoutInflater.from(activity)
                    .inflate(R.layout.bottom_sheet_expense_category_details, null, false)

                val toolbar: Toolbar = view.findViewById(R.id.toolbar)
                val imageViewIcon: ImageView = view.findViewById(R.id.imageViewIcon)
                val textViewName: TextView = view.findViewById(R.id.textViewName)
                val textViewDescription: TextView = view.findViewById(R.id.textViewDescription)

                textViewName.text = expenseCategories[position].name
                textViewDescription.text = expenseCategories[position].description
                toolbar.inflateMenu(R.menu.expense_category_details)

                imageViewIcon.setBackgroundResource(
                    Constants.roundIcons[expenseCategories[position].color]
                )

                imageViewIcon.setImageBitmap(expenseCategories[position].image?.let { it1 ->
                    ImageUtils.getImage(
                        it1
                    )
                })

                val dialog = BottomSheetDialog(activity, R.style.BottomSheetDialogTheme)
                dialog.setContentView(view)

                toolbar.setNavigationOnClickListener { dialog.dismiss() }
                toolbar.setOnMenuItemClickListener {
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

        holder.tvNameExpenseCategory.text = expenseCategory.name
        holder.tvDescriptionExpenseCategory.text = expenseCategory.description
        val bitmap = BitmapFactory.decodeByteArray(
            expenseCategory.image,
            0,
            expenseCategory.image!!.size
        )
        holder.ivItemIconExpenseCategory.setImageBitmap(bitmap)
        holder.clIconExpenseCategory.background = ResourcesCompat.getDrawable(activity.resources,
            Constants.roundIcons[expenseCategory.color],
            null
        )
    }

    @SuppressLint("ResourceAsColor")
    private fun selectItem(holder: ExpenseCategoryViewHolder, image: ExpenseCategory) {
        if (selectedItems.contains(image)) {
            selectedItems.remove(image)
            holder.itemView.setBackgroundColor(android.R.color.transparent)
        } else {
            selectedItems.add(image)
            holder.itemView.setBackgroundColor(R.color.boxBackgroundDefault)
        }
    }

    inner class ExpenseCategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val tvNameExpenseCategory: TextView
        internal val ivItemIconExpenseCategory: ImageView
        internal val tvDescriptionExpenseCategory: TextView
        internal val clIconExpenseCategory: ConstraintLayout

        init {
            tvNameExpenseCategory =
                itemView.findViewById<View>(R.id.tvNameExpenseCategory) as TextView
            ivItemIconExpenseCategory =
                itemView.findViewById<View>(R.id.ivIconExpenseCategory) as ImageView
            tvDescriptionExpenseCategory =
                itemView.findViewById<View>(R.id.tvDescriptionExpenseCategory) as TextView
            clIconExpenseCategory =
                itemView.findViewById<View>(R.id.clIconExpenseCategory) as ConstraintLayout
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




