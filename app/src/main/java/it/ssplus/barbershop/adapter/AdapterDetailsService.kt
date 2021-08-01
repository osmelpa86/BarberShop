package it.ssplus.barbershop.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import it.ssplus.barbershop.databinding.ItemServiceBinding
import it.ssplus.barbershop.model.entity.Service
import it.ssplus.barbershop.utils.textMoney

class AdapterDetailsService(
    val activity: AppCompatActivity
) :
    RecyclerView.Adapter<AdapterDetailsService.ServiceViewHolder>() {

    internal var services = arrayListOf<Service>()

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

        holder.binding.tvNameService.text = service.name
        holder.binding.tvDescriptionService.text = service.description
        holder.binding.tvServiceCost.text = service.cost.toString().textMoney()
    }

    override fun getItemCount(): Int {
        return services.size
    }
}




