package it.ssplus.barbershop.ui.expense_category

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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import it.ssplus.barbershop.R
import it.ssplus.barbershop.adapter.AdapterColorList
import it.ssplus.barbershop.adapter.AdapterExpenseCategory
import it.ssplus.barbershop.databinding.*
import it.ssplus.barbershop.model.entity.ExpenseCategory
import it.ssplus.barbershop.model.pojo.ExpensePojo
import it.ssplus.barbershop.ui.expense.ExpenseViewModel
import it.ssplus.barbershop.utils.Constants
import it.ssplus.barbershop.utils.ImageUtils
import it.ssplus.barbershop.utils.SnackBarUtil
import it.ssplus.barbershop.utils.validators.ExpenseCategoryFieldValidator
import it.ssplus.barbershop.utils.validators.RequiredFieldValidator
import it.ssplus.iconpickert.IconPicker

class ExpenseCategoryFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentExpenseCategoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var expenseCategoryViewModel: ExpenseCategoryViewModel
    private lateinit var expenseViewModel: ExpenseViewModel
    private lateinit var adapterExpenseCategory: AdapterExpenseCategory
    lateinit var listExpenseCategory: ArrayList<ExpenseCategory>
    private lateinit var allExpenseCategoryNames: ArrayList<String>
    private lateinit var noDataContainerExpenseCategory: LinearLayout
    var expenseCategory: ExpenseCategory? = null
    private var colorSelected = -1
    private lateinit var tilColorExpenseCategory: TextInputLayout
    private lateinit var menu: Menu
    private lateinit var clNoSelectItemColorExpenseCategory: ConstraintLayout
    private lateinit var adapterColorList: AdapterColorList

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Constants.Actions.expense_category_item_selected -> handleItemSelected()
                Constants.Actions.expense_category_color_item_selected -> expenseCategoryColorSelect()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpenseCategoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        expenseCategoryViewModel = ViewModelProvider(this).get(ExpenseCategoryViewModel::class.java)
        expenseViewModel = ViewModelProvider(this).get(ExpenseViewModel::class.java)

        binding.fabAddExpenseCategory.setOnClickListener(this)

        noDataContainerExpenseCategory = binding.noDataContainerExpenseCategory

        listExpenseCategory = arrayListOf()
        adapterExpenseCategory =
            AdapterExpenseCategory(this, activity = activity as AppCompatActivity)
        expenseCategoryViewModel.all.observe(requireActivity(), { items ->
            listExpenseCategory.clear()
            listExpenseCategory.addAll(items)
            adapterExpenseCategory.setData(listExpenseCategory)
            noDataContainerExpenseCategory.visibility =
                if (items.isEmpty()) View.VISIBLE else View.GONE
        })

        allExpenseCategoryNames = arrayListOf()
        expenseCategoryViewModel.allExpenseCategoryNames.observe(requireActivity(), { items ->
            allExpenseCategoryNames.clear()
            allExpenseCategoryNames.addAll(items)
        })

        binding.rvListExpenseCategory.adapter = adapterExpenseCategory
        binding.rvListExpenseCategory.layoutManager = LinearLayoutManager(context)

        binding.svExpenseCategory.setOnClickListener {
            binding.svExpenseCategory.setIconifiedByDefault(true)
            binding.svExpenseCategory.isFocusable = true
            binding.svExpenseCategory.isIconified = false
            binding.svExpenseCategory.requestFocusFromTouch()
        }

        binding.svExpenseCategory.setOnSearchClickListener {
        }

        binding.svExpenseCategory.setOnCloseListener {
            false
        }

        val searchEditText =
            binding.svExpenseCategory.findViewById<View>(androidx.appcompat.R.id.search_src_text) as EditText
        searchEditText.setTextColor(
            AppCompatResources.getColorStateList(
                requireActivity(),
                R.color.secondaryTextColor
            ).defaultColor
        )
        searchEditText.textSize = 16f
        searchEditText.hint = requireActivity().resources.getString(R.string.message_hint_search)
        val searchIcon =
            binding.svExpenseCategory.findViewById<View>(androidx.appcompat.R.id.search_button) as ImageView
        searchIcon.drawable.setTint(
            AppCompatResources.getColorStateList(
                requireActivity(),
                R.color.primaryTextColor
            ).defaultColor
        )
        val searchMagIcon =
            binding.svExpenseCategory.findViewById<View>(androidx.appcompat.R.id.search_close_btn) as ImageView
        searchMagIcon.drawable.setTint(
            AppCompatResources.getColorStateList(
                requireActivity(),
                R.color.primaryTextColor
            ).defaultColor
        )
        binding.svExpenseCategory.queryHint = this.resources.getString(R.string.message_hint_search)
        binding.svExpenseCategory.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapterExpenseCategory.filter.filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapterExpenseCategory.filter.filter(newText)
                return true
            }
        })

        registerReceiver()
        return root
    }

    private fun registerReceiver() {
        val filter = IntentFilter()
        filter.addAction(Constants.Actions.expense_category_item_selected)
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver, filter)
        val filter1 = IntentFilter()
        filter1.addAction(Constants.Actions.expense_category_color_item_selected)
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver, filter1)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.expense_category, menu)
        menu.let { this.menu = it }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.expense_category_delete -> confirmDeletion()
            R.id.expense_category_select -> adapterExpenseCategory.handleSelectAll()
            R.id.expense_category_cancel -> handleCancel()
        }
        return super.onOptionsItemSelected(item)
    }

    fun add() {
        val builderAdd =
            AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle)

        val convertView = DialogManageExpenseCategoryBinding.inflate(layoutInflater, null, false)
        val titleCustomView = DialogCustomTitleBinding.inflate(layoutInflater, null, false)

        titleCustomView.customDialogTitleName.setText(if (expenseCategory == null) R.string.menu_add else R.string.menu_edit)
        titleCustomView.customDialogTitleIcon.setImageResource(R.drawable.ic_arrow_back)
        builderAdd.setCustomTitle(titleCustomView.root)
        builderAdd.setView(convertView.root)

        tilColorExpenseCategory = convertView.tilColorExpenseCategory
        convertView.ivSelectColorExpenseCategory.setOnClickListener { colorList() }
        val iconPicker: IconPicker = convertView.iconPickert

        if (expenseCategory != null) {
            convertView.tilNameExpenseCategory.editText!!.setText(expenseCategory!!.name)
            convertView.tilDescriptionExpenseCategory.editText!!.setText(expenseCategory!!.description)
            iconPicker.value = ImageUtils.getImage(expenseCategory!!.image!!)
            colorSelected = expenseCategory!!.color
            tilColorExpenseCategory.editText!!.setText(
                resources.getString(Constants.colorNames[colorSelected])
            )
            tilColorExpenseCategory.setStartIconDrawable(Constants.roundIcons[colorSelected])

        }

        val dialogAdd: AlertDialog = builderAdd.create()
        dialogAdd.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogAdd.setCanceledOnTouchOutside(false)

        titleCustomView.imageButton.setOnClickListener {
            if (expenseCategory == null) {
                val validName = ExpenseCategoryFieldValidator(
                    convertView.tilNameExpenseCategory,
                    requireActivity(),
                    convertView.tilNameExpenseCategory.editText!!.text.toString(),
                    false,
                    allExpenseCategoryNames
                ).validate(convertView.tilNameExpenseCategory.editText!!.text.toString())

                val validColor = RequiredFieldValidator(
                    tilColorExpenseCategory,
                    requireActivity()
                ).validate(tilColorExpenseCategory.editText!!.text.toString())

                val validDescription = RequiredFieldValidator(
                    convertView.tilDescriptionExpenseCategory,
                    requireActivity()
                ).validate(convertView.tilDescriptionExpenseCategory.editText!!.text.toString())

                if (validName && validColor && validDescription) {
                    expenseCategoryViewModel.insert(
                        ExpenseCategory(
                            name = convertView.tilNameExpenseCategory.editText!!.text.toString(),
                            description = convertView.tilDescriptionExpenseCategory.editText!!.text.toString(),
                            image = ImageUtils.getImageBytes(iconPicker.value),
                            color = colorSelected
                        )
                    )
                    dialogAdd.dismiss()
                    colorSelected = -1

                    val customSnackBar: Snackbar = Snackbar.make(
                        binding.root,
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
                    val snackBinding =
                        SnackbarMessageSimpleBinding.inflate(layoutInflater, null, false)
                    snackBinding.smpSimpleMessage.text =
                        resources.getString(R.string.message_success_add)
                    snackBinding.smpCancel.setOnClickListener { customSnackBar.dismiss() }
                    layout.setPadding(0, 0, 0, 0)
                    layout.addView(snackBinding.root, 0)
                    customSnackBar.show()
                }
            } else {
                val validName = ExpenseCategoryFieldValidator(
                    convertView.tilNameExpenseCategory,
                    requireActivity(),
                    expenseCategory!!.name,
                    true,
                    allExpenseCategoryNames
                ).validate(convertView.tilNameExpenseCategory.editText!!.text.toString())

                val validColor = RequiredFieldValidator(
                    tilColorExpenseCategory,
                    requireActivity()
                ).validate(tilColorExpenseCategory.editText!!.text.toString())

                val validDescription = RequiredFieldValidator(
                    convertView.tilDescriptionExpenseCategory,
                    requireActivity()
                ).validate(convertView.tilDescriptionExpenseCategory.editText!!.text.toString())

                if (validName && validColor && validDescription) {
                    expenseCategoryViewModel.update(
                        ExpenseCategory(
                            id = expenseCategory!!.id,
                            name = convertView.tilNameExpenseCategory.editText!!.text.toString(),
                            description = convertView.tilDescriptionExpenseCategory.editText!!.text.toString(),
                            image = ImageUtils.getImageBytes(iconPicker.value),
                            color = colorSelected
                        )
                    )
                    dialogAdd.dismiss()
                    colorSelected = -1
                    val customSnackBar: Snackbar = Snackbar.make(
                        binding.root,
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
                    val snackBinding =
                        SnackbarMessageSimpleBinding.inflate(layoutInflater, null, false)
                    snackBinding.smpSimpleMessage.text =
                        resources.getString(R.string.message_success_edit)
                    snackBinding.smpCancel.setOnClickListener { customSnackBar.dismiss() }
                    layout.setPadding(0, 0, 0, 0)
                    layout.addView(snackBinding.root, 0)
                    customSnackBar.show()
                    expenseCategory = null
                }
            }
        }

        titleCustomView.customDialogTitleIcon.setOnClickListener {
            dialogAdd.dismiss()
            if (expenseCategory != null)
                expenseCategory = null
        }

        dialogAdd.show()
    }

    private fun handleItemSelected() {
        requireActivity().menuInflater.inflate(R.menu.expense_category_action, this.menu)
        requireActivity().findViewById<Toolbar>(R.id.toolbar).title =
            requireActivity().resources.getString(R.string.title_selected) + " 1"
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        binding.fabAddExpenseCategory.visibility = View.GONE
    }

    private fun handleCancel() {
        this.menu.clear()
        requireActivity().menuInflater.inflate(R.menu.expense_category, this.menu)
        requireActivity().findViewById<Toolbar>(R.id.toolbar).title =
            requireActivity().resources.getString(R.string.menu_expense_category)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.fabAddExpenseCategory.visibility = View.VISIBLE
        adapterExpenseCategory.handleCancel()
    }

    private fun confirmDeletion() {
        val builder = AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle)
        val dBinding =
            DialogConfirmDangerBinding.inflate(LayoutInflater.from(requireActivity()), null, false)
        builder.setView(dBinding.root)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)

        dBinding.textViewTitle.text = getString(R.string.message_delete_expense_categories)
        dBinding.textViewMessage.text =
            getString(R.string.message_confirm_delete_selected_expense_category)
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
        val items = adapterExpenseCategory.selectedItems
        expenseCategoryViewModel.delete(items)
        handleCancel()
    }

    private fun expenseCategoryColorSelect() {
        if (adapterColorList.getSelected() != null) {
            clNoSelectItemColorExpenseCategory.visibility = View.GONE
        } else {
            clNoSelectItemColorExpenseCategory.visibility = View.VISIBLE
        }
    }

    private fun colorList() {
        val builderAdd =
            AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle)

        val convertView =DialogListColorBinding.inflate(layoutInflater,null,false)
        val titleCustomView =DialogCustomTitleBinding.inflate(layoutInflater,null,false)

        titleCustomView.customDialogTitleName.setText(R.string.title_list_color_select)
        titleCustomView.customDialogTitleIcon.setImageResource(R.drawable.ic_arrow_back)
        builderAdd.setCustomTitle(titleCustomView.root)
        builderAdd.setView(convertView.root)

        adapterColorList = AdapterColorList(this, colorSelected)

        convertView.rvListColors.adapter = adapterColorList
        convertView.rvListColors.layoutManager = GridLayoutManager(activity, 4, RecyclerView.VERTICAL, false)

        clNoSelectItemColorExpenseCategory =convertView.clNoSelectItemColorExpenseCategory

        val dialogAdd: AlertDialog = builderAdd.create()
        dialogAdd.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogAdd.setCanceledOnTouchOutside(false)

        titleCustomView.customDialogTitleIcon.setOnClickListener { dialogAdd.dismiss() }

        titleCustomView.imageButton.setOnClickListener {
            if (adapterColorList.getSelected() != null) {
                colorSelected = adapterColorList.getSelected()!!
                tilColorExpenseCategory.editText!!.setText(
                    resources.getString(Constants.colorNames[colorSelected])
                )
                tilColorExpenseCategory.setStartIconDrawable(Constants.roundIcons[colorSelected])
//            tilColorExpenseCategory.editText!!.background =
//                resources.getDrawable(Constants.roundIcons[colorSelected], null)
                dialogAdd.dismiss()
            } else {
                clNoSelectItemColorExpenseCategory.visibility = View.VISIBLE
            }
        }

        dialogAdd.show()
    }

    override fun onClick(v: View?) {
        v?.let {
            when (it.id) {
                R.id.fabAddExpenseCategory -> {
                    add()
                }
            }
        }
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiver)
        super.onDestroy()
    }

    fun expensesByCategory(id: Long): LiveData<List<ExpensePojo>> {
        return expenseViewModel.getExpenseCategoryByExpense(id)
    }
}