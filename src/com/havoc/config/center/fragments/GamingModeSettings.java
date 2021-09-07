/*
 * Copyright (C) 2020 The exTHmUI Open Source Project
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

import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;

import androidx.preference.Preference;

import com.android.settings.R;

import com.android.internal.custom.hardware.LineageHardwareManager;
import com.android.internal.logging.nano.MetricsProto;

import com.havoc.config.center.preferences.SwitchBarPreferenceFragment;
import com.havoc.support.preferences.SystemSettingMasterSwitchPreference;
import com.havoc.support.preferences.SystemSettingSeekBarPreference;
import com.havoc.support.preferences.SystemSettingSwitchPreference;

import java.util.ArrayList;

public class GamingModeSettings extends SwitchBarPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String GAMING_MODE_SHOW_DANMAKU = "gaming_mode_show_danmaku";

    private SystemSettingMasterSwitchPreference mShowDanmaku;
    private SystemSettingSwitchPreference mChangePerformanceLevel;
    private SystemSettingSeekBarPreference mPerformanceLevel;
    private SystemSettingSwitchPreference mHardwareKeysDisable;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.gaming_mode_settings);

        final Resources res = getResources();

        mChangePerformanceLevel = findPreference("gaming_mode_change_performance_level");
        mPerformanceLevel = findPreference("gaming_mode_performance_level");
        if (!res.getBoolean(com.android.internal.R.bool.config_enableGamingPerformanceTuning)) {
            mChangePerformanceLevel.setVisible(false);
            mPerformanceLevel.setVisible(false);
        }

        mHardwareKeysDisable = findPreference("gaming_mode_disable_hw_keys");
        LineageHardwareManager mLineageHardware = LineageHardwareManager.getInstance(getActivity());
        if (!mLineageHardware.isSupported(LineageHardwareManager.FEATURE_KEY_DISABLE)) {
            mHardwareKeysDisable.setVisible(false);
        }

        updateMasterPrefs();
    }

    private void updateMasterPrefs() {
        mShowDanmaku = (SystemSettingMasterSwitchPreference) findPreference(GAMING_MODE_SHOW_DANMAKU);
        mShowDanmaku.setChecked((Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.GAMING_MODE_SHOW_DANMAKU, 1) == 1));
        mShowDanmaku.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference == mShowDanmaku) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.GAMING_MODE_SHOW_DANMAKU, value ? 1 : 0);
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

    @Override
    public boolean getSwitchState() {
        return Settings.System.getInt(getContentResolver(),
                Settings.System.GAMING_MODE_ENABLED, 0) == 1;
    }

    @Override
    public void updateSwitchState(boolean isChecked) {
        Settings.System.putInt(getContentResolver(),
                Settings.System.GAMING_MODE_ENABLED, isChecked ? 1 : 0);
    }
}
