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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import it.ssplus.barbershop.R
import it.ssplus.barbershop.adapter.AdapterColorList
import it.ssplus.barbershop.adapter.AdapterExpenseCategory
import it.ssplus.barbershop.model.entity.ExpenseCategory
import it.ssplus.barbershop.utils.Constants
import it.ssplus.barbershop.utils.ImageUtils
import it.ssplus.barbershop.utils.SnackBarUtil
import it.ssplus.barbershop.utils.validators.ExpenseCategoryFieldValidator
import it.ssplus.barbershop.utils.validators.RequiredFieldValidator
import it.ssplus.iconpickert.IconPicker

class ExpenseCategoryFragment : Fragment(), View.OnClickListener {

    private lateinit var root: View
    private lateinit var expenseCategoryViewModel: ExpenseCategoryViewModel
    private lateinit var adapterExpenseCategory: AdapterExpenseCategory
    private lateinit var rvListExpenseCategory: RecyclerView
    lateinit var listExpenseCategory: ArrayList<ExpenseCategory>
    private lateinit var allExpenseCategoryNames: ArrayList<String>
    private lateinit var noDataContainerExpenseCategory: LinearLayout
    private lateinit var svExpenseCategory: SearchView
    var expenseCategory: ExpenseCategory? = null
    private var colorSelected = -1
    private lateinit var tilColorExpenseCategory: TextInputLayout
    private lateinit var fabAddExpenseCategory: FloatingActionButton
    private lateinit var menu: Menu

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Constants.Actions.expense_category_item_selected -> handleItemSelected()
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
        root = inflater.inflate(R.layout.fragment_expense_category, container, false)

        expenseCategoryViewModel = ViewModelProvider(this).get(ExpenseCategoryViewModel::class.java)

        fabAddExpenseCategory = root.findViewById(R.id.fabAddExpenseCategory)
        fabAddExpenseCategory.setOnClickListener(this)

        noDataContainerExpenseCategory = root.findViewById(R.id.noDataContainerExpenseCategory)

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

        rvListExpenseCategory = root.findViewById(R.id.rvListExpenseCategory)
        rvListExpenseCategory.adapter = adapterExpenseCategory
        rvListExpenseCategory.layoutManager = LinearLayoutManager(context)

        svExpenseCategory = root.findViewById(R.id.svExpenseCategory)
        svExpenseCategory.setOnClickListener {
            svExpenseCategory.setIconifiedByDefault(true)
            svExpenseCategory.isFocusable = true
            svExpenseCategory.isIconified = false
            svExpenseCategory.requestFocusFromTouch()
        }

        svExpenseCategory.setOnSearchClickListener {
        }

        svExpenseCategory.setOnCloseListener {
            false
        }

