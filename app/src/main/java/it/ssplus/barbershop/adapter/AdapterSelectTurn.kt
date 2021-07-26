package it.ssplus.barbershop.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import it.ssplus.barbershop.R
import it.ssplus.barbershop.databinding.ItemTurnBinding
import it.ssplus.barbershop.model.entity.Turn
import it.ssplus.barbershop.ui.reservation.ManageReservationFragment
import it.ssplus.barbershop.utils.Constants
import java.util.*
import kotlin.collections.ArrayList

class AdapterSelectTurn(
    internal var manageReservationFragment: ManageReservationFragment,
    val activity: AppCompatActivity,
    selected: Turn?
) :
    RecyclerView.Adapter<AdapterSelectTurn.TurnViewHolder>() {

    internal var turns = arrayListOf<Turn>()
    private var checkedPosition = selected

    fun setData(turns: ArrayList<Turn>) {
        this.turns.clear()
        this.turns.addAll(turns)
        notifyDataSetChanged()
    }

    inner class TurnViewHolder(val binding: ItemTurnBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(turn: Turn) {
            if (checkedPosition == turns[adapterPosition]) {
                binding.root.background = ContextCompat.getDrawable(
                    activity,
                    R.drawable.item_color_bg_roud_shape_linear
                )
            } else {
                binding.root.background =
                    ContextCompat.getDrawable(activity, R.drawable.item_color_bg_transparent)
            }

            binding.root.setOnClickListener {
                binding.root.background =
                    ContextCompat.getDrawable(activity, R.drawable.item_color_bg_roud_shape_linear)
                if (checkedPosition != turns[adapterPosition]) {
                    notifyItemChanged(turns.indexOf(checkedPosition))
                    checkedPosition = turns[adapterPosition]
                }

                LocalBroadcastManager.getInstance(activity)
                    .sendBroadcast(Intent(Constants.Actions.turn_dialog_item_selected))
            }

            binding.tvNameTurn.text = turn.name
            binding.tvHourTurn.text = turn.hour
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TurnViewHolder {
        val binding= ItemTurnBinding.inflate(LayoutInflater.from(parent.context),null,false)
        return TurnViewHolder(binding)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: TurnViewHolder, position: Int) {
        val turn = turns[position]
        holder.bind(turn)
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
                filteredList.addAll(manageReservationFragment.listTurns)
            } else {
                for (turn in manageReservationFragment.listTurns) {
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

    fun getSelected(): Turn? {
        return if (checkedPosition != null) {
            checkedPosition
        } else null
    }
}




