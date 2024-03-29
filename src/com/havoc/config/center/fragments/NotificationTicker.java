/*
 * Copyright (C) 2021 Havoc-OS
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
import android.provider.Settings;

import com.android.internal.logging.nano.MetricsProto;

import com.android.settings.R;

import com.havoc.config.center.preferences.SwitchBarPreferenceFragment;

public class NotificationTicker extends SwitchBarPreferenceFragment {

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.notification_ticker);
    }

    @Override
    public boolean getSwitchState() {
        return Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_SHOW_TICKER, 0) == 1;
    }

    @Override
    public void updateSwitchState(boolean isChecked) {
        Settings.System.putInt(getContentResolver(),
                Settings.System.STATUS_BAR_SHOW_TICKER, isChecked ? 1 : 0);
    }
}
