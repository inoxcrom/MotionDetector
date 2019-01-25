package rock.delta2.motiondetector.Commands;

import android.content.Context;

import rock.delta2.motiondetector.Common.CmdBase;
import rock.delta2.motiondetector.Common.CmdParameters;
import rock.delta2.motiondetector.Common.ResultCmd;
import rock.delta2.motiondetector.Preferences.PreferencesHelper;
import rock.delta2.motiondetector.R;


public class CmdAutostartSet extends CmdBase {
    public static final String _COMMAND = "autostart";

    public String getDescription(Context context){
        return String.format("\n%s - %s", _COMMAND
                , context.getResources().getString(R.string.cmd_auto_start_set_description));
    }

    public CmdAutostartSet(){
        super(en_type.set, _COMMAND);
    }

    public ResultCmd run(Context context, String ori, String[] parts, CmdParameters parms){
        if (parts[2].equals(_ON))
            PreferencesHelper.setAutoStart(true);
        else if (parts[2].equals(_OFF))
            PreferencesHelper.setAutoStart(false);

        return new ResultCmd();
    }

}