package rock.delta2.motiondetector.Common;

public class ResultCmd {

    public ResultCmd(){
        result = CmdBase.en_result.ok;
    }
    public ResultCmd(CmdBase.en_result r, String m){
        result = r;
        msg = m;
    }

    public CmdBase.en_result result;
    public String msg;
}
