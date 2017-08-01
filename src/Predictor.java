import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Scanner;
import java.io.*;
import org.json.*;

public class Predictor {
    private final State state_gram;
    private final Set<String> uninstall = new HashSet<>();

    public Predictor() {
        state_gram = new State("START");
    }

    public Predictor(String file_path) throws IOException, JSONException {
        // Create File Reader
        File file = new File(file_path);
        FileInputStream fis = new FileInputStream(file);

        // Get File Content
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();

        // Create State
        JSONObject model = new JSONObject(new String(data, "UTF-8"));
        state_gram = new State(model);
    }

    public void walk(String[] path) {
        state_gram.walk(path, 0);
    }

    public void install(String packageName) {
        uninstall.remove(packageName);
    }

    public void uninstall(String packageName) {
        uninstall.add(packageName);
    }

    public String predict(String[] path) {
        List<State> states = state_gram.predict(path, 0 , uninstall);

        return states != null && states.size() > 0 ? states.get(0).getName() : null;
    }

    public void dump(String file_path) throws IOException, JSONException {
        PrintWriter out = new PrintWriter(file_path, "UTF-8");
        out.print(state_gram.toJSON().toString());
        out.close();
    }
}
