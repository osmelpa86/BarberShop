package it.ssplus.barbershop.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import de.hdodenhof.circleimageview.CircleImageView
import it.ssplus.barbershop.R
import it.ssplus.barbershop.model.entity.Client
import it.ssplus.barbershop.ui.client.ClientFragment
import it.ssplus.barbershop.utils.Constants
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.*
import kotlin.collections.ArrayList

class AdapterClient(
    internal var clientFragment: ClientFragment,
    val activity: AppCompatActivity
) :
    RecyclerView.Adapter<AdapterClient.ClientViewHolder>() {

    internal var clients = arrayListOf<Client>()
    var multiSelect = false
    val selectedItems = arrayListOf<Client>()

    fun setData(clients: ArrayList<Client>) {
        this.clients.clear()
        this.clients.addAll(clients)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.item_client,
            parent,
            false
        )
        return ClientViewHolder(v)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        val client = clients[position]

        if (selectedItems.contains(client)) {
            holder.itemView.setBackgroundColor(R.color.boxBackgroundDefault)
        } else {
            holder.itemView.setBackgroundColor(android.R.color.transparent)
        }

        holder.itemView.setOnLongClickListener {
            if (!multiSelect) {
                multiSelect = true
                LocalBroadcastManager.getInstance(activity)
                    .sendBroadcast(Intent(Constants.Actions.client_item_selected))
                selectItem(holder, client)
                true
            } else
                false
        }

        holder.itemView.setOnClickListener {
            if (multiSelect) {
                selectItem(holder, client)
                val toolbar = activity.findViewById<Toolbar>(R.id.toolbar)
                toolbar.title =
                    activity.resources.getString(R.string.title_selected) + " " + this.selectedItems.size.toString()
                holder.ibItemClientCall.visibility = View.GONE
            } else {
                val view = LayoutInflater.from(activity)
                    .inflate(R.layout.bottom_sheet_client_details, null, false)

                val toolbar: Toolbar = view.findViewById(R.id.toolbar)

                val ivPhotoClient: CircleImageView = view.findViewById(R.id.ivPhotoClient)
                val ivPhotoClientEmpty: CircleImageView = view.findViewById(R.id.ivPhotoClientEmpty)
                val tvNameClient: TextView = view.findViewById(R.id.tvNameClient)
                val llButtonCallPhone: LinearLayout = view.findViewById(R.id.llButtonCallPhone)
                val tvCellPhoneNumber: TextView = view.findViewById(R.id.tvCellPhoneNumber)
                val ivSMS: ImageButton = view.findViewById(R.id.ivSMS)
                val llButtonPhone: LinearLayout = view.findViewById(R.id.llButtonPhone)
                val tvPhoneNumber: TextView = view.findViewById(R.id.tvPhoneNumber)
                val tvDescription: TextView = view.findViewById(R.id.tvDescription)

                if (client.picture != null) {
                    ivPhotoClientEmpty.visibility = View.GONE
                    ivPhotoClient.visibility = View.VISIBLE
                    val bitmap = BitmapFactory.decodeByteArray(
                        client.picture,
                        0,
                        client.picture.size
                    )
                    ivPhotoClient.setImageBitmap(bitmap)
                } else {
                    ivPhotoClient.visibility = View.GONE
                    ivPhotoClientEmpty.visibility = View.VISIBLE
                }
                tvNameClient.text = client.name
                tvCellPhoneNumber.text = client.cellPhone
                tvPhoneNumber.text = client.phoneNumber
                tvDescription.text = client.observation

                ivSMS.setOnClickListener {
                    val sms = Intent(Intent.ACTION_SENDTO)
                    sms.data = Uri.parse("sms:${client.cellPhone}")
                    activity.startActivity(sms)
                }

                llButtonCallPhone.setOnClickListener {
                    val call = Intent(Constants.Actions.call_phone)
                    call.putExtra("phone", client.cellPhone)
                    LocalBroadcastManager.getInstance(activity).sendBroadcast(call)
                }

                llButtonPhone.visibility =
                    if (client.phoneNumber != null) View.VISIBLE else View.GONE
                llButtonPhone.setOnClickListener {
                    val call = Intent(Constants.Actions.call_phone)
                    call.putExtra("phone", client.phoneNumber)
                    LocalBroadcastManager.getInstance(activity).sendBroadcast(call)
                }

                toolbar.inflateMenu(R.menu.client_details)

                val dialog = BottomSheetDialog(activity, R.style.BottomSheetDialogTheme)
                dialog.setContentView(view)

                toolbar.setNavigationOnClickListener { dialog.dismiss() }
                toolbar.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.client_edit -> {
                            val bundle = bundleOf("client" to client)
                            holder.itemView.findNavController().navigate(R.id.manage_client, bundle)
                            dialog.dismiss()
                            true
                        }
                        else -> false
                    }
                }

                dialog.show()
            }
        }

        if (client.picture != null) {
            holder.ivIconClient.visibility = View.GONE
            holder.ivClientPhoto.visibility = View.VISIBLE
            val bitmap = BitmapFactory.decodeByteArray(
                client.picture,
                0,
                client.picture.size
            )
            holder.ivClientPhoto.setImageBitmap(bitmap)
        } else {
            holder.ivIconClient.visibility = View.VISIBLE
            holder.ivClientPhoto.visibility = View.GONE
        }

        holder.tvItemClientName.text = client.name
        holder.ibItemClientCall.visibility = View.VISIBLE
        holder.ibItemClientCall.setOnClickListener {
            if (client.phoneNumber == null) {
                val call = Intent(Constants.Actions.call_phone)
                call.putExtra("phone", client.cellPhone)
                LocalBroadcastManager.getInstance(activity).sendBroadcast(call)
            } else {
                photoMenu(holder.ibItemClientCall, client.cellPhone!!, client.phoneNumber)
            }
        }
    }

    private fun photoMenu(view: View, paramCell: String, paramPhone: String) {
        val wrapper = ContextThemeWrapper(activity, R.style.AppTheme_PopupMenu)
        val popup = PopupMenu(wrapper, view)
        try {
            val fields: Array<Field> = popup.javaClass.declaredFields
            for (field in fields) {
                if ("mPopup" == field.name) {
                    field.isAccessible = true
                    val menuPopupHelper: Any = field.get(popup)
                    val classPopupHelper =
                        Class.forName(menuPopupHelper.javaClass.name)
                    val setForceIcons: Method = classPopupHelper.getMethod(
                        "setForceShowIcon",
                        Boolean::class.javaPrimitiveType
                    )
                    setForceIcons.invoke(menuPopupHelper, true)
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        popup.menuInflater.inflate(R.menu.client_call, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.client_cell_phone_call -> {
                    val call = Intent(Constants.Actions.call_phone)
                    call.putExtra("phone", paramCell)
                    LocalBroadcastManager.getInstance(activity).sendBroadcast(call)
                    true
                }
                R.id.client_phone_call -> {
                    val call = Intent(Constants.Actions.call_phone)
                    call.putExtra("phone", paramPhone)
                    LocalBroadcastManager.getInstance(activity).sendBroadcast(call)
                    true
                }
                else -> false
            }
        }

        popup.menu.findItem(R.id.client_cell_phone_call).title = paramCell
        popup.menu.findItem(R.id.client_phone_call).title = paramPhone

        popup.show()
    }

    @SuppressLint("ResourceAsColor")
    private fun selectItem(holder: ClientViewHolder, image: Client) {
        if (selectedItems.contains(image)) {
            selectedItems.remove(image)
            holder.itemView.setBackgroundColor(android.R.color.transparent)
        } else {
            selectedItems.add(image)
            holder.itemView.setBackgroundColor(R.color.boxBackgroundDefault)
        }
    }

    inner class ClientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val ivClientPhoto: CircleImageView
        internal val ivIconClient: ImageView
        internal val tvItemClientName: TextView
        internal val ibItemClientCall: ImageButton

        init {
            ivClientPhoto =
                itemView.findViewById<View>(R.id.ivClientPhoto) as CircleImageView
            ivIconClient =
                itemView.findViewById<View>(R.id.ivIconClient) as ImageView
            tvItemClientName =
                itemView.findViewById<View>(R.id.tvItemClientName) as TextView
            ibItemClientCall =
                itemView.findViewById<View>(R.id.ibItemClientCall) as ImageButton
        }
    }

    override fun getItemCount(): Int {
        return clients.size
    }

    fun changeDataItem(position: Int, model: Client) {
        clients[position] = model
        notifyDataSetChanged()
    }

    var filter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = ArrayList<Client>()
            val filterPattern: String = constraint.toString().lowercase(Locale.ROOT).trim()
            if (filterPattern.isEmpty()) {
                filteredList.addAll(clientFragment.listClient)
            } else {
                for (client in clientFragment.listClient) {
                    if (client.name.lowercase(Locale.ROOT)
                            .contains(filterPattern)
                    ) {
                        filteredList.add(client)
                    }
                }
            }

            val results = FilterResults()
            results.values = filteredList

            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            clients.clear()
            clients.addAll(results.values as Collection<Client>)
            notifyDataSetChanged()
        }
    }

    @SuppressLint("ResourceAsColor")
    fun handleCancel() {
        multiSelect = false
        selectedItems.clear()
        notifyDataSetChanged()
    }

    fun handleSelectAll() {
        when {
            this.selectedItems.size == 0 -> {
                this.selectedItems.addAll(this.clients)
            }
            this.selectedItems.size != itemCount -> {
                this.selectedItems.clear()
                this.selectedItems.addAll(this.clients)
            }
            this.selectedItems.size == itemCount -> {
                this.selectedItems.clear()
            }
        }
        val toolbar = activity.findViewById<Toolbar>(R.id.toolbar)
        toolbar.title =
            activity.resources.getString(R.string.title_selected) + " " + this.selectedItems.size.toString()
        notifyDataSetChanged()
    }
}




