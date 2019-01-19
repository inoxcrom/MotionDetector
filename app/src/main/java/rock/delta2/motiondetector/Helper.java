package rock.delta2.motiondetector;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Helper {

    //region workdir
    static File _WorkDir;
    static File nomediaDir;

    public static File getWorkDir(){
        return  _WorkDir;
    }
    public static void setWorkDir(File f){
        _WorkDir = f;

        if(nomediaDir == null) {
            nomediaDir = new File(String.format("%s/.nomedia", _WorkDir));
            if(!nomediaDir.exists())
                try {
                    nomediaDir.createNewFile();
                }catch (Exception e){
                    Helper.Ex2Log(e);
                }
        }
    }
    //endregion workdir

    //region dt
    final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static String getNowDT(){
        return sdf.format(Calendar.getInstance().getTime());
    }

    final static SimpleDateFormat sdfFile = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");
    public static String getNowDTFile(){
        return sdfFile.format(Calendar.getInstance().getTime());
    }

    //endregion dt

    //region log
    private static File _logFile = null;

    private static boolean _isNeedLog = false;
    private static boolean _isNeedLogInit = false;
    private static boolean getIsNeedLog(){
        if (!_isNeedLogInit)
            try
            {
                _isNeedLog = (new File( Environment.getExternalStorageDirectory () + "/" + "delta.need.log")).exists() ;
            }catch (Exception e)
            {

            }
        _isNeedLogInit = true;

        return _isNeedLog;
    }

    public static void Log( String tag, String msg){
        Log( tag, msg, false);
    }

    public static void Log( String tag, String msg, boolean isMandatory){

        if (isMandatory || getIsNeedLog())

            try{
                String timeLog = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss").format(Calendar.getInstance().getTime());

                if(_logFile == null){
                    String dir;
                    if (Environment.MEDIA_MOUNTED.equals( Environment.getExternalStorageState()))
                        dir =  String.format("%s/", Environment.getExternalStorageDirectory () );
                    else
                        dir =  String.format("%s/", getWorkDir() );

                    _logFile = new File(dir, "delta.log.txt");

                    if (_logFile.exists()) {
                        _logFile.renameTo(new File(dir, String.format("delta.log_%s.txt",
                                    new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss").format(Calendar.getInstance().getTime()) )));

                    }
                    _logFile.createNewFile();
                }

                BufferedWriter bw = new BufferedWriter(new FileWriter(_logFile, true));
                bw.append( timeLog +"\t" + tag + " : \t" + msg + "\n\r");
                bw.close();

            } catch (Exception e) {

            }

    }

    public static void Ex2Log(Throwable ex){
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);

            Log("Exception", sw.toString(), true);

        }
        catch (Exception e)
        {

        }
    }


    //endregion log
}
