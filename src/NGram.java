import java.io.*;
import java.util.*;

public class NGram
{
    private Integer depth;
    private Set<String> delete_set;
    private Set<String> installed_set;
    private HashMap<String, NGram> gram_map;
    private HashMap<String, Integer> count_map;


    NGram(Integer _depth)
    {
        depth = _depth;
        installed_set = new HashSet<String>();
        delete_set = new HashSet<String>();

        if (_depth > 1) {
            gram_map = new HashMap<String, NGram>();
        } else {
            count_map = new HashMap<String, Integer>();
        }
    }

    NGram(Integer _depth,
            Set<String> _delete_set, Set<String> _installed_set)
    {
        depth = _depth;
        installed_set = _installed_set;
        delete_set = _delete_set;

        if (depth > 1) {
            gram_map = new HashMap<String, NGram>();
        } else {
            count_map = new HashMap<String, Integer>();
        }
    }

    public void addApp(String app)
    {
        if ( !installed_set.contains(app) ) {
            installed_set.add(app);
        } else if( delete_set.contains(app) ) {
            delete_set.remove(app);
        } else {
            // raise exception [Error: Double Install]
            System.out.println("[Error: Double Install]");
        }
    }

    public void deleteApp(String app)
    {
        if ( isInstalled(app) ) {
            delete_set.add(app);
        } else {
            // raise exception [Error: Remove not Installed App]
            System.out.println("[Error: Remove not Installed App]");
        }
    }

    public void addEdge(String to, String... froms)
    {
        if ( !isListEverInstalled(froms) || !isInstalled(to) ) {
            // raise exception [Error: Add Invalid Edge]
            System.out.println("[Error: Add Invalid Edge]");
            return;
        }

        if ( depth - 1 != froms.length ) {
            // raise exception [Error: Gram Num mismatch with Added List]
            System.out.println("[Error: Gram Num mismatch with Added List]");
            return;
        }

        List<String> indexes = new ArrayList<>( Arrays.asList(froms) );
        indexes.add(to);


        createEdge( indexes );
    }

    public HashMap<String, Integer> query(String... app_list)
    {
        if ( !isListEverInstalled(app_list) ) {
            // raise exception [Error: Queried for Never Installed App]
            System.out.println("[Error: Queried for Never Installed App]");
            return null;
        }

        if ( depth - 1 != app_list.length ) {
            // raise exception [Error: Gram Num mismatch with Query Length]
            System.out.println("[Error: Gram Num mismatch with Query Length]");
            return null;
        }

        return getCounts( Arrays.asList(app_list) );
    }

    public void dump(String padding)
    {
        System.out.println(padding + "{");
        if (depth > 1) {
            for (String gram_key: gram_map.keySet()) {
                System.out.println(padding + " \"" + gram_key + "\":");
                gram_map.get(gram_key).dump(padding + "  ");
            }
        } else {
            for (String count_key: count_map.keySet()) {
                System.out.println(
                        padding + " \"" + count_key + "\":" + count_map.get(count_key));
            }
        }
        System.out.println(padding + "}");
    }

    // Utils
    private HashMap<String, Integer> getCounts(List<String> indexes)
    {
        if ( depth > 1 ) {
            if ( gram_map.containsKey(indexes.get(0)) ) {
                return gram_map.get( indexes.get(0) ).getCounts(
                    indexes.size() > 1?
                        indexes.subList(1, indexes.size()) : null
                );
            }else {
                return null;
            }

        } else if ( depth == 1 ){
            return count_map;
        } else {
            return null;
        }
    }
    
    private void createEdge(List<String> indexes)
    {
        String index = indexes.get(0);

        if ( depth > 1 ) {
            if ( !gram_map.containsKey( index ) ) {
                gram_map.put(
                    index,
                    new NGram(depth - 1, delete_set, installed_set)
                );
            }

            gram_map.get(index).createEdge( indexes.subList(1, indexes.size()) );
        } else if ( depth == 1 ) {
            Integer cnt = 1;

            if ( count_map.containsKey( index ) ) {
                cnt = count_map.get(index) + 1;
            }  

            count_map.put(index, cnt);
        } else {
            // raise exception [Error: Non positive Depth]
            System.out.println("[Error: Non positive Depth]");
        }
    }
    
    private boolean isListEverInstalled(String[] app_list)
    {
        for (String app: app_list) {
            if ( !installed_set.contains(app) ) {
                return false;
            }
        }

        return true;
    }

    private boolean isInstalled(String app)
    {
        return installed_set.contains(app) &&
            !delete_set.contains(app);
    }
}
