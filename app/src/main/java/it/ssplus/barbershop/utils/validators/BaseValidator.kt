package it.ssplus.barbershop.utils.validators

import android.app.Activity
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.textfield.TextInputLayout
import it.ssplus.barbershop.R

open class BaseValidator(errorContainer: TextInputLayout, var mainActivity: Activity) {
    private var mErrorContainer: TextInputLayout = errorContainer
    protected var mErrorContainers: Array<TextInputLayout>? = null
    protected var mErrorMessage = ""
    protected var mEmptyMessage: String? = "This field is required"

    protected open fun isValid(charSequence: String): Boolean {
        return true
    }

    fun validate(charSequence: String?): Boolean {
        if (mEmptyMessage != null && (charSequence == null || charSequence.isEmpty())) {
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
            mErrorContainer.isErrorEnabled = false
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
            mErrorContainer.isErrorEnabled = false
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
