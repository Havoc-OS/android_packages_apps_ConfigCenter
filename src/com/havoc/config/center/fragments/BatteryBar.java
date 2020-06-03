/*
 * Copyright (C) 2020 Havoc-OS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.havoc.config.center.fragments;

import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;

import com.android.internal.logging.nano.MetricsProto;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.havoc.support.colorpicker.ColorPickerPreference;
import com.havoc.support.preferences.SystemSettingListPreference;
import com.havoc.support.preferences.SystemSettingSeekBarPreference;
import com.havoc.support.preferences.SystemSettingSwitchPreference;

public class BatteryBar extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener, CompoundButton.OnCheckedChangeListener {

    private SystemSettingListPreference mLocation;
    private SystemSettingSeekBarPreference mThickness;
    private SystemSettingListPreference mStyle;
    private SystemSettingSwitchPreference mAnimate;
    private SystemSettingSwitchPreference mCharging;
    private ColorPickerPreference mChargingColor;
    private ColorPickerPreference mChargingColorDark;
    private PreferenceCategory mLight;
    private PreferenceCategory mDark;

    private TextView mTextView;
    private View mSwitchBar;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        addPreferencesFromResource(R.xml.battery_bar);

        mLocation = (SystemSettingListPreference) findPreference("statusbar_battery_bar_location");
        mThickness = (SystemSettingSeekBarPreference) findPreference("statusbar_battery_bar_thickness");
        mStyle = (SystemSettingListPreference) findPreference("statusbar_battery_bar_style");
        mAnimate = (SystemSettingSwitchPreference) findPreference("statusbar_battery_bar_animate");
        mCharging = (SystemSettingSwitchPreference) findPreference("statusbar_battery_bar_enable_charging_color");
        mChargingColorDark = (ColorPickerPreference) findPreference("statusbar_battery_bar_charging_dark_color");
        mLight = (PreferenceCategory) findPreference("light_statusbar");
        mDark = (PreferenceCategory) findPreference("dark_statusbar");

        mChargingColor = (ColorPickerPreference) findPreference("statusbar_battery_bar_charging_color");
        int chargingColor = Settings.System.getInt(getContentResolver(),
                Settings.System.STATUSBAR_BATTERY_BAR_CHARGING_COLOR, 0xFFFFFF00);
        mChargingColor.setNewPreviewColor(chargingColor);
        String chargingColorHex = String.format("#%08x", (0xFFFFFF00 & chargingColor));
        if (chargingColorHex.equals("#ffffff00")) {
            mChargingColor.setSummary(R.string.default_string);
        } else {
            mChargingColor.setSummary(chargingColorHex);
        }
        mChargingColor.setOnPreferenceChangeListener(this);

        mChargingColorDark = (ColorPickerPreference) findPreference("statusbar_battery_bar_charging_dark_color");
        int chargingColorDark = Settings.System.getInt(getContentResolver(),
                Settings.System.STATUSBAR_BATTERY_BAR_CHARGING_DARK_COLOR, 0xFF0D47A1);
        mChargingColorDark.setNewPreviewColor(chargingColorDark);
        String chargingColorDarkHex = String.format("#%08x", (0xFF0D47A1 & chargingColorDark));
        if (chargingColorDarkHex.equals("#ff0d47a1")) {
            mChargingColorDark.setSummary(R.string.default_string);
        } else {
            mChargingColorDark.setSummary(chargingColorDarkHex);
        }
        mChargingColorDark.setOnPreferenceChangeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.master_setting_switch, container, false);
        ((ViewGroup) view).addView(super.onCreateView(inflater, container, savedInstanceState));
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        boolean enabled = Settings.System.getInt(getContentResolver(),
                Settings.System.STATUSBAR_BATTERY_BAR, 0) == 1;

        mTextView = view.findViewById(R.id.switch_text);
        mTextView.setText(getString(enabled ?
                R.string.switch_on_text : R.string.switch_off_text));

        mSwitchBar = view.findViewById(R.id.switch_bar);
        Switch switchWidget = mSwitchBar.findViewById(android.R.id.switch_widget);
        switchWidget.setChecked(enabled);
        switchWidget.setOnCheckedChangeListener(this);
        mSwitchBar.setActivated(enabled);
        mSwitchBar.setOnClickListener(v -> {
            switchWidget.setChecked(!switchWidget.isChecked());
            mSwitchBar.setActivated(switchWidget.isChecked());
        });

        mLocation.setEnabled(enabled);
        mThickness.setEnabled(enabled);
        mStyle.setEnabled(enabled);
        mAnimate.setEnabled(enabled);
        mCharging.setEnabled(enabled);
        mLight.setEnabled(enabled);
        mDark.setEnabled(enabled);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        Settings.System.putInt(getContentResolver(),
                Settings.System.STATUSBAR_BATTERY_BAR, isChecked ? 1 : 0);
        mTextView.setText(getString(isChecked ? R.string.switch_on_text : R.string.switch_off_text));
        mSwitchBar.setActivated(isChecked);

        mLocation.setEnabled(isChecked);
        mThickness.setEnabled(isChecked);
        mStyle.setEnabled(isChecked);
        mAnimate.setEnabled(isChecked);
        mCharging.setEnabled(isChecked);
        mLight.setEnabled(isChecked);
        mDark.setEnabled(isChecked);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mChargingColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hex.equals("#ffffff00")) {
                preference.setSummary(R.string.default_string);
            } else {
                preference.setSummary(hex);
            }
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUSBAR_BATTERY_BAR_CHARGING_COLOR, intHex);
            return true;
        } else if (preference == mChargingColorDark) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hex.equals("#ff0d47a1")) {
                preference.setSummary(R.string.default_string);
            } else {
                preference.setSummary(hex);
            }
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUSBAR_BATTERY_BAR_CHARGING_DARK_COLOR, intHex);
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
