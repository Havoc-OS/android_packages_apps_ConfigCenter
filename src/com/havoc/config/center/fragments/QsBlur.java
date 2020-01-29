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

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.provider.Settings;
import androidx.preference.*;
import android.widget.Toast;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.SettingsPreferenceFragment;
import com.havoc.support.preferences.CustomSeekBarPreference;

import com.havoc.config.center.R;

import java.util.List;
import java.util.ArrayList;

public class QsBlur extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    public static final String TAG = "QsBlur";

    private static final String QS_BACKGROUND_BLUR_ALPHA = "qs_background_blur_alpha";
    private static final String QS_BACKGROUND_BLUR_INTENSITY = "qs_background_blur_intensity";

    private CustomSeekBarPreference mQSBlurAlpha;
    private CustomSeekBarPreference mQSBlurIntensity;

    Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.qs_blur);

        mQSBlurAlpha = (CustomSeekBarPreference) findPreference(QS_BACKGROUND_BLUR_ALPHA);
        int qsBlurAlpha = Settings.System.getInt(getContentResolver(),
                Settings.System.QS_BACKGROUND_BLUR_ALPHA, 100);
        mQSBlurAlpha.setValue(qsBlurAlpha);
        mQSBlurAlpha.setOnPreferenceChangeListener(this);

        mQSBlurIntensity = (CustomSeekBarPreference) findPreference(QS_BACKGROUND_BLUR_INTENSITY);
        int qsBlurIntensity = Settings.System.getInt(getContentResolver(),
                Settings.System.QS_BACKGROUND_BLUR_INTENSITY, 30);
        mQSBlurIntensity.setValue(qsBlurIntensity);
        mQSBlurIntensity.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mQSBlurAlpha) {
            int value = (Integer) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.QS_BACKGROUND_BLUR_ALPHA, value);
            return true;
        } else if (preference == mQSBlurIntensity) {
            int value = (Integer) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.QS_BACKGROUND_BLUR_INTENSITY, value);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
