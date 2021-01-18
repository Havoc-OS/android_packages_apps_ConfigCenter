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

import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import androidx.preference.Preference;

import com.android.internal.custom.app.LineageContextConstants;
import com.android.internal.logging.nano.MetricsProto;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.havoc.support.preferences.SwitchPreference;
import com.havoc.support.preferences.SystemSettingSwitchPreference;

public class LockScreen extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    // private static final String FOD_ANIMATION_PREF = "fod_recognizing_animation";
    // private static final String KEY_SCREEN_OFF_FOD = "screen_off_fod";
    // private static final String KEY_SCREEN_OFF_FOD_ICON = "screen_off_fod_icon";

    // private SystemSettingSwitchPreference mFODAnimationEnabled;
    // private SwitchPreference mScreenOffFOD;
    // private SystemSettingSwitchPreference mScreenOffFODIcon;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.config_center_lockscreen);

        // PackageManager packageManager = getPackageManager();

        // mFODAnimationEnabled = (SystemSettingSwitchPreference) findPreference(FOD_ANIMATION_PREF);

        // boolean mScreenOffFODValue = Settings.System.getInt(getActivity().getContentResolver(),
        //         Settings.System.SCREEN_OFF_FOD, 0) != 0;

        // mScreenOffFOD = (SwitchPreference) findPreference(KEY_SCREEN_OFF_FOD);
        // mScreenOffFOD.setChecked(mScreenOffFODValue);
        // mScreenOffFOD.setOnPreferenceChangeListener(this);

        // mScreenOffFODIcon = (SystemSettingSwitchPreference) findPreference(KEY_SCREEN_OFF_FOD_ICON);

        // if (!packageManager.hasSystemFeature(LineageContextConstants.Features.FOD)) {
        //     mFODAnimationEnabled.setVisible(false);
        //     mScreenOffFOD.setVisible(false);
        //     mScreenOffFODIcon.setVisible(false);
        // }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        // if (preference == mScreenOffFOD) {
        //     int mScreenOffFODValue = (Boolean) newValue ? 1 : 0;
        //     Settings.System.putInt(resolver, Settings.System.SCREEN_OFF_FOD, mScreenOffFODValue);
        //     Settings.Secure.putInt(resolver, Settings.Secure.DOZE_ALWAYS_ON, mScreenOffFODValue);
        //     return true;
        // }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
