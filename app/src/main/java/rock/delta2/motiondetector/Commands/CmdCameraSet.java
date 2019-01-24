package rock.delta2.motiondetector.Commands;

import android.content.Context;

import rock.delta2.motiondetector.Common.CmdBase;
import rock.delta2.motiondetector.Common.CmdParameters;
import rock.delta2.motiondetector.Common.ResultCmd;
import rock.delta2.motiondetector.Preferences.PreferencesHelper;
import rock.delta2.motiondetector.R;


public class CmdCameraSet extends CmdBase {
    public static final String _COMMAND = "camera";

    public String getDescription(Context context){
        return String.format("\n%s - %s", _COMMAND
                , context.getResources().getString(R.string.cmd_camera_set_description));
    }

    public CmdCameraSet(){
        super(en_type.set, _COMMAND);
    }

    public ResultCmd run(Context context, String ori, String[] parts, CmdParameters parms){
        int val = Integer.valueOf(parts[2]);
        PreferencesHelper.setCameraIdx (val);

        return new ResultCmd();
    }

}