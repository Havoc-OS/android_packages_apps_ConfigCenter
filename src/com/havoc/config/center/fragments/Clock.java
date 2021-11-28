/*
 * Copyright (C) 2020-21 Havoc-OS
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
import android.provider.Settings;
import android.text.format.DateFormat;

import androidx.preference.ListPreference;
import androidx.preference.Preference;

import com.android.internal.util.custom.Utils;
import com.android.settings.R;
import com.havoc.config.center.preferences.SwitchBarPreferenceFragment;

public class Clock extends SwitchBarPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String STATUS_BAR_CLOCK_STYLE = "statusbar_clock_style";
    private static final String STATUS_BAR_AM_PM = "status_bar_am_pm";

    private ListPreference mStatusBarClock;
    private ListPreference mStatusBarAmPm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.clock);
        ContentResolver resolver = getActivity().getContentResolver();

        mStatusBarClock = (ListPreference) findPreference(STATUS_BAR_CLOCK_STYLE);
        mStatusBarAmPm = (ListPreference) findPreference(STATUS_BAR_AM_PM);

        int clockStyle = Settings.System.getInt(resolver,
                Settings.System.STATUSBAR_CLOCK_STYLE, 0);
        CharSequence[] NonNotchEntries = { getResources().getString(R.string.status_bar_clock_style_left),
                getResources().getString(R.string.status_bar_clock_style_center),
                getResources().getString(R.string.status_bar_clock_style_right) };
        CharSequence[] NotchEntries = { getResources().getString(R.string.status_bar_clock_style_left),
                getResources().getString(R.string.status_bar_clock_style_right) };
        CharSequence[] NonNotchValues = {"0", "1" , "2"};
        CharSequence[] NotchValues = {"0", "2"};
        mStatusBarClock.setEntries(Utils.hasNotch(getActivity()) ? NotchEntries : NonNotchEntries);
        mStatusBarClock.setEntryValues(Utils.hasNotch(getActivity()) ? NotchValues : NonNotchValues);
        mStatusBarClock.setValue(String.valueOf(clockStyle));
        mStatusBarClock.setSummary(mStatusBarClock.getEntry());
        mStatusBarClock.setOnPreferenceChangeListener(this);

        if (DateFormat.is24HourFormat(getActivity())) {
            mStatusBarAmPm.setEnabled(false);
            mStatusBarAmPm.setSummary(R.string.status_bar_am_pm_info);
        } else {
            int statusBarAmPm = Settings.System.getInt(resolver,
                    Settings.System.STATUSBAR_CLOCK_AM_PM_STYLE, 2);
            mStatusBarAmPm.setValue(String.valueOf(statusBarAmPm));
            mStatusBarAmPm.setSummary(mStatusBarAmPm.getEntry());
            mStatusBarAmPm.setOnPreferenceChangeListener(this);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mStatusBarClock) {
            int clockStyle = Integer.parseInt((String) newValue);
            int index = mStatusBarClock.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_CLOCK_STYLE, clockStyle);
            mStatusBarClock.setSummary(mStatusBarClock.getEntries()[index]);
            return true;
        } else if (preference == mStatusBarAmPm) {
            int statusBarAmPm = Integer.parseInt((String) newValue);
            int index = mStatusBarAmPm.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_CLOCK_AM_PM_STYLE, statusBarAmPm);
            mStatusBarAmPm.setSummary(mStatusBarAmPm.getEntries()[index]);
            return true;
        }
        return false;
    }

    @Override
    public boolean getSwitchState() {
        return Settings.System.getInt(getContentResolver(),
                Settings.System.STATUSBAR_CLOCK, 1) == 1;
    }

    @Override
    public void updateSwitchState(boolean isChecked) {
        Settings.System.putInt(getContentResolver(),
                Settings.System.STATUSBAR_CLOCK, isChecked ? 1 : 0);
    }
}
