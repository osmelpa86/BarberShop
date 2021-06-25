package it.ssplus.barbershop.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import it.ssplus.barbershop.R
import it.ssplus.barbershop.ui.expense_category.ExpenseCategoryFragment
import it.ssplus.barbershop.utils.Constants


class AdapterColorList :
    RecyclerView.Adapter<AdapterColorList.ColorViewHolder> {

    internal var icons = Constants.roundIcons
    internal var names = Constants.colorNames
    internal var expenseCategoryFragment: ExpenseCategoryFragment
    private var checkedPosition = -1

    constructor(expenseCategoryFragment: ExpenseCategoryFragment, selected: Int) {
        this.expenseCategoryFragment = expenseCategoryFragment
        checkedPosition = selected
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.item_color_adapter, parent, false)
        return ColorViewHolder(v)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.bind(names[position], icons[position])
    }

    inner class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewIndicator: ImageView
        val textViewColor: TextView
        val itemColorContainer: ConstraintLayout

        init {
            imageViewIndicator = itemView.findViewById(R.id.imageViewIndicator)
            textViewColor = itemView.findViewById(R.id.textViewColor)
            itemColorContainer = itemView.findViewById(R.id.itemColorContainer)
        }

        fun bind(name: Int, icon: Int) {
            if (checkedPosition == -1) {
                itemColorContainer.background = expenseCategoryFragment.requireActivity()
                    .getDrawable(R.drawable.item_color_bg_transparent)
            } else {
                if (checkedPosition == adapterPosition) {
                    itemColorContainer.background = expenseCategoryFragment.requireActivity()
                        .getDrawable(R.drawable.item_color_bg_roud_shape)
                } else {
                    itemColorContainer.background = expenseCategoryFragment.requireActivity()
                        .getDrawable(R.drawable.item_color_bg_transparent)
                }
            }

            itemView.setOnClickListener {
                itemColorContainer.background = expenseCategoryFragment.requireActivity()
                    .getDrawable(R.drawable.item_color_bg_roud_shape)
                if (checkedPosition != adapterPosition) {
                    notifyItemChanged(checkedPosition)
                    checkedPosition = adapterPosition
                }
            }
            imageViewIndicator.setImageResource(icon)
            textViewColor.setText(name)
        }
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemCount(): Int {
        return icons.size
    }

    fun getSelected(): Int? {
        return if (checkedPosition != -1) {
            checkedPosition
        } else null
    }

    fun objectPosition(color: Int): Int {
        return icons.indexOf(color)
    }
}




