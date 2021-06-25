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
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import it.ssplus.barbershop.R
import it.ssplus.barbershop.model.entity.ExpenseCategory
import it.ssplus.barbershop.ui.expense_category.ExpenseCategoryFragment
import it.ssplus.barbershop.utils.Constants
import java.util.*
import kotlin.collections.ArrayList

class AdapterExpenseCategory(
    internal var expenseCategoryFragment: ExpenseCategoryFragment,
    val activity: AppCompatActivity
) :
    RecyclerView.Adapter<AdapterExpenseCategory.ExpenseCategoryViewHolder>() {

    internal var expenseCategories = arrayListOf<ExpenseCategory>()
    lateinit var parentAux: ViewGroup
    var multiSelect = false
    private val selectedItems = arrayListOf<ExpenseCategory>()
    private lateinit var holderAux: ExpenseCategoryViewHolder

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
        parentAux = parent
        return ExpenseCategoryViewHolder(v)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ExpenseCategoryViewHolder, position: Int) {
        this.holderAux = holder
        val expenseCategory = expenseCategories.get(position)

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
                var toolbar = activity.findViewById<Toolbar>(R.id.toolbar)
                toolbar.title =
                    activity.resources.getString(R.string.title_selected) + " " + this.selectedItems.size.toString()
            } else {
                // else, simply show the image to the user
//                showPopup(currentImage.file)
                println("Mostrar Bottom Sheet")
            }
        }

        holder.tvNameExpenseCategory.setText(expenseCategory.name)
        holder.tvDescriptionExpenseCategory.setText(expenseCategory.description)
        val bitmap = BitmapFactory.decodeByteArray(
            expenseCategory.image,
            0,
            expenseCategory.image!!.size
        )
        holder.ivItemIconExpenseCategory.setImageBitmap(bitmap)
        holder.ivItemIconExpenseCategory.background = expenseCategoryFragment.resources.getDrawable(
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

        init {
            tvNameExpenseCategory =
                itemView.findViewById<View>(R.id.tvNameExpenseCategory) as TextView
            ivItemIconExpenseCategory =
                itemView.findViewById<View>(R.id.ivIconExpenseCategory) as ImageView
            tvDescriptionExpenseCategory =
                itemView.findViewById<View>(R.id.tvDescriptionExpenseCategory) as TextView
        }
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemCount(): Int {
        return expenseCategories.size
    }

    fun changeDataItem(position: Int, model: ExpenseCategory) {
        expenseCategories.set(position, model)
        notifyDataSetChanged()
    }

    fun objectPosition(categoria: ExpenseCategory): Int {
        return expenseCategories.indexOf(categoria)
    }

    var filter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = ArrayList<ExpenseCategory>()
            val filterPattern: String = constraint.toString().toLowerCase(Locale.ROOT).trim()
            if (filterPattern.isEmpty()) {
                filteredList.addAll(expenseCategoryFragment.listExpenseCategory)
            } else {
                for (categoria in expenseCategoryFragment.listExpenseCategory) {
                    if (categoria.name.toLowerCase(Locale.ROOT).contains(filterPattern)
                    ) {
                        filteredList.add(categoria)
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
}




