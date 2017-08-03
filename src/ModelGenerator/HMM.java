import java.io.*;
import java.util.*;
import org.json.*;

public class HMM
{
    private State trans_count;
    private State emmit_count;

    public HMM()
    {
        trans_count = new State("_T");
        emmit_count = new State("_E");
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
        emmit_count = new State(hmm_mdl.getJSONObject("emmit"));
    }

    public void genCounts(String trans_seq_file, String emmit_seq_file) throws IOException
    {
        // Get File Scanner
        Scanner trans_seq = getSeq(trans_seq_file);
        Scanner emmit_seq = getSeq(emmit_seq_file);

        // Initialize State Path
        List<String> states = new LinkedList<String>();
        states.add("START");
        states.add("START");

        String now_state, now_emmit;
        while (trans_seq.hasNext()) {
            now_state = trans_seq.next();

            // Add To Emmit Count
            if (emmit_seq.hasNext()) {
                now_emmit = emmit_seq.next();
                emmit_count.walk(new String[]{}, 0);
                emmit_count.walk(new String[] {now_state, now_emmit}, 0);
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
        mdl.put("Emmit", emmit_count.toJSON());

        return mdl;
    }

    private Scanner getSeq(String seq_file) throws FileNotFoundException
    {
        File seq_src = new File(seq_file);

        return new Scanner(seq_src);
    }
}
