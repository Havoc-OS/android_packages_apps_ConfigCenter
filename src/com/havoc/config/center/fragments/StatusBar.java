/*
 * Copyright (C) 2018 Havoc-OS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.havoc.config.center.fragments;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import androidx.preference.*;

import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.havoc.Utils;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.havoc.support.preferences.SystemSettingMasterSwitchPreference;
import com.havoc.support.preferences.SystemSettingSeekBarPreference;
import com.havoc.support.preferences.SystemSettingSwitchPreference;

public class StatusBar extends SettingsPreferenceFragment implements
    Preference.OnPreferenceChangeListener {

    private static final String STATUS_BAR_CLOCK = "status_bar_clock";
    private static final String STATUS_BAR_LOGO = "status_bar_logo";
    private static final String SHOW_HD_ICON = "show_hd_icon";
    private static final String KEY_NETWORK_TRAFFIC = "network_traffic_location";
    private static final String KEY_NETWORK_TRAFFIC_ARROW = "network_traffic_arrow";
    private static final String KEY_NETWORK_TRAFFIC_AUTOHIDE = "network_traffic_autohide_threshold";
    private static final String KEY_USE_OLD_MOBILETYPE = "use_old_mobiletype";

    private SystemSettingMasterSwitchPreference mStatusBarClockShow;
    private SystemSettingMasterSwitchPreference mStatusBarLogo;
    private SwitchPreference mShowHDVolte;
    private boolean mConfigShowHDVolteIcon;
    private ListPreference mNetworkTraffic;
    private SystemSettingSwitchPreference mNetworkTrafficArrow;
    private SystemSettingSeekBarPreference mNetworkTrafficAutohide;
    private SwitchPreference mUseOldMobileType;
    private boolean mConfigUseOldMobileType;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.config_center_statusbar);
        PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

        mConfigShowHDVolteIcon = getResources().getBoolean(com.android.internal.R.bool.config_display_hd_volte);
        int useHDIcon = (mConfigShowHDVolteIcon ? 1 : 0);
        mShowHDVolte = (SwitchPreference) findPreference(SHOW_HD_ICON);
        mShowHDVolte.setChecked((Settings.System.getInt(resolver,
                Settings.System.SHOW_HD_ICON, useHDIcon) == 1));
        mShowHDVolte.setOnPreferenceChangeListener(this);

        mNetworkTraffic = (ListPreference) findPreference(KEY_NETWORK_TRAFFIC);
        int networkTraffic = Settings.System.getInt(resolver,
        Settings.System.NETWORK_TRAFFIC_LOCATION, 0);
        CharSequence[] NonNotchEntries = { getResources().getString(R.string.network_traffic_disabled),
                getResources().getString(R.string.network_traffic_statusbar),
                getResources().getString(R.string.network_traffic_qs_header) };
        CharSequence[] NotchEntries = { getResources().getString(R.string.network_traffic_disabled),
                getResources().getString(R.string.network_traffic_qs_header) };
        CharSequence[] NonNotchValues = {"0", "1" , "2"};
        CharSequence[] NotchValues = {"0", "2"};
        mNetworkTraffic.setEntries(Utils.hasNotch(getActivity()) ? NotchEntries : NonNotchEntries);
        mNetworkTraffic.setEntryValues(Utils.hasNotch(getActivity()) ? NotchValues : NonNotchValues);
        mNetworkTraffic.setValue(String.valueOf(networkTraffic));
        mNetworkTraffic.setSummary(mNetworkTraffic.getEntry());
        mNetworkTraffic.setOnPreferenceChangeListener(this);

        mNetworkTrafficArrow = (SystemSettingSwitchPreference) findPreference(KEY_NETWORK_TRAFFIC_ARROW);
        mNetworkTrafficAutohide = (SystemSettingSeekBarPreference) findPreference(KEY_NETWORK_TRAFFIC_AUTOHIDE);
        updateNetworkTrafficPrefs(networkTraffic);

        mConfigUseOldMobileType = getResources().getBoolean(com.android.internal.R.bool.config_useOldMobileIcons);
        int useOldMobileIcons = (mConfigUseOldMobileType ? 1 : 0);
        mUseOldMobileType = (SwitchPreference) findPreference(KEY_USE_OLD_MOBILETYPE);
        mUseOldMobileType.setChecked((Settings.System.getInt(resolver,
                Settings.System.USE_OLD_MOBILETYPE, useOldMobileIcons) == 1));
        mUseOldMobileType.setOnPreferenceChangeListener(this);

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
        } else if (preference == mShowHDVolte) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.SHOW_HD_ICON, value ? 1 : 0);
            return true;
        } else if (preference == mNetworkTraffic) {
            int networkTraffic = Integer.valueOf((String) newValue);
            int index = mNetworkTraffic.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.NETWORK_TRAFFIC_LOCATION, networkTraffic);
            mNetworkTraffic.setSummary(mNetworkTraffic.getEntries()[index]);
            updateNetworkTrafficPrefs(networkTraffic);
            return true;
        } else if (preference == mUseOldMobileType) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.USE_OLD_MOBILETYPE, value ? 1 : 0);
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

    private void updateNetworkTrafficPrefs(int networkTraffic) {
        if (mNetworkTraffic != null) {
            if (networkTraffic == 0) {
                mNetworkTrafficArrow.setEnabled(false);
                mNetworkTrafficAutohide.setEnabled(false);
            } else {
                mNetworkTrafficArrow.setEnabled(true);
                mNetworkTrafficAutohide.setEnabled(true);
            }
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
