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
import com.google.android.material.snackbar.Snackbar
import it.ssplus.barbershop.R
import it.ssplus.barbershop.adapter.AdapterTurn
import it.ssplus.barbershop.databinding.*
import it.ssplus.barbershop.model.entity.Turn
import it.ssplus.barbershop.utils.Constants
import it.ssplus.barbershop.utils.SnackBarUtil
import it.ssplus.barbershop.utils.validators.TurnHourFieldValidator
import it.ssplus.barbershop.utils.validators.TurnNameFieldValidator
import java.util.*

class TurnFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentTurnBinding? = null
    private val binding get() = _binding!!
    private lateinit var turnViewModel: TurnViewModel

    private lateinit var adapterTurn: AdapterTurn
    lateinit var listTurn: ArrayList<Turn>
    private lateinit var noDataContainerTurn: LinearLayout
    var turn: Turn? = null
    private lateinit var menu: Menu

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
        _binding = FragmentTurnBinding.inflate(inflater, container, false)
        val root: View = binding.root

        turnViewModel = ViewModelProvider(this).get(TurnViewModel::class.java)

        binding.fabAddTurn.setOnClickListener(this)

        noDataContainerTurn = binding.noDataContainerTurn

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

        binding.rvListTurn.adapter = adapterTurn
        binding.rvListTurn.layoutManager = LinearLayoutManager(context)

        binding.svTurn.setOnClickListener {
            binding.svTurn.setIconifiedByDefault(true)
            binding.svTurn.isFocusable = true
            binding.svTurn.isIconified = false
            binding.svTurn.requestFocusFromTouch()
        }

        binding.svTurn.setOnSearchClickListener {
        }

        binding.svTurn.setOnCloseListener {
            false
        }

        val searchEditText =
            binding.svTurn.findViewById<View>(androidx.appcompat.R.id.search_src_text) as EditText
        searchEditText.setTextColor(
            AppCompatResources.getColorStateList(
                requireActivity(),
                R.color.secondaryTextColor
            ).defaultColor
        )
        searchEditText.textSize = 16f
        searchEditText.hint = requireActivity().resources.getString(R.string.message_hint_search)
        val searchIcon =
            binding.svTurn.findViewById<View>(androidx.appcompat.R.id.search_button) as ImageView
        searchIcon.drawable.setTint(
            AppCompatResources.getColorStateList(
                requireActivity(),
                R.color.primaryTextColor
            ).defaultColor
        )
        val searchMagIcon =
            binding.svTurn.findViewById<View>(androidx.appcompat.R.id.search_close_btn) as ImageView
        searchMagIcon.drawable.setTint(
            AppCompatResources.getColorStateList(
                requireActivity(),
                R.color.primaryTextColor
            ).defaultColor
        )
        binding.svTurn.queryHint = this.resources.getString(R.string.message_hint_search)
        binding.svTurn.setOnQueryTextListener(object :
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
        binding.fabAddTurn.visibility = View.GONE
    }

    private fun handleCancel() {
        this.menu.clear()
        requireActivity().menuInflater.inflate(R.menu.turn, this.menu)
        requireActivity().findViewById<Toolbar>(R.id.toolbar).title =
            requireActivity().resources.getString(R.string.menu_turn)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.fabAddTurn.visibility = View.VISIBLE
        adapterTurn.handleCancel()
    }

    private fun confirmDeletion() {
        val builder = AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle)
        val dBinding =
            DialogConfirmDangerBinding.inflate(LayoutInflater.from(requireActivity()), null, false)
        builder.setView(dBinding.root)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)

        dBinding.textViewTitle.text = getString(R.string.message_delete_turns)
        dBinding.textViewMessage.text =
            getString(R.string.message_confirm_delete_selected_turn)
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
        val convertView = DialogManageTurnBinding.inflate(layoutInflater, null, false)

        val titleCustomView = DialogCustomTitleBinding.inflate(layoutInflater, null, false)

        titleCustomView.customDialogTitleName.setText(if (turn == null) R.string.menu_add else R.string.menu_edit)
        titleCustomView.customDialogTitleIcon.setImageResource(R.drawable.ic_arrow_back)

        builderAdd.setCustomTitle(titleCustomView.root)
        builderAdd.setView(convertView.root)

        convertView.ivSelectHourTurn.setOnClickListener { showTimePicker(convertView.tilHourTurn.editText!!) }

        if (turn != null) {
            convertView.tilNameTurn.editText!!.setText(turn!!.name)
            convertView.tilHourTurn.editText!!.setText(turn!!.hour)
        }

        val dialogAdd: AlertDialog = builderAdd.create()
        dialogAdd.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogAdd.setCanceledOnTouchOutside(false)

        titleCustomView.imageButton.setOnClickListener {
            if (turn == null) {
                val validName = TurnNameFieldValidator(
                    convertView.tilNameTurn,
                    requireActivity(),
                    convertView.tilNameTurn.editText!!.text.toString(),
                    false,
                    allTurnNames = listTurn.map(Turn::name) as ArrayList<String>
                ).validate(convertView.tilNameTurn.editText!!.text.toString())

                val validHour = TurnHourFieldValidator(
                    convertView.tilHourTurn,
                    requireActivity(),
                    convertView.tilHourTurn.editText!!.text.toString(),
                    false,
                    allTurnHours = listTurn.map(Turn::hour) as ArrayList<String>
                ).validate(convertView.tilHourTurn.editText!!.text.toString())

                if (validName && validHour) {
                    turnViewModel.insert(
                        Turn(
                            name = convertView.tilNameTurn.editText!!.text.toString(),
                            hour = convertView.tilHourTurn.editText!!.text.toString()
                        )
                    )
                    dialogAdd.dismiss()
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
                val validName = TurnNameFieldValidator(
                    convertView.tilNameTurn,
                    requireActivity(),
                    turn!!.name,
                    true,
                    allTurnNames = listTurn.map(Turn::name) as ArrayList<String>
                ).validate(convertView.tilNameTurn.editText!!.text.toString())

                val validHour = TurnHourFieldValidator(
                    convertView.tilHourTurn,
                    requireActivity(),
                    turn!!.hour,
                    true,
                    allTurnHours = listTurn.map(Turn::hour) as ArrayList<String>
                ).validate(convertView.tilHourTurn.editText!!.text.toString())

                if (validName && validHour) {
                    turnViewModel.update(
                        Turn(
                            id = turn!!.id,
                            name = convertView.tilNameTurn.editText!!.text.toString(),
                            hour = convertView.tilHourTurn.editText!!.text.toString()
                        )
                    )
                    dialogAdd.dismiss()

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
                    turn = null
                }
            }
        }

        titleCustomView.customDialogTitleIcon.setOnClickListener {
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
                    if (!(timePicker.hour !== 12 && timePicker.hour !== 0)) 12 else timePicker.hour % 12,
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