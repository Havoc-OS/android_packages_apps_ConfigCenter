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

import android.provider.Settings;

import com.android.settings.R;

import com.havoc.config.center.preferences.AppSelectorPreferenceFragment;

public class DanmakuBlacklist extends AppSelectorPreferenceFragment {

    @Override
    public String getTitle() {
        return mContext.getString(R.string.heads_up_blacklist_title);
    }

    @Override
    public String getPreferenceKey() {
        return Settings.System.GAMING_MODE_DANMAKU_APP_BLACKLIST;
    }
}
