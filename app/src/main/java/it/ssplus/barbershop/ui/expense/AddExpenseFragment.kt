package it.ssplus.barbershop.ui.expense

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import it.ssplus.barbershop.R
import it.ssplus.barbershop.adapter.AdapterExpenseCategorySelect
import it.ssplus.barbershop.model.entity.ExpenseCategory
import it.ssplus.barbershop.model.pojo.ExpensePojo
import it.ssplus.barbershop.ui.expense_category.ExpenseCategoryViewModel
import it.ssplus.barbershop.utils.Constants
import it.ssplus.barbershop.utils.ImageUtils
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AddExpenseFragment : Fragment(), View.OnClickListener {

    private lateinit var root: View
    private lateinit var expenseViewModel: ExpenseViewModel
    private lateinit var expenseCategoryViewModel: ExpenseCategoryViewModel
    var expense: ExpensePojo? = null
    private lateinit var listExpenseCategories: ArrayList<ExpenseCategory>
    private lateinit var ivSelectExpenseCategory: ImageView
    private lateinit var tilExpenseCategory: TextInputLayout
    private lateinit var ivIconCategory: ImageView
    private lateinit var ivSelectDateExpense: ImageView
    private lateinit var tilDateExpense: TextInputLayout
    private lateinit var tilAmountExpense: TextInputLayout
    private lateinit var tilDescriptionExpense: TextInputLayout
    private val df = SimpleDateFormat("d/M/yyyy")

    private var expenseSelected = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.dialog_manage_expense, container, false)

        expenseViewModel = ViewModelProvider(this).get(ExpenseViewModel::class.java)
        expenseCategoryViewModel = ViewModelProvider(this).get(ExpenseCategoryViewModel::class.java)

        tilExpenseCategory = root.findViewById(R.id.tilExpenseCategory)
        ivIconCategory = root.findViewById(R.id.ivIconCategory)
        ivSelectExpenseCategory = root.findViewById(R.id.ivSelectExpenseCategory)
        ivSelectExpenseCategory.setOnClickListener(this)
        listExpenseCategories = arrayListOf()
        expenseCategoryViewModel.all.observe(requireActivity(), { items ->
            listExpenseCategories.clear()
            listExpenseCategories.addAll(items)
        })

        ivSelectDateExpense = root.findViewById(R.id.ivSelectDateExpense)
        ivSelectDateExpense.setOnClickListener(this)
        tilDateExpense = root.findViewById(R.id.tilDateExpense)
        tilAmountExpense = root.findViewById(R.id.tilAmountExpense)
        tilDescriptionExpense = root.findViewById(R.id.tilDescriptionExpense)

        return root
    }

    private fun expenseCategoriesList() {
        val builderAdd =
            AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle)
        val inflater = activity?.layoutInflater
        val convertView =
            inflater?.inflate(R.layout.dialog_list_expense_categories, null) as View
        val titleCustomView =
            inflater.inflate(R.layout.dialog_custom_title, null) as View
        val titleIcon: ImageView =
            titleCustomView.findViewById(R.id.customDialogTitleIcon)
        val titleName: TextView =
            titleCustomView.findViewById(R.id.customDialogTitleName)
        titleName.setText(R.string.title_list_expense_category_select)
        titleIcon.setImageResource(R.drawable.ic_arrow_back)
        builderAdd.setCustomTitle(titleCustomView)
        builderAdd.setView(convertView)

        val rvListExpenseCategories: RecyclerView =
            convertView.findViewById(R.id.rvListExpenseCategories)
        val adapterExpenseCategories =
            AdapterExpenseCategorySelect(requireActivity(), expenseSelected)
        adapterExpenseCategories.setData(listExpenseCategories)

        rvListExpenseCategories.adapter = adapterExpenseCategories
        rvListExpenseCategories.layoutManager =
            GridLayoutManager(activity, 4, RecyclerView.VERTICAL, false)

        val dialogAdd: AlertDialog = builderAdd.create()
        dialogAdd.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogAdd.setCanceledOnTouchOutside(false)

        titleIcon.setOnClickListener { dialogAdd.dismiss() }
        val accept: ImageButton =
            titleCustomView.findViewById(R.id.imageButton)
        accept.setOnClickListener {
            expenseSelected = adapterExpenseCategories.getSelected()!!
            tilExpenseCategory.editText!!.setText(listExpenseCategories[expenseSelected].name)
            tilExpenseCategory.setStartIconDrawable(Constants.roundIcons[listExpenseCategories[expenseSelected].color])

            ivIconCategory.setImageBitmap(listExpenseCategories[expenseSelected].image?.let { it1 ->
                ImageUtils.getImage(
                    it1
                )
            })
            ivIconCategory.visibility = View.VISIBLE
            dialogAdd.dismiss()
        }

        dialogAdd.show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivSelectExpenseCategory -> expenseCategoriesList()
            R.id.ivSelectDateExpense -> showCalendar(tilDateExpense.editText)
        }
    }

    private fun showCalendar(editText: EditText?) {
        val builderAdd =
            AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle)
        val inflater = activity?.layoutInflater
        val convertView =
            inflater?.inflate(R.layout.dialog_date_picker, null) as View

        builderAdd.setView(convertView)

        val buttonCancel = convertView.findViewById<Button>(R.id.buttonCancel)
        val buttonSelect = convertView.findViewById<Button>(R.id.buttonSelect)
        val datePicker = convertView.findViewById<DatePicker>(R.id.datePicker)

        val dialogAdd: AlertDialog = builderAdd.create()
        dialogAdd.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogAdd.setCanceledOnTouchOutside(false)

        buttonCancel.setOnClickListener { dialogAdd.dismiss() }
        buttonSelect.setOnClickListener {
            editText!!.setText(datePicker.dayOfMonth.toString() + "/" + datePicker.month + "/" + datePicker.year)
            dialogAdd.dismiss()
        }

        dialogAdd.show()

        val c = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)

        datePicker.updateDate(mYear, mMonth, mDay)
    }
}