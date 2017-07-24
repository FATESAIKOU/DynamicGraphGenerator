import java.util.*;

public class PredictNextApp
{
    private Integer counter = 0;
    private HashMap<String, Integer> app_dict; // Installed: value >=0, Not Installed: value < 0

    private HashMap<Integer, HashMap<Integer, Integer>> app_entry;

    public static void main(String[] args)
    {
        System.out.println("Hello World");
    }


    public HashMap<Integer, Integer> getNextList(String app_name)
    {
        if ( isInstalled(app_name) ) {
            return app_entry.get( app_dict.get(app_name) );
        } else {
            // raise exception
            return null;
        }
    }

    
    private void addApp(String app_name)
    {
        if ( ! app_dict.containsKey(app_name) ) {
            ++ counter;

            // Create App Id & App Entry
            app_dict.put(app_name, counter);
            app_entry.put(counter, new HashMap<Integer, Integer>());

        } else {
            int app_id = app_dict.get(app_name);

            if (app_id > 0) {
                // raise exception
                return;
            }
            
            // Set Back App Id to positive.
            app_dict.put(app_name, app_id * -1);
        }
    }

    
    private void deleteApp(String app_name)
    {
        if ( isInstalled(app_name) ) {
            app_dict.put(
                app_name,
                app_dict.get(app_name) * -1
            );
        } else {
            // raise exception
        }
    }


    private void addEdge(String from, String to)
    {
        if ( !isInstalled(from) || !isInstalled(to) ) {
            // raise exception
            return;
        }

        int from_id = app_dict.get(from);
        int to_id = app_dict.get(to);
        
        int update_value = 1;
        if ( app_entry.get(from_id).containsKey(to_id) ) {
            update_value = app_entry.get(from_id).get(to_id) + 1 ; 
        }

        app_entry.get(from_id).put(to_id, update_value);
    }


    private boolean isInstalled(String app_name)
    {
        return app_dict.containsKey(app_name) && app_dict.get(app_name) >= 0;
    }
}
