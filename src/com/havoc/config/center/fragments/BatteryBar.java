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

import androidx.preference.Preference;

import com.android.internal.util.custom.Utils;
import com.android.settings.R;
import com.havoc.config.center.preferences.SwitchBarPreferenceFragment;
import com.havoc.support.colorpicker.ColorPickerPreference;
import com.havoc.support.preferences.SystemSettingListPreference;

public class BatteryBar extends SwitchBarPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private SystemSettingListPreference mLocation;
    private ColorPickerPreference mChargingColor;
    private ColorPickerPreference mChargingColorDark;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        addPreferencesFromResource(R.xml.battery_bar);

        boolean isButtonNavigation = (Utils.isThemeEnabled("com.android.internal.systemui.navbar.threebutton")
                || Utils.isThemeEnabled("com.android.internal.systemui.navbar.twobutton"));

        mLocation = (SystemSettingListPreference) findPreference("statusbar_battery_bar_location");
        int batteryBarLocation = Settings.System.getInt(getContentResolver(),
                Settings.System.STATUSBAR_BATTERY_BAR_LOCATION, 1);
        CharSequence[] ButtonNavEntries = { getResources().getString(R.string.battery_bar_location_statusbar_top),
                getResources().getString(R.string.battery_bar_location_statusbar_bottom),
                getResources().getString(R.string.battery_bar_location_navbar_top),
                getResources().getString(R.string.battery_bar_location_navbar_bottom) };
        CharSequence[] GestureNavEntries = { getResources().getString(R.string.battery_bar_location_statusbar_top),
                getResources().getString(R.string.battery_bar_location_statusbar_bottom),
                getResources().getString(R.string.battery_bar_location_navbar_bottom) };
        CharSequence[] ButtonNavValues = {"1", "2", "3", "4"};
        CharSequence[] GestureNavValues = {"1", "2", "4"};
        mLocation.setEntries(isButtonNavigation ? ButtonNavEntries : GestureNavEntries);
        mLocation.setEntryValues(isButtonNavigation ? ButtonNavValues : GestureNavValues);
        mLocation.setValue(String.valueOf(batteryBarLocation));
        mLocation.setSummary(mLocation.getEntry());
        mLocation.setOnPreferenceChangeListener(this);

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
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mLocation) {
            int batteryBarLocation = Integer.parseInt((String) newValue);
            int index = mLocation.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_BATTERY_BAR_LOCATION, batteryBarLocation);
            mLocation.setSummary(mLocation.getEntries()[index]);
            return true;
        } else if (preference == mChargingColor) {
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
    public boolean getSwitchState() {
        return Settings.System.getInt(getContentResolver(),
                Settings.System.STATUSBAR_BATTERY_BAR, 0) == 1;
    }

    @Override
    public void updateSwitchState(boolean isChecked) {
        Settings.System.putInt(getContentResolver(),
                Settings.System.STATUSBAR_BATTERY_BAR, isChecked ? 1 : 0);
    }
}
