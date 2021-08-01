package it.ssplus.barbershop.ui.expense

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputFilter
import android.view.*
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputLayout
import it.ssplus.barbershop.R
import it.ssplus.barbershop.adapter.AdapterExpense
import it.ssplus.barbershop.adapter.AdapterExpenseCategorySelect
import it.ssplus.barbershop.databinding.*
import it.ssplus.barbershop.model.entity.Expense
import it.ssplus.barbershop.model.entity.ExpenseCategory
import it.ssplus.barbershop.model.pojo.ExpensePojo
import it.ssplus.barbershop.ui.expense_category.ExpenseCategoryViewModel
import it.ssplus.barbershop.utils.*
import it.ssplus.barbershop.utils.validators.RequiredFieldValidator
import java.util.*

class ExpenseFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentExpenseBinding? = null
    private val binding get() = _binding!!

    private lateinit var expenseViewModel: ExpenseViewModel
    private lateinit var expenseCategoryViewModel: ExpenseCategoryViewModel
    private lateinit var adapterExpense: AdapterExpense
    lateinit var listExpense: ArrayList<ExpensePojo>
    private lateinit var noDataContainerExpense: LinearLayout
    var expense: ExpensePojo? = null
    private lateinit var menu: Menu

    private lateinit var listExpenseCategories: ArrayList<ExpenseCategory>
    private lateinit var ivSelectExpenseCategory: ImageView
    private lateinit var tilExpenseCategory: TextInputLayout
    private lateinit var ivIconCategory: ImageView
    private lateinit var ivSelectDateExpense: ImageView
    private lateinit var tilDateExpense: TextInputLayout
    private var expenseCategorySelected = -1
    private lateinit var clNoSelectItemExpenseCategory: ConstraintLayout
    private lateinit var adapterExpenseCategories: AdapterExpenseCategorySelect

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Constants.Actions.expense_item_selected -> handleItemSelected()
                Constants.Actions.expense_category_expense_item_selected -> expenseCategorySelect()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        expenseViewModel = ViewModelProvider(this).get(ExpenseViewModel::class.java)
        expenseCategoryViewModel = ViewModelProvider(this).get(ExpenseCategoryViewModel::class.java)

        _binding = FragmentExpenseBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.fabAddExpense.setOnClickListener(this)

        noDataContainerExpense = binding.noDataContainerExpense

        listExpense = arrayListOf()
        adapterExpense =
            AdapterExpense(this, activity = activity as AppCompatActivity)
        expenseViewModel.all.observe(requireActivity(), { items ->
            listExpense.clear()
            listExpense.addAll(items)
            adapterExpense.setData(listExpense)
            noDataContainerExpense.visibility =
                if (items.isEmpty()) View.VISIBLE else View.GONE
        })

        binding.rvListExpense.adapter = adapterExpense
        binding.rvListExpense.layoutManager = LinearLayoutManager(context)

        searchview(binding.svExpense)
        binding.svExpense.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapterExpense.filter.filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapterExpense.filter.filter(newText)
                return true
            }
        })

        listExpenseCategories = arrayListOf()
        expenseCategoryViewModel.all.observe(requireActivity(), { items ->
            listExpenseCategories.clear()
            listExpenseCategories.addAll(items)
        })

        registerReceiver()

        return root
    }

    private fun registerReceiver() {
        val filter = IntentFilter()
        filter.addAction(Constants.Actions.expense_item_selected)
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver, filter)
        val filter1 = IntentFilter()
        filter1.addAction(Constants.Actions.expense_category_expense_item_selected)
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver, filter1)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.expense, menu)
        menu.let { this.menu = it }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.expense_category -> findNavController().navigate(R.id.expense_category)
            R.id.expense_delete -> confirmDeletion()
            R.id.expense_select -> adapterExpense.handleSelectAll()
            R.id.expense_cancel -> handleCancel()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun handleItemSelected() {
        this.menu.clear()
        requireActivity().menuInflater.inflate(R.menu.expense_action, this.menu)
        requireActivity().findViewById<Toolbar>(R.id.toolbar).title =
            requireActivity().resources.getString(R.string.title_selected) + " 1"
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        binding.fabAddExpense.visibility = View.GONE
    }

    private fun handleCancel() {
        this.menu.clear()
        requireActivity().menuInflater.inflate(R.menu.expense, this.menu)
        requireActivity().findViewById<Toolbar>(R.id.toolbar).title =
            requireActivity().resources.getString(R.string.menu_expense)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.fabAddExpense.visibility = View.VISIBLE
        adapterExpense.handleCancel()
    }

    private fun confirmDeletion() {
        val builder = AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle)
        val dBinding =
            DialogConfirmDangerBinding.inflate(LayoutInflater.from(requireActivity()), null, false)
        builder.setView(dBinding.root)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)

        dBinding.textViewTitle.text = getString(R.string.message_delete_expenses)
        dBinding.textViewMessage.text =
            getString(R.string.message_confirm_delete_selected_expense)
        dBinding.buttonCancel.setOnClickListener {
            dialog.dismiss()
        }
        dBinding.buttonOk.setOnClickListener {
            deleteSelection()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun deleteSelection() {
        val items = adapterExpense.selectedItems
        val expenses = items.map(ExpensePojo::expense)
        expenseViewModel.delete(expenses)
        handleCancel()
    }

    override fun onClick(v: View?) {
        v?.let {
            when (it.id) {
                R.id.fabAddExpense -> {
                    add()
                }
            }
        }
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiver)
        super.onDestroy()
    }

    private fun expenseCategorySelect() {
        if (adapterExpenseCategories.getSelected() != null) {
            clNoSelectItemExpenseCategory.visibility = View.GONE
        } else {
            clNoSelectItemExpenseCategory.visibility = View.VISIBLE
        }
    }

    private fun expenseCategoriesList() {
        val builderAdd =
            AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle)
        val convertView = DialogListExpenseCategoriesBinding.inflate(layoutInflater, null, false)
        val titleCustomView = DialogCustomTitleBinding.inflate(layoutInflater, null, false)

        titleCustomView.customDialogTitleName.setText(R.string.title_list_expense_category_select)
        titleCustomView.customDialogTitleIcon.setImageResource(R.drawable.ic_arrow_back)
        builderAdd.setCustomTitle(titleCustomView.root)
        builderAdd.setView(convertView.root)

        clNoSelectItemExpenseCategory = convertView.clNoSelectItemExpenseCategory

        convertView.noDataContainerExpenseCategories.visibility =
            if (listExpenseCategories.isEmpty()) View.VISIBLE else View.GONE
        titleCustomView.imageButton.visibility =
            if (listExpenseCategories.isEmpty()) View.GONE else View.VISIBLE

        adapterExpenseCategories =
            AdapterExpenseCategorySelect(requireActivity(), expenseCategorySelected)
        adapterExpenseCategories.setData(listExpenseCategories)

        convertView.rvListExpenseCategories.adapter = adapterExpenseCategories
        convertView.rvListExpenseCategories.layoutManager = LinearLayoutManager(context)

        val dialogAdd: AlertDialog = builderAdd.create()
        dialogAdd.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogAdd.setCanceledOnTouchOutside(false)

        titleCustomView.customDialogTitleIcon.setOnClickListener { dialogAdd.dismiss() }

        titleCustomView.imageButton.setOnClickListener {
            if (adapterExpenseCategories.getSelected() != null) {
                expenseCategorySelected = adapterExpenseCategories.getSelected()!!
                tilExpenseCategory.editText!!.setText(listExpenseCategories[expenseCategorySelected].name)
                tilExpenseCategory.setStartIconDrawable(Constants.roundIcons[listExpenseCategories[expenseCategorySelected].color])
                ivIconCategory.setImageBitmap(listExpenseCategories[expenseCategorySelected].image?.let { it1 ->
                    ImageUtils.getImage(
                        it1
                    )
                })
                ivIconCategory.visibility = View.VISIBLE
                dialogAdd.dismiss()
            } else {
                clNoSelectItemExpenseCategory.visibility = View.VISIBLE
            }
        }

        dialogAdd.show()
    }

    @SuppressLint("SetTextI18n")
    private fun showCalendar(editText: EditText?) {
        val builderAdd =
            AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle)

        val convertView = DialogDatePickerBinding.inflate(layoutInflater, null, false)

        builderAdd.setView(convertView.root)

        val c = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)

        convertView.datePicker.updateDate(mYear, mMonth, mDay)

        if (editText!!.text.isNotEmpty()) {
            convertView.datePicker.maxDate = parseDate(editText.text.toString()).time
        } else {
            convertView.datePicker.maxDate = System.currentTimeMillis()
        }

        val dialogAdd: AlertDialog = builderAdd.create()
        dialogAdd.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogAdd.setCanceledOnTouchOutside(false)

        convertView.buttonCancel.setOnClickListener { dialogAdd.dismiss() }
        convertView.buttonSelect.setOnClickListener {
            val calendar = GregorianCalendar.getInstance()
            calendar.set(GregorianCalendar.YEAR, convertView.datePicker.year)
            calendar.set(GregorianCalendar.MONTH, convertView.datePicker.month)
            calendar.set(GregorianCalendar.DAY_OF_MONTH, convertView.datePicker.dayOfMonth)
            editText.setText(formatDate(calendar.time))
            dialogAdd.dismiss()
        }

        dialogAdd.show()
    }

    fun add() {
        val builderAdd =
            AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle)
        val convertView = DialogManageExpenseBinding.inflate(layoutInflater, null, false)
        val titleCustomView = DialogCustomTitleBinding.inflate(layoutInflater, null, false)

        titleCustomView.customDialogTitleName.setText(if (expense.isNull()) R.string.menu_add else R.string.menu_edit)
        titleCustomView.customDialogTitleIcon.setImageResource(R.drawable.ic_arrow_back)
        builderAdd.setCustomTitle(titleCustomView.root)
        builderAdd.setView(convertView.root)

        tilExpenseCategory = convertView.tilExpenseCategory
        ivSelectExpenseCategory = convertView.ivSelectExpenseCategory
        ivSelectExpenseCategory.setOnClickListener { expenseCategoriesList() }
        ivIconCategory = convertView.ivIconCategory
        ivSelectDateExpense = convertView.ivSelectDateExpense
        ivSelectDateExpense.setOnClickListener { showCalendar(tilDateExpense.editText) }
        tilDateExpense = convertView.tilDateExpense
        convertView.tilDescriptionExpense.editText!!.filters = arrayOf(InputFilter.LengthFilter(60))

        if (expense != null) {
            expenseCategorySelected = listExpenseCategories.indexOf(expense!!.expenseCategory)
            tilExpenseCategory.editText!!.setText(expense!!.expenseCategory.name)
            tilExpenseCategory.setStartIconDrawable(Constants.roundIcons[expense!!.expenseCategory.color])
            ivIconCategory.setImageBitmap(expense!!.expenseCategory.image?.let { it1 ->
                ImageUtils.getImage(
                    it1
                )
            })
            ivIconCategory.visibility = View.VISIBLE
            tilDateExpense.editText!!.setText(formatDate(expense!!.expense.date))
            convertView.tilAmountExpense.editText!!.setText(expense!!.expense.amount.toString())
            convertView.tilDescriptionExpense.editText!!.setText(expense!!.expense.description)
        }

        val dialogAdd: AlertDialog = builderAdd.create()
        dialogAdd.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogAdd.setCanceledOnTouchOutside(false)

        titleCustomView.imageButton.setOnClickListener {
            val validExpenseCategory = RequiredFieldValidator(
                tilExpenseCategory,
                requireActivity()
            ).validate(tilExpenseCategory.editText!!.text.toString())

            val validAmount = RequiredFieldValidator(
                convertView.tilAmountExpense,
                requireActivity()
            ).validate(convertView.tilAmountExpense.editText!!.text.toString())

            val validDate = RequiredFieldValidator(
                tilDateExpense,
                requireActivity()
            ).validate(tilDateExpense.editText!!.text.toString())

            val validDescription = RequiredFieldValidator(
                convertView.tilDescriptionExpense,
                requireActivity()
            ).validate(convertView.tilDescriptionExpense.editText!!.text.toString())

            if (expense.isNull()) {
                if (validExpenseCategory && validAmount && validDate && validDescription) {
                    expenseViewModel.insert(
                        Expense(
                            idExpenseCategory = listExpenseCategories[expenseCategorySelected].id,
                            amount = convertView.tilAmountExpense.editText!!.text.toString()
                                .toDouble(),
                            date = parseDate(tilDateExpense.editText!!.text.toString()),
                            description = convertView.tilDescriptionExpense.editText!!.text.toString()
                        )
                    )
                    dialogAdd.dismiss()
                    expenseCategorySelected = -1

                    snackbar(binding.root, R.string.message_success_add)
                }
            } else {
                if (validExpenseCategory && validAmount && validDate && validDescription) {
                    expenseViewModel.update(
                        Expense(
                            id = expense!!.expense.id,
                            idExpenseCategory = listExpenseCategories[expenseCategorySelected].id,
                            amount = convertView.tilAmountExpense.editText!!.text.toString()
                                .toDouble(),
                            date = parseDate(tilDateExpense.editText!!.text.toString()),
                            description = convertView.tilDescriptionExpense.editText!!.text.toString()
                        )
                    )
                    dialogAdd.dismiss()
                    expenseCategorySelected = -1
                    snackbar(binding.root, R.string.message_success_edit)
                    expense = null
                }
            }
        }

        titleCustomView.customDialogTitleIcon.setOnClickListener {
            dialogAdd.dismiss()
            if (expense != null)
                expense = null
        }

        dialogAdd.show()
    }
}