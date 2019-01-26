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
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import java.util.Timer;
import java.util.TimerTask;

import rock.delta2.motiondetector.Commands.CmdAutostartGet;
import rock.delta2.motiondetector.Commands.CmdAutostartSet;
import rock.delta2.motiondetector.Commands.CmdCameraGet;
import rock.delta2.motiondetector.Commands.CmdCameraSet;
import rock.delta2.motiondetector.Commands.CmdStart;
import rock.delta2.motiondetector.Commands.CmdStop;
import rock.delta2.motiondetector.Commands.CmdTurn;
import rock.delta2.motiondetector.Commands.CmdVoiceCallGet;
import rock.delta2.motiondetector.Commands.CmdVoiceCallSet;
import rock.delta2.motiondetector.Common.CmdParameters;
import rock.delta2.motiondetector.Common.RawPicture;
import rock.delta2.motiondetector.Detector.CamearaProps;
import rock.delta2.motiondetector.Mediator.ICommandExcecuted;
import rock.delta2.motiondetector.Mediator.IGetRawPictureCallback;
import rock.delta2.motiondetector.Mediator.MediatorMD;
import rock.delta2.motiondetector.Preferences.PreferencesHelper;

public class MainActivity extends AppCompatActivity
        implements IGetRawPictureCallback, ICommandExcecuted {

    private Timer mTimer;
    private TimerTaskPict mTimerTask;
    RawPicture _current;


    private SurfaceView sfPreviw;

    CheckBox cbAutoStart;
    CheckBox cbVoiceCall;
    Spinner spCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MediatorMD.registerCommandExcecuted(this);

        sfPreviw = findViewById(R.id.sfPrev);
        cbVoiceCall = findViewById(R.id.cbVoiceCall);
        cbAutoStart = findViewById(R.id.cbAutoStart);
        spCamera = findViewById(R.id.spCamera);

         refresh("");
    }

    @Override
    public void onStart(){
        super.onStart();

        _current = new RawPicture(this);
        mTimer = new Timer();
        mTimerTask = new TimerTaskPict(_current);

     //   mTimer.schedule(mTimerTask, 200, 400);
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

        if (isAll || prop.equals(CmdAutostartGet._COMMAND) || prop.equals(CmdAutostartSet._COMMAND))
            cbAutoStart.setChecked(PreferencesHelper.getAutoStart());

        if (isAll || prop.equals(CmdVoiceCallGet._COMMAND) || prop.equals(CmdVoiceCallSet._COMMAND))
            cbVoiceCall.setChecked(PreferencesHelper.getIsVoiceCall());

        if (isAll || prop.equals(CmdCameraGet._COMMAND) || prop.equals(CmdCameraSet._COMMAND))
            refreshSpinner(spCamera, CmdCameraGet._COMMAND);

    }

    private void setStartStop(){

    }

    private void refreshSpinner(Spinner s, String p){

        try {
            Thread.sleep(5000);
        }catch (Exception e){}

        CamearaProps prop = MediatorMD.GetCameraProps(p);
        if(prop == null)
            return;

        ArrayAdapter<String> a = new ArrayAdapter(this, android.R.layout.simple_spinner_item, prop.values);

        s.setAdapter(a);

        s.setSelection(prop.current);

    }

    public void onClick(View view) {
        if (view.equals(cbAutoStart))
            PreferencesHelper.setAutoStart(cbAutoStart.isChecked());
        else if (view.equals(cbVoiceCall))
            PreferencesHelper.setAutoStart(cbVoiceCall.isChecked());

    }

    public void onCloseClick(View view) {
        stopService(new Intent(this, MainService.class));
        finish();
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
