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
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;

import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;

import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.custom.Utils;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.havoc.support.preferences.CustomSeekBarPreference;
import com.havoc.support.preferences.SecureSettingSwitchPreference;

public class Screen extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String SYSUI_ROUNDED_SIZE = "sysui_rounded_size";
    private static final String SYSUI_ROUNDED_FWVALS = "sysui_rounded_fwvals";
    private static final String KEY_CUTOUT_CATEGORY = "cutout_category";

    private CustomSeekBarPreference mCornerRadius;
    private SecureSettingSwitchPreference mRoundedFwvals;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.config_center_screen);
        Resources res = null;
        Context ctx = getContext();
        float density = Resources.getSystem().getDisplayMetrics().density;

        try {
            res = ctx.getPackageManager().getResourcesForApplication("com.android.systemui");
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        final PreferenceCategory cutoutCategory =
            (PreferenceCategory) getPreferenceScreen().findPreference(KEY_CUTOUT_CATEGORY);
        if (!Utils.hasNotch(getContext())) {
            getPreferenceScreen().removePreference(cutoutCategory);
        }

        // Rounded Corner Radius
        mCornerRadius = (CustomSeekBarPreference) findPreference(SYSUI_ROUNDED_SIZE);
        int resourceIdRadius = (int) ctx.getResources().getDimension(com.android.internal.R.dimen.rounded_corner_radius);
        mCornerRadius.setValue((int) (resourceIdRadius / density));
        mCornerRadius.setOnPreferenceChangeListener(this);

        // Rounded use Framework Values
        mRoundedFwvals = (SecureSettingSwitchPreference) findPreference(SYSUI_ROUNDED_FWVALS);
        mRoundedFwvals.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mCornerRadius) {
            Settings.Secure.putIntForUser(getContext().getContentResolver(), Settings.Secure.SYSUI_ROUNDED_SIZE,
                    (int) newValue, UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mRoundedFwvals) {
            restoreCorners();
            return true;
        }
        return false;
    }

    private void restoreCorners() {
        Resources res = null;
        float density = Resources.getSystem().getDisplayMetrics().density;
        Context ctx = getContext();

        try {
            res = ctx.getPackageManager().getResourcesForApplication("com.android.systemui");
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        int resourceIdRadius = (int) ctx.getResources().getDimension(com.android.internal.R.dimen.rounded_corner_radius);
        mCornerRadius.setValue((int) (resourceIdRadius / density));
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
