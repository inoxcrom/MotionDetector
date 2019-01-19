package rock.delta2.motiondetector.Common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;

import java.io.ByteArrayOutputStream;

import rock.delta2.motiondetector.Helper;
import rock.delta2.motiondetector.Mediator.IGetRawPictureCallback;


public class RawPicture {

    public byte data[];

    public byte[] getData() {return data;}


    public int w;


    public int h;


    public int angle;

    public int delta;

    public CmdParameters cmdParam;

    IGetRawPictureCallback callback;



    public String addInfo;

    public RawPicture(IGetRawPictureCallback c){
        callback = c;
    }

    public void NotifyNewData(){
        callback.OnGetRawPicture(cmdParam);
    }

    public void OnDestroy(){

    }

    public Bitmap getBitmap() {
        try {
            Helper.Log("RawPicture", "getBitmap" );

            byte[]  copyData = data.clone();

            YuvImage yuv = new YuvImage(copyData, ImageFormat.NV21, w, h, null);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            yuv.compressToJpeg(new Rect(0, 0, w, h), 100, out);

            byte[] bytes = out.toByteArray();
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            Bitmap rotatedBitmap;
            if (angle != 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(angle);
                rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            } else
                rotatedBitmap = bitmap;

            return rotatedBitmap;
        }catch (Exception e)
        {
            Helper.Ex2Log(e);
            return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        }
    }


    public Bitmap getBitmapPreview(int newW, int newH){
        return Bitmap.createScaledBitmap(getBitmapPreview100(), newW, newH, false);
    }

    public byte[] getJpg() {
        try {
            Helper.Log("RawPicture", "getJpg");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            getBitmap().compress(Bitmap.CompressFormat.JPEG, 90, stream);
            return stream.toByteArray();
        }catch (Exception e)
        {
            Helper.Ex2Log(e);
            return null;
        }

    }

    //region to bmp small
    public class point{
        public int y;
        public int u;
        public int v;
    }

    private final static int previewHeight = 120;
    private final static int previewWidth = 160;
    private final static int previewSize = previewWidth * previewHeight;

    private static int prevHeight = -1;
    private static int prevWidth = -1;
    public static point[] previewPoints = new point[previewSize];

    private void initPoints(int width, int height){

        int frameSize = width * height;

        int scaleH[] = new int[previewHeight];
        int scaleW[] = new int[previewWidth];

        for(int i = 0; i < previewHeight; i++)
            scaleH[i] = (int) Math.floor(height * ((float)i / previewHeight));

        for(int i = 0; i < previewWidth; i++)
            scaleW[i] = (int) Math.floor(width * ((float)i / previewWidth ));

        int pointCrr = 0;
        for (int h = 0; h < previewHeight; h++)
            for (int w = 0; w < previewWidth; w++){

                previewPoints[pointCrr] = new point();
                previewPoints[pointCrr].y = (scaleH[h] * width) + scaleW[w];

                double k = previewPoints[pointCrr].y / (width * 2.0);
                int r = (int) Math.floor(k);
                double ost = k - r;
                if (ost > 0.5)
                    ost -= 0.5;
                int c = (int) Math.floor(ost * width) * 2;
                previewPoints[pointCrr].u = frameSize + (r * width) + c;
                previewPoints[pointCrr].v = previewPoints[pointCrr].u + 1;

                pointCrr++;
            }
    }

    private static int convertYUVtoRGB(int y, int u, int v) {
        int r,g,b;

        r = y + (int)(1.402f*v);
        g = y - (int)(0.344f*u +0.714f*v);
        b = y + (int)(1.772f*u);
        r = r>255? 255 : r<0 ? 0 : r;
        g = g>255? 255 : g<0 ? 0 : g;
        b = b>255? 255 : b<0 ? 0 : b;
        return 0xff000000 | (b<<16) | (g<<8) | r;
    }

    public Bitmap getBitmapPreview100() {

        if(prevHeight !=h || prevWidth != w ){
            prevWidth = w;
            prevHeight = h;
            initPoints(w, h);
        }

        int[] argb = new int[previewSize];

        for(int i =0; i< previewSize; i++){

            int y = data[previewPoints[i].y]&0xff;
            int u = data[previewPoints[i].u]&0xff;
            int v = data[previewPoints[i].v]&0xff;
            u = u-128;
            v = v-128;
            argb[i] = convertYUVtoRGB(y, u, v);

        }

        Bitmap bitmap = Bitmap.createBitmap(argb, previewWidth, previewHeight, Bitmap.Config.ARGB_8888);

        Bitmap rotatedBitmap;
        if(angle !=0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        else
            rotatedBitmap = bitmap;

        return rotatedBitmap;
    }

    //endregion to bmp small
}
