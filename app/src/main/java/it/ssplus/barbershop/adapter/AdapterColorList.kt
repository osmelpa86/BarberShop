package it.ssplus.barbershop.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import it.ssplus.barbershop.R
import it.ssplus.barbershop.databinding.ItemColorBinding
import it.ssplus.barbershop.ui.expense_category.ExpenseCategoryFragment
import it.ssplus.barbershop.utils.Constants
import it.ssplus.barbershop.utils.drawable


class AdapterColorList(
    internal var expenseCategoryFragment: ExpenseCategoryFragment,
    selected: Int
) :
    RecyclerView.Adapter<AdapterColorList.ColorViewHolder>() {

    private var icons = Constants.roundIcons
    private var names = Constants.colorNames
    private var checkedPosition = selected

    inner class ColorViewHolder(private val binding: ItemColorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(name: Int, icon: Int) {
            if (checkedPosition == -1) {
                binding.root.background = drawable(expenseCategoryFragment.requireContext(),R.drawable.item_color_bg_transparent)
            } else {
                if (checkedPosition == adapterPosition) {
                    binding.root.background = drawable(expenseCategoryFragment.requireContext(),R.drawable.item_color_bg_roud_shape)
                } else {
                    binding.root.background = drawable(expenseCategoryFragment.requireContext(),R.drawable.item_color_bg_transparent)
                }
            }

            binding.root.setOnClickListener {
                binding.root.background = drawable(expenseCategoryFragment.requireContext(),R.drawable.item_color_bg_roud_shape)
                if (checkedPosition != adapterPosition) {
                    notifyItemChanged(checkedPosition)
                    checkedPosition = adapterPosition
                }
                LocalBroadcastManager.getInstance(expenseCategoryFragment.requireActivity())
                    .sendBroadcast(Intent(Constants.Actions.expense_category_color_item_selected))
            }
            binding.imageViewIndicator.setImageResource(icon)
            binding.textViewColor.setText(name)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val binding= ItemColorBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ColorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.bind(names[position], icons[position])
    }

    override fun getItemCount(): Int {
        return icons.size
    }

    fun getSelected(): Int? {
        return if (checkedPosition != -1) {
            checkedPosition
        } else null
    }
}




