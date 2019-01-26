package rock.delta2.motiondetector.Mediator;

import rock.delta2.motiondetector.Detector.CamearaProps;

public interface IGetCameraProp {
    void SendCameraProp(String msgId, String prop);

    CamearaProps GetCameraProps(String prop);
}
