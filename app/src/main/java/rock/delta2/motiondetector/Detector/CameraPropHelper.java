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
        CamearaProps res = new CamearaProps();

        if (c != null) {
            Camera.Parameters parm = c.getParameters();

            if (p.equals(CmdCameraGet._COMMAND))
                res = getPropsCamera(c);
            else if (p.equals(CmdCameraAngleGet._COMMAND))
                res = getPropsCameraAngle(parm);
            else if (p.equals(CmdCameraSizeGet._COMMAND))
                res = getPropsCameraSize(parm);
        }

        return res;
    }

    public static CamearaProps getPropsCamera(Camera c){
        CamearaProps res = new CamearaProps();

        res.current = PreferencesHelper.getCameraIdx();

        res.values.add("back");

        if( Camera.getNumberOfCameras() > 1)
            res.values.add("front");

        return res;
    }

    public static CamearaProps getPropsCameraAngle(Camera.Parameters p){
        CamearaProps res = new CamearaProps();

        res.current = PreferencesHelper.getCameraAngleIdx();

        res.values.add("0");
        res.values.add("90");
        res.values.add("180");
        res.values.add("270");

        return res;
    }

    public static int getAngle(int idx){
        int res = 0;
        if(idx == 0 )
            res = 0;
        else if (idx == 1)
            res = 90;
        else if (idx == 2)
            res = 180;
        else if (idx == 3)
            res = 270;

        return res;
    }

    public static CamearaProps getPropsCameraSize(Camera.Parameters p){
        CamearaProps res = new CamearaProps();

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
