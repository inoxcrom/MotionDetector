package rock.delta2.motiondetector;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import rock.delta2.motiondetector.Detector.MDManager;
import rock.delta2.motiondetector.Mediator.MediatorMD;
import rock.delta2.motiondetector.Preferences.PreferencesHelper;
import rock.delta2.motiondetector.Sender.Sender;

public class MainService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    MDManager Manager;

    @Override
    public void onCreate() {

        PreferencesHelper.init(this);

        startForeground(R.drawable.ic_notify_proc, "motion detector", 4524);

        Manager = new MDManager(this);

        MediatorMD.setTransport(new Sender(this));
        MediatorMD.setCommandCheckMessage(new CommandManager(this));


        startActivity(new Intent(this, MainActivity.class));


    }


    @Override
    public void onDestroy() {
        MediatorMD.onDestroy();
        Manager.onDestroy();
    }

    protected  void startForeground(int ico, String title, int notifyId) {

            Notification.Builder builder = new Notification.Builder(this)
                    .setSmallIcon(ico)
                    .setContentTitle(title)
                    .setContentText("")
                    .setOnlyAlertOnce(true)
                    .setOngoing(true);
            Notification notification;

            notification = builder.build();

            notification.contentIntent = PendingIntent.getActivity(this,
                    0, new Intent(getApplicationContext(), MainActivity.class)
                    , PendingIntent.FLAG_UPDATE_CURRENT);


            startForeground(notifyId, notification);
    }
}
