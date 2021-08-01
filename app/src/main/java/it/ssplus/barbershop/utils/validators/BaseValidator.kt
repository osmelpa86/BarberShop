package it.ssplus.barbershop.utils.validators

import android.app.Activity
import com.google.android.material.textfield.TextInputLayout
import it.ssplus.barbershop.R
import it.ssplus.barbershop.utils.colorStateListValidation
import it.ssplus.barbershop.utils.isNull

open class BaseValidator(errorContainer: TextInputLayout, var mainActivity: Activity) {
    private var mErrorContainer: TextInputLayout = errorContainer
    protected var mErrorContainers: Array<TextInputLayout>? = null
    protected var mErrorMessage = ""
    protected var mEmptyMessage: String? = "This field is required"

    protected open fun isValid(charSequence: String): Boolean {
        return true
    }

    fun validate(charSequence: String?): Boolean {
        if (mEmptyMessage != null && (charSequence.isNull() || charSequence!!.isEmpty())) {
            mErrorContainer.error = mEmptyMessage
            mErrorContainer.setErrorIconTintList(
                colorStateListValidation(mainActivity, R.color.errorColor)
            )
            mErrorContainer.editText!!.setTextColor(colorStateListValidation(mainActivity, R.color.errorColor).defaultColor)
            mErrorContainer.setErrorTextColor(
                colorStateListValidation(mainActivity, R.color.errorColor)
            )

            mErrorContainer.hintTextColor =
                colorStateListValidation(mainActivity, R.color.errorColor)

            return false
        } else if (isValid(charSequence!!)) {
            mErrorContainer.error = null
            mErrorContainer.isErrorEnabled = false
            mErrorContainer.boxBackgroundColor =
                colorStateListValidation(mainActivity, R.color.boxBackgroundDefault).defaultColor
            mErrorContainer.hintTextColor =
                colorStateListValidation(mainActivity, R.color.secondaryTextColor)
            return true
        } else {
            mErrorContainer.error = mErrorMessage
            mErrorContainer.setErrorIconTintList(
                colorStateListValidation(mainActivity, R.color.errorColor)
            )
            mErrorContainer.setErrorTextColor(
                colorStateListValidation(mainActivity, R.color.errorColor)
            )
            mErrorContainer.hintTextColor =
                colorStateListValidation(mainActivity, R.color.errorColor)
            return false
        }
    }

    fun validateIgnoreNull(charSequence: String): Boolean {
        if (isValid(charSequence)) {
            mErrorContainer.error = null
            mErrorContainer.isErrorEnabled = false
            mErrorContainer.boxBackgroundColor =
                colorStateListValidation(mainActivity, R.color.boxBackgroundDefault).defaultColor
            mErrorContainer.hintTextColor =
                colorStateListValidation(mainActivity, R.color.secondaryTextColor)
            return true
        } else {
            mErrorContainer.error = mErrorMessage
            mErrorContainer.setErrorIconTintList(
                colorStateListValidation(mainActivity, R.color.errorColor)
            )
            mErrorContainer.setErrorTextColor(
                colorStateListValidation(mainActivity, R.color.errorColor)
            )
            mErrorContainer.hintTextColor =
                colorStateListValidation(mainActivity, R.color.errorColor)
            return false
        }
    }

    fun confirm(s1: String, s2: String): Boolean {
        return s1 == s2
    }
}
