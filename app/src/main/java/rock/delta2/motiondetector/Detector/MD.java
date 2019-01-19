package rock.delta2.motiondetector.Detector;

import android.graphics.Color;

import rock.delta2.motiondetector.Helper;

public class MD {


    private static final int _NO_USE = -1;
    private static final int _OUT_W = 40;
    private static final int _OUT_H = 40;
    private static final int _OUT_CNT= _OUT_H * _OUT_W;

    private static final int _AVG_WHITE = 0;
    private static final int _AVG_BLACK = 1;

    private static final int _COLOR_WHITE = Color.WHITE;
    private static final int _COLOR_BLACK = Color.DKGRAY;
    private static final int _COLOR_NO_USE = Color.YELLOW;
    private static final int _COLOR_DETECT = Color.RED;


    private long [] mNewHash;

    private long[] mPrevHash;

    int[] m_argb;

    private int mDelta;
    private boolean mIsNoFirst;

    MD(){
        mDelta = 0;
        mIsNoFirst = false;

        mNewHash = new long[_OUT_CNT];
        mPrevHash = new long[_OUT_CNT];
        mCounts = new long[_OUT_CNT];
        m_argb  = new int[_OUT_CNT];
    }

    public void onDestroy(){
        mNewHash = null;
        mPrevHash = null;
        mCounts = null;
        m_argb = null;
    }

    int GetDelta(){
        return mDelta;
    }

    //region hash
    private static int prevH = -1;
    private static int prevW = -1;

    int [] mapData2Hash;

    private void initHashMeas(int w, int h){
        PointConverter wConv = new PointConverter(_OUT_W, w);
        PointConverter hConv = new PointConverter(_OUT_H, h);

        int hw = h * w;
        mapData2Hash = new int[hw];

        for (int i = 0; i < hw; i++ ){

            int hh =  (int) Math.floor(i / w);
            int ww = i - (hh * w);

            mapData2Hash[i] =  (hConv.get(hh) * _OUT_H) + wConv.get(ww)  ;
        }
    }

    public long[] getHash(byte[] yuv,  int width, int height){
        try {
            if(prevH != height || prevW != width)
                initHashMeas(width, height);

            prevH = height;
            prevW = width;

            for (int p = 0; p < _OUT_CNT; p++)
                mNewHash[p] = mCounts[p] = 0;

            int qnt = width *height;
            for (int i = 0; i<qnt; i++){
                int pos = mapData2Hash[i];
                mNewHash[pos] += (0xff & yuv[i]);
                mCounts[pos] ++;
            }

            long sum = 0;
            for (int p = 0; p < _OUT_CNT; p++) {
                int y = 0;
                if (mCounts[p] > 0) {
                    y = (int) (mNewHash[p] / mCounts[p]);
                }

                sum += y;
                mNewHash[p] = y;
            }

            float avg = ((float)sum / (float) _OUT_CNT);

    //        MediatorMD.OnSendDevMsg("decodeOld . avg = " + String.valueOf(avg))  ;

            for (int p = 0; p < _OUT_CNT; p++)
                if (mNewHash[p] > avg - 3 && mNewHash[p] < avg + 3)
                    mNewHash[p] = _NO_USE;
                else if (mNewHash[p] > avg)
                    mNewHash[p] = _AVG_WHITE;
                else
                    mNewHash[p] = _AVG_BLACK;


        }catch (Exception ex)
        {
            Helper.Ex2Log(ex);
        }
        return mNewHash;
    }

    //endregion hash

    int chkImg(byte[] data, int w, int h) {

        long[] ch;

        ch = getHash(data, w, h);

        int delta = 0;
        for (int i = 0; i < _OUT_CNT; i++){
            boolean isOk = (mIsNoFirst && mPrevHash[i] != ch[i]  &&  ch[i] != _NO_USE && mPrevHash[i] != _NO_USE);

            mPrevHash[i] = ch[i];

            if (isOk)
                delta++;
        }

        mIsNoFirst = true;
        mDelta = delta;

        return delta;
    }

     private long[] mCounts;

     private class PointConverter{
        int mMap[];
        int mQnt;

        public PointConverter(int mapQnt, int qntPoints){
            mQnt = mapQnt;

            float step = (float)qntPoints / mapQnt;

            mMap = new int[mapQnt];
            for(int i=0; i<mapQnt; i++)
                mMap[i] = (int) Math.ceil(i*step);
        }

        public int get(int val){
            int result = 0;
            for(int i=0; i<mQnt; i++) {
                if (val < mMap[i]) {
                    result = i;
                    break;
                }
            }
            return  result;
        }
     }
}
