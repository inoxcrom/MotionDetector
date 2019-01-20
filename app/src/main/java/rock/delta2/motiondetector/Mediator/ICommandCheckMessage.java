package rock.delta2.motiondetector.Mediator;


public interface ICommandCheckMessage {
    void CheckMessage(String inTxt, boolean repeatCmd, boolean isSilent, String msgId);
    void CheckMessage(String inTxt, String msgId);
}
