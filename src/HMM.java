/*
 * Implementation of HMM model
 *
 * @author: Eric Chiang
 */

import java.io.*;
import java.util.*;
import org.json.*;

public class HMM
{
    private State trans_count;
    private State emit_count;

    public HMM()
    {
        trans_count = new State("_T");
        emit_count = new State("_E");
    }

    public HMM(String model_file)
        throws IOException, JSONException
    {
        // Create File Reader
        File file = new File(model_file);
        FileInputStream fis = new FileInputStream(file);

        // Get File Content
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();

        // Create State
        JSONObject hmm_mdl = new JSONObject(new String(data, "UTF-8"));

        // Load Model
        trans_count = new State(hmm_mdl.getJSONObject("trans"));
        emit_count = new State(hmm_mdl.getJSONObject("emit"));
    }

    public List<String> getSeq(String emit_seq_file) throws IOException
    {
        Scanner emit_seq = loadSeq(emit_seq_file);
        
        // create transform variables
        Viterbi v = new Viterbi();
        
        // init predict variables
        v.init();

        // process
        List<String> state_seq = v.decode(emit_seq);
        
        return state_seq;
    }

    public void genCounts(String trans_seq_file, String emit_seq_file) throws IOException
    {
        // Get File Scanner
        Scanner trans_seq = loadSeq(trans_seq_file);
        Scanner emit_seq = loadSeq(emit_seq_file);

        // Initialize State Path
        List<String> states = new LinkedList<String>();
        states.add("work");
        states.add("work");

        String now_state, now_emit;
        while (trans_seq.hasNext()) {
            now_state = trans_seq.next();

            // Add To Emmit Count
            if (emit_seq.hasNext()) {
                now_emit = emit_seq.next();
                emit_count.walk(new String[]{}, 0);
                emit_count.walk(new String[] {now_state}, 0);
                emit_count.walk(new String[] {now_state, now_emit}, 0);
            }
            
            // Add To State Count
            states.add(now_state);
            trans_count.walk(new String[]{}, 0);
            trans_count.walk(new String[] {states.get(0)}, 0);
            trans_count.walk(new String[] {states.get(0), states.get(1)}, 0);
            trans_count.walk(new String[] {states.get(0), states.get(1), states.get(2)}, 0);
            states.remove(0);
        }
    }

    public JSONObject toJSON() throws JSONException 
    {
        JSONObject mdl = new JSONObject();

        mdl.put("Trans", trans_count.toJSON());
        mdl.put("Emit", emit_count.toJSON());

        return mdl;
    }

    // Utils
    
    private Scanner loadSeq(String seq_file) throws FileNotFoundException
    {
        File seq_src = new File(seq_file);

        return new Scanner(seq_src);
    }

    // Inner Class for query
    private class Viterbi {
        private Map<String, List<String>> __paths;
        private Map<String, List<String>> __new_paths;
        private Map<String, Double> __probs;          
        private Map<String, Double> __new_probs;

        public Viterbi()
        {
            __paths     = new HashMap<String, List<String>>();
            __new_paths = new HashMap<String, List<String>>();
            __probs     = new HashMap<String, Double>();
            __new_probs = new HashMap<String, Double>();
        }

        private void init()
        {
            __new_paths.put("START", new ArrayList<String>());
            __new_probs.put("START", Math.log(1.0));
            for (State s : trans_count.getRoutes().values()) {
                __new_paths.put(s.getName(), new ArrayList<String>());
                __new_probs.put(s.getName(), Math.log(0.0));
            }
        }

        private void iterate() 
        {
            // swap __prob and __new_prob
            Map<String, Double> tmp;
            tmp = __probs;
            __probs = __new_probs;
            __new_probs = tmp;

            // reset __new_prob
            for (String key : __probs.keySet()) {
                __new_probs.put(key, Double.POSITIVE_INFINITY * -1);
            }
          
            // update paths
            __paths = __new_paths;
        }

        private List<String> decode(Scanner seq)
        {
            List<String> big_str = new ArrayList<String>();
            Double big_prob = 0.0;

            String n_emit;
            while (seq.hasNext()) {
                // iterate the paramaters in Viterbi
                iterate();

                // Get next emission 
                n_emit = seq.next();
                
                String[] sub_path = new String[2];
                State node;
                
                big_prob = Double.POSITIVE_INFINITY * -1;
                big_str = null;

                // Start updating all of the __new_probs and __new_paths of all the states
                for (String now_state : __new_probs.keySet()) {
                    // Get the path for getting next transform route
                    int path_len = __paths.get(now_state).size();
                    __paths.get(now_state).subList(Math.max(path_len - 2, 0), path_len).toArray(sub_path);

                    // Get next transform routes and count
                    node = trans_count.getChild(sub_path, 0);

                    // If there is no route for providing path, use No-Gram route
                    if (node == null)
                        node = trans_count;

                    // update each state in the routes
                    for (State n_state : node.getRoutes().values()) {
                        String n_name = n_state.getName();
                        Double t_prob_n = Math.log(n_state.getCount() * 1.0 / node.getCount());
                        Double e_prob_n = Math.log(getEmitProb(n_name, n_emit));

                        // update __new_probs and __new_paths, if the new probility is bigger than the original one.
                        if (__new_probs.get(n_name) < __probs.get(now_state) + t_prob_n + e_prob_n) {
                            big_prob = __probs.get(now_state) + t_prob_n + e_prob_n;
                            __new_probs.put(n_name, big_prob);
                           
                            // Copy path to the new or Other!!!
                            big_str = new ArrayList<String>(__paths.get(now_state));
                            big_str.add(now_state);
                            __new_paths.put(n_name, big_str);
                        }
                    }
                }
            }

            System.out.println("Prob:\t" + big_prob);

            return big_str;
        }

        private Double getEmitProb(String state, String emission)
        {
            State emit_c_state = emit_count.getChild(new String[] {state, emission}, 0);
            int count = emit_c_state == null ? 0 : emit_c_state.getCount();

            State emit_t_state = emit_count.getChild(new String[] {state}, 0);
            int total = emit_t_state.getCount();

            return count * 1.0 / total;
        }
    }
}
