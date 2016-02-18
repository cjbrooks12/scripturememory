package com.caseybrooks.common.fragments;

import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.util.Pair;

import com.caseybrooks.common.R;
import com.caseybrooks.common.app.AppFeature;
import com.caseybrooks.common.app.AppSettings;
import com.caseybrooks.common.app.ExpandableNavigationView;
import com.caseybrooks.common.app.PreferenceFragmentBase;
import com.caseybrooks.common.notifications.DailyNotification;
import com.caseybrooks.common.notifications.ScheduledNotification;

import java.util.ArrayList;

public class SettingsFragment extends PreferenceFragmentBase {
    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle data = new Bundle();
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        super.onCreatePreferences(bundle, s);
        addPreferencesFromResource(R.xml.settings);
        settingsScreens.push(getPreferenceScreen());

        createDefaultScreenFeatures();

        findPreference("votd_enabled").setOnPreferenceChangeListener(votdChangedListener);
        findPreference("votd_time").setOnPreferenceChangeListener(votdChangedListener);

        findPreference("schedule_enabled").setOnPreferenceChangeListener(scheduledChangedListener);
        findPreference("schedule_start").setOnPreferenceChangeListener(scheduledChangedListener);
        findPreference("schedule_end").setOnPreferenceChangeListener(scheduledChangedListener);
        findPreference("schedule_interval").setOnPreferenceChangeListener(scheduledChangedListener);

        findPreference("APP_THEME").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                getActivity().recreate();
                return true;
            }
        });

    }

    @Override
    public Pair<AppFeature, Integer> getFeatureForFragment() {
        return new Pair<>(AppFeature.Settings, 0);
    }

    private Preference.OnPreferenceChangeListener votdChangedListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            if(o instanceof Boolean) {
                boolean value = (boolean) o;

                if(value) {
                    DailyNotification.setNotificationAlarm(getContext());
                }
                else {
                    DailyNotification.cancelNotificationAlarm(getContext());
                }
            }
            else {
                DailyNotification.setNotificationAlarm(getContext());
            }

            return true;
        }
    };

    private Preference.OnPreferenceChangeListener scheduledChangedListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            if(o instanceof Boolean) {
                boolean value = (boolean) o;

                if(value) {
                    ScheduledNotification.setNotificationAlarm(getContext());
                }
                else {
                    ScheduledNotification.cancelNotificationAlarm(getContext());
                }
            }
            else {
                ScheduledNotification.setNotificationAlarm(getContext());
            }

            return true;
        }
    };

    //Create common preferences programatically
//--------------------------------------------------------------------------------------------------

    public void createDefaultScreenFeatures() {
        AppFeature defaultFeature = AppSettings.getDefaultFeature(getContext()).first;

        ArrayList<AppFeature> features = getActivityBase().getFeatures();
        features.add(0, AppFeature.LastVisited);

        ArrayList<String> featureNames = new ArrayList<>();
        ArrayList<String> featureIds = new ArrayList<>();

        int i = 0;
        for(AppFeature feature : features) {
            if(feature == AppFeature.LastVisited ||
                (feature.isTopLevel()) && feature != AppFeature.Settings &&feature != AppFeature.Help) {

                String name = feature.getTitle();
                if(feature.hasChildren())
                    name += "...";

                featureNames.add(name);
                featureIds.add("" + feature.getId());

                i++;
            }
        }

        String[] featureNamesArray = new String[featureNames.size()];
        String[] featureIdsArray = new String[featureIds.size()];

        featureNames.toArray(featureNamesArray);
        featureIds.toArray(featureIdsArray);

        final ListPreference pref = (ListPreference) findPreference("DEFAULT_FEATURE");

        pref.setEntries(featureNamesArray);
        pref.setEntryValues(featureIdsArray);
        pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                int id = Integer.parseInt((String) o);

                AppFeature feature = AppFeature.getFeatureForId(id);

                pref.setValue("" + feature.getId());
                pref.setSummary(feature.getTitle());

                if(feature.hasChildren()) {
                    findPreference("DEFAULT_FEATURE_ID").setEnabled(true);
                    findPreference("DEFAULT_FEATURE_ID").setSummary("Select Child");
                    createDefaultScreenFeatureChildren(feature);
                }
                else {
                    AppSettings.putDefaultFeature(getContext(), feature, 0);
                    findPreference("DEFAULT_FEATURE_ID").setEnabled(false);
                    findPreference("DEFAULT_FEATURE_ID").setSummary(null);
                }

                return false;
            }
        });
        pref.setValue("" + defaultFeature.getId());
        pref.setSummary(defaultFeature.getTitle());

        if(defaultFeature.hasChildren()) {
            findPreference("DEFAULT_FEATURE_ID").setEnabled(true);
            createDefaultScreenFeatureChildren(defaultFeature);
        }
        else {
            findPreference("DEFAULT_FEATURE_ID").setEnabled(false);
        }
    }

    public void createDefaultScreenFeatureChildren(final AppFeature feature) {
        ArrayList<String> childrenNames = new ArrayList<>();
        ArrayList<String> childrenIds = new ArrayList<>();

        ArrayList<ExpandableNavigationView.NavChildItem> children = getActivityBase().getChildrenForFeature(feature);

        for(ExpandableNavigationView.NavChildItem child : children) {
            childrenNames.add(child.subitemText);
            childrenIds.add("" + child.appFeatureId);
        }

        String[] featureNamesArray = new String[childrenNames.size()];
        String[] featureIdsArray = new String[childrenIds.size()];

        childrenNames.toArray(featureNamesArray);
        childrenIds.toArray(featureIdsArray);

        ListPreference pref = (ListPreference) findPreference("DEFAULT_FEATURE_ID");

        pref.setEntries(featureNamesArray);
        pref.setEntryValues(featureIdsArray);
        pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                int id = Integer.parseInt((String) o);

                AppSettings.putDefaultFeature(getContext(), feature, id);
                if(feature.hasChildren()) {
                    findPreference("DEFAULT_FEATURE_ID").setEnabled(true);
                    createDefaultScreenFeatureChildren(feature);
                }
                else {
                    findPreference("DEFAULT_FEATURE_ID").setEnabled(false);
                }

                return false;
            }
        });
    }
}
