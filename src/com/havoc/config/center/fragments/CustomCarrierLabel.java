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

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.havoc.Utils;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.havoc.support.preferences.SystemSettingListPreference;

public class CustomCarrierLabel extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener, CompoundButton.OnCheckedChangeListener {

    public static final String TAG = "CarrierLabel";
    private static final String CUSTOM_CARRIER_LABEL = "custom_carrier_label";
    private static final String KEY_CARRIER_LABEL = "carrier_label_location";

    private PreferenceScreen mCustomCarrierLabel;
    private String mCustomCarrierLabelText;
    private SystemSettingListPreference mShowCarrierLabel;

    private TextView mTextView;
    private View mSwitchBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContentResolver resolver = getActivity().getContentResolver();

        addPreferencesFromResource(R.xml.custom_carrier_label);
        PreferenceScreen prefSet = getPreferenceScreen();

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.master_setting_switch, container, false);
        ((ViewGroup) view).addView(super.onCreateView(inflater, container, savedInstanceState));
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        boolean enabled = Settings.System.getInt(getContentResolver(),
                Settings.System.CARRIER_LABEL_ENABLED, 1) == 1;

        mTextView = view.findViewById(R.id.switch_text);
        mTextView.setText(getString(enabled ?
                R.string.switch_on_text : R.string.switch_off_text));

        mSwitchBar = view.findViewById(R.id.switch_bar);
        Switch switchWidget = mSwitchBar.findViewById(android.R.id.switch_widget);
        switchWidget.setChecked(enabled);
        switchWidget.setOnCheckedChangeListener(this);
        mSwitchBar.setActivated(enabled);
        mSwitchBar.setOnClickListener(v -> {
            switchWidget.setChecked(!switchWidget.isChecked());
            mSwitchBar.setActivated(switchWidget.isChecked());
        });

        mCustomCarrierLabel.setEnabled(enabled);
        mShowCarrierLabel.setEnabled(enabled);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        Settings.System.putInt(getContentResolver(),
                Settings.System.CARRIER_LABEL_ENABLED, isChecked ? 1 : 0);
        mTextView.setText(getString(isChecked ? R.string.switch_on_text : R.string.switch_off_text));
        mSwitchBar.setActivated(isChecked);

        mCustomCarrierLabel.setEnabled(isChecked);
        mShowCarrierLabel.setEnabled(isChecked);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
 		ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mShowCarrierLabel) {
            int value = Integer.parseInt((String) newValue);
            updateCarrierLabelSummary(value);
            return true;
        }
        return false;
    }

    private void updateCarrierLabelSummary(int value) {
        Resources res = getResources();

        if (value == 0) {
            mShowCarrierLabel.setSummary(res.getString(R.string.show_carrier_keyguard));
        } else if (value == 1) {
            mShowCarrierLabel.setSummary(res.getString(R.string.show_carrier_statusbar));
        } else if (value == 2) {
            mShowCarrierLabel.setSummary(res.getString(R.string.show_carrier_both));
        }
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        ContentResolver resolver = getActivity().getContentResolver();
        boolean value;
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
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String value = ((Spannable) input.getText()).toString().trim();
                            Settings.System.putString(resolver, Settings.System.CUSTOM_CARRIER_LABEL, value);
                            updateCustomLabelTextSummary();
                            Intent i = new Intent();
                            i.setAction(Intent.ACTION_CUSTOM_CARRIER_LABEL_CHANGED);
                            getActivity().sendBroadcast(i);
                }
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
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HAVOC_SETTINGS;
    }
}
