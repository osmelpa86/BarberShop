package it.ssplus.barbershop.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import it.ssplus.barbershop.R
import it.ssplus.barbershop.databinding.ItemSelectClientBinding
import it.ssplus.barbershop.model.entity.Client
import it.ssplus.barbershop.ui.reservation.ManageReservationFragment
import it.ssplus.barbershop.utils.Constants
import it.ssplus.barbershop.utils.drawable
import it.ssplus.barbershop.utils.isNull
import java.util.*
import kotlin.collections.ArrayList

class AdapterSelectClient(
    internal var manageReservationFragment: ManageReservationFragment,
    val activity: AppCompatActivity,
    selected: Client?
) :
    RecyclerView.Adapter<AdapterSelectClient.ClientViewHolder>() {

    internal var clients = arrayListOf<Client>()
    private var checkedPosition = selected

    fun setData(clients: ArrayList<Client>) {
        this.clients.clear()
        this.clients.addAll(clients)
        notifyDataSetChanged()
    }

    inner class ClientViewHolder(val binding: ItemSelectClientBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(client: Client) {
            if (checkedPosition.isNull()) {
                binding.root.background = drawable(
                    activity,
                    R.drawable.item_color_bg_transparent
                )
            } else {
                if (checkedPosition == clients[adapterPosition]) {
                    binding.root.background = drawable(
                        activity,
                        R.drawable.item_color_bg_roud_shape_linear
                    )
                } else {
                    binding.root.background =
                        drawable(activity, R.drawable.item_color_bg_transparent)
                }
            }

            binding.root.setOnClickListener {
                binding.root.background =
                    drawable(activity, R.drawable.item_color_bg_roud_shape_linear)
                if (checkedPosition != clients[adapterPosition]) {
                    notifyItemChanged(clients.indexOf(checkedPosition))
                    checkedPosition = clients[adapterPosition]
                }
                LocalBroadcastManager.getInstance(activity)
                    .sendBroadcast(Intent(Constants.Actions.client_dialog_item_selected))
            }

            if (client.picture != null) {
                binding.ivIconClient.visibility = View.GONE
                binding.ivClientPhoto.visibility = View.VISIBLE
                val bitmap = BitmapFactory.decodeByteArray(
                    client.picture,
                    0,
                    client.picture.size
                )
                binding.ivClientPhoto.setImageBitmap(bitmap)
            } else {
                binding.ivIconClient.visibility = View.VISIBLE
                binding.ivClientPhoto.visibility = View.GONE
            }

            binding.tvItemClientName.text = client.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val binding =
            ItemSelectClientBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClientViewHolder(binding)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        val client = clients[position]
        holder.bind(client)
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
                filteredList.addAll(manageReservationFragment.listClients)
            } else {
                for (client in manageReservationFragment.listClients) {
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

    fun getSelected(): Client? {
        return if (checkedPosition != null) {
            checkedPosition
        } else null
    }
}




