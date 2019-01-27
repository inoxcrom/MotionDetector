package rock.delta2.motiondetector;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Calendar;

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
    static long _startTimeMls = 0;

    @Override
    public void onCreate() {
        _startTimeMls = Calendar.getInstance().getTimeInMillis();

        PreferencesHelper.init(this);

        startForeground(R.drawable.ic_notify_proc, "motion detector", 4524);

        Manager = new MDManager(this);

        MediatorMD.setTransport(new Sender(this));
        MediatorMD.setCommandCheckMessage(new CommandManager(this));

        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);


    }


    @Override
    public void onDestroy() {
        MediatorMD.onDestroy();
        Manager.onDestroy();
    }

    protected  void startForeground(int ico, String title, int notifyId) {

            Notification.Builder builder = new Notification.Builder(this)
                //    .setSmallIcon(ico)
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

    public static String getWorkingTime(Context c) {
        long ssInDay = 86400;
        long hhInDay = 3600;
        long mmInDay = 60;

        try {

            long wts = (Calendar.getInstance().getTimeInMillis() - _startTimeMls) / 1000;

            long dd = wts / ssInDay;
            wts -= (dd * ssInDay);

            long hh = wts / hhInDay;
            wts -= (hh * hhInDay);

            long mm = wts / mmInDay;
            wts -= (mm * mmInDay);

            long ss = wts;

            StringBuilder res = new StringBuilder();
            if (dd > 0)
                res.append(String.format("%s%s ", dd, c.getString(R.string.working_time_d)));
            if (hh > 0 || dd > 0)
                res.append(String.format("%s%s ", (hh < 10 ? "0" : "") + String.valueOf(hh), c.getString(R.string.working_time_h)));
            if (mm > 0 || hh > 0 || dd > 0)
                res.append(String.format("%s%s ", (mm < 10 ? "0" : "") + String.valueOf(mm), c.getString(R.string.working_time_m)));

            res.append(String.format("%s%s ", (ss < 10 ? "0" : "") + String.valueOf(ss), c.getString(R.string.working_time_s)));

            return res.toString();
        }catch (Exception ex){
            return "";
        }
    }

}
