package it.ssplus.barbershop.utils

import it.ssplus.barbershop.R


object Constants {

    val roundIcons = arrayListOf(
        R.drawable.round_shape_blue_grey,
        R.drawable.round_shape_teal,
        R.drawable.round_shape_green,
        R.drawable.round_shape_yellow,
        R.drawable.round_shape_orange,
        R.drawable.round_shape_red,
        R.drawable.round_shape_indigo,
        R.drawable.round_shape_blue,
        R.drawable.round_shape_deep_purple,
        R.drawable.round_shape_purple,
        R.drawable.round_shape_pink,
    )

    val colorNames = arrayListOf(
        R.string.grey,
        R.string.teal,
        R.string.green,
        R.string.yellow,
        R.string.orange,
        R.string.red,
        R.string.indigo,
        R.string.blue,
        R.string.deep_purple,
        R.string.purple,
        R.string.pink,

        )

    object Actions {
        const val expense_category_item_selected = "expense_category_item_selected"
        const val expense_item_selected = "expense_item_selected"
        const val turn_item_selected = "turn_item_selected"
        const val client_item_selected = "client_item_selected"
        const val service_item_selected = "service_item_selected"
        const val reservation_item_selected = "reservation_item_selected"
        const val expense_category_expense_item_selected = "expense_category_expense_item_selected"
        const val expense_category_color_item_selected = "expense_category_color_item_selected"
        const val turn_dialog_item_selected = "turn_dialog_item_selected"
        const val client_dialog_item_selected = "client_dialog_item_selected"
        const val service_dialog_item_selected = "service_dialog_item_selected"

        const val call_phone = "call_phone"
    }

    val serviceStatusName = arrayListOf(
        R.string.service_status_open,
        R.string.service_status_cancel,
        R.string.service_status_completed,
        R.string.service_status_closed
    )

    val serviceStatusColor = arrayListOf(
        R.drawable.status_round_shape_blue,
        R.drawable.status_round_shape_red,
        R.drawable.status_round_shape_green,
        R.drawable.status_round_shape_orange
    )

    val serviceStatusIcon = arrayListOf(
        R.drawable.ic_more_horiz_24,
        R.drawable.ic_close,
        R.drawable.ic_check,
        R.drawable.ic_close
    )
}