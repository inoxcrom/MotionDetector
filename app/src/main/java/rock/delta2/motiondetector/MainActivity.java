package rock.delta2.motiondetector;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

import java.util.Timer;
import java.util.TimerTask;

import rock.delta2.motiondetector.Commands.CmdAutostartGet;
import rock.delta2.motiondetector.Commands.CmdAutostartSet;
import rock.delta2.motiondetector.Commands.CmdCameraAngleSet;
import rock.delta2.motiondetector.Commands.CmdCameraGet;
import rock.delta2.motiondetector.Commands.CmdCameraSet;
import rock.delta2.motiondetector.Commands.CmdCameraSizeSet;
import rock.delta2.motiondetector.Commands.CmdStart;
import rock.delta2.motiondetector.Commands.CmdStop;
import rock.delta2.motiondetector.Commands.CmdTurn;
import rock.delta2.motiondetector.Commands.CmdVoiceCallGet;
import rock.delta2.motiondetector.Commands.CmdVoiceCallSet;
import rock.delta2.motiondetector.Common.CmdParameters;
import rock.delta2.motiondetector.Common.RawPicture;
import rock.delta2.motiondetector.Detector.CamearaProps;
import rock.delta2.motiondetector.Mediator.ICameraStarted;
import rock.delta2.motiondetector.Mediator.ICommandExcecuted;
import rock.delta2.motiondetector.Mediator.IGetRawPictureCallback;
import rock.delta2.motiondetector.Mediator.MediatorMD;
import rock.delta2.motiondetector.Preferences.PreferencesHelper;

