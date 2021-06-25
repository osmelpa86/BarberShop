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
import it.ssplus.barbershop.utils.validation.ExpenseCategoryFieldValidator
import it.ssplus.barbershop.utils.validators.RequiredFieldValidator
import it.ssplus.iconpickert.IconPickert

class ExpenseCategoryFragment : Fragment(), View.OnClickListener {

    lateinit var root: View
    private lateinit var expenseCategoryViewModel: ExpenseCategoryViewModel
    private lateinit var adapterExpenseCategory: AdapterExpenseCategory
    private lateinit var rvListExpenseCategory: RecyclerView
    lateinit var listExpenseCategory: ArrayList<ExpenseCategory>
    lateinit var allExpenseCategoryNames: ArrayList<String>
    lateinit var noDataContainerExpenseCategory: LinearLayout
    lateinit var svExpenseCategory: SearchView
    private var expenseCategory: ExpenseCategory? = null
    var isUpdate = false
    var coloSelected = -1
    lateinit var tilColorExpenseCategory: TextInputLayout
    lateinit var fabAddExpenseCategory: FloatingActionButton
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
    ): View? {
        root = inflater.inflate(R.layout.fragment_expense_category, container, false)

        expenseCategoryViewModel = ViewModelProvider(this).get(ExpenseCategoryViewModel::class.java)

        fabAddExpenseCategory = root.findViewById(R.id.fabAddExpenseCategory)
        fabAddExpenseCategory.setOnClickListener(this)

        noDataContainerExpenseCategory = root.findViewById(R.id.noDataContainerExpenseCategory)

        listExpenseCategory = arrayListOf<ExpenseCategory>()
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
        rvListExpenseCategory.adapter = adapterExpenseCategory;
        rvListExpenseCategory.layoutManager = LinearLayoutManager(context)

        svExpenseCategory = root.findViewById(R.id.svExpenseCategory)
        svExpenseCategory.setOnClickListener {
            svExpenseCategory.setIconifiedByDefault(true)
            svExpenseCategory.setFocusable(true)
            svExpenseCategory.setIconified(false)
            svExpenseCategory.requestFocusFromTouch()
        }

        svExpenseCategory.setOnSearchClickListener {
        }

        svExpenseCategory.setOnCloseListener {
            false
        }

        var searchEditText =
            svExpenseCategory.findViewById<View>(androidx.appcompat.R.id.search_src_text) as EditText
        searchEditText.setTextColor(
            AppCompatResources.getColorStateList(
                requireActivity(),
                R.color.secondaryTextColor
            ).defaultColor
        )
        searchEditText.textSize = 16f
        searchEditText.hint = requireActivity().resources.getString(R.string.message_hint_search)
        var searchIcon =
            svExpenseCategory.findViewById<View>(androidx.appcompat.R.id.search_button) as ImageView
        searchIcon.drawable.setTint(
            AppCompatResources.getColorStateList(
                requireActivity(),
                R.color.primaryTextColor
            ).defaultColor
        )
        var searchMagIcon =
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
        menu?.let { this.menu = it }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.expense_category_delete -> Toast.makeText(
                requireContext(),
                "Eliminar",
                Toast.LENGTH_LONG
            ).show()
            R.id.expense_category_select -> Toast.makeText(
                requireContext(),
                "Seleccionar",
                Toast.LENGTH_LONG
            ).show()
            R.id.expense_category_cancel -> handleCancel()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun handleItemSelected() {
        requireActivity().menuInflater.inflate(R.menu.expense_category_action, this.menu)
        requireActivity().findViewById<Toolbar>(R.id.toolbar).title =
            requireActivity().resources.getString(R.string.title_selected) + " 1"
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        fabAddExpenseCategory.visibility = View.GONE
    }

    fun handleCancel() {
        this.menu.clear()
        requireActivity().menuInflater.inflate(R.menu.expense_category, this.menu)
        requireActivity().findViewById<Toolbar>(R.id.toolbar).title =
            requireActivity().resources.getString(R.string.menu_expense_category)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fabAddExpenseCategory.visibility = View.VISIBLE
        adapterExpenseCategory.handleCancel()
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
        titleName.setText(R.string.menu_add)
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
        val iconPickert: IconPickert = convertView.findViewById(R.id.iconPickert)

        val dialogAdd: AlertDialog = builderAdd.create()
        dialogAdd.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogAdd.setCanceledOnTouchOutside(false)

        val accept: ImageButton =
            titleCustomView.findViewById(R.id.imageButton)
        accept.setOnClickListener {
            var validName = ExpenseCategoryFieldValidator(
                tilNameExpenseCategory,
                requireActivity(),
                tilNameExpenseCategory.editText!!.text.toString(),
                isUpdate,
                allExpenseCategoryNames
            ).validate(tilNameExpenseCategory.editText!!.text.toString())

            var validDescription = RequiredFieldValidator(
                tilDescriptionExpenseCategory,
                requireActivity()
            ).validate(tilDescriptionExpenseCategory.editText!!.text.toString())

            if (validName && validDescription) {
                expenseCategoryViewModel.insert(
                    ExpenseCategory(
                        name = tilNameExpenseCategory.editText!!.text.toString(),
                        description = tilDescriptionExpenseCategory.editText!!.text.toString(),
                        image = ImageUtils.getBitmapFromVectorDrawable(
                            requireActivity(),
                            iconPickert.value
                        )?.let { it1 ->
                            ImageUtils.getImageBytes(it1)
                        },
                        color = coloSelected
                    )
                )
                dialogAdd.dismiss();
                coloSelected = -1
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
                    customSnackBar.getView() as Snackbar.SnackbarLayout
                val customsnackView: View =
                    layoutInflater.inflate(R.layout.snackbar_message_simple, null)
                val smpMensajeSimple =
                    customsnackView.findViewById<View>(R.id.smpSimpleMessage) as TextView
                smpMensajeSimple.text = resources.getString(R.string.message_success_add)
                val smpCancelarSimple =
                    customsnackView.findViewById<View>(R.id.smpCancel) as ImageView
                smpCancelarSimple.setOnClickListener { customSnackBar.dismiss() }
                layout.setPadding(0, 0, 0, 0)
                layout.addView(customsnackView, 0)
                customSnackBar.show()
            }
        }

        titleIcon.setOnClickListener { dialogAdd.dismiss() }

        dialogAdd.show()
    }

    fun colorList() {
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
        var adapterColorList = AdapterColorList(this, coloSelected)

        rvListColors.adapter = adapterColorList
        rvListColors.layoutManager = GridLayoutManager(activity, 4, RecyclerView.VERTICAL, false)

        val dialogAdd: AlertDialog = builderAdd.create()
        dialogAdd.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogAdd.setCanceledOnTouchOutside(false)

        titleIcon.setOnClickListener { dialogAdd.dismiss() }
        val accept: ImageButton =
            titleCustomView.findViewById(R.id.imageButton)
        accept.setOnClickListener {
            coloSelected = adapterColorList.getSelected()!!
            tilColorExpenseCategory.editText!!.setText(
                resources.getString(Constants.colorNames[coloSelected]).toString()
            )
            tilColorExpenseCategory.setStartIconDrawable(Constants.roundIcons[coloSelected])
//            tilColorExpenseCategory.editText!!.background =
//                resources.getDrawable(Constants.roundIcons[coloSelected], null)
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