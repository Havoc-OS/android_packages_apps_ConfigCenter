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

import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;

import com.android.internal.logging.nano.MetricsProto; 
import com.android.internal.util.custom.Utils; 

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.havoc.support.preferences.GlobalSettingMasterSwitchPreference;
import com.havoc.support.preferences.SystemSettingMasterSwitchPreference;

public class Notifications extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    public static final String TAG = "Notifications";
    // private static final String LIGHTS_CATEGORY = "notification_lights";
    private static final String HEADS_UP_NOTIFICATIONS_ENABLED = "heads_up_notifications_enabled";
    private static final String AMBIENT_NOTIFICATION_LIGHT = "pulse_ambient_light";
    private static final String STATUS_BAR_SHOW_TICKER = "status_bar_show_ticker";

    // private PreferenceCategory mLightsCategory;
    private GlobalSettingMasterSwitchPreference mHeadsUpEnabled;
    private SystemSettingMasterSwitchPreference mEdgeLightEnabled;
    private SystemSettingMasterSwitchPreference mTickerEnabled;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.config_center_notifications);

        updateMasterPrefs();
    }

    private void updateMasterPrefs() {
        mHeadsUpEnabled = (GlobalSettingMasterSwitchPreference) findPreference(HEADS_UP_NOTIFICATIONS_ENABLED);
        mHeadsUpEnabled.setOnPreferenceChangeListener(this);
        int headsUpEnabled = Settings.Global.getInt(getContentResolver(),
                HEADS_UP_NOTIFICATIONS_ENABLED, 1);
        mHeadsUpEnabled.setChecked(headsUpEnabled != 0);

        mEdgeLightEnabled = (SystemSettingMasterSwitchPreference) findPreference(AMBIENT_NOTIFICATION_LIGHT);
        mEdgeLightEnabled.setOnPreferenceChangeListener(this);
        int edgeLightEnabled = Settings.System.getInt(getContentResolver(),
                AMBIENT_NOTIFICATION_LIGHT, 0);
        mEdgeLightEnabled.setChecked(edgeLightEnabled != 0);

        mTickerEnabled = (SystemSettingMasterSwitchPreference) findPreference(STATUS_BAR_SHOW_TICKER);
        mTickerEnabled.setOnPreferenceChangeListener(this);
        int tickerEnabled = Settings.System.getInt(getContentResolver(),
                STATUS_BAR_SHOW_TICKER, 0);
        mTickerEnabled.setChecked(tickerEnabled != 0);

        if (Utils.hasNotch(getActivity())) {
            mTickerEnabled.setVisible(false);
        }
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
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mHeadsUpEnabled) {
            boolean value = (Boolean) newValue;
            Settings.Global.putInt(getContentResolver(),
		            HEADS_UP_NOTIFICATIONS_ENABLED, value ? 1 : 0);
            return true;
        } else if (preference == mEdgeLightEnabled) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(),
                    AMBIENT_NOTIFICATION_LIGHT, value ? 1 : 0);
            return true;
        } else if (preference == mTickerEnabled) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(),
                    STATUS_BAR_SHOW_TICKER, value ? 1 : 0);
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
