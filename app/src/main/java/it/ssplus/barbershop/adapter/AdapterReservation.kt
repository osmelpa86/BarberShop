package it.ssplus.barbershop.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import it.ssplus.barbershop.R
import it.ssplus.barbershop.databinding.BottomSheetReservationDetailsBinding
import it.ssplus.barbershop.databinding.ItemReservationBinding
import it.ssplus.barbershop.model.entity.Service
import it.ssplus.barbershop.model.pojo.ReservationPojo
import it.ssplus.barbershop.ui.reservation.ReservationFragment
import it.ssplus.barbershop.utils.*
import java.util.*
import kotlin.collections.ArrayList

class AdapterReservation(
    internal var reservationFragment: ReservationFragment,
    val activity: AppCompatActivity
) :
    RecyclerView.Adapter<AdapterReservation.ReservationViewHolder>() {

    internal var reservations = arrayListOf<ReservationPojo>()
    var multiSelect = false
    val selectedItems = arrayListOf<ReservationPojo>()

    fun setData(reservations: ArrayList<ReservationPojo>) {
        this.reservations.clear()
        this.reservations.addAll(reservations)
        notifyDataSetChanged()
    }

    inner class ReservationViewHolder(val binding: ItemReservationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
        val binding =
            ItemReservationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReservationViewHolder(binding)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
        val reservation: ReservationPojo = reservations[position]

        if (selectedItems.contains(reservation)) {
            holder.binding.root.setBackgroundColor(R.color.boxBackgroundDefault)
        } else {
            holder.binding.root.setBackgroundColor(android.R.color.transparent)
        }

        holder.binding.root.setOnLongClickListener {
            if (!multiSelect) {
                multiSelect = true
                LocalBroadcastManager.getInstance(activity)
                    .sendBroadcast(Intent(Constants.Actions.reservation_item_selected))
                selectItem(holder, reservation)
                true
            } else
                false
        }

        holder.binding.root.setOnClickListener {
            if (multiSelect) {
                selectItem(holder, reservation)
                val toolbar = activity.findViewById<Toolbar>(R.id.toolbar)
                toolbar.title =
                    activity.resources.getString(R.string.title_selected) + " " + this.selectedItems.size.toString()
            } else {
                val sheetBinding = BottomSheetReservationDetailsBinding.inflate(
                    LayoutInflater.from(activity),
                    null,
                    false
                )

                val adapterService =
                    AdapterDetailsService(activity = activity)
                adapterService.setData(reservation.services as ArrayList<Service>)

                sheetBinding.rvServicesReservation.adapter = adapterService
                sheetBinding.rvServicesReservation.layoutManager = LinearLayoutManager(activity)

                if (reservation.client.picture != null) {
                    sheetBinding.ivPhotoClientEmpty.visibility = View.GONE
                    sheetBinding.ivPhotoClient.visibility = View.VISIBLE
                    val bitmap = BitmapFactory.decodeByteArray(
                        reservation.client.picture,
                        0,
                        reservation.client.picture.size
                    )
                    sheetBinding.ivPhotoClient.setImageBitmap(bitmap)
                } else {
                    sheetBinding.ivPhotoClient.visibility = View.GONE
                    sheetBinding.ivPhotoClientEmpty.visibility = View.VISIBLE
                }
                sheetBinding.tvNameClient.text = reservation.client.name
                sheetBinding.tvHourTurn.text = formatTime(
                    reservation.turn.hour.split(":")[0].trim().toInt(),
                    reservation.turn.hour.split(":")[1].trim().toInt()
                )
                sheetBinding.tvDateReservation.text = formatDate(reservation.reservation.date)
                sheetBinding.tvAdditionalCost.text =
                    reservation.reservation.additionalCost.toString().textMoney()
                val result = reservation.services.map { it.cost }.sum()
                sheetBinding.tvTotalCost.text =
                    (result + reservation.reservation.additionalCost).toString().textMoney()

                val pos = Constants.serviceStatusName.indexOf(reservation.reservation.status)
                sheetBinding.tvStatus.text =
                    activity.resources.getString(reservation.reservation.status)
                sheetBinding.statusConatinerReservation.background =
                    drawable(activity, Constants.serviceStatusColor[pos])
                sheetBinding.ivStatusIcon.background =
                    drawable(activity, Constants.serviceStatusIcon[pos])

                sheetBinding.toolbar.inflateMenu(R.menu.reservation_details)

                val dialog = BottomSheetDialog(activity, R.style.BottomSheetDialogTheme)
                dialog.setContentView(sheetBinding.root)

                sheetBinding.toolbar.setNavigationOnClickListener { dialog.dismiss() }
                sheetBinding.toolbar.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.reservation_edit -> {
                            val bundle = bundleOf("reservation" to reservation)
                            holder.binding.root.findNavController()
                                .navigate(R.id.manage_reservation, bundle)
                            dialog.dismiss()
                            true
                        }
                        else -> false
                    }
                }

                dialog.show()
            }
        }

        if (reservation.client.picture != null) {
            holder.binding.ivIconClient.visibility = View.GONE
            holder.binding.ivClientPhoto.visibility = View.VISIBLE
            val bitmap = BitmapFactory.decodeByteArray(
                reservation.client.picture,
                0,
                reservation.client.picture.size
            )
            holder.binding.ivClientPhoto.setImageBitmap(bitmap)
        } else {
            holder.binding.ivIconClient.visibility = View.VISIBLE
            holder.binding.ivClientPhoto.visibility = View.GONE
        }

        holder.binding.tvItemClientName.text = reservation.client.name
        holder.binding.tvHourTurn.text = formatTime(
            reservation.turn.hour.split(":")[0].trim().toInt(),
            reservation.turn.hour.split(":")[1].trim().toInt()
        )
        holder.binding.tvDateTurn.text = formatDate(reservation.reservation.date)
        val pos = Constants.serviceStatusName.indexOf(reservation.reservation.status)
        holder.binding.tvStatus.text =
            activity.resources.getString(reservation.reservation.status)
        holder.binding.itemStatusConatinerReservation.background =
            drawable(activity, Constants.serviceStatusColor[pos])
        holder.binding.ivStatusIcon.background =
            drawable(activity, Constants.serviceStatusIcon[pos])
    }

    @SuppressLint("ResourceAsColor")
    private fun selectItem(holder: ReservationViewHolder, image: ReservationPojo) {
        if (selectedItems.contains(image)) {
            selectedItems.remove(image)
            holder.binding.root.setBackgroundColor(android.R.color.transparent)
        } else {
            selectedItems.add(image)
            holder.binding.root.setBackgroundColor(R.color.boxBackgroundDefault)
        }
    }

    override fun getItemCount(): Int {
        return reservations.size
    }

    fun changeDataItem(position: Int, model: ReservationPojo) {
        reservations[position] = model
        notifyDataSetChanged()
    }

    var filter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = ArrayList<ReservationPojo>()
            val filterPattern: String = constraint.toString().lowercase(Locale.ROOT).trim()
            if (filterPattern.isEmpty()) {
                filteredList.addAll(reservationFragment.listReservation)
            } else {
                for (reservation in reservationFragment.listReservation) {
                    if (reservation.client.name.lowercase(Locale.ROOT)
                            .contains(filterPattern) ||
                        reservation.turn.hour.contains(filterPattern) ||
                        reservation.reservation.additionalCost.toString().contains(filterPattern)
                    ) {
                        filteredList.add(reservation)
                    }
                }
            }

            val results = FilterResults()
            results.values = filteredList

            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            reservations.clear()
            reservations.addAll(results.values as Collection<ReservationPojo>)
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
                this.selectedItems.addAll(this.reservations)
            }
            this.selectedItems.size != itemCount -> {
                this.selectedItems.clear()
                this.selectedItems.addAll(this.reservations)
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




