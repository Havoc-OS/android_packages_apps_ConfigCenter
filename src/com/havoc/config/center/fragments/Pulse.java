/*
 * Copyright (C) 2020 The Dirty Unicorns Project
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

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceCategory;

import com.android.settings.R;

import com.havoc.config.center.preferences.SwitchBarPreferenceFragment;
import com.havoc.support.colorpicker.ColorPickerPreference;

public class Pulse extends SwitchBarPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PULSE_LOCATION_KEY = "pulse_location";
    private static final String PULSE_COLOR_MODE_KEY = "pulse_color_mode";
    private static final String PULSE_COLOR_MODE_CHOOSER_KEY = "pulse_color_user";
    private static final String PULSE_COLOR_MODE_LAVA_SPEED_KEY = "pulse_lavalamp_speed";
    private static final String PULSE_RENDER_CATEGORY_SOLID = "pulse_2";
    private static final String PULSE_RENDER_CATEGORY_FADING = "pulse_fading_bars_category";
    private static final String PULSE_RENDER_MODE_KEY = "pulse_render_style";
    private static final int RENDER_STYLE_FADING_BARS = 0;
    private static final int RENDER_STYLE_SOLID_LINES = 1;
    private static final int COLOR_TYPE_ACCENT = 0;
    private static final int COLOR_TYPE_USER = 1;
    private static final int COLOR_TYPE_LAVALAMP = 2;
    private static final int COLOR_TYPE_AUTO = 3;

    private PreferenceCategory mFadingBarsCat;
    private PreferenceCategory mSolidBarsCat;
    private Preference mRenderMode;
    private ListPreference mColorModePref;
    private ColorPickerPreference mColorPickerPref;
    private Preference mLavaSpeedPref;
    private ListPreference mLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pulse);

        mFadingBarsCat = (PreferenceCategory) findPreference(PULSE_RENDER_CATEGORY_FADING);
        mSolidBarsCat = (PreferenceCategory) findPreference(PULSE_RENDER_CATEGORY_SOLID);
        mLavaSpeedPref = findPreference(PULSE_COLOR_MODE_LAVA_SPEED_KEY);

        mColorModePref = (ListPreference) findPreference(PULSE_COLOR_MODE_KEY);
        mColorModePref.setOnPreferenceChangeListener(this);
        int colorMode = Settings.Secure.getIntForUser(getContentResolver(),
                Settings.Secure.PULSE_COLOR_MODE, COLOR_TYPE_ACCENT, UserHandle.USER_CURRENT);

        mColorPickerPref = (ColorPickerPreference) findPreference(PULSE_COLOR_MODE_CHOOSER_KEY);
        int pulseColor = Settings.Secure.getInt(getContentResolver(),
                Settings.Secure.PULSE_COLOR_USER, 0xFFFFFFFF);
        mColorPickerPref.setNewPreviewColor(pulseColor);
        String pulseColorHex = String.format("#%08x", pulseColor);
        if (pulseColorHex.equals("#ffffffff")) {
            mColorPickerPref.setSummary(R.string.default_string);
        } else {
            mColorPickerPref.setSummary(pulseColorHex);
        }
        mColorPickerPref.setOnPreferenceChangeListener(this);

        mRenderMode = findPreference(PULSE_RENDER_MODE_KEY);
        mRenderMode.setOnPreferenceChangeListener(this);
        int renderMode = Settings.Secure.getIntForUser(getContentResolver(),
                Settings.Secure.PULSE_RENDER_STYLE, 0, UserHandle.USER_CURRENT);

        mLocation = (ListPreference) findPreference(PULSE_LOCATION_KEY);
        mLocation.setOnPreferenceChangeListener(this);
        int location = Settings.Secure.getIntForUser(getContentResolver(),
                Settings.Secure.PULSE_LOCATION, 0, UserHandle.USER_CURRENT);

        updateColorPrefs(colorMode);
        updateRenderCategories(renderMode);
        updateLocationSummary(location);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.equals(mColorModePref)) {
            updateColorPrefs(Integer.parseInt(String.valueOf(newValue)));
            return true;
        } else if (preference.equals(mRenderMode)) {
            updateRenderCategories(Integer.parseInt(String.valueOf(newValue)));
            return true;
        } else if (preference == mColorPickerPref) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hex.equals("#ffffffff")) {
                preference.setSummary(R.string.default_string);
            } else {
                preference.setSummary(hex);
            }
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.Secure.putInt(getContentResolver(),
                    Settings.Secure.PULSE_COLOR_USER, intHex);
            return true;
        } else if (preference.equals(mLocation)) {
            updateLocationSummary(Integer.parseInt(String.valueOf(newValue)));
            return true;
        }
        return false;
    }

    private void updateColorPrefs(int val) {
        switch (val) {
            case COLOR_TYPE_ACCENT:
            case COLOR_TYPE_AUTO:
                mColorPickerPref.setVisible(false);
                mLavaSpeedPref.setVisible(false);
                break;
            case COLOR_TYPE_USER:
                mColorPickerPref.setVisible(true);
                mLavaSpeedPref.setVisible(false);
                break;
            case COLOR_TYPE_LAVALAMP:
                mColorPickerPref.setVisible(false);
                mLavaSpeedPref.setVisible(true);
                break;
        }
    }

    private void updateRenderCategories(int mode) {
        mFadingBarsCat.setVisible(mode == RENDER_STYLE_FADING_BARS);
        mSolidBarsCat.setVisible(mode == RENDER_STYLE_SOLID_LINES);
    }

    private void updateLocationSummary(int location) {
        String prefix = (String) mLocation.getEntries()
                [mLocation.findIndexOfValue(String.valueOf(location))];
        switch (location) {
            case 0:
                mLocation.setSummary(getResources().getString(R.string.pulse_location_lockscreen, prefix));
                break;
            case 1:
                mLocation.setSummary(getResources().getString(R.string.pulse_location_navbar, prefix));
                break;
            case 2:
                mLocation.setSummary(getResources().getString(R.string.pulse_location_both_summary, prefix));
                break;
        }
    }

    @Override
    public boolean getSwitchState() {
        return Settings.Secure.getInt(getContentResolver(),
                Settings.Secure.PULSE_ENABLED, 0) == 1;
    }

    @Override
    public void updateSwitchState(boolean isChecked) {
        Settings.Secure.putInt(getContentResolver(),
                Settings.Secure.PULSE_ENABLED, isChecked ? 1 : 0);
    }
}
