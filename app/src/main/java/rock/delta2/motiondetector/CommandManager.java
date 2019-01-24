package rock.delta2.motiondetector;


import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import rock.delta2.motiondetector.Commands.CmdCameraGet;
import rock.delta2.motiondetector.Commands.CmdCameraSet;
import rock.delta2.motiondetector.Commands.CmdCameraSizeGet;
import rock.delta2.motiondetector.Commands.CmdCameraSizeSet;
import rock.delta2.motiondetector.Commands.CmdDeltaGet;
import rock.delta2.motiondetector.Commands.CmdDeltaSet;
import rock.delta2.motiondetector.Commands.CmdPhotoGet;
import rock.delta2.motiondetector.Commands.CmdStart;
import rock.delta2.motiondetector.Commands.CmdStop;
import rock.delta2.motiondetector.Commands.CmdTurn;
import rock.delta2.motiondetector.Common.CmdBase;
import rock.delta2.motiondetector.Common.CmdParameters;
import rock.delta2.motiondetector.Common.ResultCmd;
import rock.delta2.motiondetector.Mediator.ICommandCheckMessage;
import rock.delta2.motiondetector.Mediator.MediatorMD;
import rock.delta2.motiondetector.Preferences.PreferencesHelper;

public class CommandManager implements ICommandCheckMessage {
    private static final String _OK = "ok";
    private static final String _NO = "???";

    private List<CmdBase> _commands;

    private CmdBase _CmdPhotoGet;

    private Context _context;

    public CommandManager(Context context){
        _context = context;


        _commands = new ArrayList<>();
        _CmdPhotoGet = new CmdPhotoGet();

        _commands.add(new CmdStart());
        _commands.add(new CmdStop());
        _commands.add(new CmdTurn());
        
        _commands.add(new CmdCameraSet());
        _commands.add(new CmdCameraGet());

        _commands.add(new CmdCameraSizeSet());
        _commands.add(new CmdCameraSizeGet());

        _commands.add(new CmdDeltaSet());
        _commands.add(new CmdDeltaGet());


    }

    public void onDestroy(){

    }


    public void CheckMessage(String inTxt, String msgId) {

        Helper.Log("CheckMessage", inTxt);

        inTxt = inTxt.toLowerCase();

        if(inTxt.contains(", ")){
            if(!inTxt.startsWith(PreferencesHelper.getDeviceName()))
                return;

            inTxt = inTxt.replace(PreferencesHelper.getDeviceName(), "");
            inTxt = inTxt.replace(", ", "");

        }



            CheckMessage(inTxt, false, false, msgId) ;

    }

    public void CheckMessage(String inTxt, boolean repeatCmd, boolean isSilent, String msgId) {

        inTxt = inTxt.toLowerCase();
        String[] inLines = inTxt.split("\\s+");

        if (inLines.length > 0) {

            String first = inLines[0];
            CmdBase.en_type cmdType = first.equals("get") ? CmdBase.en_type.get :
                    first.equals("set") ? CmdBase.en_type.set :
                            CmdBase.en_type.other;

            ResultCmd cmdResult = null;

            for (CmdBase cmd : _commands) {
                if (cmd.type == cmdType && (
                        (inLines.length > 1 && cmd.cmd.equals(inLines[1]))
                                || (inLines.length == 1 && cmd.cmd.equals(first)))
                        ) {
                    cmdResult = cmd.exec(_context, inTxt, inLines,new CmdParameters(msgId));
                    break;
                }
            }

            if (cmdResult == null) {
                if (cmdType == CmdBase.en_type.get || cmdType == CmdBase.en_type.set) {
                    if (!isSilent)
                        MediatorMD.sendText(msgId, repeatCmd ? (inTxt + " : ") : "" + _NO);
                } else {
                    chkError(_CmdPhotoGet.exec(_context, inTxt, inLines, new CmdParameters( msgId)), msgId);
                }
            } else if (cmdResult.result == CmdBase.en_result.ok &&
                    (cmdType == CmdBase.en_type.set
                            || CmdStart._COMMAND.equals(inLines[0])
                            || CmdStop._COMMAND.equals(inLines[0]))
                    ) {
                if (!isSilent)
                    MediatorMD.sendText(msgId,(repeatCmd ? (inTxt + " : ") : "") + _OK);
                else
                    chkError(cmdResult, msgId);
            }
        }
    }

    private void chkError(ResultCmd cmdResult, String msgId){
        if (cmdResult.result == CmdBase.en_result.error_msg)
            MediatorMD.sendText(msgId, cmdResult.msg);
        else if (cmdResult.result == CmdBase.en_result.exception)
            MediatorMD.sendText(msgId, "error : " + cmdResult.msg);
    }
}
