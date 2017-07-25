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
        
        pna.addApp("test1");
        pna.addApp("test2");
        pna.addApp("test3");
        pna.addApp("test4");
        pna.addApp("test5");
        pna.addApp("test6");
        
        pna.addEdge("test1", "test2");
        pna.addEdge("test1", "test3");
        pna.addEdge("test1", "test4");
        pna.addEdge("test1", "test5");
        pna.addEdge("test1", "test6");

        HashMap<Integer, Integer> example = pna.getNexts("test1");
        
        // Dump Key
        for (Integer id: example.keySet()) {
            String name = pna.app_rdict.get(id);
            String value = example.get(id).toString();
            System.out.println(name + " " + value);
        }

        System.out.println("Hello World ");
    }


    public HashMap<Integer, Integer> getNexts(String app_name)
    {
        if ( isInstalled( app_name ) ) {
            return app_entry.get( app_dict.get(app_name) );
        } else {
            // raise exception
            System.out.println("g-Not Install : " + app_name);
            return null;
        }
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
            System.out.println("a-Installed: " + app_name);
            // raise exception
        }
    }

    
    private void deleteApp(String app_name)
    {
        if ( isInstalled(app_name) ) {
            app_delete_set.add( app_name.hashCode() );
        } else {
            System.out.println("d-Not Installed: " + app_name);
            // raise exception
        }
    }


    private void addEdge(String from, String to)
    {
        if ( !isInstalled(from) || !isInstalled(to) ) {
            System.out.println("aE-Not Installed: " + from + " / " + to);
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
        return app_dict.containsKey(app_name) &&
            !app_delete_set.contains( app_dict.get(app_name) );
    }
}