        val searchEditText =
            svExpenseCategory.findViewById<View>(androidx.appcompat.R.id.search_src_text) as EditText
        searchEditText.setTextColor(
            AppCompatResources.getColorStateList(
                requireActivity(),
                R.color.secondaryTextColor
            ).defaultColor
        )
        searchEditText.textSize = 16f
        searchEditText.hint = requireActivity().resources.getString(R.string.message_hint_search)
        val searchIcon =
            svExpenseCategory.findViewById<View>(androidx.appcompat.R.id.search_button) as ImageView
        searchIcon.drawable.setTint(
            AppCompatResources.getColorStateList(
                requireActivity(),
                R.color.primaryTextColor
            ).defaultColor
        )
        val searchMagIcon =
            svExpenseCategory.findViewById<View>(androidx.appcompat.R.id.search_close_btn) as ImageView
        searchMagIcon.drawable.setTint(
            AppCompatResources.getColorStateList(
                requireActivity(),
                R.color.primaryTextColor
            ).defaultColor
        )
        svExpenseCategory.queryHint = this.resources.getString(R.string.message_hint_search)
        svExpenseCategory.setOnQueryTextListener(object :
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
        val inflater = activity?.layoutInflater
        val convertView =
            inflater?.inflate(R.layout.dialog_manage_expense_category, null) as View
        val titleCustomView =
            inflater.inflate(R.layout.dialog_custom_title, null) as View
        val titleIcon: ImageView =
            titleCustomView.findViewById(R.id.customDialogTitleIcon)
        val titleName: TextView =
            titleCustomView.findViewById(R.id.customDialogTitleName)
        titleName.setText(if (expenseCategory == null) R.string.menu_add else R.string.menu_edit)
        titleIcon.setImageResource(R.drawable.ic_arrow_back)
        builderAdd.setCustomTitle(titleCustomView)
        builderAdd.setView(convertView)

        val tilNameExpenseCategory: TextInputLayout =
            convertView.findViewById(R.id.tilNameExpenseCategory)
        val tilDescriptionExpenseCategory: TextInputLayout =
            convertView.findViewById(R.id.tilDescriptionExpenseCategory)
        tilColorExpenseCategory = convertView.findViewById(R.id.tilColorExpenseCategory)
        val ivSelectColorExpenseCategory: ImageView =
            convertView.findViewById(R.id.ivSelectColorExpenseCategory)
        ivSelectColorExpenseCategory.setOnClickListener { colorList() }
        val iconPicker: IconPicker = convertView.findViewById(R.id.iconPickert)

        if (expenseCategory != null) {
            tilNameExpenseCategory.editText!!.setText(expenseCategory!!.name)
            tilDescriptionExpenseCategory.editText!!.setText(expenseCategory!!.description)
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

        val accept: ImageButton =
            titleCustomView.findViewById(R.id.imageButton)
        accept.setOnClickListener {
            if (expenseCategory == null) {
                val validName = ExpenseCategoryFieldValidator(
                    tilNameExpenseCategory,
                    requireActivity(),
                    tilNameExpenseCategory.editText!!.text.toString(),
                    false,
                    allExpenseCategoryNames
                ).validate(tilNameExpenseCategory.editText!!.text.toString())

                val validColor = RequiredFieldValidator(
                    tilColorExpenseCategory,
                    requireActivity()
                ).validate(tilColorExpenseCategory.editText!!.text.toString())

                val validDescription = RequiredFieldValidator(
                    tilDescriptionExpenseCategory,
                    requireActivity()
                ).validate(tilDescriptionExpenseCategory.editText!!.text.toString())

                if (validName && validColor && validDescription) {
                    expenseCategoryViewModel.insert(
                        ExpenseCategory(
                            name = tilNameExpenseCategory.editText!!.text.toString(),
                            description = tilDescriptionExpenseCategory.editText!!.text.toString(),
                            image = ImageUtils.getImageBytes(iconPicker.value),
                            color = colorSelected
                        )
                    )
                    dialogAdd.dismiss()
                    colorSelected = -1
                    val customSnackBar: Snackbar = Snackbar.make(
                        requireActivity().findViewById(R.id.expenseCategoryFragment),
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
                val validName = ExpenseCategoryFieldValidator(
                    tilNameExpenseCategory,
                    requireActivity(),
                    expenseCategory!!.name,
                    true,
                    allExpenseCategoryNames
                ).validate(tilNameExpenseCategory.editText!!.text.toString())

                val validColor = RequiredFieldValidator(
                    tilColorExpenseCategory,
                    requireActivity()
                ).validate(tilColorExpenseCategory.editText!!.text.toString())

                val validDescription = RequiredFieldValidator(
                    tilDescriptionExpenseCategory,
                    requireActivity()
                ).validate(tilDescriptionExpenseCategory.editText!!.text.toString())

                if (validName && validColor && validDescription) {
                    expenseCategoryViewModel.update(
                        ExpenseCategory(
                            id = expenseCategory!!.id,
                            name = tilNameExpenseCategory.editText!!.text.toString(),
                            description = tilDescriptionExpenseCategory.editText!!.text.toString(),
                            image = ImageUtils.getImageBytes(iconPicker.value),
                            color = colorSelected
                        )
                    )
                    dialogAdd.dismiss()
                    colorSelected = -1
                    val customSnackBar: Snackbar = Snackbar.make(
                        requireActivity().findViewById(R.id.expenseCategoryFragment),
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
                    expenseCategory = null
                }
            }
        }

        titleIcon.setOnClickListener {
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
        fabAddExpenseCategory.visibility = View.GONE
    }

    private fun handleCancel() {
        this.menu.clear()
        requireActivity().menuInflater.inflate(R.menu.expense_category, this.menu)
        requireActivity().findViewById<Toolbar>(R.id.toolbar).title =
            requireActivity().resources.getString(R.string.menu_expense_category)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fabAddExpenseCategory.visibility = View.VISIBLE
        adapterExpenseCategory.handleCancel()
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

        textViewTitle.text = getString(R.string.message_delete_expense_categories)
        textViewMessage.text =
            getString(R.string.message_confirm_delete_selected_expense_category)
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
        val items = adapterExpenseCategory.selectedItems
        expenseCategoryViewModel.delete(items)
        handleCancel()
    }

    private fun colorList() {
        val builderAdd =
            AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle)
        val inflater = activity?.layoutInflater
        val convertView =
            inflater?.inflate(R.layout.dialog_list_color, null) as View
        val titleCustomView =
            inflater.inflate(R.layout.dialog_custom_title, null) as View
        val titleIcon: ImageView =
            titleCustomView.findViewById(R.id.customDialogTitleIcon)
        val titleName: TextView =
            titleCustomView.findViewById(R.id.customDialogTitleName)
        titleName.setText(R.string.title_list_color_select)
        titleIcon.setImageResource(R.drawable.ic_arrow_back)
        builderAdd.setCustomTitle(titleCustomView)
        builderAdd.setView(convertView)

        val rvListColors: RecyclerView =
            convertView.findViewById(R.id.rvListColors)
        val adapterColorList = AdapterColorList(this, colorSelected)

        rvListColors.adapter = adapterColorList
        rvListColors.layoutManager = GridLayoutManager(activity, 4, RecyclerView.VERTICAL, false)

        val dialogAdd: AlertDialog = builderAdd.create()
        dialogAdd.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogAdd.setCanceledOnTouchOutside(false)

        titleIcon.setOnClickListener { dialogAdd.dismiss() }
        val accept: ImageButton =
            titleCustomView.findViewById(R.id.imageButton)
        accept.setOnClickListener {
            colorSelected = adapterColorList.getSelected()!!
            tilColorExpenseCategory.editText!!.setText(
                resources.getString(Constants.colorNames[colorSelected])
            )
            tilColorExpenseCategory.setStartIconDrawable(Constants.roundIcons[colorSelected])
//            tilColorExpenseCategory.editText!!.background =
//                resources.getDrawable(Constants.roundIcons[colorSelected], null)
            dialogAdd.dismiss()
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
}