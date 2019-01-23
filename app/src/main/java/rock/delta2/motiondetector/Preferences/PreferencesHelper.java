package rock.delta2.motiondetector.Preferences;

import android.content.Context;
import android.content.SharedPreferences;


public class PreferencesHelper {

    public static final String APP_PREFERENCES = "preference";

    public static final String AUTO_START = "AUTO_START";
    public static final String DELTA = "DELTA";
    public static final String DEVICE_NAME = "DEVICE_NAME";

    public static final String CAMERA_IDX = "CAMERA_IDX";
    public static final String CAMERA_SIZE_IDX = "CAMERA_SIZE_IDX";



    private static SharedPreferences mSettings;

    private static PreferenceValue _autoStart;
    private static PreferenceValue _isActive;
    private static PreferenceValue _delta;
    private static PreferenceValue _deviceName;

    private static PreferenceValue _cameraIdx;
    private static PreferenceValue _cameraSizeIdx;


    public static void init(Context context) {
        mSettings = context.getSharedPreferences(PreferencesHelper.APP_PREFERENCES, Context.MODE_PRIVATE);

        _autoStart  = new PreferenceValue(mSettings, AUTO_START, true);

        _isActive  = new PreferenceValue(mSettings, AUTO_START, true);
        _delta = new PreferenceValue(mSettings, DELTA, 8);
        _deviceName = new PreferenceValue(mSettings, DEVICE_NAME, "");

        _cameraIdx = new PreferenceValue(mSettings, CAMERA_IDX, 0);
        _cameraSizeIdx = new PreferenceValue(mSettings, CAMERA_SIZE_IDX, 0);
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

    //region DELTA
    public static void setDelta(int val) {
        _delta.setInt(val);
    }

    public static int getDelta() {
        return _delta.getInt();
    }
    //endregion DELTA

    //region DEVICE_NAME
    public static void setDeviceName(String val) {
        _deviceName.setStr(val);
    }

    public static String getDeviceName() {
        return _deviceName.getStr();
    }
    //endregion DELTA

    //region CAMERA_IDX
    public static void setCameraIdx(int val) {
        _cameraIdx.setInt(val);
    }

    public static int getCameraIdx() {
        return _cameraIdx.getInt();
    }
    //endregion CAMERA_IDX

    //region CAMERA_SIZE_IDX
    public static void setCameraSizeIdx(int val) {
        _cameraSizeIdx.setInt(val);
    }

    public static int getCameraSizeIdx() {
        return _cameraSizeIdx.getInt();
    }
    //endregion CAMERA_SIZE_IDX

}
