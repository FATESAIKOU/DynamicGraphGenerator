import java.io.*;
import java.util.*;
import org.json.*;


public class HMM
{
    private State trans_count;
    private HashMap<String, HashMap<String, Integer>> emmit_count;

    public HMM()
    {
        // init
    }

    public HMM(String model_file)
    {
        // Generate JSON object
        //    trans_count = new State(json['trans']);
        //    emmit_count = new EmmitProb(json['trans']);
    }

    public void genTrans(String trans_seq_file)
    {
        // load sequence file
        //    trans_count.walk(String[] path);
    }

    public void genEmmit(String trans_seq_file, String emmit_seq_file)
    {
        // load sequence file
        //    emmit.addCount(String stateA, String e_stateA);
    }
}
