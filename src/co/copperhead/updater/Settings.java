package co.copperhead.updater;

import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.UserManager;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class Settings extends PreferenceActivity {
    private static final String TAG = "Settings";
    static final String KEY_NETWORK_TYPE = "network_type";

    static SharedPreferences getPreferences(final Context context) {
        final Context deviceContext = context.createDeviceProtectedStorageContext();
        return PreferenceManager.getDefaultSharedPreferences(deviceContext);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!UserManager.get(this).isSystemUser()) {
            throw new SecurityException("system user only");
        }
        getPreferenceManager().setStorageDeviceProtected();
        addPreferencesFromResource(R.xml.settings);
        final Preference networkType = findPreference(KEY_NETWORK_TYPE);
        networkType.setOnPreferenceChangeListener((final Preference preference, final Object newValue) -> {
            final int value = Integer.parseInt((String) newValue);
            final SharedPreferences preferences = getPreferences(Settings.this);
            preferences.edit().putInt(KEY_NETWORK_TYPE, value).apply();
            PeriodicJob.schedule(Settings.this);
            return true;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        final ListPreference networkType = (ListPreference) findPreference(KEY_NETWORK_TYPE);
        networkType.setValue(Integer.toString(PeriodicJob.getNetworkType(this)));
    }
}
