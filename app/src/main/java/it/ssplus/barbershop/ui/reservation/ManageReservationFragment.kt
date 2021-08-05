package it.ssplus.barbershop.ui.reservation

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
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import it.ssplus.barbershop.R
import it.ssplus.barbershop.adapter.AdapterSelectClient
import it.ssplus.barbershop.adapter.AdapterSelectService
import it.ssplus.barbershop.adapter.AdapterSelectTurn
import it.ssplus.barbershop.adapter.AdapterStatus
import it.ssplus.barbershop.databinding.*
import it.ssplus.barbershop.model.entity.Client
import it.ssplus.barbershop.model.entity.Reservation
import it.ssplus.barbershop.model.entity.Service
import it.ssplus.barbershop.model.entity.Turn
import it.ssplus.barbershop.model.pojo.ReservationPojo
import it.ssplus.barbershop.ui.client.ClientViewModel
import it.ssplus.barbershop.ui.service.ServiceViewModel
import it.ssplus.barbershop.ui.turn.TurnViewModel
import it.ssplus.barbershop.utils.*
import it.ssplus.barbershop.utils.validators.RequiredFieldValidator
import java.text.SimpleDateFormat
import java.util.*


class ManageReservationFragment : Fragment(), View.OnClickListener,
    AdapterView.OnItemClickListener {

    private var _binding: FragmentManageReservationBinding? = null
    private val binding get() = _binding!!

    private lateinit var reservationViewModel: ReservationViewModel
    private lateinit var serviceViewModel: ServiceViewModel
    private lateinit var clientViewModel: ClientViewModel
    private lateinit var turnViewModel: TurnViewModel
    lateinit var listServices: ArrayList<Service>
    private val listAuxServices: ArrayList<Service> = arrayListOf()
    private lateinit var listServicesDelete: ArrayList<Service>
    lateinit var listClients: ArrayList<Client>
    lateinit var listTurns: ArrayList<Turn>
    private lateinit var adapterSelectService: AdapterSelectService

    private var selectClient: Client? = null
    private var selectTurn: Turn? = null

    private lateinit var noDataContainerServices: LinearLayout
    private lateinit var clNoSelectItemTurn: ConstraintLayout
    private lateinit var clNoSelectItemClient: ConstraintLayout
    private lateinit var clNoSelectItemService: ConstraintLayout
    private lateinit var adapterSelectClient: AdapterSelectClient
    private lateinit var adapterSelectTurn: AdapterSelectTurn
    private lateinit var menu: Menu
    var reservation: ReservationPojo? = null
    var status: Int = -1

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Constants.Actions.turn_dialog_item_selected -> handleItemSelectedTurn()
                Constants.Actions.client_dialog_item_selected -> handleItemSelectedClient()
                Constants.Actions.service_dialog_item_selected -> handleItemSelectedService()
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
        _binding = FragmentManageReservationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        reservationViewModel = ViewModelProvider(this).get(ReservationViewModel::class.java)
        serviceViewModel = ViewModelProvider(this).get(ServiceViewModel::class.java)
        clientViewModel = ViewModelProvider(this).get(ClientViewModel::class.java)
        turnViewModel = ViewModelProvider(this).get(TurnViewModel::class.java)

        binding.ivSelectClientReservation.setOnClickListener(this)
        binding.ivSelectTurnReservation.setOnClickListener(this)
        binding.ivSelectDateReservation.setOnClickListener(this)

        val adapterStatus = AdapterStatus(
            requireActivity(),
            R.layout.item_status_dropdown
        )
        status = R.string.service_status_open
        (binding.tilStatusReservation.editText as? AutoCompleteTextView)!!.background =
            drawable(Constants.serviceStatusColor[0])
        binding.tilStatusReservation.startIconDrawable = drawable(Constants.serviceStatusIcon[0])
        (binding.tilStatusReservation.editText as? AutoCompleteTextView)!!.setText(
            resources.getString(
                R.string.service_status_open
            )
        )
        binding.acStatusReservation.setDropDownBackgroundDrawable(drawable(R.drawable.dropdown_background_round_shape))
        (binding.tilStatusReservation.editText as? AutoCompleteTextView)?.setAdapter(
            adapterStatus
        )
        (binding.tilStatusReservation.editText as? AutoCompleteTextView)?.onItemClickListener = this

        listServices = arrayListOf()
        listServicesDelete = arrayListOf()
        noDataContainerServices = binding.noDataContainerServices
        adapterSelectService =
            AdapterSelectService(this, activity = activity as AppCompatActivity)
        serviceViewModel.all.observe(requireActivity(), {
            listServices.clear()
            listServices.addAll(it)
            adapterSelectService.setData(listServices)
            noDataContainerServices.visibility =
                if (listServices.isEmpty()) View.VISIBLE else View.GONE
        })

        clNoSelectItemService = root.findViewById(R.id.clNoSelectItemService)
        binding.rvListServiceSelect.adapter = adapterSelectService
        binding.rvListServiceSelect.layoutManager = LinearLayoutManager(context)

        listClients = arrayListOf()
        clientViewModel.all.observe(requireActivity(), {
            listClients.clear()
            listClients.addAll(it)
        })

        listTurns = arrayListOf()
        turnViewModel.all.observe(requireActivity(), {
            listTurns.clear()
            listTurns.addAll(it)
        })

        if (arguments?.getSerializable("reservation") != null) {
            reservation = arguments?.getSerializable("reservation") as ReservationPojo
            requireActivity().findViewById<Toolbar>(R.id.toolbar).title =
                requireActivity().resources.getString(R.string.menu_edit)

            listAuxServices.addAll(reservation!!.services as ArrayList<Service>)
            binding.tilClientReservation.editText!!.setText(reservation!!.client.name)
            selectClient = reservation!!.client
            binding.tilTurnReservation.editText!!.setText(reservation!!.turn.name)
            selectTurn = reservation!!.turn
            if (reservation!!.reservation.additionalCost != 0.0) binding.tilCostReservation.editText!!.setText(
                reservation!!.reservation.additionalCost.toString()
            )
            binding.tilDateReservation.editText!!.setText(formatDate(reservation!!.reservation.date))
            val pos = Constants.serviceStatusName.indexOf(reservation!!.reservation.status)
            status = reservation!!.reservation.status
            (binding.tilStatusReservation.editText as? AutoCompleteTextView)!!.background =
                drawable(Constants.serviceStatusColor[pos])
            binding.tilStatusReservation.startIconDrawable =
                drawable(Constants.serviceStatusIcon[pos])
            (binding.tilStatusReservation.editText as? AutoCompleteTextView)!!.setText(
                resources.getString(
                    Constants.serviceStatusName[pos]
                )
            )

            binding.tilDateReservation.editText!!.setText(formatDate(reservation!!.reservation.date))
            adapterSelectService.selectedItems = reservation!!.services as ArrayList<Service>
        }

        registerReceiver()

        return root
    }

    private fun registerReceiver() {
        val filter = IntentFilter()
        filter.addAction(Constants.Actions.turn_dialog_item_selected)
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver, filter)
        val filter1 = IntentFilter()
        filter1.addAction(Constants.Actions.client_dialog_item_selected)
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver, filter1)
        val filter2 = IntentFilter()
        filter2.addAction(Constants.Actions.service_dialog_item_selected)
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver, filter2)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.reservation_manage_action, menu)
        menu.let { this.menu = it }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.reservation_manage_accept -> add()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivSelectClientReservation -> clientsList()
            R.id.ivSelectTurnReservation -> turnsList()
            R.id.ivSelectDateReservation -> showCalendar(binding.tilDateReservation.editText)
        }
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
            convertView.datePicker.minDate = parseDate(editText.text.toString()).time
        } else {
            convertView.datePicker.minDate = System.currentTimeMillis()
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

    private fun add() {
        if (adapterSelectService.selectedItems.isNotEmpty()) {
            val validClient = RequiredFieldValidator(
                binding.tilClientReservation,
                requireActivity()
            ).validate(binding.tilClientReservation.editText!!.text.toString())

            val validTurn = RequiredFieldValidator(
                binding.tilTurnReservation,
                requireActivity()
            ).validate(binding.tilTurnReservation.editText!!.text.toString())

            val validDate = RequiredFieldValidator(
                binding.tilDateReservation,
                requireActivity()
            ).validate(binding.tilDateReservation.editText!!.text.toString())

            if (validClient && validTurn && validDate) {
                if (reservation.isNull()) {
                    val reservation = Reservation(
                        idTurn = selectTurn!!.id,
                        idClient = selectClient!!.id,
                        status = status,
                        additionalCost = if (binding.tilCostReservation.editText!!.text.toString()
                                .isEmpty()
                        ) 0.0 else binding.tilCostReservation.editText!!.text.toString().toDouble(),
                        date = parseDate(binding.tilDateReservation.editText!!.text.toString()),
                    )
                    reservationViewModel.insert(reservation, adapterSelectService.selectedItems)

                    snackbar(binding.root, R.string.message_success_add)
                    findNavController().navigate(R.id.action_manage_reservation_to_nav_reservation)
                } else {
                    listServicesDelete.addAll(listAuxServices subtract adapterSelectService.selectedItems)
                    val aux: ArrayList<Service> = arrayListOf()
                    aux.addAll(adapterSelectService.selectedItems)
                    aux.removeAll(listAuxServices intersect adapterSelectService.selectedItems)

                    val r = Reservation(
                        id = reservation!!.reservation.id,
                        idTurn = selectTurn!!.id,
                        idClient = selectClient!!.id,
                        status = status,
                        additionalCost = if (binding.tilCostReservation.editText!!.text.toString()
                                .isEmpty()
                        ) 0.0 else binding.tilCostReservation.editText!!.text.toString().toDouble(),
                        date = parseDate(binding.tilDateReservation.editText!!.text.toString())
                    )

                    reservationViewModel.update(
                        r,
                        aux,
                        listServicesDelete
                    )
                    snackbar(binding.root, R.string.message_success_edit)
                    reservation = null
                    findNavController().navigate(R.id.action_manage_reservation_to_nav_reservation)
                }
            }

        } else {
            clNoSelectItemService.visibility = View.VISIBLE
        }
    }

    private fun handleItemSelectedClient() {
        clNoSelectItemClient.visibility = View.GONE
    }

    private fun handleItemSelectedTurn() {
        clNoSelectItemTurn.visibility = View.GONE
    }

    private fun handleItemSelectedService() {
        if (adapterSelectService.selectedItems.isNotEmpty()) {
            clNoSelectItemService.visibility = View.GONE
        } else {
            clNoSelectItemService.visibility = View.VISIBLE
        }
    }

    private fun clientsList() {
        val builderAdd =
            AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle)

        val convertView = DialogListClientsBinding.inflate(layoutInflater, null, false)
        val titleCustomView = DialogCustomTitleBinding.inflate(layoutInflater, null, false)

        titleCustomView.customDialogTitleName.setText(R.string.title_list_client_select)
        titleCustomView.customDialogTitleIcon.setImageResource(R.drawable.ic_arrow_back)
        builderAdd.setCustomTitle(titleCustomView.root)
        builderAdd.setView(convertView.root)

        clNoSelectItemClient = convertView.clNoSelectItemClient

        convertView.noDataContainerClients.visibility =
            if (listClients.isEmpty()) View.VISIBLE else View.GONE
        titleCustomView.imageButton.visibility =
            if (listClients.isEmpty()) View.GONE else View.VISIBLE

        adapterSelectClient =
            AdapterSelectClient(this, requireActivity() as AppCompatActivity, selectClient)
        adapterSelectClient.setData(listClients)

        convertView.rvListSelectClients.adapter = adapterSelectClient
        convertView.rvListSelectClients.layoutManager = LinearLayoutManager(context)

        val dialogAdd: AlertDialog = builderAdd.create()
        dialogAdd.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogAdd.setCanceledOnTouchOutside(false)

        titleCustomView.customDialogTitleIcon.setOnClickListener { dialogAdd.dismiss() }
        titleCustomView.imageButton.setOnClickListener {
            selectClient = adapterSelectClient.getSelected()
            if (selectClient != null) {
                binding.tilClientReservation.editText!!.setText(selectClient!!.name)
                dialogAdd.dismiss()
            } else {
                clNoSelectItemClient.visibility = View.VISIBLE
            }
        }

        dialogAdd.show()
    }

    private fun turnsList() {
        val builderAdd =
            AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle)
        val convertView = DialogListTurnsBinding.inflate(layoutInflater, null, false)
        val titleCustomView = DialogCustomTitleBinding.inflate(layoutInflater, null, false)

        titleCustomView.customDialogTitleName.setText(R.string.title_list_turn_select)
        titleCustomView.customDialogTitleIcon.setImageResource(R.drawable.ic_arrow_back)
        builderAdd.setCustomTitle(titleCustomView.root)
        builderAdd.setView(convertView.root)

        clNoSelectItemTurn = convertView.clNoSelectItemTurn

        convertView.noDataContainerTurns.visibility =
            if (listTurns.isEmpty()) View.VISIBLE else View.GONE
        titleCustomView.imageButton.visibility =
            if (listTurns.isEmpty()) View.GONE else View.VISIBLE

        adapterSelectTurn =
            AdapterSelectTurn(this, requireActivity() as AppCompatActivity, selectTurn)
        adapterSelectTurn.setData(listTurns)

        convertView.rvListSelectTurns.adapter = adapterSelectTurn
        convertView.rvListSelectTurns.layoutManager = LinearLayoutManager(context)

        val dialogAdd: AlertDialog = builderAdd.create()
        dialogAdd.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogAdd.setCanceledOnTouchOutside(false)

        titleCustomView.customDialogTitleIcon.setOnClickListener { dialogAdd.dismiss() }
        titleCustomView.imageButton.setOnClickListener {
            selectTurn = adapterSelectTurn.getSelected()
            if (selectTurn != null) {
                binding.tilTurnReservation.editText!!.setText(selectTurn!!.name)
                dialogAdd.dismiss()
            } else {
                clNoSelectItemTurn.visibility = View.VISIBLE
            }
        }

        dialogAdd.show()
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiver)
        super.onDestroy()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        status = Constants.serviceStatusName[position]
        (binding.tilStatusReservation.editText as? AutoCompleteTextView)!!.background =
            drawable(Constants.serviceStatusColor[position])
        binding.tilStatusReservation.startIconDrawable =
            drawable(Constants.serviceStatusIcon[position])
    }
}