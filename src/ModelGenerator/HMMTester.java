import java.io.*;
import org.json.*;

public class HMMTester
{
    private HMM hmm;

    public static void main(String[] args)
    {
        HMMTester hmmt;
        String emmit_seq_file = args[1];
        String state_seq_file = args[2];
        String model_name = args[3];
        System.out.println("Init");

        // Load or Build Model
        if (args.length > 4) {
            hmmt = new HMMTester(args[4]);
        } else {
            hmmt = new HMMTester();
            hmmt.process(emmit_seq_file, state_seq_file);
        }
        System.out.println("Load Model End");


        // Test Model
        hmmt.initTest();
        hmmt.test(emmit_seq_file, state_seq_file);
        System.out.println("Testing Model");

        // Report
        hmmt.report();
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

    public void process(String emmit_seq_file, String state_seq_file)
    {
        try {
            hmm.genCounts(state_seq_file, emmit_seq_file);
        } catch (IOException e) {
            System.out.println("Something went wrong :)");
            e.printStackTrace();
        }
    }

    public void initTest()
    {
        // Init vars for testing
    }

    public void test(String test_emmit_seq, String test_state_seq)
    {
        // Testing
    }

    public void report()
    {
        // Report testing result
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
