package it.ssplus.barbershop.ui.service

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
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import it.ssplus.barbershop.R
import it.ssplus.barbershop.adapter.AdapterService
import it.ssplus.barbershop.databinding.DialogConfirmDangerBinding
import it.ssplus.barbershop.databinding.DialogCustomTitleBinding
import it.ssplus.barbershop.databinding.DialogManageServiceBinding
import it.ssplus.barbershop.databinding.FragmentServiceBinding
import it.ssplus.barbershop.model.entity.Service
import it.ssplus.barbershop.utils.Constants
import it.ssplus.barbershop.utils.isNull
import it.ssplus.barbershop.utils.searchview
import it.ssplus.barbershop.utils.snackbar
import it.ssplus.barbershop.utils.validators.RequiredFieldValidator
import java.util.*

class ServiceFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentServiceBinding? = null
    private val binding get() = _binding!!
    private lateinit var serviceViewModel: ServiceViewModel

    private lateinit var adapterService: AdapterService

    lateinit var listService: ArrayList<Service>
    private lateinit var noDataContainerService: LinearLayout
    var service: Service? = null
    private lateinit var menu: Menu

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Constants.Actions.service_item_selected -> handleItemSelected()
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
        _binding = FragmentServiceBinding.inflate(inflater, container, false)
        val root: View = binding.root

        serviceViewModel = ViewModelProvider(this).get(ServiceViewModel::class.java)

        binding.fabAddService.setOnClickListener(this)

        noDataContainerService = binding.noDataContainerService

        listService = arrayListOf()
        adapterService =
            AdapterService(this, activity = activity as AppCompatActivity)
        serviceViewModel.all.observe(requireActivity(), { items ->
            listService.clear()
            listService.addAll(items)
            adapterService.setData(listService)
            noDataContainerService.visibility =
                if (items.isEmpty()) View.VISIBLE else View.GONE
        })

        binding.rvListService.adapter = adapterService
        binding.rvListService.layoutManager = LinearLayoutManager(context)

        searchview(binding.svService)
        binding.svService.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapterService.filter.filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapterService.filter.filter(newText)
                return true
            }
        })

        registerReceiver()

        return root
    }

    private fun registerReceiver() {
        val filter = IntentFilter()
        filter.addAction(Constants.Actions.service_item_selected)
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver, filter)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.service, menu)
        menu.let { this.menu = it }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.service_delete -> confirmDeletion()
            R.id.service_select -> adapterService.handleSelectAll()
            R.id.service_cancel -> handleCancel()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun handleItemSelected() {
        this.menu.clear()
        requireActivity().menuInflater.inflate(R.menu.service_action, this.menu)
        requireActivity().findViewById<Toolbar>(R.id.toolbar).title =
            requireActivity().resources.getString(R.string.title_selected) + " 1"
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        binding.fabAddService.visibility = View.GONE
    }

    private fun handleCancel() {
        this.menu.clear()
        requireActivity().menuInflater.inflate(R.menu.service, this.menu)
        requireActivity().findViewById<Toolbar>(R.id.toolbar).title =
            requireActivity().resources.getString(R.string.menu_service)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.fabAddService.visibility = View.VISIBLE
        adapterService.handleCancel()
    }

    private fun confirmDeletion() {
        val builder = AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle)
        val dBinding =
            DialogConfirmDangerBinding.inflate(LayoutInflater.from(requireActivity()), null, false)
        builder.setView(dBinding.root)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)

        dBinding.textViewTitle.text = getString(R.string.message_delete_services)
        dBinding.textViewMessage.text =
            getString(R.string.message_confirm_delete_selected_service)
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
        val items = adapterService.selectedItems
        serviceViewModel.delete(items)
        handleCancel()
    }

    override fun onClick(v: View?) {
        v?.let {
            when (it.id) {
                R.id.fabAddService -> add()
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
        val convertView = DialogManageServiceBinding.inflate(layoutInflater, null, false)
        val titleCustomView = DialogCustomTitleBinding.inflate(layoutInflater, null, false)

        titleCustomView.customDialogTitleName.setText(if (service.isNull()) R.string.menu_add else R.string.menu_edit)
        titleCustomView.customDialogTitleIcon.setImageResource(R.drawable.ic_arrow_back)
        builderAdd.setCustomTitle(titleCustomView.root)
        builderAdd.setView(convertView.root)

        convertView.tilDescriptionService.editText!!.filters = arrayOf(InputFilter.LengthFilter(60))

        if (service != null) {
            convertView.tilNameService.editText!!.setText(service!!.name)
            convertView.tilCostService.editText!!.setText(service!!.cost.toString())
            convertView.tilDescriptionService.editText!!.setText(service!!.description)
        }

        val dialogAdd: AlertDialog = builderAdd.create()
        dialogAdd.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogAdd.setCanceledOnTouchOutside(false)

        titleCustomView.imageButton.setOnClickListener {
            val validName = RequiredFieldValidator(
                convertView.tilNameService,
                requireActivity()
            ).validate(convertView.tilNameService.editText!!.text.toString())
            val validCost = RequiredFieldValidator(
                convertView.tilCostService,
                requireActivity()
            ).validate(convertView.tilCostService.editText!!.text.toString())

            if (validName && validCost) {
                if (service.isNull()) {
                    serviceViewModel.insert(
                        Service(
                            name = convertView.tilNameService.editText!!.text.toString(),
                            cost = convertView.tilCostService.editText!!.text.toString().toDouble(),
                            description = convertView.tilDescriptionService.editText!!.text.toString()
                        )
                    )
                    dialogAdd.dismiss()
                    snackbar(binding.root, R.string.message_success_add)
                } else {
                    serviceViewModel.update(
                        Service(
                            id = service!!.id,
                            name = convertView.tilNameService.editText!!.text.toString(),
                            cost = convertView.tilCostService.editText!!.text.toString().toDouble(),
                            description = convertView.tilDescriptionService.editText!!.text.toString()
                        )
                    )
                    dialogAdd.dismiss()

                    snackbar(binding.root, R.string.message_success_edit)
                    service = null
                }
            }
        }

        titleCustomView.customDialogTitleIcon.setOnClickListener {
            dialogAdd.dismiss()
            if (service != null)
                service = null
        }

        dialogAdd.show()
    }
}
