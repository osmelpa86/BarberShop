package it.ssplus.barbershop.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import it.ssplus.barbershop.R
import it.ssplus.barbershop.databinding.BottomSheetServiceDetailsBinding
import it.ssplus.barbershop.databinding.ItemServiceBinding
import it.ssplus.barbershop.model.entity.Service
import it.ssplus.barbershop.ui.service.ServiceFragment
import it.ssplus.barbershop.utils.Constants
import java.util.*
import kotlin.collections.ArrayList

class AdapterService(
    internal var serviceFragment: ServiceFragment,
    val activity: AppCompatActivity
) :
    RecyclerView.Adapter<AdapterService.ServiceViewHolder>() {

    internal var services = arrayListOf<Service>()
    var multiSelect = false
    val selectedItems = arrayListOf<Service>()

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
        val service = services[position]

        if (selectedItems.contains(service)) {
            holder.binding.root.setBackgroundColor(R.color.boxBackgroundDefault)
        } else {
            holder.binding.root.setBackgroundColor(android.R.color.transparent)
        }

        holder.binding.root.setOnLongClickListener {
            if (!multiSelect) {
                multiSelect = true
                LocalBroadcastManager.getInstance(activity)
                    .sendBroadcast(Intent(Constants.Actions.service_item_selected))
                selectItem(holder, service)
                true
            } else
                false
        }

        holder.binding.root.setOnClickListener {
            if (multiSelect) {
                selectItem(holder, service)
                val toolbar = activity.findViewById<Toolbar>(R.id.toolbar)
                toolbar.title =
                    activity.resources.getString(R.string.title_selected) + " " + this.selectedItems.size.toString()
            } else {
                val sheetBinding = BottomSheetServiceDetailsBinding.inflate(
                    LayoutInflater.from(activity),
                    null,
                    false
                )

                sheetBinding.tvNameService.text = services[position].name
                sheetBinding.tvDescriptionService.text = services[position].description
                sheetBinding.tvCostService.text = "$" + services[position].cost.toString()
                sheetBinding.toolbar.inflateMenu(R.menu.service_details)

                val dialog = BottomSheetDialog(activity, R.style.BottomSheetDialogTheme)
                dialog.setContentView(sheetBinding.root)

                sheetBinding.toolbar.setNavigationOnClickListener { dialog.dismiss() }
                sheetBinding.toolbar.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.service_edit -> {
                            serviceFragment.service = services[position]
                            serviceFragment.add()
                            dialog.dismiss()
                            true
                        }
                        else -> false
                    }
                }

                dialog.show()
            }
        }

        holder.binding.tvNameService.text = service.name
        holder.binding.tvDescriptionService.text = service.description
        holder.binding.tvServiceCost.text = "$" + service.cost.toString()
    }

    @SuppressLint("ResourceAsColor")
    private fun selectItem(holder: ServiceViewHolder, image: Service) {
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
                filteredList.addAll(serviceFragment.listService)
            } else {
                for (service in serviceFragment.listService) {
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

    @SuppressLint("ResourceAsColor")
    fun handleCancel() {
        multiSelect = false
        selectedItems.clear()
        notifyDataSetChanged()
    }

    fun handleSelectAll() {
        when {
            this.selectedItems.size == 0 -> {
                this.selectedItems.addAll(this.services)
            }
            this.selectedItems.size != itemCount -> {
                this.selectedItems.clear()
                this.selectedItems.addAll(this.services)
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




