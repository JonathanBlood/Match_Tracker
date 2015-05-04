package com.jonathanbloodmatchtracker.main;

import android.content.Context;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

/**
 * Utility class
 * Created by Jonathan on 04/05/2015.
 */
public class Utility {

    /**
     * Validate autoCompleteTextView - non empty.
     *
     * @param autoCompleteTextView textview
     * @return boolean true if validation was successful.
     */
    public static boolean validateAutoTextViewValue(AutoCompleteTextView autoCompleteTextView, Context context) {
        String value = autoCompleteTextView.getText().toString();
        if (value.length() == 0) {
            autoCompleteTextView.setError(context.getString(R.string.validate_name));
            return false;
        }
        return true;
    }

    /**
     * Validate EditText - value is non empty and int.
     *
     * @param editText textview
     * @return boolean true if validation was successful.
     */
    public static boolean validateEditTextValue(EditText editText, Context context) {
        String value = editText.getText().toString();

        // Check for non empty.
        if (value.length() == 0) {
            editText.setError(context.getString(R.string.validate_number));
            return false;
        }

        // Check that value is an int.
        try {
            Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
