package rock.delta2.motiondetector.Commands;

import android.content.Context;

import rock.delta2.motiondetector.Common.CmdBase;
import rock.delta2.motiondetector.Common.CmdParameters;
import rock.delta2.motiondetector.Common.ResultCmd;
import rock.delta2.motiondetector.Mediator.MediatorMD;
import rock.delta2.motiondetector.R;


public class CmdAutostartGet extends CmdBase {
    public static final String _COMMAND = "autostart";

    public String getDescription(Context context){
        return String.format("\n%s - %s", _COMMAND
                , context.getResources().getString(R.string.cmd_auto_start_get_description));
    }

    public CmdAutostartGet(){
        super(en_type.get, _COMMAND);
    }

    public ResultCmd run(Context context, String ori, String[] parts, CmdParameters parms){
        //MediatorMD.SendCameraProp(parms.msgId, _COMMAND);

        return new ResultCmd();
    }

}