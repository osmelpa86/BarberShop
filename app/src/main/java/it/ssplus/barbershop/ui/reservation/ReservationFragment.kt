package it.ssplus.barbershop.ui.reservation

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import it.ssplus.barbershop.R
import it.ssplus.barbershop.adapter.AdapterReservation
import it.ssplus.barbershop.databinding.DialogConfirmDangerBinding
import it.ssplus.barbershop.databinding.FragmentReservationBinding
import it.ssplus.barbershop.model.pojo.ReservationPojo
import it.ssplus.barbershop.utils.Constants

class ReservationFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentReservationBinding? = null
    private val binding get() = _binding!!
    private lateinit var reservationViewModel: ReservationViewModel
    private lateinit var adapterReservation: AdapterReservation
    lateinit var listReservation: ArrayList<ReservationPojo>
    private lateinit var noDataContainerReservation: LinearLayout
    private lateinit var menu: Menu

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Constants.Actions.reservation_item_selected -> handleItemSelected()
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
        _binding = FragmentReservationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        reservationViewModel = ViewModelProvider(this).get(ReservationViewModel::class.java)

        binding.fabAddReservation.setOnClickListener(this)

        noDataContainerReservation = binding.noDataContainerReservation

        listReservation = arrayListOf()
        adapterReservation =
            AdapterReservation(this, activity = activity as AppCompatActivity)
        reservationViewModel.all.observe(requireActivity(), { items ->
            listReservation.clear()
            listReservation.addAll(items)
            adapterReservation.setData(listReservation)
            noDataContainerReservation.visibility =
                if (items.isEmpty()) View.VISIBLE else View.GONE
        })

        binding.rvListReservation.adapter = adapterReservation
        binding.rvListReservation.layoutManager = LinearLayoutManager(context)

        binding.svReservation.setOnClickListener {
            binding.svReservation.setIconifiedByDefault(true)
            binding.svReservation.isFocusable = true
            binding.svReservation.isIconified = false
            binding.svReservation.requestFocusFromTouch()
        }

        binding.svReservation.setOnSearchClickListener {
        }

        binding.svReservation.setOnCloseListener {
            false
        }

        val searchEditText =
            binding.svReservation.findViewById<View>(androidx.appcompat.R.id.search_src_text) as EditText
        searchEditText.setTextColor(
            AppCompatResources.getColorStateList(
                requireActivity(),
                R.color.secondaryTextColor
            ).defaultColor
        )
        searchEditText.textSize = 16f
        searchEditText.hint = requireActivity().resources.getString(R.string.message_hint_search)
        val searchIcon =
            binding.svReservation.findViewById<View>(androidx.appcompat.R.id.search_button) as ImageView
        searchIcon.drawable.setTint(
            AppCompatResources.getColorStateList(
                requireActivity(),
                R.color.primaryTextColor
            ).defaultColor
        )
        val searchMagIcon =
            binding.svReservation.findViewById<View>(androidx.appcompat.R.id.search_close_btn) as ImageView
        searchMagIcon.drawable.setTint(
            AppCompatResources.getColorStateList(
                requireActivity(),
                R.color.primaryTextColor
            ).defaultColor
        )
        binding.svReservation.queryHint = this.resources.getString(R.string.message_hint_search)
        binding.svReservation.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapterReservation.filter.filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapterReservation.filter.filter(newText)
                return true
            }
        })

        registerReceiver()

        return root
    }

    private fun registerReceiver() {
        val filter = IntentFilter()
        filter.addAction(Constants.Actions.reservation_item_selected)
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver, filter)
        val filter1 = IntentFilter()
        filter1.addAction(Constants.Actions.call_phone)
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver, filter1)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.reservation, menu)
        menu.let { this.menu = it }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.reservation_delete -> confirmDeletion()
            R.id.reservation_select -> adapterReservation.handleSelectAll()
            R.id.reservation_cancel -> handleCancel()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun handleItemSelected() {
        this.menu.clear()
        requireActivity().menuInflater.inflate(R.menu.reservation_action, this.menu)
        requireActivity().findViewById<Toolbar>(R.id.toolbar).title =
            requireActivity().resources.getString(R.string.title_selected) + " 1"
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        binding.fabAddReservation.visibility = View.GONE
    }

    private fun handleCancel() {
        this.menu.clear()
        requireActivity().menuInflater.inflate(R.menu.reservation, this.menu)
        requireActivity().findViewById<Toolbar>(R.id.toolbar).title =
            requireActivity().resources.getString(R.string.menu_reservation)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.fabAddReservation.visibility = View.VISIBLE
        adapterReservation.handleCancel()
    }

    private fun confirmDeletion() {
        val builder = AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle)
        val dBinding =
            DialogConfirmDangerBinding.inflate(LayoutInflater.from(requireActivity()), null, false)
        builder.setView(dBinding.root)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)

        dBinding.textViewTitle.text = getString(R.string.message_delete_reservations)
        dBinding.textViewMessage.text =
            getString(R.string.message_confirm_delete_selected_reservation)
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
        val items = adapterReservation.selectedItems
        val reservations = items.map(ReservationPojo::reservation)
        reservationViewModel.delete(reservations)
        handleCancel()
    }

    override fun onClick(v: View?) {
        v?.let {
            when (it.id) {
                R.id.fabAddReservation -> findNavController().navigate(R.id.manage_reservation)
            }
        }
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiver)
        super.onDestroy()
    }

}