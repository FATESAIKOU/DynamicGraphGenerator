import java.io.*;
import java.util.*;

public class PredictTester
{
    public static void main(String[] args)
    {
        Predictor p = new Predictor();

        boolean is_queried = false;

        int line          = 0;
        int query_count   = 0;
        int bi_currect    = 0;
        int tri_currect   = 0;
        String bi_answer  = null;
        String tri_answer = null;
        
        List<String> states = new ArrayList<String>();
        states.add(" HOME");
        states.add(" HOME");
        p.install(" HOME");
        String next_app;

        try {
            File file = new File(args[1]);
            Scanner in = new Scanner(file);
        
            while (in.hasNext()) {
                line ++;
                String command = in.next();

                switch (command.charAt(0)) {
                    case '0':
                        next_app = in.nextLine();
                        states.add(next_app);
                        p.walk(new String[] {states.get(1), states.get(2)});
                        p.walk(new String[] {states.get(0), states.get(1), states.get(2)});
                        states.remove(0);

                        if (is_queried) {
                            is_queried = !is_queried;

                            //System.out.println(bi_answer + "-" + tri_answer + ":" + next_app);
                            bi_currect += next_app.equals(bi_answer) ? 1 : 0;
                            tri_currect += next_app.equals(tri_answer) ? 1 : 0;
                        }
                        break;

                    case '1':
                        next_app = in.nextLine();
                        p.install(next_app);
                        break;

                    case '2':
                        next_app = in.nextLine();
                        p.uninstall(next_app);
                        break;

                    case 'q':
                        in.nextLine();
                        //System.out.print("At line " + line + ":");

                        is_queried = true;
                        query_count ++;

                        bi_answer = p.predict(new String[] {states.get(1)});
                        tri_answer = p.predict(new String[] {states.get(0), states.get(1)});
                }
            }
            
            System.out.println("Bigram Currect Count = " + bi_currect + " / " + query_count + " = " + bi_currect / (query_count * 1.0));
            System.out.println("Trigram Currect Count = " + tri_currect + " / " + query_count + " = " + tri_currect / (query_count * 1.0));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
