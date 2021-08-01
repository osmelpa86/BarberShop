package it.ssplus.barbershop.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import it.ssplus.barbershop.databinding.ItemStatusDropdownBinding
import it.ssplus.barbershop.utils.Constants
import it.ssplus.barbershop.utils.drawable

class AdapterStatus(c: Activity, @LayoutRes itemLayoutResourceId: Int) :
    ArrayAdapter<Any>(c, itemLayoutResourceId) {
    private val names = Constants.serviceStatusName
    private val colorList = Constants.serviceStatusColor
    private val iconList = Constants.serviceStatusIcon

    override fun getCount(): Int {
        return names.size
    }

    override fun getItem(position: Int): String {
        return context.resources.getString(names[position])
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: StatusViewHolder
        if (convertView == null) {
            val binding =
                ItemStatusDropdownBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            holder = StatusViewHolder(binding)
            holder.view = binding.root
            binding.textViewName.text =
                context.resources.getString(names[position])
            binding.imageViewIcon.background = drawable(context, colorList[position])
            binding.imageViewIcon.setImageResource(iconList[position])
            holder.view.tag = holder
        } else {
            holder = convertView.tag as StatusViewHolder
        }

        return holder.view
    }

    private class StatusViewHolder(binding: ItemStatusDropdownBinding) {
        var view: View = binding.root
    }
}