package it.ssplus.barbershop.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import it.ssplus.barbershop.R
import it.ssplus.barbershop.databinding.SnackbarMessageSimpleBinding
import java.text.SimpleDateFormat
import java.util.*

fun String.textMoney(): String = "$$this"

fun formatTime(hour: Int, minute: Int): String {
    val isPM: Boolean = hour >= 12
    return String.format(
        "%2d:%02d %s",
        if (!(hour !== 12 && hour !== 0)) 12 else hour % 12,
        minute,
        if (isPM) "PM" else "AM"
    )
}

fun formatDate(date: Date): String = SimpleDateFormat("dd/M/yyyy").format(date)
fun parseDate(date: String): Date = SimpleDateFormat("dd/M/yyyy").parse(date)

fun Activity.color(@ColorRes color: Int) = ContextCompat.getColor(this, color)
fun Fragment.color(@ColorRes color: Int) = ContextCompat.getColor(requireActivity(), color)

fun Activity.colorStateList(@ColorRes color: Int) =
    AppCompatResources.getColorStateList(this, color).defaultColor

fun Fragment.colorStateList(@ColorRes color: Int) =
    AppCompatResources.getColorStateList(requireActivity(), color).defaultColor

fun colorStateListValidation(activity: Activity, @ColorRes color: Int) =
    AppCompatResources.getColorStateList(activity, color)

fun Activity.drawable(@DrawableRes drawable: Int) =
    ContextCompat.getDrawable(this, drawable)

fun Fragment.drawable(@DrawableRes drawable: Int) =
    ContextCompat.getDrawable(requireActivity(), drawable)

fun drawable(context: Context, @DrawableRes drawable: Int) =
    ContextCompat.getDrawable(context, drawable)

fun Any?.isNull() = this == null

fun Activity.snackbar(root: View, message: Int) {
    val customSnackBar: Snackbar = Snackbar.make(
        root,
        "",
        Snackbar.LENGTH_LONG
    )
    SnackBarUtil.getColorfulAndDrawableBacgroundSnackBar(
        customSnackBar,
        this,
        R.drawable.snackbar_background_roud_shape,
        R.color.primaryTextColor,
        R.color.primaryTextColor
    )
    val layout: Snackbar.SnackbarLayout =
        customSnackBar.view as Snackbar.SnackbarLayout
    val snackBinding =
        SnackbarMessageSimpleBinding.inflate(layoutInflater, null, false)
    snackBinding.smpSimpleMessage.text =
        resources.getString(message)
    snackBinding.smpCancel.setOnClickListener { customSnackBar.dismiss() }
    layout.setPadding(0, 0, 0, 0)
    layout.addView(snackBinding.root, 0)
    customSnackBar.show()
}

fun Fragment.snackbar(root: View, message: Int) {
    val customSnackBar: Snackbar = Snackbar.make(
        root,
        "",
        Snackbar.LENGTH_LONG
    )
    SnackBarUtil.getColorfulAndDrawableBacgroundSnackBar(
        customSnackBar,
        this.requireActivity(),
        R.drawable.snackbar_background_roud_shape,
        R.color.primaryTextColor,
        R.color.primaryTextColor
    )
    val layout: Snackbar.SnackbarLayout =
        customSnackBar.view as Snackbar.SnackbarLayout
    val snackBinding =
        SnackbarMessageSimpleBinding.inflate(layoutInflater, null, false)
    snackBinding.smpSimpleMessage.text =
        resources.getString(message)
    snackBinding.smpCancel.setOnClickListener { customSnackBar.dismiss() }
    layout.setPadding(0, 0, 0, 0)
    layout.addView(snackBinding.root, 0)
    customSnackBar.show()
}

fun Fragment.searchview(s: SearchView) {
    s.setOnClickListener {
        s.setIconifiedByDefault(true)
        s.isFocusable = true
        s.isIconified = false
        s.requestFocusFromTouch()
    }

    s.setOnSearchClickListener {
    }

    s.setOnCloseListener {
        false
    }

    val searchEditText =
        s.findViewById<View>(androidx.appcompat.R.id.search_src_text) as EditText
    searchEditText.setTextColor(
        colorStateList(R.color.secondaryTextColor)
    )
    searchEditText.textSize = 16f
    searchEditText.hint = requireActivity().resources.getString(R.string.message_hint_search)
    val searchIcon =
        s.findViewById<View>(androidx.appcompat.R.id.search_button) as ImageView
    searchIcon.drawable.setTint(
        colorStateList(R.color.primaryTextColor)
    )
    val searchMagIcon =
        s.findViewById<View>(androidx.appcompat.R.id.search_close_btn) as ImageView
    searchMagIcon.drawable.setTint(
        colorStateList(R.color.primaryTextColor)
    )
    s.queryHint = this.resources.getString(R.string.message_hint_search)
}