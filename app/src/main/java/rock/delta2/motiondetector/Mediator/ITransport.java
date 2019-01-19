package rock.delta2.motiondetector.Mediator;


public interface ITransport {


    // rock.delta2.send.txt
    void sendTxt(String replMsgId, String msg);

    // rock.delta2.send.photo
    void sendPhoto(String replMsgId, String file, String caption);

    // rock.delta2.send.file
    void sendFile(String replMsgId, String file);

    // rock.delta2.send.location
    //void sendLocation(String replMsgId, String lat, String lon);

    // rock.delta2.call.voice
    void callVoice();

}
