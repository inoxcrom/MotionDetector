package rock.delta2.motiondetector.Detector;

import android.content.Context;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import rock.delta2.motiondetector.Commands.CmdStart;
import rock.delta2.motiondetector.Commands.CmdStop;
import rock.delta2.motiondetector.Common.CmdParameters;
import rock.delta2.motiondetector.Common.RawPicture;
import rock.delta2.motiondetector.Helper;
import rock.delta2.motiondetector.Mediator.ICommandExcecuted;
import rock.delta2.motiondetector.Mediator.IGetRawPictureCallback;
import rock.delta2.motiondetector.Mediator.MediatorMD;
import rock.delta2.motiondetector.Preferences.PreferencesHelper;


public class MDManager implements IGetRawPictureCallback, ICommandExcecuted {
    private Context mContext;
    private SurfaceViewExt mMDCamera;
    private MD mMD;
    RawPicture _current;

    private Timer mTimer;
    private MDManagerTimerTask  mTimerTask;

    private int moveCnt = 0;
    private long lastSend = 0;

    @Override
    public void OnGetRawPicture(CmdParameters parms) {
        try {

            if (mMD.chkImg(_current.data, _current.w, _current.h) >= PreferencesHelper.getDelta()) {
                if (mDetectNum == en_detect_mun.none)
                    mDetectNum = en_detect_mun.first;
                else if(mDetectNum == en_detect_mun.first) {
                    mDetectNum = en_detect_mun.detect;
                }

                if (mDetectNum == en_detect_mun.detect) {

                    long interval = 0;//PreferencesHelper.GetNotifyMinInterval();

                    if (interval== 0 ||
                          (Calendar.getInstance().getTimeInMillis() - lastSend) / 1000 > interval){

                        lastSend = Calendar.getInstance().getTimeInMillis();

                        String addInfo = String.format("∆ = %s", mMD.GetDelta());
                        Helper.Log("delta", addInfo);

                        //if (PreferencesHelper.GetInTwo())
                         //   MediatorMD.TexSendText(mContext.getString(R.string.move_description) + " " + addInfo, null);

                        SendPhoto.Send(mContext, _current.getJpg(), addInfo, parms);

                    }

                    if(++moveCnt > 2)
                        MediatorMD.callVoice();
                }
            }
            else if (mDetectNum != en_detect_mun.none)
            {
                mDetectNum = en_detect_mun.none;
                moveCnt = 0;
            }
        } catch (Exception e) {
            Helper.Ex2Log(e);
        }

    }

    @Override
    public void OnCommandExcecuted(String comand) {
        if(comand.equals(CmdStart._COMMAND)){
            timerStop();
            stopCamera();
            openCamera();
            timerStart();
        }
        else if (comand.equals(CmdStop._COMMAND)){
            timerStop();
            stopCamera();
        }
        //else if (comand.equals(CmdS))



    }

    enum en_detect_mun
    {
        none,
        first,
        detect
    }

    private en_detect_mun mDetectNum = en_detect_mun.none;

    public MDManager(Context context){
        mContext = context;
        _current = new RawPicture(this);

        mMD = new MD();

        MediatorMD.registerCommandExcecuted(this);

        if (PreferencesHelper.GetIsActive()) {
            openCamera();
            timerStart();
        }


    }

    public void onDestroy(){
        timerStop();

        MediatorMD.unregisterCommandExcecuted(this);

        stopCamera();

        if (mMD != null)
            mMD.onDestroy();
        mMD = null;



        mContext = null;
    }

    private void stopCamera(){
        if (mMDCamera != null)
            mMDCamera.onDestroy();
        mMDCamera = null;
    }

    private void openCamera(){

        CameraParameters p = new CameraParameters();
        p.camIdx = PreferencesHelper.getCameraIdx();
        p.sizeIdx = PreferencesHelper.getCameraSizeIdx();

        mMDCamera = new SurfaceViewExt(mContext, p);
    }

    private void timerStart(){
        mTimer = new Timer();
        mTimerTask = new MDManagerTimerTask(this);
        mTimer.schedule(mTimerTask, 1000, 400);
    }

    private void timerStop(){
        if (mTimer != null)
            mTimer.cancel();

        mTimer = null;
        mTimerTask = null;
    }

    class MDManagerTimerTask extends TimerTask {

        MDManager mMDManager;

        public MDManagerTimerTask(MDManager m){
            mMDManager = m;
        }

        @Override
        public void run() {
            if(PreferencesHelper.GetIsActive()) {
                MediatorMD.GetRawPciture(mMDManager._current, null, true);
            }
        }
    }
}
