package rock.delta2.motiondetector.Commands;

import android.content.Context;

import rock.delta2.motiondetector.Common.CmdBase;
import rock.delta2.motiondetector.Common.CmdParameters;
import rock.delta2.motiondetector.Common.RawPicture;
import rock.delta2.motiondetector.Common.ResultCmd;
import rock.delta2.motiondetector.Detector.SendPhoto;
import rock.delta2.motiondetector.Mediator.IGetRawPictureCallback;
import rock.delta2.motiondetector.Mediator.MediatorMD;
import rock.delta2.motiondetector.R;

public class CmdPhotoGet extends CmdBase implements IGetRawPictureCallback {
        public static final String _COMMAND = "";
        RawPicture _currentPict = null;

        public String getDescription(Context context){
                return "";
        }

        public static String GetDescription(Context context){
                return String.format("\n\n%s"
                        , context.getResources().getString(R.string.cmd_get_photo_description));
        }

        public CmdPhotoGet(){
                super(en_type.get, _COMMAND);
        }

        @Override
        public void OnDestroy(){
                super.OnDestroy();
                _context = null;
                _currentPict.OnDestroy();
                _currentPict = null;
        }


        Context _context;
        public ResultCmd run(Context context, String ori, String[] parts, CmdParameters parms){
                _context = context;

                if (_currentPict == null) {
                    _currentPict = new RawPicture(this);
                }

                MediatorMD.GetRawPciture(_currentPict, parms, false);

                return new ResultCmd();
        }

        @Override
        public void OnGetRawPicture(CmdParameters parms) {
                SendPhoto.Send(_context, _currentPict.getJpg(), "", parms);
        }
}