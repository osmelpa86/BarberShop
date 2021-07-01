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

    object DateFormats {
        const val database = "yyyy-MM-dd"
        const val display = "dd/MM/yyyy"
    }

    object Actions {
        const val expense_category_item_selected = "expense_category_item_selected"
        const val expense_item_selected = "expense_item_selected"
    }
}