package rock.delta2.motiondetector.Detector;

import android.hardware.Camera;

import java.util.ArrayList;
import java.util.List;

import rock.delta2.motiondetector.Commands.CmdCameraAngleGet;
import rock.delta2.motiondetector.Commands.CmdCameraGet;
import rock.delta2.motiondetector.Commands.CmdCameraSizeGet;
import rock.delta2.motiondetector.Preferences.PreferencesHelper;

public class CameraPropHelper {

    public static CamearaProps getProps(String p, Camera c){
        CamearaProps res = null;

        if (c != null) {
            Camera.Parameters parm = c.getParameters();

            if (p.equals(CmdCameraGet._COMMAND))
                res = getPropsCamera(parm);
            else if (p.equals(CmdCameraAngleGet._COMMAND))
                res = getPropsCameraAngle(parm);
            else if (p.equals(CmdCameraSizeGet._COMMAND))
                res = getPropsCameraSize(parm);
        }

        return res;
    }

    public static CamearaProps getPropsCamera(Camera.Parameters p){
        CamearaProps res = null;

        res.current = PreferencesHelper.getCameraIdx();

        res.values.add("front");
        res.values.add("back");

        return res;
    }

    public static CamearaProps getPropsCameraAngle(Camera.Parameters p){
        CamearaProps res = null;

        res.current = PreferencesHelper.getCameraAngleIdx();

        res.values.add("0");
        res.values.add("90");
        res.values.add("180");
        res.values.add("270");

        return res;
    }

    public static CamearaProps getPropsCameraSize(Camera.Parameters p){
        CamearaProps res = null;

        Camera.Size c = p.getPreviewSize();
        List<Camera.Size> s = p.getSupportedPreviewSizes();

        for (int i = 0; i < s.size(); i++ ){
            res.values.add( String.format("%s x %s", s.get(i).width, s.get(i).height) );
            if (c.width == s.get(i).width && c.height == s.get(i).height)
                res.current = i;
        }


        return res;
    }
}
