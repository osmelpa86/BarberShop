package it.ssplus.barbershop.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import it.ssplus.barbershop.R
import it.ssplus.barbershop.ui.expense_category.ExpenseCategoryFragment
import it.ssplus.barbershop.utils.Constants


class AdapterColorList(
    internal var expenseCategoryFragment: ExpenseCategoryFragment,
    selected: Int
) :
    RecyclerView.Adapter<AdapterColorList.ColorViewHolder>() {

    private var icons = Constants.roundIcons
    private var names = Constants.colorNames
    private var checkedPosition = selected

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
        private val textViewColor: TextView
        val itemColorContainer: ConstraintLayout

        init {
            imageViewIndicator = itemView.findViewById(R.id.imageViewIndicator)
            textViewColor = itemView.findViewById(R.id.textViewColor)
            itemColorContainer = itemView.findViewById(R.id.itemColorContainer)
        }

        fun bind(name: Int, icon: Int) {
            if (checkedPosition == -1) {
                itemColorContainer.background = ContextCompat.getDrawable(
                    expenseCategoryFragment.requireActivity(),
                    R.drawable.item_color_bg_transparent
                )
            } else {
                if (checkedPosition == adapterPosition) {
                    itemColorContainer.background = ContextCompat.getDrawable(
                        expenseCategoryFragment.requireActivity(),
                        R.drawable.item_color_bg_roud_shape
                    )
                } else {
                    itemColorContainer.background = ContextCompat.getDrawable(expenseCategoryFragment.requireActivity(),R.drawable.item_color_bg_transparent)
                }
            }

            itemView.setOnClickListener {
                itemColorContainer.background = ContextCompat.getDrawable(expenseCategoryFragment.requireActivity(),R.drawable.item_color_bg_roud_shape)
                if (checkedPosition != adapterPosition) {
                    notifyItemChanged(checkedPosition)
                    checkedPosition = adapterPosition
                }
            }
            imageViewIndicator.setImageResource(icon)
            textViewColor.setText(name)
        }
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




