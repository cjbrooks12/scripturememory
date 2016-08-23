package com.caseybrooks.common.features.debug.preferences;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.caseybrooks.common.app.activity.FeatureConfiguration;
import com.caseybrooks.common.app.fragment.FragmentBase;
import com.caseybrooks.common.app.fragment.FragmentConfiguration;

public class DebugPreferencesFragment extends FragmentBase {

    public static FragmentBase newInstance(Bundle args) {
        DebugPreferencesFragment fragment = new DebugPreferencesFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TextView v = new TextView(getContext());
        v.setText("Debug Preferences");

        return v;
    }

    @NonNull
    @Override
    public Class<? extends FragmentConfiguration> getFragmentConfigurationClass() {
        return DebugPreferencesFragmentConfiguration.class;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public static class DebugPreferencesFragmentConfiguration extends FragmentConfiguration {
        public DebugPreferencesFragmentConfiguration(Context context) {
            super(context);
        }

        @NonNull
        @Override
        public Class<? extends FeatureConfiguration> getFeatureConfigurationClass() {
            return DebugPreferencesConfiguration.class;
        }

        @NonNull
        @Override
        public Class<? extends FragmentBase> getFragmentClass() {
            return DebugPreferencesFragment.class;
        }
    }
}