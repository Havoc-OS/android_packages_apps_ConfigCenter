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

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.internal.util.custom.Utils;
import com.android.settings.R;
import com.havoc.config.center.preferences.SwitchBarPreferenceFragment;
import com.havoc.support.preferences.SystemSettingListPreference;

public class CustomCarrierLabel extends SwitchBarPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String CUSTOM_CARRIER_LABEL = "custom_carrier_label";
    private static final String KEY_CARRIER_LABEL = "carrier_label_location";

    private PreferenceScreen mCustomCarrierLabel;
    private SystemSettingListPreference mShowCarrierLabel;
    private String mCustomCarrierLabelText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContentResolver resolver = getActivity().getContentResolver();

        addPreferencesFromResource(R.xml.custom_carrier_label);

        mCustomCarrierLabel = (PreferenceScreen) findPreference(CUSTOM_CARRIER_LABEL);
        updateCustomLabelTextSummary();

        mShowCarrierLabel = (SystemSettingListPreference) findPreference(KEY_CARRIER_LABEL);
        int showCarrierLabel = Settings.System.getInt(resolver,
                Settings.System.CARRIER_LABEL_LOCATION, 0);
        CharSequence[] NonNotchEntries = {
                getResources().getString(R.string.show_carrier_keyguard),
                getResources().getString(R.string.show_carrier_statusbar),
                getResources().getString(R.string.show_carrier_enabled)
        };
        CharSequence[] NotchEntries = {
                getResources().getString(R.string.show_carrier_keyguard)
        };
        CharSequence[] NonNotchValues = {"0", "1" , "2"};
        CharSequence[] NotchValues = {"0"};
        mShowCarrierLabel.setEntries(Utils.hasNotch(getActivity()) ? NotchEntries : NonNotchEntries);
        mShowCarrierLabel.setEntryValues(Utils.hasNotch(getActivity()) ? NotchValues : NonNotchValues);
        mShowCarrierLabel.setValue(String.valueOf(showCarrierLabel));
        mShowCarrierLabel.setSummary(mShowCarrierLabel.getEntry());
        mShowCarrierLabel.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mShowCarrierLabel) {
            int value = Integer.parseInt((String) newValue);
            updateCarrierLabelSummary(value);
            return true;
        }
        return false;
    }

    private void updateCarrierLabelSummary(int value) {
        switch (value) {
            default:
            case 0:
                mShowCarrierLabel.setSummary(getResources().getString(R.string.show_carrier_keyguard));
                break;
            case 1:
                mShowCarrierLabel.setSummary(getResources().getString(R.string.show_carrier_statusbar));
                break;
            case 2:
                mShowCarrierLabel.setSummary(getResources().getString(R.string.show_carrier_both));
                break;
        }
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mCustomCarrierLabel) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle(R.string.custom_carrier_label_title);
            alert.setMessage(R.string.custom_carrier_label_explain);
            LinearLayout container = new LinearLayout(getActivity());
            container.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(55, 20, 55, 20);
            final EditText input = new EditText(getActivity());
            int maxLength = 25;
            input.setLayoutParams(lp);
            input.setGravity(android.view.Gravity.TOP| Gravity.START);
            input.setText(TextUtils.isEmpty(mCustomCarrierLabelText) ? "" : mCustomCarrierLabelText);
            input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
            container.addView(input);
            alert.setView(container);
            alert.setPositiveButton(getString(android.R.string.ok),
                    (DialogInterface.OnClickListener) (dialog, whichButton) -> {
                        String value = input.getText().toString().trim();
                        Settings.System.putString(resolver, Settings.System.CUSTOM_CARRIER_LABEL, value);
                        updateCustomLabelTextSummary();
                        Intent i = new Intent();
                        i.setAction(Intent.ACTION_CUSTOM_CARRIER_LABEL_CHANGED);
                        getActivity().sendBroadcast(i);
                    });
            alert.setNegativeButton(getString(android.R.string.cancel), null);
            alert.show();
            return true;
        }
        return false;
    }

    private void updateCustomLabelTextSummary() {
        mCustomCarrierLabelText = Settings.System.getString(
                getContentResolver(), Settings.System.CUSTOM_CARRIER_LABEL);
        if (TextUtils.isEmpty(mCustomCarrierLabelText)) {
            mCustomCarrierLabel.setSummary(R.string.custom_carrier_label_notset);
        } else {
            mCustomCarrierLabel.setSummary(mCustomCarrierLabelText);
        }
    }

    @Override
    public boolean getSwitchState() {
        return Settings.System.getInt(getContentResolver(),
                Settings.System.CARRIER_LABEL_ENABLED, 1) == 1;
    }

    @Override
    public void updateSwitchState(boolean isChecked) {
        Settings.System.putInt(getContentResolver(),
                Settings.System.CARRIER_LABEL_ENABLED, isChecked ? 1 : 0);
    }
}
