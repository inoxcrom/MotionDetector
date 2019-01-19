package rock.delta2.motiondetector;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Timer;
import java.util.TimerTask;

import rock.delta2.motiondetector.Common.CmdParameters;
import rock.delta2.motiondetector.Common.RawPicture;
import rock.delta2.motiondetector.Mediator.IGetRawPictureCallback;
import rock.delta2.motiondetector.Mediator.MediatorMD;

public class MainActivity extends AppCompatActivity implements IGetRawPictureCallback {

    private Timer mTimer;
    private TimerTaskPict mTimerTask;
    RawPicture _current;


    private SurfaceView sfPreviw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sfPreviw = (SurfaceView)findViewById(R.id.sfPrev);
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
}
