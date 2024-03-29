package rock.delta2.motiondetector.Detector;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import rock.delta2.motiondetector.Common.CmdParameters;
import rock.delta2.motiondetector.Common.RawPicture;
import rock.delta2.motiondetector.Helper;
import rock.delta2.motiondetector.Mediator.IGetCameraProp;
import rock.delta2.motiondetector.Mediator.IGetRawPciture;
import rock.delta2.motiondetector.Mediator.MediatorMD;
import rock.delta2.motiondetector.Preferences.PreferencesHelper;

public class SurfaceViewExt extends SurfaceView implements SurfaceHolder.Callback
        , Camera.PreviewCallback
        , IGetRawPciture
        , IGetCameraProp

{
    private Camera _camera;
    private Context _context;

    HandlerExt _handler;

    CameraParameters _params;

   // private Timer mTimer;
  //  private CameraStopTask  mTimerTask;

    private long lastCameraTime = 0;

    public SurfaceViewExt(Context context, CameraParameters param) {
        super(context);

        _context  = context;

        MediatorMD.registerGetRawPciture(this);
        MediatorMD.setCameraProp(this);

        _handler = new HandlerExt(Looper.getMainLooper());

        _rawPictures = Collections.synchronizedList(new ArrayList<RawPicture>());

        cameraOpen(param);

    }

    boolean isOpened = false;
    int _camIdx;
    private void cameraOpen(CameraParameters param){
        if(/*_camIdx != camIdx && */isOpened) {
            CameraRelase();
        }

        if(!isOpened){
            lastCameraTime = Calendar.getInstance().getTimeInMillis();
            openCamera(param);

        //    mTimer = new Timer();
        //    mTimerTask = new CameraStopTask();
        //    mTimer.schedule(mTimerTask, 1000, 20000);
        }
        isOpened = true;

    }

    private void cameraRelase(){
        if(isOpened){
            CameraRelase();
        //    mTimer.cancel();
        //    mTimer = null;
        //    mTimerTask = null;
        }
        isOpened = false;
    }

    boolean isStarted = false;
    private void cameraStart(){
        if(!isStarted) {

            _camera.setPreviewCallbackWithBuffer(this);
            _camera.startPreview();
            MediatorMD.OnCameraStartted(true);

        }
        isStarted = true;
    }
    private void cameraStop(){
        if(isStarted) {
            _camera.setPreviewCallbackWithBuffer(null);
            _camera.stopPreview();
            MediatorMD.OnCameraStartted(false);

        }
        isStarted = false;
    }


    private List<RawPicture> _rawPictures;

    static int sizeH = 0;
    static int sizeW = 0;

    @Override
    public void GetRawPicture(RawPicture p, CmdParameters param, boolean isSmalPict) {

        if (sizeH == 0  || sizeW == 0)
            return;

        p.h = sizeH;
        p.w = sizeW;

        p.angle = CameraPropHelper.getAngle(PreferencesHelper.getCameraAngleIdx());

        p.cmdParam = param;

        int s = getNV21size(sizeW, sizeH);

        if (p.data == null || p.data.length != s)
            p.data = new byte[s];

        synchronized (_rawPictures) {

            _rawPictures.add(p);
        }

         if(_camera != null)
            _camera.addCallbackBuffer(p.data);
         else
             cameraOpen(_params);

    }

    @Override
    public void SendCameraProp(String msgId,String prop) {

        StringBuilder sb = new StringBuilder();

        CamearaProps p = CameraPropHelper.getProps(prop, _camera);

        for(int i =0; i < p.values.size(); i++){
            sb.append(String.format("%s  %s  %s \r\n", i, i == p.current ? "[x]" : "[  ]", p.values.get(i)));
        }

        MediatorMD.sendText(msgId, sb.toString());

    }

    @Override
    public CamearaProps GetCameraProps(String prop) {
        return CameraPropHelper.getProps(prop, _camera);
    }


    class HandlerExt extends Handler {

        public HandlerExt(Looper l){
            super(l);
        }

        @Override
        public void handleMessage(Message message) {
            openCameraWork( (CameraParameters) message.obj);
        }
    }


    protected void onDestroy(){
        MediatorMD.setCameraProp(null);
        MediatorMD.registerGetRawPciture(null);

        cameraRelase();
        destroySurfaceHolder();
    }

    //region surfaceholder
    SurfaceHolder _surfaceHolder;
    private void createSurfaceHolder(Context context){
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                1,
                1,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSPARENT

        );
        params.gravity = Gravity.RIGHT | Gravity.TOP;
        WindowManager wm = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
        wm.addView(this, params);

        _surfaceHolder = this.getHolder();
        _surfaceHolder.addCallback(this);
        _surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    private void destroySurfaceHolder(){
        if(_surfaceHolder != null) {
            _surfaceHolder.removeCallback(this);
            ((WindowManager) _context.getSystemService(_context.WINDOW_SERVICE)).removeView(this);
        }
    }
    //endregion surfaceholder

    public void openCamera(CameraParameters param){
        Message message = _handler.obtainMessage(0, param);
        message.sendToTarget();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void openCameraWork(CameraParameters parm){

        destroySurfaceHolder();

        _camera = parm.camIdx == 0 ? openBackCamera(parm) : openFrontamera(parm);

        createSurfaceHolder(_context);

        cameraStart();

        if(_camera != null) {
            for (RawPicture p: _rawPictures)
            _camera.addCallbackBuffer(p.data);
        }

    }

    public void CameraRelase(){
        if (_camera != null)
        {

            cameraStop();
            //_camera.setPreviewCallback(null);
            _camera.setPreviewCallbackWithBuffer(null);
            _camera.release();
            _camera = null;
        }
    }

    private static Camera openBackCamera(CameraParameters parm) {
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                try {
                    cam = Camera.open(camIdx);


                    setPreviewSize(cam, parm.sizeIdx);


                } catch (RuntimeException e) {
                    Helper.Ex2Log(e);
                }
            }
        }

        return cam;
    }

    private static Camera openFrontamera(CameraParameters parm) {
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cam = Camera.open(camIdx);

                    setPreviewSize(cam, parm.sizeIdx);

                } catch (RuntimeException e) {
                    Helper.Ex2Log(e);
                }
            }
        }

        return cam;
    }


    private static void setPreviewSize(Camera cam, int idx){
        Camera.Parameters p = cam.getParameters();


        long hw = Long.MAX_VALUE;

        Camera.Size  pos = p.getSupportedPreviewSizes().get(idx);
        if (pos.width > 1024) {
            pos = p.getSupportedPreviewSizes().get(0);

            for (Camera.Size c : p.getSupportedPreviewSizes()) {
                long m = c.height * c.width;

                if (m < hw && c.width >= 320) {
                    hw = m;
                    pos = c;
                }
            }
        }

        p.setPreviewSize(pos.width, pos.height);
        cam.setParameters(p);

        Camera.Size s = p.getPreviewSize();

        sizeH = s.height;
        sizeW = s.width;
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        try
        {
            if (_camera!= null) {
                _camera.setPreviewDisplay(holder);
                //_camera.setPreviewCallback(this);
                _camera.setPreviewCallbackWithBuffer(this);
            }
        }
        catch (Exception e)
        {
            Helper.Ex2Log(e);
        }
        cameraStart();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }


    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if(data == null)
            return;

        RawPicture c = null;
        lastCameraTime = Calendar.getInstance().getTimeInMillis();


        synchronized (_rawPictures) {
            if (_rawPictures.size() > 0) {
                for(RawPicture p : _rawPictures) {
                    if(p.data == data) {
                        c = p;
                        _rawPictures.remove(c);
                        break;
                    }
                }
            }
         }

        if (c != null) {

            Camera.Parameters p = camera.getParameters();
            Camera.Size s = p.getPreviewSize();

            sizeH = s.height;
            sizeW = s.width;

            c.h =  s.height;
            c.w = s.width;
            c.NotifyNewData();
        }
    }


    public int getNV21size(int W, int H){
        int yStride   = (int) Math.ceil(W / 16.0) * 16;
        int uvStride  = (int) Math.ceil( (yStride / 2) / 16.0) * 16;
        int ySize     = yStride * H;
        int uvSize    = uvStride * H / 2;
        int size      = ySize + uvSize * 2;

        return size;
    }

/*
    class CameraStopTask extends TimerTask {
        @Override
        public void run() {
            if(!PreferencesHelper.GetPwrSaveMode())
                return;

            if(lastCameraTime > 0 && Calendar.getInstance().getTimeInMillis() -  lastCameraTime > 30000)
                cameraRelase();
        }
    }
    */

}