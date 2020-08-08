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

import android.content.ContentResolver;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.internal.logging.nano.MetricsProto;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.havoc.support.preferences.SwitchPreference;
import com.havoc.support.preferences.SystemSettingMasterSwitchPreference;

public class StatusBar extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String STATUS_BAR_CLOCK = "status_bar_clock";
    private static final String STATUS_BAR_LOGO = "status_bar_logo";
    private static final String KEY_USE_OLD_MOBILETYPE = "use_old_mobiletype";
    private static final String NETWORK_TRAFFIC = "network_traffic_state";
    private static final String BATTERY_BAR = "statusbar_battery_bar";
    private static final String CARRIER_LABEL = "carrier_label_enabled";

    private SystemSettingMasterSwitchPreference mStatusBarClockShow;
    private SystemSettingMasterSwitchPreference mStatusBarLogo;
    private SystemSettingMasterSwitchPreference mNetworkTraffic;
    private SystemSettingMasterSwitchPreference mBatteryBar;
    private SystemSettingMasterSwitchPreference mCarrierLabel;
    private SwitchPreference mUseOldMobileType;
    private boolean mConfigUseOldMobileType;
    private ListPreference mBatteryStyle;
    private ListPreference mBatteryPercent;
    private int mBatteryPercentValue;
    private boolean mIsBarSwitchingMode = false;
    private Handler mHandler;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.config_center_statusbar);
        PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

        mHandler = new Handler();

        mConfigUseOldMobileType = getResources().getBoolean(com.android.internal.R.bool.config_useOldMobileIcons);
        int useOldMobileIcons = (mConfigUseOldMobileType ? 1 : 0);
        mUseOldMobileType = (SwitchPreference) findPreference(KEY_USE_OLD_MOBILETYPE);
        mUseOldMobileType.setChecked((Settings.System.getInt(resolver,
                Settings.System.USE_OLD_MOBILETYPE, useOldMobileIcons) == 1));
        mUseOldMobileType.setOnPreferenceChangeListener(this);

        mBatteryStyle = (ListPreference) findPreference("status_bar_battery_style");
        int batterystyle = Settings.System.getIntForUser(resolver,
                Settings.System.STATUS_BAR_BATTERY_STYLE, 0, UserHandle.USER_CURRENT);
        mBatteryStyle.setValue(String.valueOf(batterystyle));
        mBatteryStyle.setSummary(mBatteryStyle.getEntry());
        mBatteryStyle.setOnPreferenceChangeListener(this);

        mBatteryPercent = (ListPreference) findPreference("status_bar_show_battery_percent");
        int batteryPercent = Settings.System.getIntForUser(resolver,
                Settings.System.STATUS_BAR_SHOW_BATTERY_PERCENT, 0, UserHandle.USER_CURRENT);
        mBatteryPercent.setValue(String.valueOf(batteryPercent));
        mBatteryPercent.setSummary(mBatteryPercent.getEntry());
        mBatteryPercent.setOnPreferenceChangeListener(this);
        mBatteryPercent.setEnabled(batterystyle != 4 && batterystyle != 5);

        updateMasterPrefs();
    }

    private void updateMasterPrefs() {
        mStatusBarClockShow = (SystemSettingMasterSwitchPreference) findPreference(STATUS_BAR_CLOCK);
        mStatusBarClockShow.setChecked((Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.STATUS_BAR_CLOCK, 1) == 1));
        mStatusBarClockShow.setOnPreferenceChangeListener(this);
    
        mStatusBarLogo = (SystemSettingMasterSwitchPreference) findPreference(STATUS_BAR_LOGO);
        mStatusBarLogo.setChecked((Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.STATUS_BAR_LOGO, 0) == 1));
        mStatusBarLogo.setOnPreferenceChangeListener(this);
    
        mNetworkTraffic = (SystemSettingMasterSwitchPreference) findPreference(NETWORK_TRAFFIC);
        mNetworkTraffic.setChecked((Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.NETWORK_TRAFFIC_STATE, 0) == 1));
        mNetworkTraffic.setOnPreferenceChangeListener(this);

        mBatteryBar = (SystemSettingMasterSwitchPreference) findPreference(BATTERY_BAR);
        mBatteryBar.setChecked((Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.STATUSBAR_BATTERY_BAR, 0) == 1));
        mBatteryBar.setOnPreferenceChangeListener(this);

        mCarrierLabel = (SystemSettingMasterSwitchPreference) findPreference(CARRIER_LABEL);
        mCarrierLabel.setChecked((Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.CARRIER_LABEL_ENABLED, 1) == 1));
        mCarrierLabel.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mStatusBarClockShow) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_CLOCK, value ? 1 : 0);
            return true;
		} else if (preference == mStatusBarLogo) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_LOGO, value ? 1 : 0);
            return true;
        } else if (preference == mUseOldMobileType) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.USE_OLD_MOBILETYPE, value ? 1 : 0);
            return true;
		} else if (preference == mBatteryStyle) {
            int batterystyle = Integer.parseInt((String) newValue);
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                Settings.System.STATUS_BAR_BATTERY_STYLE, batterystyle,
                UserHandle.USER_CURRENT);
            int index = mBatteryStyle.findIndexOfValue((String) newValue);
            mBatteryStyle.setSummary(mBatteryStyle.getEntries()[index]);
            mBatteryPercent.setEnabled(batterystyle != 4 && batterystyle != 5);
            return true;
        } else if (preference == mBatteryPercent) {
            int batteryPercent = Integer.parseInt((String) newValue);
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_SHOW_BATTERY_PERCENT, batteryPercent,
                    UserHandle.USER_CURRENT);
            int index = mBatteryPercent.findIndexOfValue((String) newValue);
            mBatteryPercent.setSummary(mBatteryPercent.getEntries()[index]);
            return true;
        } else if (preference == mNetworkTraffic) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.NETWORK_TRAFFIC_STATE, value ? 1 : 0);
            return true;
	    } else if (preference == mBatteryBar) {
            if (mIsBarSwitchingMode) {
                return false;
            }
            mIsBarSwitchingMode = true;
            boolean showing = (Boolean) newValue;
            Settings.System.putIntForUser(getActivity().getContentResolver(), Settings.System.STATUSBAR_BATTERY_BAR,
                    showing ? 1 : 0, UserHandle.USER_CURRENT);
            mBatteryBar.setChecked(showing);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mIsBarSwitchingMode = false;
                }
            }, 1500);
            return true;
        } else if (preference == mCarrierLabel) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.CARRIER_LABEL_ENABLED, value ? 1 : 0);
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
