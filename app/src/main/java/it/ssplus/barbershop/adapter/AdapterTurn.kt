package it.ssplus.barbershop.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import it.ssplus.barbershop.R
import it.ssplus.barbershop.model.entity.Turn
import it.ssplus.barbershop.ui.turn.TurnFragment
import it.ssplus.barbershop.utils.Constants
import java.util.*
import kotlin.collections.ArrayList

class AdapterTurn(
    internal var turnFragment: TurnFragment,
    val activity: AppCompatActivity
) :
    RecyclerView.Adapter<AdapterTurn.TurnViewHolder>() {

    internal var turns = arrayListOf<Turn>()
    var multiSelect = false
    val selectedItems = arrayListOf<Turn>()

    fun setData(turns: ArrayList<Turn>) {
        this.turns.clear()
        this.turns.addAll(turns)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TurnViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.item_turn,
            parent,
            false
        )
        return TurnViewHolder(v)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: TurnViewHolder, position: Int) {
        val turn = turns[position]

        if (selectedItems.contains(turn)) {
            holder.itemView.setBackgroundColor(R.color.boxBackgroundDefault)
        } else {
            holder.itemView.setBackgroundColor(android.R.color.transparent)
        }

        holder.itemView.setOnLongClickListener {
            if (!multiSelect) {
                multiSelect = true
                LocalBroadcastManager.getInstance(activity)
                    .sendBroadcast(Intent(Constants.Actions.turn_item_selected))
                selectItem(holder, turn)
                true
            } else
                false
        }

        holder.itemView.setOnClickListener {
            if (multiSelect) {
                selectItem(holder, turn)
                val toolbar = activity.findViewById<Toolbar>(R.id.toolbar)
                toolbar.title =
                    activity.resources.getString(R.string.title_selected) + " " + this.selectedItems.size.toString()
            } else {
                val view = LayoutInflater.from(activity)
                    .inflate(R.layout.bottom_sheet_turn_details, null, false)

                val toolbar: Toolbar = view.findViewById(R.id.toolbar)

                val tvNameTurn: TextView = view.findViewById(R.id.tvNameTurn)
                val tvHourTurn: TextView = view.findViewById(R.id.tvHourTurn)

                tvNameTurn.text = turns[position].name
                tvHourTurn.text = turns[position].hour

                toolbar.inflateMenu(R.menu.turn_details)

                val dialog = BottomSheetDialog(activity, R.style.BottomSheetDialogTheme)
                dialog.setContentView(view)

                toolbar.setNavigationOnClickListener { dialog.dismiss() }
                toolbar.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.turn_edit -> {
                            turnFragment.turn = turns[position]
                            turnFragment.add()
                            dialog.dismiss()
                            true
                        }
                        else -> false
                    }
                }

                dialog.show()
            }
        }

        holder.tvNameTurn.text = turn.name
        holder.tvHourTurn.text = turn.hour
    }

    @SuppressLint("ResourceAsColor")
    private fun selectItem(holder: TurnViewHolder, image: Turn) {
        if (selectedItems.contains(image)) {
            selectedItems.remove(image)
            holder.itemView.setBackgroundColor(android.R.color.transparent)
        } else {
            selectedItems.add(image)
            holder.itemView.setBackgroundColor(R.color.boxBackgroundDefault)
        }
    }

    inner class TurnViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val tvNameTurn: TextView
        internal val tvHourTurn: TextView

        init {
            tvNameTurn =
                itemView.findViewById<View>(R.id.tvNameTurn) as TextView
            tvHourTurn =
                itemView.findViewById<View>(R.id.tvHourTurn) as TextView
        }
    }

    override fun getItemCount(): Int {
        return turns.size
    }

    fun changeDataItem(position: Int, model: Turn) {
        turns[position] = model
        notifyDataSetChanged()
    }

    var filter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = ArrayList<Turn>()
            val filterPattern: String = constraint.toString().lowercase(Locale.ROOT).trim()
            if (filterPattern.isEmpty()) {
                filteredList.addAll(turnFragment.listTurn)
            } else {
                for (turn in turnFragment.listTurn) {
                    if (turn.name.lowercase(Locale.ROOT)
                            .contains(filterPattern) || turn.name.contains(filterPattern)
                    ) {
                        filteredList.add(turn)
                    }
                }
            }

            val results = FilterResults()
            results.values = filteredList

            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            turns.clear()
            turns.addAll(results.values as Collection<Turn>)
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
                this.selectedItems.addAll(this.turns)
            }
            this.selectedItems.size != itemCount -> {
                this.selectedItems.clear()
                this.selectedItems.addAll(this.turns)
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




