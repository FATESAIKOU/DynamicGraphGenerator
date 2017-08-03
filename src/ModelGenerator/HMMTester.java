public class HMMTester
{
    private HMM hmm;

    public static void main(String[] args)
    {
        HMMTester hmmt;
        String emmit_seq_file = args[1];
        String state_seq_file = args[2];
        String model_name = args[3];


        // Load or Build Model
        if (args.length > 4) {
            hmmt = new HMMTester(args[4]);
        } else {
            hmmt = new HMMTester();
            hmmt.process(emmit_seq_file, state_seq_file);
        }

        // Test Model
        hmmt.initTest();
        hmmt.test(emmit_seq_file, state_seq_file);

        // Report
        hmmt.report();

        // Save Model
        hmmt.save(model_name);
    }

    public HMMTester()
    {
        hmm = new HMM();
    }

    public HMMTester(String model_file)
    {
        hmm = new HMM(model_file);
    }

    public void process(String emmit_seq_file, String state_seq_file)
    {
        hmm.genTrans(state_seq_file);
        hmm.genEmmit(state_seq_file, emmit_seq_file);
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
        // HMM dump
    }
}
