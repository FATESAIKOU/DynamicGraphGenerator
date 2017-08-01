import java.io.*;
import java.util.*;
import org.json.*;

public class PredictTester
{
    private Predictor predictor;
    private List<String> states = new ArrayList<String>();

    private boolean queried    = false;
    private int now_at         = 0;
    private int query_count    = 0;
    private int bi_currect     = 0;
    private int tri_currect    = 0;
    private String bi_answer   = "";
    private String tri_answer  = "";

    public static void main(String[] args)
    {
        PredictTester pt;
        // Load Model or not
        if (args.length > 2) {
            pt = new PredictTester(args[2]);
        } else {
            pt = new PredictTester();
        }
        
        // Initialize enviroment
        init(pt);

        // Processing
        pt.process(args[1]);

        // Get Report
        pt.report();
        
        // Save Model
        if (args.length > 3) {
            try {
                pt.predictor.dump(args[3]);
            } catch (Exception e) {
                System.out.println("Something went wrong :)");
                e.printStackTrace();
            }
        }
    }

    public PredictTester()
    {
        predictor = new Predictor();
    }

    public PredictTester(String filename)
    {
        try {
            predictor = new Predictor(filename);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void init(PredictTester pt)
    {
        pt.predictor.install(" HOME");
        pt.states.add(" HOME");
        pt.states.add(" HOME");
    }

    private void process(String filename)
    {
        try {
            File file = new File(filename);
            Scanner in = new Scanner(file);

            while(in.hasNext()) {
                now_at ++;
                String op = in.next();
                String app = in.nextLine();

                if (op.charAt(0) == 'q') {
                    query();
                } else if (op.charAt(0) == '0') {
                    use(app);

                    if (queried){
                        queried = !queried;
                        verify(app);
                    }
                } else if (op.charAt(0) == '1') {
                    install(app);
                } else if (op.charAt(0) == '2') {
                    uninstall(app);
                }
            }

            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void report()
    {
        System.out.println("Bigram Currect Count = " + bi_currect + " / " + query_count + " = " + bi_currect / (query_count * 1.0));
        System.out.println("Trigram Currect Count = " + tri_currect + " / " + query_count + " = " + tri_currect / (query_count * 1.0));
    }

    private void query()
    {
        queried = true;
        query_count ++;

        bi_answer = predictor.predict(new String[] {states.get(1)});
        tri_answer = predictor.predict(new String[] {states.get(0), states.get(1)});
    }

    private void use(String app)
    {
        states.add(app);
        predictor.walk(new String[] {states.get(1), states.get(2)});
        predictor.walk(new String[] {states.get(0), states.get(1), states.get(2)});
        states.remove(0);
    }

    private void verify(String app)
    {
        bi_currect += app.equals(bi_answer) ? 1 : 0;
        tri_currect += app.equals(tri_answer) ? 1 : 0;
    }

    private void install(String app)
    {
        predictor.install(app);
    }

    private void uninstall(String app)
    {
        predictor.uninstall(app);
    }
}
