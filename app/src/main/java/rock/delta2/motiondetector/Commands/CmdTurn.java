package rock.delta2.motiondetector.Commands;

import android.content.Context;

import rock.delta2.motiondetector.Common.CmdBase;
import rock.delta2.motiondetector.Common.CmdParameters;
import rock.delta2.motiondetector.Common.ResultCmd;
import rock.delta2.motiondetector.Preferences.PreferencesHelper;
import rock.delta2.motiondetector.R;


public class CmdTurn extends CmdBase {
    public static final String _COMMAND = "turn";

    public String getDescription(Context context){
        return String.format("\n%s - %s", _COMMAND
                , context.getResources().getString(R.string.cmd_turn_description));
    }

    public CmdTurn(){
        super(en_type.other, _COMMAND);
    }

    public ResultCmd run(Context context, String ori, String[] parts, CmdParameters parms){

            PreferencesHelper.SetIsActive(!PreferencesHelper.GetIsActive());

//        MediatorMD.notifyStartStop(false);

        return new ResultCmd();
    }

}