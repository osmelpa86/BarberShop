package it.ssplus.barbershop.ui.turn

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import it.ssplus.barbershop.R
import it.ssplus.barbershop.adapter.AdapterTurn
import it.ssplus.barbershop.model.entity.Turn
import it.ssplus.barbershop.utils.Constants
import it.ssplus.barbershop.utils.SnackBarUtil
import it.ssplus.barbershop.utils.validation.TurnHourFieldValidator
import it.ssplus.barbershop.utils.validation.TurnNameFieldValidator
import java.util.*

class TurnFragment : Fragment(), View.OnClickListener {

    private lateinit var root: View
    private lateinit var turnViewModel: TurnViewModel

    private lateinit var adapterTurn: AdapterTurn
    private lateinit var rvListTurn: RecyclerView
    lateinit var listTurn: ArrayList<Turn>
    private lateinit var noDataContainerTurn: LinearLayout
    private lateinit var svTurn: SearchView
    var turn: Turn? = null
    private lateinit var fabAddTurn: FloatingActionButton
    private lateinit var menu: Menu
    private lateinit var tilNameTurn: TextInputLayout
    private lateinit var ivSelectHourTurn: ImageView
    private lateinit var tilHourTurn: TextInputLayout

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Constants.Actions.turn_item_selected -> handleItemSelected()
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
        root = inflater.inflate(R.layout.fragment_turn, container, false)

        turnViewModel = ViewModelProvider(this).get(TurnViewModel::class.java)

        fabAddTurn = root.findViewById(R.id.fabAddTurn)
        fabAddTurn.setOnClickListener(this)

        noDataContainerTurn = root.findViewById(R.id.noDataContainerTurn)

        listTurn = arrayListOf()
        adapterTurn =
            AdapterTurn(this, activity = activity as AppCompatActivity)
        turnViewModel.all.observe(requireActivity(), { items ->
            listTurn.clear()
            listTurn.addAll(items)
            adapterTurn.setData(listTurn)
            noDataContainerTurn.visibility =
                if (items.isEmpty()) View.VISIBLE else View.GONE
        })

        rvListTurn = root.findViewById(R.id.rvListTurn)
        rvListTurn.adapter = adapterTurn
        rvListTurn.layoutManager = LinearLayoutManager(context)

        svTurn = root.findViewById(R.id.svTurn)
        svTurn.setOnClickListener {
            svTurn.setIconifiedByDefault(true)
            svTurn.isFocusable = true
            svTurn.isIconified = false
            svTurn.requestFocusFromTouch()
        }

        svTurn.setOnSearchClickListener {
        }

        svTurn.setOnCloseListener {
            false
        }

