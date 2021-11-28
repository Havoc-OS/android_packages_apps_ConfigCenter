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
import android.os.UserHandle;
import android.provider.Settings;
import android.widget.CompoundButton;

import androidx.preference.Preference;

import com.android.settings.R;

import com.havoc.config.center.preferences.SwitchBarPreferenceFragment;
import com.havoc.support.colorpicker.ColorPickerPreference;
import com.havoc.support.preferences.SystemSettingListPreference;

public class EdgeLight extends SwitchBarPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String AMBIENT_LIGHT_COLOR = "ambient_notification_color_mode";
    private static final String AMBIENT_LIGHT_CUSTOM_COLOR = "ambient_notification_light_color";

    private SystemSettingListPreference mEdgeLightColorMode;
    private ColorPickerPreference mEdgeLightColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.edge_light);

        mEdgeLightColorMode = (SystemSettingListPreference) findPreference(AMBIENT_LIGHT_COLOR);
        int edgeLightColorMode = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.NOTIFICATION_PULSE_COLOR_MODE, 0, UserHandle.USER_CURRENT);
        mEdgeLightColorMode.setValue(String.valueOf(edgeLightColorMode));
        mEdgeLightColorMode.setSummary(mEdgeLightColorMode.getEntry());
        mEdgeLightColorMode.setOnPreferenceChangeListener(this);

        mEdgeLightColor = (ColorPickerPreference) findPreference(AMBIENT_LIGHT_CUSTOM_COLOR);
        int edgeLightColor = Settings.System.getInt(getContentResolver(),
                Settings.System.NOTIFICATION_PULSE_COLOR, 0xFFFFFFFF);
        mEdgeLightColor.setNewPreviewColor(edgeLightColor);
        String edgeLightColorHex = String.format("#%08x", edgeLightColor);
        if (edgeLightColorHex.equals("#ffffffff")) {
            mEdgeLightColor.setSummary(R.string.default_string);
        } else {
            mEdgeLightColor.setSummary(edgeLightColorHex);
        }
        mEdgeLightColor.setOnPreferenceChangeListener(this);

        updateColorPrefs(edgeLightColorMode);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mEdgeLightColorMode) {
            int edgeLightColorMode = Integer.parseInt((String) newValue);
            int index = mEdgeLightColorMode.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.NOTIFICATION_PULSE_COLOR_MODE, edgeLightColorMode, UserHandle.USER_CURRENT);
            mEdgeLightColorMode.setSummary(mEdgeLightColorMode.getEntries()[index]);
            updateColorPrefs(edgeLightColorMode);
            return true;
        } else if (preference == mEdgeLightColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hex.equals("#ffffffff")) {
                preference.setSummary(R.string.default_string);
            } else {
                preference.setSummary(hex);
            }
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.NOTIFICATION_PULSE_COLOR, intHex);
            return true;
        }
        return false;
    }

    private void updateColorPrefs(int edgeLightColorMode) {
        if (mEdgeLightColor != null) {
            if (edgeLightColorMode == 3) {
                getPreferenceScreen().addPreference(mEdgeLightColor);
            } else {
                getPreferenceScreen().removePreference(mEdgeLightColor);
            }
        }
    }

    @Override
    public boolean getSwitchState() {
        return Settings.System.getInt(getContentResolver(),
                Settings.System.NOTIFICATION_PULSE, 1) == 1;
    }

    @Override
    public void updateSwitchState(boolean isChecked) {
        Settings.System.putInt(getContentResolver(),
                Settings.System.NOTIFICATION_PULSE, isChecked ? 1 : 0);
    }
}
