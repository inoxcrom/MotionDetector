package rock.delta2.motiondetector.Sender;

import android.content.Context;
import android.content.Intent;

import rock.delta2.motiondetector.Mediator.ITransport;

public class Sender implements ITransport {

    Context context;

    public Sender(Context c){
        context = c;
    }

    @Override
    public void sendTxt(String replMsgId, String msg) {
        final Intent intent=new Intent();
        intent.setAction("rock.delta2.send.txt");
        intent.putExtra("replMsgId",replMsgId);
        intent.putExtra("msg",msg);
        context.sendBroadcast(intent);
    }

    @Override
    public void sendPhoto(String replMsgId, String file, String caption) {
        final Intent intent=new Intent();
        intent.setAction("rock.delta2.send.photo");
        intent.putExtra("replMsgId",replMsgId);
        intent.putExtra("file",file);
        intent.putExtra("caption",caption);
        context.sendBroadcast(intent);
    }

    @Override
    public void sendFile(String replMsgId, String file) {
        final Intent intent=new Intent();
        intent.setAction("rock.delta2.send.file");
        intent.putExtra("replMsgId",replMsgId);
        intent.putExtra("file",file);
        context.sendBroadcast(intent);
    }

    @Override
    public void callVoice() {
        final Intent intent=new Intent();
        intent.setAction("rock.delta2.call.voice");
        context.sendBroadcast(intent);
    }
}
