package it.ssplus.iconpickert

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.toBitmap
import androidx.gridlayout.widget.GridLayout
import it.ssplus.iconpickert.ResourceUtil.getBitmap


class IconPicker : LinearLayout {
    private val DEFAULT_LAYOUT = R.layout.icon_pickert_layout
    private var layout = 0
    private var mContext: Context? = null
    private var selectIconPicker: ConstraintLayout? = null
    private var selectIconOpen: ImageView? = null
    private var currentValue: Bitmap? = null
    var transparent = false

    constructor(context: Context?) : super(context, null)
    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        initialize(context, attrs)
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    private fun initialize(
        context: Context,
        attrs: AttributeSet?
    ) {
        val attributes =
            context.theme.obtainStyledAttributes(attrs, R.styleable.IconPickert, 0, 0)
        layout =
            attributes.getResourceId(R.styleable.IconPickert_custom_layout, DEFAULT_LAYOUT)
        mContext = context
        LayoutInflater.from(mContext).inflate(layout, this, true)
        selectIconPicker =
            findViewById<View>(R.id.selectIconPickert) as ConstraintLayout
        selectIconOpen =
            findViewById<View>(R.id.selectIconOpen) as ImageView

        currentValue = selectIconOpen!!.drawable.toBitmap(
            ResourceUtil.convertDpToPx(
                mContext!!,
                24F
            ).toInt(), ResourceUtil.convertDpToPx(
                mContext!!,
                24F
            ).toInt(), null
        )

        selectIconPicker!!.setOnClickListener {
            dialogIconList()
        }

        transparent = attributes.getBoolean(R.styleable.IconPickert_transparent, false)

        if (transparent) {
            selectIconPicker!!.setBackgroundResource(android.R.color.transparent)
        }
    }

    private fun dialogIconList() {
        val builderAdd =
            AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyleLibrary)
        val inflater = LayoutInflater.from(mContext)
        val convertView =
            inflater?.inflate(R.layout.dialog_icons_list, null) as View
        val titleCustomView =
            inflater.inflate(R.layout.dialog_library_custom_title, null) as View
        val titleIcon: ImageView =
            titleCustomView.findViewById(R.id.customDialogTitleIcon)
        val titleName: TextView =
            titleCustomView.findViewById(R.id.customDialogTitleName)
        titleName.setText(R.string.title)
        titleIcon.setImageResource(R.drawable.ic_arrow_back)
        builderAdd.setCustomTitle(titleCustomView)
        builderAdd.setView(convertView)

        val ll = convertView.findViewById<View>(R.id.tlListIcons) as GridLayout

        val dialogAdd: AlertDialog = builderAdd.create()
        dialogAdd.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogAdd.setCanceledOnTouchOutside(false)

        titleIcon.setOnClickListener { dialogAdd.dismiss() }

        for (i in 0 until Utils.icons.size) {
            val imageView = ImageView(mContext)
            imageView.isClickable = true
            imageView.adjustViewBounds = true
            imageView.cropToPadding = true
            imageView.layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT

                )

            val marginParams1 = MarginLayoutParams(imageView.layoutParams)
            marginParams1.setMargins(95, 60, 95, 60)
            val layoutParams1 = LayoutParams(marginParams1)
            imageView.layoutParams = layoutParams1


            val outValue = TypedValue()
            mContext!!.theme.resolveAttribute(
                android.R.attr.selectableItemBackgroundBorderless,
                outValue,
                true
            )
            imageView.setBackgroundResource(outValue.resourceId)
            imageView.setImageResource(Utils.icons[i])
            imageView.setOnClickListener {
                selectIconOpen!!.setImageDrawable(imageView.drawable)
                value = getBitmap(mContext, Utils.icons[i])
                dialogAdd.dismiss()
            }
            ll.addView(imageView)
        }

        dialogAdd.show()
    }

    private fun refresh() {
        selectIconOpen!!.setImageBitmap(currentValue)
    }

    var value: Bitmap
        get() = currentValue!!
        set(value) {
            currentValue = value
            refresh()
        }
}