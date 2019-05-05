package zafar.multimediademo.utility;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;
import java.util.Set;

import zafar.multimediademo.R;


/**
 * base class to use shared prefs in the application
 *
 * @author amit
 */
public class AppSharedPrefs {


    private static AppSharedPrefs sharedPrefs = null;
    private SharedPreferences sf;

    public AppSharedPrefs(Context context) {
        sf = context.getSharedPreferences(context.getString(R.string.key_shared_prefs), Context.MODE_PRIVATE);
    }

    public static AppSharedPrefs getInstance(Context context) {
        if (sharedPrefs == null) {
            sharedPrefs = new AppSharedPrefs(context);
        }

        return sharedPrefs;
    }

    public Object get(String key) {
        Map<String, ?> map = sf.getAll();
        return map.get((String) key);
    }

    public void put(String key, Object value) {
        SharedPreferences.Editor edit = sf.edit();

        if (value == null) {
            edit.putString(key, null);
        } else if (value instanceof Boolean) {
            edit.putBoolean(key, (boolean) value);
        } else if (value instanceof Float) {
            edit.putFloat(key, (float) value);
        } else if (value instanceof Integer) {
            edit.putInt(key, (int) value);
        } else if (value instanceof Long) {
            edit.putLong(key, (long) value);
        } else if (value instanceof String) {
            edit.putString(key, (String) value);
        } else if (value instanceof Set) {
            edit.putStringSet(key, (Set) value);
        }

        edit.apply();
    }

    public void clearAll() {
        sf.edit().clear().apply();
    }

    public void clear(String key) {
        sf.edit().remove(key).apply();
    }
}