public class MainActivity extends AppCompatActivity
        implements IGetRawPictureCallback, ICommandExcecuted , ICameraStarted, AdapterView.OnItemSelectedListener {

    private Timer mTimer;
    private TimerTaskPict mTimerTask;
    RawPicture _current;


    private SurfaceView sfPreviw;

    CheckBox cbAutoStart;
    CheckBox cbVoiceCall;
    CheckBox cbShowPreview;
    Spinner spCamera;
    Spinner spCameraSize;
    Spinner spCameraAngle;
    Button btStartStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MediatorMD.registerCommandExcecuted(this);
        MediatorMD.setOnCameraStarted(this);

        sfPreviw = findViewById(R.id.sfPrev);
        cbVoiceCall = findViewById(R.id.cbVoiceCall);
        cbAutoStart = findViewById(R.id.cbAutoStart);

        spCamera = findViewById(R.id.spCamera);
        spCamera.setOnItemSelectedListener(this);

        spCameraSize = findViewById(R.id.spCameraSize);
        spCameraSize.setOnItemSelectedListener(this);

        spCameraAngle = findViewById(R.id.spCameraAngle);
        spCameraAngle.setOnItemSelectedListener(this);

        btStartStop = findViewById(R.id.btStartStop);

        cbShowPreview = findViewById(R.id.cbShowPreview);

        refresh("");
    }


    @Override
    public void onStart(){
        super.onStart();

        _current = new RawPicture(this);
        mTimer = new Timer();
        mTimerTask = new TimerTaskPict(_current);

    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        MediatorMD.unregisterCommandExcecuted(this);

        mTimer.cancel();
        mTimerTask = null;
        mTimer = null;

        _current.OnDestroy();
        _current = null;

    }

    @Override
    public void OnGetRawPicture(CmdParameters parms) {
        runOnUiThread( new DrawPicture(this));

        if(sfPreviw != null && _current != null){
            Canvas canvas = null;
            SurfaceHolder surfaceHolder =  sfPreviw.getHolder();
            try {
                // получаем объект Canvas и выполняем отрисовку
                canvas = surfaceHolder.lockCanvas(null);
                if (canvas!= null) {
                    synchronized (surfaceHolder) {
                        canvas.drawColor(Color.BLACK);
                        canvas.drawBitmap(
                                _current.getBitmapPreview(sfPreviw.getWidth()
                                        , sfPreviw.getHeight()), new Matrix(), null);
                    }
                }
            }
            catch (Exception ex){
                Helper.Ex2Log(ex);
            }
            finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    @Override
    public void OnCommandExcecuted(String comand) {
        refresh(comand);
    }

    private void refresh(String prop){
        boolean isAll = prop.equals("");


        if (isAll || prop.equals(CmdStart._COMMAND) || prop.equals(CmdStop._COMMAND) || prop.equals(CmdTurn._COMMAND) )
            setStartStop();

        if (isAll || prop.equals(CmdAutostartSet._COMMAND))
            cbAutoStart.setChecked(PreferencesHelper.getAutoStart());

        if (isAll || prop.equals(CmdVoiceCallSet._COMMAND))
            cbVoiceCall.setChecked(PreferencesHelper.getIsVoiceCall());

        if (isAll || prop.equals(CmdCameraSet._COMMAND))
            refreshSpinner(spCamera, CmdCameraGet._COMMAND);

        if (isAll || prop.equals(CmdCameraSizeSet._COMMAND))
            refreshSpinner(spCameraSize, CmdCameraSizeSet._COMMAND);

        if (isAll || prop.equals(CmdCameraSizeSet._COMMAND))
            refreshSpinner(spCameraAngle, CmdCameraAngleSet._COMMAND);


    }

    private void setStartStop(){
        if(PreferencesHelper.GetIsActive())
            btStartStop.setText(R.string.stop);
        else
            btStartStop.setText(R.string.start);
    }

    private void refreshSpinner(Spinner s, String p){

        try {
            CamearaProps prop = MediatorMD.GetCameraProps(p);
            if (prop == null)
                return;

            ArrayAdapter<String> a = new ArrayAdapter(this, android.R.layout.simple_spinner_item, prop.values);

            s.setAdapter(a);

            s.setSelection(prop.current);
        }catch (Exception e){
            Helper.Ex2Log(e);
        }

    }

    public void onClick(View view) {
        if (view.equals(cbAutoStart))
            PreferencesHelper.setAutoStart(cbAutoStart.isChecked());
        else if (view.equals(cbVoiceCall))
            PreferencesHelper.setAutoStart(cbVoiceCall.isChecked());
        else if(view.equals(cbShowPreview))
            startStopPreviev(cbShowPreview.isChecked());

    }

    private void startStopPreviev(boolean isShow){
        if(isShow){
            mTimer.schedule(mTimerTask, 200, 400);
        }
        else {
            mTimer.cancel();
        }
    }

    public void onCloseClick(View view) {
        stopService(new Intent(this, MainService.class));
        finish();
    }

    public void onMinimizeClick(View view) {
        finish();
    }

    public void onStartStopClick(View view) {
        MediatorMD.CheckMessage(PreferencesHelper.GetIsActive()? CmdStop._COMMAND : CmdStart._COMMAND, "0");

    }

    @Override
    public void OnCameraStartted(boolean isStarted) {
        if(isStarted)
            refresh("");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.equals(spCamera) && PreferencesHelper.getCameraIdx() != position)
            MediatorMD.CheckMessage(String.format("set %s %s", CmdCameraSet._COMMAND, position), "0");
        else if(parent.equals(spCameraSize) && PreferencesHelper.getCameraSizeIdx() != position)
            MediatorMD.CheckMessage(String.format("set %s %s", CmdCameraSizeSet._COMMAND, position), "0");
        else if(parent.equals(spCameraAngle) && PreferencesHelper.getCameraAngleIdx() != position)
            MediatorMD.CheckMessage(String.format("set %s %s", CmdCameraAngleSet._COMMAND, position), "0");

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //region camera preview
    static class DrawPicture implements  Runnable{

        MainActivity activity;

        public DrawPicture(MainActivity a){
            activity = a;
        }

        @Override
        public void run() {
            if(activity.sfPreviw != null && activity._current != null){
                Canvas canvas = null;
                SurfaceHolder surfaceHolder =  activity.sfPreviw.getHolder();
                try {
                    // получаем объект Canvas и выполняем отрисовку
                    canvas = surfaceHolder.lockCanvas(null);
                    if (canvas!= null) {
                        synchronized (surfaceHolder) {
                            canvas.drawColor(Color.BLACK);
                            canvas.drawBitmap(
                                    activity._current.getBitmapPreview(activity.sfPreviw.getWidth()
                                            , activity.sfPreviw.getHeight()), new Matrix(), null);
                        }
                    }
                }
                catch (Exception ex){
                    Helper.Ex2Log(ex);
                }
                finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }

    class TimerTaskPict extends TimerTask {

        RawPicture _pic;

        public TimerTaskPict(RawPicture pic){
            _pic = pic;
        }

        @Override
        public void run() {
            MediatorMD.GetRawPciture(_pic, null, true);
        }
    }
//endregion camera preview

}
