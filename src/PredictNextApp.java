import java.io.*;
import java.util.*;

public class PredictNextApp
{
    private Set<String> app_delete_set = new HashSet<String>();

    private HashMap<String, HashMap<String, Integer>> bi_app_entry = new HashMap<String, HashMap<String, Integer>>();
    private HashMap<String, HashMap<String, HashMap<String, Integer>>> tri_app_entry = new HashMap<String, HashMap<String, HashMap<String, Integer>>>();

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
                        pna.addEdge(next_app, app_queue.get(1));
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
                        
                        HashMap<String, Double> result = pna.query( app_queue.get(0), app_queue.get(1) );
                        //HashMap<String, Double> result = pna.query( app_queue.get(1) );
                        if (result != null && result.size() > 0) {
                            predict_answer = Collections.max(result.entrySet(), Map.Entry.comparingByValue()).getKey();
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

    public HashMap<String, Double> query(String... query_strings)
    {
        if ( !promiseHavenBeenInstalled(query_strings) ) {
            System.out.println("[Exception] Querid app haven been installed or invalid chain length!");
            return null;   
        }

        HashMap<String, Integer> count_map;
        switch (query_strings.length) {
            case 1:
                count_map = bi_app_entry.get(query_strings[0]);
                break;
            case 2:
                count_map = tri_app_entry.get(query_strings[0])
                                         .get(query_strings[1]);
                break;
            default:
                return null;
        }

        if (count_map == null) {
            return null;
        }

        return getProbabilities( count_map );
    }

    public void addApp(String app_name)
    {
        if ( !bi_app_entry.containsKey( app_name ) && !tri_app_entry.containsKey( app_name ) ) {
            bi_app_entry.put(
                app_name,
                new HashMap<String, Integer>()
            );

            tri_app_entry.put(
                app_name,
                new HashMap<String, HashMap<String, Integer>>()
            );
        } else if ( app_delete_set.contains( app_name ) ) {
            app_delete_set.remove( app_name );
        } else {
            System.out.println("[Exception] Install targe was installed: " + app_name);
            // raise exception
        }
    }
    
    public void deleteApp(String app_name)
    {
        if ( isInstalled(app_name) ) {
            app_delete_set.add( app_name );
        } else {
            System.out.println("[Exception] Delete targe wasn't installed: " + app_name);
            // raise exception
        }
    }

    public void addEdge(String to, String... froms)
    {
        if ( !promiseHavenBeenInstalled(froms) || !isInstalled(to) ) {
            System.out.println("[Excepion] AddEdge-Not Installed: " + froms + " / " + to);

            return;
        }
   
        HashMap<String, Integer> aim_map = promiseMap(froms);

        Integer update_value = 1;
        if ( aim_map.containsKey(to) ) {
            update_value = aim_map.get(to) + 1 ; 
        }

        aim_map.put(to, update_value);
    }


    // Utils
    private HashMap<String, Double> getProbabilities(HashMap<String, Integer> count_map)
    {
        HashMap<String, Double> probabilities = new HashMap<String, Double>();

        // Count Total Appearance
        Integer total_count = 0;
        for (String next_app: count_map.keySet()) {
            if ( !app_delete_set.contains(next_app)) {
                total_count += count_map.get(next_app);
            }
        }

        // Caculate Probabilities
        for (Map.Entry<String, Integer> entry: count_map.entrySet()) {
            if ( !app_delete_set.contains(entry.getKey()) ) {
                probabilities.put(
                    entry.getKey(),
                    entry.getValue() / (total_count * 1.0)
                );
            }
        }

        return probabilities;
    }
    
    private boolean promiseHavenBeenInstalled(String[] app_list)
    {
        switch (app_list.length) {
            case 1:
                return bi_app_entry.containsKey(app_list[0]);
            case 2:
                return tri_app_entry.containsKey(app_list[0]) &&
                    tri_app_entry.containsKey(app_list[1]);
            default:
                return false;
        }
    }

    private HashMap<String, Integer> promiseMap(String[] app_list)
    {
        switch (app_list.length) {
            case 1:
                return bi_app_entry.get(app_list[0]);
            case 2:
                if ( !tri_app_entry.get(app_list[0]).containsKey(app_list[1]) ) {
                    tri_app_entry.get(app_list[0]).put(
                        app_list[1],
                        new HashMap<String, Integer>()
                    );
                }

                return tri_app_entry.get(app_list[0]).get(app_list[1]);
            default:
                // raise exception
                System.out.println("[Exception] PrmiseMap-error chain size!!");
                return null;
        }
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
        return bi_app_entry.containsKey(app_name) &&
            tri_app_entry.containsKey(app_name) &&
            !app_delete_set.contains( app_name );
    }
}
