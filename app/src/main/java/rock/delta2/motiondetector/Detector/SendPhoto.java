package rock.delta2.motiondetector.Detector;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Comparator;

import rock.delta2.motiondetector.Common.CmdParameters;
import rock.delta2.motiondetector.Helper;
import rock.delta2.motiondetector.Mediator.MediatorMD;

public class SendPhoto {



    //region savedir
    static final String _DIR_TMP = "/MotionDetector/";
    static File saveDir = null;
    static public File getDir(Context c){
        if(saveDir == null) {
            //saveDir = new File(String.format("%s%s", Helper.getWorkDir(), _DIR_TMP));
            saveDir = new File(String.format("%s%s", Environment.getExternalStorageDirectory(), _DIR_TMP));

            if (!saveDir.exists()) {
                saveDir.mkdirs();
                saveDir.setReadable(true, false);
                saveDir.setWritable(true, false);

            }

            for (File child : saveDir.listFiles())
                child.delete();

        }

        if(saveDir.listFiles().length > 120){
            final File[] sortedByDate = saveDir.listFiles();

            if (sortedByDate != null && sortedByDate.length > 1) {
                Arrays.sort(sortedByDate, new Comparator<File>() {
                    @Override
                    public int compare(File object1, File object2) {
                        return (int) ((object1.lastModified() < object2.lastModified()) ? object1.lastModified(): object2.lastModified());
                    }
                });
            }

            for (int i=0; i < 20; i++ )
                sortedByDate[i].delete();
        }



        return saveDir;
    }
    //endregion savedir

    public static void Send(Context contest, byte[] jpg, String info, CmdParameters parms){

        try {
            File saveDir =  getDir(contest);

            Helper.Log( "photo.send ", "start");
            String fileName = String.format( "%s/%d.jpg", saveDir.getAbsolutePath(), System.currentTimeMillis());
            FileOutputStream os = new FileOutputStream(fileName);
            os.write(jpg);
            os.close();
            Helper.Log( "photo.send ", "file ok");
            String msgId = "";
            if (parms != null)
                msgId = parms.msgId;
            MediatorMD.sendPhoto(msgId, fileName, Helper.getNowDT() + " " + info);
            Helper.Log( "photo.send ", "2transport");

        } catch (Exception e) {
            Helper.Ex2Log(e);
        }
    }

}
