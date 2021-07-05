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
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import it.ssplus.barbershop.R
import it.ssplus.barbershop.adapter.AdapterExpense
import it.ssplus.barbershop.adapter.AdapterExpenseCategorySelect
import it.ssplus.barbershop.model.entity.Expense
import it.ssplus.barbershop.model.entity.ExpenseCategory
import it.ssplus.barbershop.model.pojo.ExpensePojo
import it.ssplus.barbershop.ui.expense_category.ExpenseCategoryViewModel
import it.ssplus.barbershop.utils.Constants
import it.ssplus.barbershop.utils.ImageUtils
import it.ssplus.barbershop.utils.SnackBarUtil
import it.ssplus.barbershop.utils.validators.RequiredFieldValidator
import java.text.SimpleDateFormat
import java.util.*


class ExpenseFragment : Fragment(), View.OnClickListener {

    private lateinit var root: View
    private lateinit var expenseViewModel: ExpenseViewModel
    private lateinit var expenseCategoryViewModel: ExpenseCategoryViewModel
    private lateinit var adapterExpense: AdapterExpense
    private lateinit var rvListExpense: RecyclerView
    lateinit var listExpense: ArrayList<ExpensePojo>
    private lateinit var noDataContainerExpense: LinearLayout
    private lateinit var svExpense: SearchView
    var expense: ExpensePojo? = null
    private lateinit var fabAddExpense: FloatingActionButton
    private lateinit var menu: Menu

    @SuppressLint("SimpleDateFormat")
    private val df = SimpleDateFormat("d/M/yyyy")

    private lateinit var listExpenseCategories: ArrayList<ExpenseCategory>
    private lateinit var ivSelectExpenseCategory: ImageView
    private lateinit var tilExpenseCategory: TextInputLayout
    private lateinit var ivIconCategory: ImageView
    private lateinit var ivSelectDateExpense: ImageView
    private lateinit var tilDateExpense: TextInputLayout
    private lateinit var tilAmountExpense: TextInputLayout
    private lateinit var tilDescriptionExpense: TextInputLayout
    private var expenseCategorySelected = -1

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Constants.Actions.expense_item_selected -> handleItemSelected()
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

        root = inflater.inflate(R.layout.fragment_expense, container, false)

        expenseViewModel = ViewModelProvider(this).get(ExpenseViewModel::class.java)
        expenseCategoryViewModel = ViewModelProvider(this).get(ExpenseCategoryViewModel::class.java)

        fabAddExpense = root.findViewById(R.id.fabAddExpense)
        fabAddExpense.setOnClickListener(this)

        noDataContainerExpense = root.findViewById(R.id.noDataContainerExpense)

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

        rvListExpense = root.findViewById(R.id.rvListExpense)
        rvListExpense.adapter = adapterExpense
        rvListExpense.layoutManager = LinearLayoutManager(context)

        svExpense = root.findViewById(R.id.svExpense)
        svExpense.setOnClickListener {
            svExpense.setIconifiedByDefault(true)
            svExpense.isFocusable = true
            svExpense.isIconified = false
            svExpense.requestFocusFromTouch()
        }

        svExpense.setOnSearchClickListener {
        }

        svExpense.setOnCloseListener {
            false
        }

        val searchEditText =
            svExpense.findViewById<View>(androidx.appcompat.R.id.search_src_text) as EditText
        searchEditText.setTextColor(
            AppCompatResources.getColorStateList(
                requireActivity(),
                R.color.secondaryTextColor
            ).defaultColor
        )
        searchEditText.textSize = 16f
        searchEditText.hint = requireActivity().resources.getString(R.string.message_hint_search)
        val searchIcon =
            svExpense.findViewById<View>(androidx.appcompat.R.id.search_button) as ImageView
        searchIcon.drawable.setTint(
            AppCompatResources.getColorStateList(
                requireActivity(),
                R.color.primaryTextColor
            ).defaultColor
        )
        val searchMagIcon =
            svExpense.findViewById<View>(androidx.appcompat.R.id.search_close_btn) as ImageView
        searchMagIcon.drawable.setTint(
            AppCompatResources.getColorStateList(
                requireActivity(),
                R.color.primaryTextColor
            ).defaultColor
        )
        svExpense.queryHint = this.resources.getString(R.string.message_hint_search)
        svExpense.setOnQueryTextListener(object :
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
        fabAddExpense.visibility = View.GONE
    }

