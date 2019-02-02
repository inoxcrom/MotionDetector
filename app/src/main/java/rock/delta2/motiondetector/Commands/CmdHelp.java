package rock.delta2.motiondetector.Commands;

import android.content.Context;

import rock.delta2.motiondetector.Common.CmdBase;
import rock.delta2.motiondetector.Common.CmdParameters;
import rock.delta2.motiondetector.Common.ResultCmd;
import rock.delta2.motiondetector.InfoHelper;
import rock.delta2.motiondetector.R;


public class CmdHelp extends CmdBase {
    public static final String _COMMAND = "help";

    public String getDescription(Context context){
        return String.format("\n%s - %s", _COMMAND
                , context.getResources().getString(R.string.cmd_help_description));
    }

    public CmdHelp(){
        super(en_type.other, _COMMAND);
    }

    public ResultCmd run(Context context, String ori, String[] parts, CmdParameters parms){
        InfoHelper.sendHelp(context);

        return new ResultCmd();
    }

}