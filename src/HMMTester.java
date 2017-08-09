/*
 * This program was written to generate HMM sequence with model built with <Emission Seq> and <State Seq>.
 * 
 * @author: Eric Chiang
 *
 * @Args:
 *  - 1: emission sequence file path
 *  - 2: state sequence file path
 *  - 3: [Optional] name for saved model (path)
 *  - 4: [Optional] load pre-generated model (path)
 */

import java.util.*;
import java.io.*;
import org.json.*;

public class HMMTester
{
    private HMM hmm;

    public static void main(String[] args)
    {
        HMMTester hmmt;
        String emit_seq_file = args[1];
        String state_seq_file = args[2];
        String model_name = args[3];
        System.out.println("Init");

        // Load or Build Model
        if (args.length > 4) {
            hmmt = new HMMTester(args[4]);
        } else {
            hmmt = new HMMTester();
            hmmt.process(emit_seq_file, state_seq_file);
        }
        System.out.println("Load Model End");


        // Test Model
        List<String> predict_seq = hmmt.test(emit_seq_file, state_seq_file);
        System.out.println("Testing Model");

        // Report
        hmmt.report(predict_seq);
        System.out.println("Reporting");

        // Save Model
        hmmt.save(model_name);
        System.out.println("Saving");
    }

    public HMMTester()
    {
        hmm = new HMM();
    }

    public HMMTester(String model_file)
    {
        try {
            hmm = new HMM(model_file);
        } catch (Exception e) {
            System.out.println("Something went wrong :)");
            e.printStackTrace();
        }
    }

    public void process(String emit_seq_file, String state_seq_file)
    {
        try {
            hmm.genCounts(state_seq_file, emit_seq_file);
        } catch (IOException e) {
            System.out.println("Something went wrong :)");
            e.printStackTrace();
        }
    }

    public List<String> test(String test_emit_seq, String test_state_seq)
    {
        try {
            return hmm.getSeq(test_emit_seq);
        } catch (IOException e) {
            System.out.println("Something went wrong :)");
            e.printStackTrace();
            return null;
        }
    }

    public void report(List<String> seq)
    {
        try {
            PrintWriter writer = new PrintWriter("result_seq", "UTF-8");
            for (String state : seq) {
                writer.println(state);
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Something went wrong :)");
            e.printStackTrace();
        }
    }

    public void save(String model_name)
    {
        try {
            PrintWriter out = new PrintWriter(model_name, "UTF-8");
            out.print(hmm.toJSON().toString());
            out.close();
        } catch (Exception e) {
            System.out.println("Something went wrong :)");
            e.printStackTrace();
        }
    }
}
