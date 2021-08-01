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
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import it.ssplus.barbershop.R
import it.ssplus.barbershop.adapter.AdapterTurn
import it.ssplus.barbershop.databinding.DialogConfirmDangerBinding
import it.ssplus.barbershop.databinding.DialogCustomTitleBinding
import it.ssplus.barbershop.databinding.DialogManageTurnBinding
import it.ssplus.barbershop.databinding.FragmentTurnBinding
import it.ssplus.barbershop.model.entity.Turn
import it.ssplus.barbershop.utils.*
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
    private var originalTime: String? = null

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

        searchview(binding.svTurn)
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

        titleCustomView.customDialogTitleName.setText(if (turn.isNull()) R.string.menu_add else R.string.menu_edit)
        titleCustomView.customDialogTitleIcon.setImageResource(R.drawable.ic_arrow_back)

        builderAdd.setCustomTitle(titleCustomView.root)
        builderAdd.setView(convertView.root)

        convertView.ivSelectHourTurn.setOnClickListener { showTimePicker(convertView.tilHourTurn.editText!!) }

        if (turn != null) {
            convertView.tilNameTurn.editText!!.setText(turn!!.name)
            originalTime = turn!!.hour
            convertView.tilHourTurn.editText!!.setText(turn!!.hour)
        }

        val dialogAdd: AlertDialog = builderAdd.create()
        dialogAdd.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogAdd.setCanceledOnTouchOutside(false)

        titleCustomView.imageButton.setOnClickListener {
            if (turn.isNull()) {
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
                            hour = originalTime!!
                        )
                    )
                    dialogAdd.dismiss()
                    snackbar(binding.root, R.string.message_success_add)
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
                            hour = originalTime!!
                        )
                    )
                    dialogAdd.dismiss()

                    snackbar(binding.root, R.string.message_success_edit)
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
            originalTime = timePicker.hour.toString() + ":" + timePicker.minute
            editText!!.setText(formatTime(timePicker.hour, timePicker.minute))
            dialogAdd.dismiss()
        }

        dialogAdd.show()

        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        if (turn.isNull()) {
            timePicker.hour = hour
            timePicker.minute = minute
        } else {
            c.set(Calendar.HOUR_OF_DAY, originalTime!!.split(":")[0].toInt())
            c.set(Calendar.MINUTE, originalTime!!.split(":")[1].toInt())
            timePicker.hour = c.get(Calendar.HOUR_OF_DAY)
            timePicker.minute = c.get(Calendar.MINUTE)
        }

    }
}