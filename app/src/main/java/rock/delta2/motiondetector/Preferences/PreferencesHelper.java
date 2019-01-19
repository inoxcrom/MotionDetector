package rock.delta2.motiondetector.Preferences;

import android.content.Context;
import android.content.SharedPreferences;


public class PreferencesHelper {

    public static final String APP_PREFERENCES = "preference";

    public static final String AUTO_START = "AUTO_START";


    private static SharedPreferences mSettings;
    private static PreferenceValue _autoStart;
    private static PreferenceValue _isActive;


    public static void init(Context context) {
        mSettings = context.getSharedPreferences(PreferencesHelper.APP_PREFERENCES, Context.MODE_PRIVATE);

        _autoStart  = new PreferenceValue(mSettings, AUTO_START, true);

        _isActive  = new PreferenceValue(mSettings, AUTO_START, true);
    }


    //region AUTO_START
    public static void setAutoStart(boolean val) {
        _autoStart.setBool(val);
    }

    public static boolean getAutoStart() {
        return _autoStart.getBool();
    }
    //endregion AUTO_START

    //region IS_ACTIVE
    public static void SetIsActive(boolean isActive) {
        _isActive.setBool(isActive);
    }

    public static boolean GetIsActive(){
        return _isActive.getBool();
    }
    // endregion IS_ACTIVE

}
