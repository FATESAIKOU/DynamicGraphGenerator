import java.io.*;
import java.util.*;

public class PredictNextApp
{
    private Integer counter = 0;
    private HashMap<String, Integer> app_dict = new HashMap<String, Integer>();
    private HashMap<Integer, String> app_rdict = new HashMap<Integer, String>();
    private Set<Integer> app_delete_set = new HashSet<Integer>();

    private HashMap<Integer, HashMap<Integer, Integer>> app_entry = new HashMap<Integer, HashMap<Integer, Integer>>();

    public static void main(String[] args)
    {
        PredictNextApp pna = new PredictNextApp();
        List<String> app_queue = new ArrayList<String>();
        app_queue.add(" HOME");
        app_queue.add(" HOME");
        String next_app;
        pna.addApp(" HOME");

        Integer is_queried = 0;
        Integer currect_count = 0;
        Integer query_count = 0;
        String predict_answer = "";


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
                        pna.addEdge(next_app, app_queue.get(0));
                        pna.addEdge(next_app, app_queue.get(0), app_queue.get(1));
                        app_queue.add(next_app);
                        app_queue.remove(0);

                        if (is_queried == 1) {
                            is_queried = 0;

                            System.out.println(predict_answer + ":" + next_app);
                            if (next_app.equals(predict_answer)) {
                                currect_count ++;
                            } else {
                                //System.err.println("Line <" + line + "> " + next_app + " : " + predict_answer);
                            }
                        }

                        break;

                    case '1':
                        next_app = in.nextLine();
                        pna.addApp(next_app);
                        break;

                    case '2':
                        next_app = in.nextLine();
                        pna.deleteApp(next_app);
                        break;

                    case 'q':
                        System.out.print("At " + line + ": " + app_queue.subList(0, 2) + " ");
                        in.nextLine();

                        is_queried = 1;
                        query_count ++;
                        
                        HashMap<Integer, Double> result = pna.query( app_queue.get(0), app_queue.get(1) );
                        if (result.size() > 0) {
                            Integer app_id = Collections.max(result.entrySet(), Map.Entry.comparingByValue()).getKey();
                            predict_answer = pna.app_rdict.get(app_id);
                        } else {
                            System.out.println("<<<Nothing to predict.>>>");
                            predict_answer = "";
                        }
                        
                }
            }
            
            System.out.println("Currect Count = " + currect_count + " / " + query_count);
            System.out.println(" - " + currect_count + " / " + query_count + " = " + currect_count / (query_count * 1.0));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static <T1, T2, T3> void dumpMap(HashMap<T1, T2> example, HashMap<T1, T3> name_dict)
    {
        for (T1 key: example.keySet()) {
            T3 name = name_dict.get(key);
            T2 value = example.get(key);
            System.out.println(name + " " + value);
        }
    }

    public HashMap<Integer, Double> query(String... query_strings)
    {
        Integer app_list_id = genAppListId(query_strings, false);

        if (app_list_id == -1) {
            return new HashMap<Integer, Double>();
        }

        return getProbabilities( app_list_id );
    }

    private HashMap<Integer, Double> getProbabilities(Integer entry_id)
    {
        HashMap<Integer, Double> probabilities = new HashMap<Integer, Double>();

        // Get Count Sum
        Integer total_count = 0;
        HashMap<Integer, Integer> tmp_counts = app_entry.get(entry_id);
    
        if (tmp_counts == null) {
            return probabilities;
        }

        for (Integer next_app_id: tmp_counts.keySet()) {
            if ( !app_delete_set.contains(next_app_id)) {
                total_count += tmp_counts.get(next_app_id);
            }
        }

        // Caculate Probabilities
        for (Map.Entry<Integer, Integer> entry: app_entry.get(entry_id).entrySet()) {
            if ( !app_delete_set.contains(entry.getKey())) {
                probabilities.put(
                    entry.getKey(),
                    entry.getValue() / (total_count * 1.0)
                );
            }
        }

        return probabilities;
    }

    
    private void addApp(String app_name)
    {
        if ( !app_dict.containsKey( app_name ) ) {
            
            ++ counter;
            app_dict.put(app_name, counter);
            app_rdict.put(counter, app_name);

            app_entry.put(
                counter,
                new HashMap<Integer, Integer>()
            );

        } else if ( app_delete_set.contains( app_dict.get(app_name) ) ) {
            app_delete_set.remove( app_dict.get(app_name) );
        } else {
            System.out.println("[Exception] Install targe was installed: " + app_name);
            // raise exception
        }
    }

    
    private void deleteApp(String app_name)
    {
        if ( isInstalled(app_name) ) {
            app_delete_set.add( app_dict.get(app_name) );
        } else {
            System.out.println("[Exception] Delete targe wasn't installed: " + app_name);
            // raise exception
        }
    }


    private void addEdge(String to, String... froms)
    {
        if ( !isAppListInstalled(froms) || !isInstalled(to) ) {
            System.out.println("AddEdge-Not Installed: " + froms + " / " + to);

            return;
        }
    
        Integer from_id = genAppListId(froms, true);
        Integer to_id = app_dict.get(to);

        Integer update_value = 1;
        if ( !app_entry.containsKey(from_id) ) {
            app_entry.put(from_id, new HashMap<Integer, Integer>());
        }

        if ( app_entry.get(from_id).containsKey(to_id) ) {
            update_value = app_entry.get(from_id).get(to_id) + 1 ; 
        }

        app_entry.get(from_id).put(to_id, update_value);
    }


    /*
     * Add Mode => add entry or not while id not found.
     */
    private Integer genAppListId(String[] app_list, boolean addMode)
    {
        String union_str = String.join(", ", app_list);

        if ( !app_dict.containsKey(union_str) ) {
            if ( addMode ) {
                ++ counter;
                app_dict.put(union_str, counter);
            } else {
                // raise exception
                System.out.println("[Exception] Queried app_list not found");
                return -1;
            }
        }

        Integer app_list_id = app_dict.get(union_str);

        return app_list_id;
    }


    private boolean isAppListInstalled(String[] app_names)
    {
        for (String app_name: app_names) {
            if ( !isInstalled(app_name) ) {
                return false;
            }
        }

        return true;
    }
    

    private boolean isInstalled(String app_name)
    {
        return app_dict.containsKey(app_name) &&
            !app_delete_set.contains( app_dict.get(app_name) );
    }
}
