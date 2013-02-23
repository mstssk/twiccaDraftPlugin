
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
    protected void onCreate(final Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        super.onCreate(savedInstanceState);
        this.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.icon);
        this.addPreferencesFromResource(R.xml.pref);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences,
            final String key) {
        if (key.equals(this.getString(R.string.prefkey_quick_save))) {
            // クイック保存Activityの有効無効を切り替え
            final boolean def = this.getResources().getBoolean(R.bool.pref_default);
            final boolean enabled = sharedPreferences.getBoolean(key, def);
            final int state = enabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                    : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
            final PackageManager pm = this.getPackageManager();
            final ComponentName cn = new ComponentName(this, QuickSaveActivity.class);
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
        this.getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPause() {
        super.onPause();
        // OnSharedPreferenceChangeListenerを解除
        this.getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
