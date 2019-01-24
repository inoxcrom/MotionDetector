package rock.delta2.motiondetector.Mediator;


import java.util.ArrayList;
import java.util.List;

import rock.delta2.motiondetector.Common.CmdParameters;
import rock.delta2.motiondetector.Common.RawPicture;

public class MediatorMD {

    //region ITransport
    private static ITransport _Transport;

    public static void setTransport(ITransport t){
        _Transport = t;
    }

    public static void sendText(String replMsgId, String txt){
        if(_Transport != null)
            _Transport.sendTxt(replMsgId, txt);
    }

    public static void sendPhoto(String replMsgId, String file, String caption){
        if(_Transport != null)
            _Transport.sendPhoto(replMsgId, file, caption);
    }

    public static void sendFile(String replMsgId, String file){
        if(_Transport != null)
            _Transport.sendFile(replMsgId, file);
    }

    public static void callVoice(){
        if(_Transport != null)
            _Transport.callVoice();
    }


    //endregion ITransport

    //region ICommandCheckMessage
    private static ICommandCheckMessage _CommandCheckMessage;

    public static void setCommandCheckMessage(ICommandCheckMessage t){
        _CommandCheckMessage = t;
    }
    public static void CheckMessage(String inTxt, boolean repeatCmd, boolean isSilent, String msgId){
        if(_CommandCheckMessage != null)
            _CommandCheckMessage.CheckMessage(inTxt, repeatCmd, isSilent, msgId);
    }
    public static void CheckMessage(String inTxt, String msgId){
        if(_CommandCheckMessage != null)
            _CommandCheckMessage.CheckMessage(inTxt, msgId);
    }

    //endregion ICommandCheckMessage

    // region IGetRawPciture
    private  static IGetRawPciture _IGetRawPciture;

    public static void registerGetRawPciture(IGetRawPciture p){
        _IGetRawPciture = p;
    }

    public static void GetRawPciture(RawPicture p, CmdParameters param, boolean isSmalPict){
        if(_IGetRawPciture != null)
            _IGetRawPciture.GetRawPicture(p, param,  isSmalPict);
    }

    public static void onDestroy(){
        _Transport = null;

    }
    //endregion IGetRawPciture

    //region ICommandExcecuted
    private static List<ICommandExcecuted> _CommandExcecuted = new ArrayList<>();

    public static void registerCommandExcecuted(ICommandExcecuted t){
        _CommandExcecuted.add(t);

    }
    public static void unregisterCommandExcecuted(ICommandExcecuted t){
        _CommandExcecuted .remove(t);
    }

    public static void OnCommandExcecuted( String cmd){
        if(_CommandExcecuted != null) {
            for (ICommandExcecuted c :  _CommandExcecuted)
                c.OnCommandExcecuted(cmd);
        }
    }

    //endregion ICommandExcecuted


}

