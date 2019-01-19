package rock.delta2.motiondetector.Mediator;


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

}

