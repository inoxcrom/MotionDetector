package rock.delta2.motiondetector.Mediator;

import rock.delta2.motiondetector.Common.CmdParameters;
import rock.delta2.motiondetector.Common.RawPicture;

public interface IGetRawPciture {
    void GetRawPicture(RawPicture p, CmdParameters parm, boolean isSmalPict);
}
