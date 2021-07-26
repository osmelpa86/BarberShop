package it.ssplus.barbershop.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import it.ssplus.barbershop.R
import it.ssplus.barbershop.databinding.ItemServiceBinding
import it.ssplus.barbershop.model.entity.Service
import it.ssplus.barbershop.ui.reservation.ManageReservationFragment
import it.ssplus.barbershop.utils.Constants
import java.util.*
import kotlin.collections.ArrayList

class AdapterSelectService(
    internal var manageReservationFragment: ManageReservationFragment,
    val activity: AppCompatActivity
) :
    RecyclerView.Adapter<AdapterSelectService.ServiceViewHolder>() {

    internal var services = arrayListOf<Service>()
    var selectedItems = arrayListOf<Service>()

    fun setData(services: ArrayList<Service>) {
        this.services.clear()
        this.services.addAll(services)
        notifyDataSetChanged()
    }

    inner class ServiceViewHolder(val binding: ItemServiceBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val binding = ItemServiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ServiceViewHolder(binding)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val service: Service = services[position]

        if (selectedItems.contains(service)) {
            holder.binding.root.setBackgroundColor(R.color.boxBackgroundDefault)
        } else {
            holder.binding.root.setBackgroundColor(android.R.color.transparent)
        }

        holder.binding.root.setOnClickListener {
            selectItem(holder, service)
            if (selectedItems.size == 1) {
                LocalBroadcastManager.getInstance(activity)
                    .sendBroadcast(Intent(Constants.Actions.service_dialog_item_selected))
            }
        }

        holder.binding.tvNameService.text = service.name
        holder.binding.tvDescriptionService.text = service.description
        holder.binding.tvServiceCost.text = "$" + service.cost.toString()
    }

    @SuppressLint("ResourceAsColor")
    private fun selectItem(holder: AdapterSelectService.ServiceViewHolder, image: Service) {
        if (selectedItems.contains(image)) {
            selectedItems.remove(image)
            holder.binding.root.setBackgroundColor(android.R.color.transparent)
        } else {
            selectedItems.add(image)
            holder.binding.root.setBackgroundColor(R.color.boxBackgroundDefault)
        }
    }

    override fun getItemCount(): Int {
        return services.size
    }

    fun changeDataItem(position: Int, model: Service) {
        services[position] = model
        notifyDataSetChanged()
    }

    var filter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = ArrayList<Service>()
            val filterPattern: String = constraint.toString().lowercase(Locale.ROOT).trim()
            if (filterPattern.isEmpty()) {
                filteredList.addAll(manageReservationFragment.listServices)
            } else {
                for (service in manageReservationFragment.listServices) {
                    if (service.name.lowercase(Locale.ROOT).contains(filterPattern) ||
                        service.description!!.lowercase(Locale.ROOT).contains(filterPattern) ||
                        service.cost.toString().contains(filterPattern)
                    ) {
                        filteredList.add(service)
                    }
                }
            }

            val results = FilterResults()
            results.values = filteredList

            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            services.clear()
            services.addAll(results.values as Collection<Service>)
            notifyDataSetChanged()
        }
    }
}




