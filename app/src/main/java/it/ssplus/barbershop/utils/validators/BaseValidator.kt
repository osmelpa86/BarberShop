package it.ssplus.barbershop.utils.validation

import android.app.Activity
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.textfield.TextInputLayout
import it.ssplus.barbershop.R

open class BaseValidator {
    var mainActivity: Activity
    protected lateinit var mErrorContainer: TextInputLayout
    protected var mErrorContainers: Array<TextInputLayout>? = null
    protected var mErrorMessage = ""
    protected var mEmptyMessage: String? = "This field is required"

    constructor(errorContainer: TextInputLayout, mainActivity: Activity) {
        mErrorContainer = errorContainer
        this.mainActivity = mainActivity
    }

    constructor(mErrorContainers: Array<TextInputLayout>, mainActivity: Activity) {
        var mErrorContainers = mErrorContainers
        mErrorContainers = mErrorContainers
        this.mainActivity = mainActivity
    }

    protected open fun isValid(charSequence: String): Boolean {
        //other classed shall overide this method and have thrie custom
        //implementation
        return true
    }

    fun validate(charSequence: String?): Boolean {
        if (mEmptyMessage != null && (charSequence == null || charSequence.length == 0)) {
            mErrorContainer.error = mEmptyMessage
            mErrorContainer.setErrorIconTintList(
                AppCompatResources.getColorStateList(
                    mainActivity,
                    R.color.errorColor
                )
            )
//            mErrorContainer.editText!!.setTextColor(AppCompatResources.getColorStateList(mainActivity, R.color.errorColor).defaultColor)
            mErrorContainer.setErrorTextColor(
                AppCompatResources.getColorStateList(
                    mainActivity,
                    R.color.errorColor
                )
            )

            mErrorContainer.hintTextColor = AppCompatResources.getColorStateList(
                mainActivity,
                R.color.errorColor
            )

            return false
        } else if (isValid(charSequence!!)) {
            mErrorContainer.error = null
            mErrorContainer.setErrorEnabled(false)
            mErrorContainer.boxBackgroundColor = AppCompatResources.getColorStateList(
                mainActivity,
                R.color.boxBackgroundDefault
            ).defaultColor
            mErrorContainer.hintTextColor = AppCompatResources.getColorStateList(
                mainActivity,
                R.color.secondaryTextColor
            )
            return true
        } else {
            mErrorContainer.error = mErrorMessage
            mErrorContainer.setErrorIconTintList(
                AppCompatResources.getColorStateList(
                    mainActivity,
                    R.color.errorColor
                )
            )
            mErrorContainer.setErrorTextColor(
                AppCompatResources.getColorStateList(
                    mainActivity,
                    R.color.errorColor
                )
            )
            mErrorContainer.hintTextColor = AppCompatResources.getColorStateList(
                mainActivity,
                R.color.errorColor
            )
            return false
        }
    }

    fun validateIgnoreNull(charSequence: String): Boolean {
        if (isValid(charSequence)) {
            mErrorContainer.error = null
            mErrorContainer.setErrorEnabled(false)
            mErrorContainer.boxBackgroundColor = AppCompatResources.getColorStateList(
                mainActivity,
                R.color.boxBackgroundDefault
            ).defaultColor
            mErrorContainer.hintTextColor = AppCompatResources.getColorStateList(
                mainActivity,
                R.color.secondaryTextColor
            )
            return true
        } else {
            mErrorContainer.error = mErrorMessage
            mErrorContainer.setErrorIconTintList(
                AppCompatResources.getColorStateList(
                    mainActivity,
                    R.color.errorColor
                )
            )
            mErrorContainer.setErrorTextColor(
                AppCompatResources.getColorStateList(
                    mainActivity,
                    R.color.errorColor
                )
            )
            mErrorContainer.hintTextColor = AppCompatResources.getColorStateList(
                mainActivity,
                R.color.errorColor
            )
            return false
        }
    }


    fun confirm(s1: String, s2: String): Boolean {
        return s1 == s2
    }
}
