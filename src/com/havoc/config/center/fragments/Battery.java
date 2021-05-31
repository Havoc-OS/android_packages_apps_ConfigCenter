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

import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.format.DateFormat;

import androidx.preference.Preference;

import com.android.internal.logging.nano.MetricsProto;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.havoc.support.preferences.SystemSettingMasterSwitchPreference;

import java.time.format.DateTimeFormatter;
import java.time.LocalTime;

public class Battery extends SettingsPreferenceFragment implements 
        Preference.OnPreferenceChangeListener {

    public static final String TAG = "Battery";

    private static final String SMART_PIXELS_ENABLED = "smart_pixels_enable";
    private static final String SENSOR_BLOCK = "sensor_block";

    private Preference mSleepMode;
    private SystemSettingMasterSwitchPreference mSmartPixelsEnabled;
    private SystemSettingMasterSwitchPreference mSensorBlockEnabled;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.config_center_battery);

        mSleepMode = findPreference("sleep_mode");

        updateMasterPrefs();
        updateSleepModeSummary();
    }

    private void updateSleepModeSummary() {
        if (mSleepMode == null) return;
        boolean enabled = Settings.Secure.getIntForUser(getActivity().getContentResolver(),
                Settings.Secure.SLEEP_MODE_ENABLED, 0, UserHandle.USER_CURRENT) == 1;
        int mode = Settings.Secure.getIntForUser(getActivity().getContentResolver(),
                Settings.Secure.SLEEP_MODE_AUTO_MODE, 0, UserHandle.USER_CURRENT);
        String timeValue = Settings.Secure.getStringForUser(getActivity().getContentResolver(),
                Settings.Secure.SLEEP_MODE_AUTO_TIME, UserHandle.USER_CURRENT);
        if (timeValue == null || timeValue.equals("")) timeValue = "20:00,07:00";
        String[] time = timeValue.split(",", 0);
        String outputFormat = DateFormat.is24HourFormat(getContext()) ? "HH:mm" : "h:mm a";
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(outputFormat);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime sinceValue = LocalTime.parse(time[0], formatter);
        LocalTime tillValue = LocalTime.parse(time[1], formatter);
        String detail;
        switch (mode) {
            default:
            case 0:
                detail = getActivity().getString(enabled
                        ? R.string.night_display_summary_on_auto_mode_never
                        : R.string.night_display_summary_off_auto_mode_never);
                break;
            case 1:
                detail = getActivity().getString(enabled
                        ? R.string.night_display_summary_on_auto_mode_twilight
                        : R.string.night_display_summary_off_auto_mode_twilight);
                break;
            case 2:
                if (enabled) {
                    detail = getActivity().getString(R.string.night_display_summary_on_auto_mode_custom, tillValue.format(outputFormatter));
                } else {
                    detail = getActivity().getString(R.string.night_display_summary_off_auto_mode_custom, sinceValue.format(outputFormatter));
                }
                break;
            case 3:
                if (enabled) {
                    detail = getActivity().getString(R.string.night_display_summary_on_auto_mode_custom, tillValue.format(outputFormatter));
                } else {
                    detail = getActivity().getString(R.string.night_display_summary_off_auto_mode_twilight);
                }
                break;
            case 4:
                if (enabled) {
                    detail = getActivity().getString(R.string.night_display_summary_on_auto_mode_twilight);
                } else {
                    detail = getActivity().getString(R.string.night_display_summary_off_auto_mode_custom, sinceValue.format(outputFormatter));
                }
                break;
        }
        String summary = getActivity().getString(enabled
                ? R.string.night_display_summary_on
                : R.string.night_display_summary_off, detail);
        mSleepMode.setSummary(summary);
    }

    private void updateMasterPrefs() {
        mSmartPixelsEnabled = (SystemSettingMasterSwitchPreference) findPreference(SMART_PIXELS_ENABLED);
        mSmartPixelsEnabled.setOnPreferenceChangeListener(this);
        int smartPixelsEnabled = Settings.System.getInt(getContentResolver(),
                SMART_PIXELS_ENABLED, 0);
        mSmartPixelsEnabled.setChecked(smartPixelsEnabled != 0);

        if (!getResources().getBoolean(com.android.internal.R.bool.config_enableSmartPixels)) {
            mSmartPixelsEnabled.setVisible(false);
        }

        mSensorBlockEnabled = (SystemSettingMasterSwitchPreference) findPreference(SENSOR_BLOCK);
        int sensorBlockEnabled = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.SENSOR_BLOCK, 0, UserHandle.USER_CURRENT);
        mSensorBlockEnabled.setChecked(sensorBlockEnabled != 0);
        mSensorBlockEnabled.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mSmartPixelsEnabled) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(getContentResolver(),
		            SMART_PIXELS_ENABLED, value ? 1 : 0);
            return true;
        } else if (preference == mSensorBlockEnabled) {
            boolean value = (Boolean) objValue;
            Settings.System.putIntForUser(getContentResolver(),
		            SENSOR_BLOCK, value ? 1 : 0, UserHandle.USER_CURRENT);
            return true;
        }
        return false; 
    }

    @Override
    public void onResume() {
        super.onResume();
        updateMasterPrefs();
        updateSleepModeSummary();
    }

    @Override
    public void onPause() {
        super.onPause();
        updateMasterPrefs();
        updateSleepModeSummary();
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
