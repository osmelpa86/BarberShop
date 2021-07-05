package it.ssplus.barbershop.ui.client

import android.Manifest
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import it.ssplus.barbershop.R
import it.ssplus.barbershop.adapter.AdapterClient
import it.ssplus.barbershop.model.entity.Client
import it.ssplus.barbershop.utils.Constants
import java.util.ArrayList

class ClientFragment : Fragment(), View.OnClickListener {

    private lateinit var root: View
    private lateinit var clientViewModel: ClientViewModel

    private lateinit var adapterClient: AdapterClient
    private lateinit var rvListClient: RecyclerView
    lateinit var listClient: ArrayList<Client>
    private lateinit var noDataContainerClient: LinearLayout
    private lateinit var svClient: SearchView
    private lateinit var fabAddClient: FloatingActionButton
    private lateinit var menu: Menu
    private lateinit var phone: String

    companion object {
        const val CALL_PERMISSION = 1
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Constants.Actions.client_item_selected -> handleItemSelected()
                Constants.Actions.call_phone -> handleItemCall(intent)
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
        root = inflater.inflate(R.layout.fragment_client, container, false)

        clientViewModel = ViewModelProvider(this).get(ClientViewModel::class.java)

        fabAddClient = root.findViewById(R.id.fabAddClient)
        fabAddClient.setOnClickListener(this)

        noDataContainerClient = root.findViewById(R.id.noDataContainerClient)

        listClient = arrayListOf()
        adapterClient =
            AdapterClient(this, activity = activity as AppCompatActivity)
        clientViewModel.all.observe(requireActivity(), { items ->
            listClient.clear()
            listClient.addAll(items)
            adapterClient.setData(listClient)
            noDataContainerClient.visibility =
                if (items.isEmpty()) View.VISIBLE else View.GONE
        })

        rvListClient = root.findViewById(R.id.rvListClient)
        rvListClient.adapter = adapterClient
        rvListClient.layoutManager = LinearLayoutManager(context)

        svClient = root.findViewById(R.id.svClient)
        svClient.setOnClickListener {
            svClient.setIconifiedByDefault(true)
            svClient.isFocusable = true
            svClient.isIconified = false
            svClient.requestFocusFromTouch()
        }

        svClient.setOnSearchClickListener {
        }

        svClient.setOnCloseListener {
            false
        }

        val searchEditText =
            svClient.findViewById<View>(androidx.appcompat.R.id.search_src_text) as EditText
        searchEditText.setTextColor(
            AppCompatResources.getColorStateList(
                requireActivity(),
                R.color.secondaryTextColor
            ).defaultColor
        )
        searchEditText.textSize = 16f
        searchEditText.hint = requireActivity().resources.getString(R.string.message_hint_search)
        val searchIcon =
            svClient.findViewById<View>(androidx.appcompat.R.id.search_button) as ImageView
        searchIcon.drawable.setTint(
            AppCompatResources.getColorStateList(
                requireActivity(),
                R.color.primaryTextColor
            ).defaultColor
        )
        val searchMagIcon =
            svClient.findViewById<View>(androidx.appcompat.R.id.search_close_btn) as ImageView
        searchMagIcon.drawable.setTint(
            AppCompatResources.getColorStateList(
                requireActivity(),
                R.color.primaryTextColor
            ).defaultColor
        )
        svClient.queryHint = this.resources.getString(R.string.message_hint_search)
        svClient.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapterClient.filter.filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapterClient.filter.filter(newText)
                return true
            }
        })

        registerReceiver()

        return root
    }

    private fun registerReceiver() {
        val filter = IntentFilter()
        filter.addAction(Constants.Actions.client_item_selected)
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver, filter)
        val filter1 = IntentFilter()
        filter1.addAction(Constants.Actions.call_phone)
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver, filter1)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.client, menu)
        menu.let { this.menu = it }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.client_delete -> confirmDeletion()
            R.id.client_select -> adapterClient.handleSelectAll()
            R.id.client_cancel -> handleCancel()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun handleItemSelected() {
        this.menu.clear()
        requireActivity().menuInflater.inflate(R.menu.client_action, this.menu)
        requireActivity().findViewById<Toolbar>(R.id.toolbar).title =
            requireActivity().resources.getString(R.string.title_selected) + " 1"
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        fabAddClient.visibility = View.GONE
    }

    private fun handleItemCall(intent: Intent) {
        intent.let {
            val extra = it.getStringExtra("phone")
            extra?.let {
                phone = extra
                callPhone()
            }
        }
    }

    private fun callPhone() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CALL_PHONE),
                CALL_PERMISSION
            )
        } else {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phone"))
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            CALL_PERMISSION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    callPhone()
                } else {
                    Toast.makeText(
                        requireActivity(),
                        R.string.you_must_grant_call_permission,
                        Toast.LENGTH_LONG
                    ).show()
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun handleCancel() {
        this.menu.clear()
        requireActivity().menuInflater.inflate(R.menu.client, this.menu)
        requireActivity().findViewById<Toolbar>(R.id.toolbar).title =
            requireActivity().resources.getString(R.string.menu_client)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fabAddClient.visibility = View.VISIBLE
        adapterClient.handleCancel()
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

        textViewTitle.text = getString(R.string.message_delete_clients)
        textViewMessage.text =
            getString(R.string.message_confirm_delete_selected_client)
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
        val items = adapterClient.selectedItems
        clientViewModel.delete(items)
        handleCancel()
    }

    override fun onClick(v: View?) {
        v?.let {
            when (it.id) {
                R.id.fabAddClient -> findNavController().navigate(R.id.manage_client)
            }
        }
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiver)
        super.onDestroy()
    }
}