        val searchEditText =
            svTurn.findViewById<View>(androidx.appcompat.R.id.search_src_text) as EditText
        searchEditText.setTextColor(
            AppCompatResources.getColorStateList(
                requireActivity(),
                R.color.secondaryTextColor
            ).defaultColor
        )
        searchEditText.textSize = 16f
        searchEditText.hint = requireActivity().resources.getString(R.string.message_hint_search)
        val searchIcon =
            svTurn.findViewById<View>(androidx.appcompat.R.id.search_button) as ImageView
        searchIcon.drawable.setTint(
            AppCompatResources.getColorStateList(
                requireActivity(),
                R.color.primaryTextColor
            ).defaultColor
        )
        val searchMagIcon =
            svTurn.findViewById<View>(androidx.appcompat.R.id.search_close_btn) as ImageView
        searchMagIcon.drawable.setTint(
            AppCompatResources.getColorStateList(
                requireActivity(),
                R.color.primaryTextColor
            ).defaultColor
        )
        svTurn.queryHint = this.resources.getString(R.string.message_hint_search)
        svTurn.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapterTurn.filter.filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapterTurn.filter.filter(newText)
                return true
            }
        })

        registerReceiver()

        return root
    }

    private fun registerReceiver() {
        val filter = IntentFilter()
        filter.addAction(Constants.Actions.turn_item_selected)
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver, filter)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.turn, menu)
        menu.let { this.menu = it }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.turn_delete -> confirmDeletion()
            R.id.turn_select -> adapterTurn.handleSelectAll()
            R.id.turn_cancel -> handleCancel()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun handleItemSelected() {
        this.menu.clear()
        requireActivity().menuInflater.inflate(R.menu.turn_action, this.menu)
        requireActivity().findViewById<Toolbar>(R.id.toolbar).title =
            requireActivity().resources.getString(R.string.title_selected) + " 1"
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        fabAddTurn.visibility = View.GONE
    }

    private fun handleCancel() {
        this.menu.clear()
        requireActivity().menuInflater.inflate(R.menu.turn, this.menu)
        requireActivity().findViewById<Toolbar>(R.id.toolbar).title =
            requireActivity().resources.getString(R.string.menu_turn)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fabAddTurn.visibility = View.VISIBLE
        adapterTurn.handleCancel()
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

        textViewTitle.text = getString(R.string.message_delete_turns)
        textViewMessage.text =
            getString(R.string.message_confirm_delete_selected_turn)
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
        val items = adapterTurn.selectedItems
        turnViewModel.delete(items)
        handleCancel()
    }

    override fun onClick(v: View?) {
        v?.let {
            when (it.id) {
                R.id.fabAddTurn -> add()
            }
        }
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiver)
        super.onDestroy()
    }

    fun add() {
        val builderAdd =
            AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle)
        val inflater = activity?.layoutInflater
        val convertView =
            inflater?.inflate(R.layout.dialog_manage_turn, null) as View
        val titleCustomView =
            inflater.inflate(R.layout.dialog_custom_title, null) as View
        val titleIcon: ImageView =
            titleCustomView.findViewById(R.id.customDialogTitleIcon)
        val titleName: TextView =
            titleCustomView.findViewById(R.id.customDialogTitleName)
        titleName.setText(if (turn == null) R.string.menu_add else R.string.menu_edit)
        titleIcon.setImageResource(R.drawable.ic_arrow_back)
        builderAdd.setCustomTitle(titleCustomView)
        builderAdd.setView(convertView)

        tilNameTurn = convertView.findViewById(R.id.tilNameTurn)
        tilHourTurn = convertView.findViewById(R.id.tilHourTurn)
        ivSelectHourTurn = convertView.findViewById(R.id.ivSelectHourTurn)
        ivSelectHourTurn.setOnClickListener { showTimePicker(tilHourTurn.editText!!) }

        if (turn != null) {
            tilNameTurn.editText!!.setText(turn!!.name)
            tilHourTurn.editText!!.setText(turn!!.hour)
        }

        val dialogAdd: AlertDialog = builderAdd.create()
        dialogAdd.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogAdd.setCanceledOnTouchOutside(false)

        val accept: ImageButton =
            titleCustomView.findViewById(R.id.imageButton)
        accept.setOnClickListener {
            if (turn == null) {
                val validName = TurnNameFieldValidator(
                    tilNameTurn,
                    requireActivity(),
                    tilNameTurn.editText!!.text.toString(),
                    false,
                    allTurnNames = listTurn.map(Turn::name) as ArrayList<String>
                ).validate(tilNameTurn.editText!!.text.toString())

                val validHour = TurnHourFieldValidator(
                    tilHourTurn,
                    requireActivity(),
                    tilHourTurn.editText!!.text.toString(),
                    false,
                    allTurnHours = listTurn.map(Turn::hour) as ArrayList<String>
                ).validate(tilHourTurn.editText!!.text.toString())

                if (validName && validHour) {
                    turnViewModel.insert(
                        Turn(
                            name = tilNameTurn.editText!!.text.toString(),
                            hour = tilHourTurn.editText!!.text.toString()
                        )
                    )
                    dialogAdd.dismiss()
                    val customSnackBar: Snackbar = Snackbar.make(
                        requireActivity().findViewById(R.id.turnFragment),
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
                val validName = TurnNameFieldValidator(
                    tilNameTurn,
                    requireActivity(),
                    turn!!.name,
                    true,
                    allTurnNames = listTurn.map(Turn::name) as ArrayList<String>
                ).validate(tilNameTurn.editText!!.text.toString())

                val validHour = TurnHourFieldValidator(
                    tilHourTurn,
                    requireActivity(),
                    turn!!.hour,
                    true,
                    allTurnHours = listTurn.map(Turn::hour) as ArrayList<String>
                ).validate(tilHourTurn.editText!!.text.toString())

                if (validName && validHour) {
                    turnViewModel.update(
                        Turn(
                            id = turn!!.id,
                            name = tilNameTurn.editText!!.text.toString(),
                            hour = tilHourTurn.editText!!.text.toString()
                        )
                    )
                    dialogAdd.dismiss()

                    val customSnackBar: Snackbar = Snackbar.make(
                        requireActivity().findViewById(R.id.turnFragment),
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
                    turn = null
                }
            }
        }

        titleIcon.setOnClickListener {
            dialogAdd.dismiss()
            if (turn != null)
                turn = null
        }

        dialogAdd.show()
    }

    @SuppressLint("NewApi")
    private fun showTimePicker(editText: EditText?) {
        val builderAdd =
            AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle)
        val inflater = activity?.layoutInflater
        val convertView =
            inflater?.inflate(R.layout.dialog_time_picker, null) as View

        builderAdd.setView(convertView)

        val buttonCancel = convertView.findViewById<Button>(R.id.buttonCancel)
        val buttonSelect = convertView.findViewById<Button>(R.id.buttonSelect)
        val timePicker = convertView.findViewById<DatePicker>(R.id.timePicker) as TimePicker
        timePicker.setIs24HourView(false)

        val dialogAdd: AlertDialog = builderAdd.create()
        dialogAdd.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogAdd.setCanceledOnTouchOutside(false)

        buttonCancel.setOnClickListener { dialogAdd.dismiss() }
        buttonSelect.setOnClickListener {
            val isPM: Boolean = timePicker.hour >= 12

            editText!!.setText(
                String.format(
                    "%2d:%02d %s",
                    if (timePicker.hour === 12 || timePicker.hour === 0) 12 else timePicker.hour % 12,
                    timePicker.minute,
                    if (isPM) "PM" else "AM"
                )
            )
            dialogAdd.dismiss()
        }

        dialogAdd.show()

        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        timePicker.hour = hour
        timePicker.minute = minute
    }
}