package rock.delta2.motiondetector;

import android.content.Context;

import rock.delta2.motiondetector.Commands.CmdCameraAngleSet;
import rock.delta2.motiondetector.Commands.CmdCameraSet;
import rock.delta2.motiondetector.Commands.CmdCameraSizeSet;
import rock.delta2.motiondetector.Detector.CamearaProps;
import rock.delta2.motiondetector.Mediator.MediatorMD;
import rock.delta2.motiondetector.Preferences.PreferencesHelper;

public class InfoHelper {

    public static void sendHelp(Context context){
        StringBuilder sb = new StringBuilder("MotionDetector");
        sb.append("\n\n------------------");

        sb.append("\n" + context.getResources().getString(R.string.cmd_start_description ));
        sb.append("\n" + context.getResources().getString(R.string.cmd_stop_description ));
        sb.append("\n" + context.getResources().getString(R.string.cmd_turn_description ));

        sb.append("\n" + context.getResources().getString(R.string.cmd_camera_get_description ));
        sb.append("\n" + context.getResources().getString(R.string.cmd_camera_set_description ));

        sb.append("\n" + context.getResources().getString(R.string.cmd_size_get_description));
        sb.append("\n" + context.getResources().getString(R.string.cmd_size_set_description));

        sb.append("\n" + context.getResources().getString(R.string.cmd_angle_get_description));
        sb.append("\n" + context.getResources().getString(R.string.cmd_angle_set_description));

        sb.append("\n" + context.getResources().getString(R.string.cmd_delta_get_description));
        sb.append("\n" + context.getResources().getString(R.string.cmd_delta_set_description));

        sb.append("\n" + context.getResources().getString(R.string.cmd_auto_start_get_description));
        sb.append("\n" + context.getResources().getString(R.string.cmd_auto_start_set_description));

        sb.append("\n" + context.getResources().getString(R.string.cmd_voice_call_get_description));
        sb.append("\n" + context.getResources().getString(R.string.cmd_voice_call_set_description));

        sb.append("\n" + context.getResources().getString(R.string.cmd_info_description));


        sb.append("\n\n------------------");
        MediatorMD.sendText("0", sb.toString());
    }

    public static void sendInfo(Context context){
        StringBuilder sb = new StringBuilder("MotionDetector");
        sb.append("\n\n------------------");

        sb.append( String.format("\n\n%s = %s", context.getResources().getString(R.string.status), context.getResources().getString( PreferencesHelper.GetIsActive() ? R.string.status_started : R.string.status_stopped)));

        sb.append(String.format("\n\n%s = %s",context.getResources().getString(R.string.delta), PreferencesHelper.getDelta()));

        sb.append(getCamProp(CmdCameraSet._COMMAND));
        sb.append(getCamProp(CmdCameraSizeSet._COMMAND));
        sb.append(getCamProp(CmdCameraAngleSet._COMMAND));

        sb.append( "\n\n-------------------------");

        sb.append( String.format("\n%s : %s", context.getResources().getString(R.string.working_time)
                , MainService.getWorkingTime(context)));

        MediatorMD.sendText("0", sb.toString());
    }

    private static String getCamProp(String prop){
        CamearaProps p = MediatorMD.GetCameraProps(prop);
        return String.format("\n%s : %s", prop, p.values.get(p.current));
    }

}
