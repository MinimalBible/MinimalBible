/**
 * Copyright CMW Mobile.com, 2010.
 */
package com.cmwmobile.android.samples;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import org.bspeice.minimalbible.R;
import org.jetbrains.annotations.NotNull;

/**
 * The SeekBarDialogPreference class is a DialogPreference based and provides a
 * seekbar preference.
 *
 * @author Casper Wakkers
 */
public class SeekBarDialogPreference extends
        DialogPreference implements SeekBar.OnSeekBarChangeListener {

    // Layout widgets.
    private SeekBar seekBar = null;
    private TextView valueText = null;

    // Custom xml attributes.
    private int maximumValue = 0;
    private int minimumValue = 0;
    private int stepSize = 0;
    private String units = null;

    private int value = 0;

    /**
     * The SeekBarDialogPreference constructor.
     *
     * @param context of this preference.
     * @param attrs   custom xml attributes.
     */
    public SeekBarDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.SeekBarDialogPreference);

        maximumValue = typedArray.getInteger(
                R.styleable.SeekBarDialogPreference_maximumValue, 0);
        minimumValue = typedArray.getInteger(
                R.styleable.SeekBarDialogPreference_minimumValue, 0);
        stepSize = typedArray.getInteger(
                R.styleable.SeekBarDialogPreference_stepSize, 1);
        units = typedArray.getString(
                R.styleable.SeekBarDialogPreference_units);

        typedArray.recycle();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected View onCreateDialogView() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());

        View view = layoutInflater.inflate(
                R.layout.seekbardialogpreference_layout, null);

        seekBar = (SeekBar) view.findViewById(R.id.preference_seekbar);
        valueText = (TextView) view.findViewById(R.id.preference_text);

        // Get the persistent value and correct it for the minimum value.
        value = getPersistedInt(minimumValue) - minimumValue;

        // You're never know...
        if (value < 0) {
            value = 0;
        }

        seekBar.setKeyProgressIncrement(stepSize);
        seekBar.setMax(maximumValue - minimumValue);

        // setProgress must come before we start listening to events, otherwise
        // we may receive the initialization (i.e. 0) event destroying our value
        seekBar.setProgress(value);
        updateValueText(value);

        seekBar.setOnSeekBarChangeListener(this);

        return view;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onProgressChanged(@NotNull SeekBar seek, int newValue,
                                  boolean fromTouch) {
        // Round the value to the closest integer value.
        if (stepSize >= 1) {
            value = Math.round(newValue / stepSize) * stepSize;
        } else {
            value = newValue;
        }

        updateValueText(value);

        callChangeListener(value);
    }

    private void updateValueText(int value) {
        valueText.setText(String.valueOf(value + minimumValue) +
                (units == null ? "" : units));
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult && shouldPersist())
            persistInt(value + minimumValue);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}