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

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;

import androidx.preference.ListPreference;
import androidx.preference.Preference;

import com.android.internal.logging.nano.MetricsProto; 

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.havoc.support.preferences.SecureSettingMasterSwitchPreference;

public class Media extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    public static final String TAG = "Media";

    private static final String PULSE_ENABLED = "pulse_enabled";
    private static final String KEY_RINGTONE_FOCUS_MODE = "ringtone_focus_mode";

    private SecureSettingMasterSwitchPreference mPulse;
    private ListPreference mRingtoneFocusMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.config_center_media);

        final Resources res = getResources();

        mRingtoneFocusMode = (ListPreference) findPreference(KEY_RINGTONE_FOCUS_MODE);

        if (!res.getBoolean(com.android.internal.R.bool.config_deviceRingtoneFocusMode)) {
            mRingtoneFocusMode.setVisible(false);
        }

        updateMasterPrefs();
    }

    private void updateMasterPrefs() {
        mPulse = (SecureSettingMasterSwitchPreference) findPreference(PULSE_ENABLED);
        mPulse.setChecked((Settings.Secure.getInt(getActivity().getContentResolver(),
                Settings.Secure.PULSE_ENABLED, 0) == 1));
        mPulse.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference == mPulse) {
            boolean value = (Boolean) newValue;
            Settings.Secure.putInt(getActivity().getContentResolver(),
                    Settings.Secure.PULSE_ENABLED, value ? 1 : 0);
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateMasterPrefs();
    }

    @Override
    public void onPause() {
        super.onPause();
        updateMasterPrefs();
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
