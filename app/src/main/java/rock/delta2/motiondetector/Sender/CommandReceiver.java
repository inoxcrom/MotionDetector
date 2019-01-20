package rock.delta2.motiondetector.Sender;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import rock.delta2.motiondetector.Mediator.MediatorMD;


public class CommandReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("rock.delta2.cmd"))
        {
            String msgId = intent.getStringExtra("replMsgId");
            String cmd = intent.getStringExtra("cmd");
            MediatorMD.CheckMessage(cmd, msgId);
        }

    }
}
