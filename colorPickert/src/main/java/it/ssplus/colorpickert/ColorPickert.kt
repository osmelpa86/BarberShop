package it.ssplus.colorpickert

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout


class ColorPickert : LinearLayout {
    private val DEFAULT_LAYOUT = R.layout.color_pickert_layout
    private var layout = 0
    private var mContext: Context? = null
    private var selectIconPickert: ConstraintLayout? = null
//    private var selectIconOpen: ImageView? = null
//    private var currentValue: Drawable? = null
    var transparent = false
    private lateinit var drawableList:ArrayList<Drawable>
    private lateinit var nameList: ArrayList<String>

    constructor(context: Context?) : super(context, null) {}
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
    ) : super(context, attrs, defStyleAttr) {
    }

    private fun initialize(
        context: Context,
        attrs: AttributeSet?
    ) {
        val attributes =
            context.theme.obtainStyledAttributes(attrs, R.styleable.ColorPickert, 0, 0)
        layout =
            attributes.getResourceId(R.styleable.ColorPickert_custom_layout, DEFAULT_LAYOUT)
        mContext = context
        LayoutInflater.from(mContext).inflate(layout, this, true)
//        selectIconPickert =
//            findViewById<View>(R.id.selectIconPickert) as ConstraintLayout
//        selectIconOpen =
//            findViewById<View>(R.id.selectIconOpen) as ImageView
//        currentValue = selectIconOpen!!.drawable
//        selectIconPickert!!.setOnClickListener {
//            dialogIconList()
//        }


//        transparent = attributes.getBoolean(R.styleable.IconPickert_transparent, false)
//        drawableList = arrayListOf<Drawable>()
//        nameList = arrayListOf<String>()
//        drawableList = attributes.getTextArray()getBoolean(R.styleable.ColorPickert_drawableList, false)
//        nameList = attributes.getTextArray(R.styleable.ColorPickert_nameList, false)

//        if (transparent == true) {
//            selectIconPickert!!.setBackgroundResource(android.R.color.transparent)
//        }

    }

//    fun dialogIconList() {
//        val builderAdd =
//            AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyleLibrary)
//        val inflater = LayoutInflater.from(mContext)
//        val convertView =
//            inflater?.inflate(R.layout.dialog_icons_list, null) as View
//        val titleCustomView =
//            inflater.inflate(R.layout.dialog_library_custom_title, null) as View
//        val titleIcon: ImageView =
//            titleCustomView.findViewById(R.id.customDialogTitleIcon)
//        val titleName: TextView =
//            titleCustomView.findViewById(R.id.customDialogTitleName)
//        titleName.setText(R.string.title)
//        titleIcon.setImageResource(R.drawable.ic_arrow_back)
//        builderAdd.setCustomTitle(titleCustomView)
//        builderAdd.setView(convertView)
//
//        val ll = convertView.findViewById<View>(R.id.tlListIcons) as GridLayout
//
//        val dialogAdd: AlertDialog = builderAdd.create()
//        dialogAdd.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        dialogAdd.setCanceledOnTouchOutside(false)
//
//        titleIcon.setOnClickListener { dialogAdd.dismiss() }
//
//        for (i in 0 until Utils.icons.size) {
//            var imageView = ImageView(mContext)
//            imageView.isClickable = true
//            imageView.adjustViewBounds = true
//            imageView.cropToPadding = true
//            imageView.layoutParams =
//                ViewGroup.LayoutParams(
//                    ViewGroup.LayoutParams.WRAP_CONTENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT
//
//                )
//
//            val marginParams1 = MarginLayoutParams(imageView.getLayoutParams())
//            marginParams1.setMargins(95, 60, 95, 60)
//            val layoutParams1 = LayoutParams(marginParams1)
//            imageView.setLayoutParams(layoutParams1)
//
//
//            val outValue = TypedValue()
//            mContext!!.theme.resolveAttribute(
//                android.R.attr.selectableItemBackgroundBorderless,
//                outValue,
//                true
//            )
//            imageView.setBackgroundResource(outValue.resourceId)
//            imageView.setImageResource(Utils.icons[i])
//            imageView.setOnClickListener {
//                selectIconOpen!!.setImageDrawable(imageView.drawable)
//                value = selectIconOpen!!.drawable
//                dialogAdd.dismiss()
//            }
//            ll.addView(imageView)
//        }
//
//        dialogAdd.show()
//    }
//
//    fun refresh() {
//        selectIconOpen!!.setImageDrawable(currentValue)
//    }
//
//    var value: Drawable
//        get() = currentValue!!
//        set(value) {
//            currentValue = value
//            refresh()
//        }
}