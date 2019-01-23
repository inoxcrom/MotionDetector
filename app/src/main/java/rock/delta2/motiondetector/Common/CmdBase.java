package rock.delta2.motiondetector.Common;

import android.content.Context;

import rock.delta2.motiondetector.Helper;
import rock.delta2.motiondetector.Mediator.MediatorMD;

public abstract class CmdBase {

    public final String _ON = "on";
    public final String _OFF = "off";

    public en_type type ;
    public String cmd;

    public CmdBase(en_type tp, String c){
        type = tp;
        cmd = c;
    }

    public enum en_type{
        set,
        get,
        other
    }

    public enum en_result{
        ok,
        exception,
        error_msg
    }

    public ResultCmd exec(Context context, String orig, String[] parts, CmdParameters parms){
        ResultCmd res;
        try {
            Helper.Log("Cmd.exec",cmd + " " + orig);
            res = run(context, orig, parts, parms);
            MediatorMD.OnCommandExcecuted(cmd);
        } catch (Exception ex) {
            Helper.Ex2Log(ex);
            res = new ResultCmd(en_result.exception, ex.getMessage());
        }

        return res;

    }

    public void OnDestroy(){}

    public abstract ResultCmd run(Context context, String orig, String[] parts, CmdParameters parms);

    public abstract String getDescription(Context context);
}
