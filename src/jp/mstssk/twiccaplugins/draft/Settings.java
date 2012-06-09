package jp.mstssk.twiccaplugins.draft;

import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Window;

/**
 * 設定画面
 * 
 * @author mstssk
 */
public class Settings extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_LEFT_ICON);
        super.onCreate(savedInstanceState);
        setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.icon);
        addPreferencesFromResource(R.xml.pref);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.prefkey_quick_save))) {
            // クイック保存Activityの有効無効を切り替え
            boolean def = getResources().getBoolean(R.bool.pref_default);
            boolean enabled = sharedPreferences.getBoolean(key, def);
            int state = enabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                    : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
            PackageManager pm = getPackageManager();
            ComponentName cn = new ComponentName(this, QuickSaveActivity.class);
            pm.setComponentEnabledSetting(cn, state, PackageManager.DONT_KILL_APP);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume() {
        super.onResume();
        // OnSharedPreferenceChangeListenerを登録
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPause() {
        super.onPause();
        // OnSharedPreferenceChangeListenerを解除
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