    private fun handleCancel() {
        this.menu.clear()
        requireActivity().menuInflater.inflate(R.menu.expense, this.menu)
        requireActivity().findViewById<Toolbar>(R.id.toolbar).title =
            requireActivity().resources.getString(R.string.menu_expense)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fabAddExpense.visibility = View.VISIBLE
        adapterExpense.handleCancel()
    }

    private fun confirmDeletion() {
        val builder = AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle)
        val view: View =
            LayoutInflater.from(activity).inflate(R.layout.dialog_confirm_danger, null, false)
        builder.setView(view)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        val textViewMessage: TextView = view.findViewById(R.id.textViewMessage)
        val buttonCancel: Button = view.findViewById(R.id.buttonCancel)
        val buttonOk: Button = view.findViewById(R.id.buttonOk)

        textViewTitle.text = getString(R.string.message_delete_expenses)
        textViewMessage.text =
            getString(R.string.message_confirm_delete_selected_expense)
        buttonCancel.text = getString(android.R.string.cancel)
        buttonOk.text = getString(R.string.menu_delete)

        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }
        buttonOk.setOnClickListener {
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
            AdapterExpenseCategorySelect(requireActivity(), expenseCategorySelected)
        adapterExpenseCategories.setData(listExpenseCategories)

        rvListExpenseCategories.adapter = adapterExpenseCategories
        rvListExpenseCategories.layoutManager = LinearLayoutManager(context)

        val dialogAdd: AlertDialog = builderAdd.create()
        dialogAdd.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogAdd.setCanceledOnTouchOutside(false)

        titleIcon.setOnClickListener { dialogAdd.dismiss() }
        val accept: ImageButton =
            titleCustomView.findViewById(R.id.imageButton)
        accept.setOnClickListener {
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
        }

        dialogAdd.show()
    }

    @SuppressLint("SetTextI18n")
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

        datePicker.minDate = System.currentTimeMillis()
        datePicker.updateDate(mYear, mMonth, mDay)
    }

    fun add() {
        val builderAdd =
            AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle)
        val inflater = activity?.layoutInflater
        val convertView =
            inflater?.inflate(R.layout.dialog_manage_expense, null) as View
        val titleCustomView =
            inflater.inflate(R.layout.dialog_custom_title, null) as View
        val titleIcon: ImageView =
            titleCustomView.findViewById(R.id.customDialogTitleIcon)
        val titleName: TextView =
            titleCustomView.findViewById(R.id.customDialogTitleName)
        titleName.setText(if (expense == null) R.string.menu_add else R.string.menu_edit)
        titleIcon.setImageResource(R.drawable.ic_arrow_back)
        builderAdd.setCustomTitle(titleCustomView)
        builderAdd.setView(convertView)

        tilExpenseCategory = convertView.findViewById(R.id.tilExpenseCategory)
        ivSelectExpenseCategory = convertView.findViewById(R.id.ivSelectExpenseCategory)
        ivSelectExpenseCategory.setOnClickListener { expenseCategoriesList() }
        ivIconCategory = convertView.findViewById(R.id.ivIconCategory)
        ivSelectDateExpense = convertView.findViewById(R.id.ivSelectDateExpense)
        ivSelectDateExpense.setOnClickListener { showCalendar(tilDateExpense.editText) }
        tilDateExpense = convertView.findViewById(R.id.tilDateExpense)
        tilAmountExpense = convertView.findViewById(R.id.tilAmountExpense)
        tilDescriptionExpense = convertView.findViewById(R.id.tilDescriptionExpense)

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
            tilDateExpense.editText!!.setText(df.format(expense!!.expense.date))
            tilAmountExpense.editText!!.setText(expense!!.expense.amount.toString())
            tilDescriptionExpense.editText!!.setText(expense!!.expense.description)
        }

        val dialogAdd: AlertDialog = builderAdd.create()
        dialogAdd.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogAdd.setCanceledOnTouchOutside(false)

        val accept: ImageButton =
            titleCustomView.findViewById(R.id.imageButton)
        accept.setOnClickListener {
            val validExpenseCategory = RequiredFieldValidator(
                tilExpenseCategory,
                requireActivity()
            ).validate(tilExpenseCategory.editText!!.text.toString())

            val validAmount = RequiredFieldValidator(
                tilAmountExpense,
                requireActivity()
            ).validate(tilAmountExpense.editText!!.text.toString())

            val validDate = RequiredFieldValidator(
                tilDateExpense,
                requireActivity()
            ).validate(tilDateExpense.editText!!.text.toString())

            val validDescription = RequiredFieldValidator(
                tilDescriptionExpense,
                requireActivity()
            ).validate(tilDescriptionExpense.editText!!.text.toString())

            if (expense == null) {
                if (validExpenseCategory && validAmount && validDate && validDescription) {
                    expenseViewModel.insert(
                        Expense(
                            idExpenseCategory = listExpenseCategories[expenseCategorySelected].id,
                            amount = tilAmountExpense.editText!!.text.toString().toDouble(),
                            date = df.parse(tilDateExpense.editText!!.text.toString()),
                            description = tilDescriptionExpense.editText!!.text.toString()
                        )
                    )
                    dialogAdd.dismiss()
                    expenseCategorySelected = -1
                    val customSnackBar: Snackbar = Snackbar.make(
                        requireActivity().findViewById(R.id.expenseFragment),
                        "",
                        Snackbar.LENGTH_LONG
                    )

                    SnackBarUtil.getColorfulAndDrawableBacgroundSnackBar(
                        customSnackBar,
                        requireActivity(),
                        R.drawable.snackbar_background_roud_shape,
                        R.color.primaryTextColor,
                        R.color.primaryTextColor
                    )

                    val layout: Snackbar.SnackbarLayout =
                        customSnackBar.view as Snackbar.SnackbarLayout
                    val customSnackView: View =
                        layoutInflater.inflate(R.layout.snackbar_message_simple, null)
                    val smpMessageSimple =
                        customSnackView.findViewById<View>(R.id.smpSimpleMessage) as TextView
                    smpMessageSimple.text = resources.getString(R.string.message_success_add)
                    val smpCancelSimple =
                        customSnackView.findViewById<View>(R.id.smpCancel) as ImageView
                    smpCancelSimple.setOnClickListener { customSnackBar.dismiss() }
                    layout.setPadding(0, 0, 0, 0)
                    layout.addView(customSnackView, 0)
                    customSnackBar.show()
                }
            } else {
                if (validExpenseCategory && validAmount && validDate && validDescription) {
                    expenseViewModel.update(
                        Expense(
                            id = expense!!.expense.id,
                            idExpenseCategory = listExpenseCategories[expenseCategorySelected].id,
                            amount = tilAmountExpense.editText!!.text.toString().toDouble(),
                            date = df.parse(tilDateExpense.editText!!.text.toString()),
                            description = tilDescriptionExpense.editText!!.text.toString()
                        )
                    )
                    dialogAdd.dismiss()
                    expenseCategorySelected = -1

                    val customSnackBar: Snackbar = Snackbar.make(
                        requireActivity().findViewById(R.id.expenseFragment),
                        "",
                        Snackbar.LENGTH_LONG
                    )

                    SnackBarUtil.getColorfulAndDrawableBacgroundSnackBar(
                        customSnackBar,
                        requireActivity(),
                        R.drawable.snackbar_background_roud_shape,
                        R.color.primaryTextColor,
                        R.color.primaryTextColor
                    )

                    val layout: Snackbar.SnackbarLayout =
                        customSnackBar.view as Snackbar.SnackbarLayout
                    val customSnackView: View =
                        layoutInflater.inflate(R.layout.snackbar_message_simple, null)
                    val smpMessageSimple =
                        customSnackView.findViewById<View>(R.id.smpSimpleMessage) as TextView
                    smpMessageSimple.text = resources.getString(R.string.message_success_edit)
                    val smpCancelSimple =
                        customSnackView.findViewById<View>(R.id.smpCancel) as ImageView
                    smpCancelSimple.setOnClickListener { customSnackBar.dismiss() }
                    layout.setPadding(0, 0, 0, 0)
                    layout.addView(customSnackView, 0)
                    customSnackBar.show()
                    expense = null
                }
            }
        }

        titleIcon.setOnClickListener {
            dialogAdd.dismiss()
            if (expense != null)
                expense = null
        }

        dialogAdd.show()
    }
}