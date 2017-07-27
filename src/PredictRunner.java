import java.io.*;
import java.util.*;

public class PredictRunner
{
    public static void main(String[] args)
    {
        NGram test_tri = new NGram(3);
        NGram test_bi = new NGram(2);

        String next_app;
        List<String> app_queue = new ArrayList<String>();

        app_queue.add(" HOME");
        app_queue.add(" HOME");
        test_tri.addApp(" HOME");
        test_bi.addApp(" HOME");


        Integer is_queried = 0;
        Integer query_count = 0;
        Integer bi_currect = 0;
        Integer tri_currect = 0;
        String bi_answer = "";
        String tri_answer = "";


        try {
            File file = new File(args[1]);
            Scanner in = new Scanner(file);
            Integer line = 0;
        
            while (in.hasNext()) {
                line ++;
                String command = in.next();
                switch (command.charAt(0)) {
                    case '0':
                        next_app = in.nextLine();
                        test_bi.addEdge(next_app, app_queue.get(1));
                        test_tri.addEdge(next_app, app_queue.get(0), app_queue.get(1));
                        app_queue.add(next_app);
                        app_queue.remove(0);

                        if (is_queried == 1) {
                            is_queried = 0;

                            System.out.println(bi_answer + "-" + tri_answer + ":" + next_app);
                            bi_currect += next_app.equals(bi_answer) ? 1 : 0;
                            tri_currect += next_app.equals(tri_answer) ? 1 : 0;
                        }

                        break;

                    case '1':
                        next_app = in.nextLine();
                        test_bi.addApp(next_app);
                        test_tri.addApp(next_app);
                        break;

                    case '2':
                        next_app = in.nextLine();
                        test_bi.deleteApp(next_app);
                        test_tri.deleteApp(next_app);
                        break;

                    case 'q':
                        System.out.print("At " + line + ": " + app_queue.subList(0, 2) + " ");
                        in.nextLine();

                        is_queried = 1;
                        query_count ++;
                        
                        HashMap<String, Double> bi_result = test_bi.query( app_queue.get(1) );
                        if (bi_result != null && bi_result.size() > 0) {
                            bi_answer = Collections.max(bi_result.entrySet(), Map.Entry.comparingByValue()).getKey();
                        } else {
                            System.out.print("<<<Bi-gram: Nothing to predict.>>>");
                            bi_answer = "";
                        }
                        
                        HashMap<String, Double> tri_result = test_tri.query( app_queue.get(0), app_queue.get(1) );
                        if (tri_result != null && tri_result.size() > 0) {
                            tri_answer = Collections.max(tri_result.entrySet(), Map.Entry.comparingByValue()).getKey();
                        } else {
                            System.out.println("<<<Tri-gram Nothing to predict.>>>");
                            tri_answer = "";
                        }
                        
                }
            }
            
            System.out.println("Bigram Currect Count = " + bi_currect + " / " + query_count + " = " + bi_currect / (query_count * 1.0));
            System.out.println("Trigram Currect Count = " + tri_currect + " / " + query_count + " = " + tri_currect / (query_count * 1.0));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
