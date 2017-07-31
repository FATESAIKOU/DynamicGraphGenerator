import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Scanner;
import java.io.*;

public class Predictor {
    private final State state_gram = new State("START");
    private final Set<String> uninstall = new HashSet<>();

    public void walk(String[] path) {
        state_gram.walk(path, 1, 0);
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

    public void dump(String file_path) {
        try {
            PrintWriter out = new PrintWriter(file_path, "UTF-8");
            out.print(state_gram.dump(""));
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dumpJson(String file_path) {
        try {
            PrintWriter writer = new PrintWriter(file_path, "UTF-8");
            writer.print(state_gram.dumpJson(""));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load(String file_path) {
        try {
            File file = new File(file_path);
            Scanner in = new Scanner(file);
            
            String[] tmps;
            String[] path;
            int count;
            while (in.hasNextLine()) {
                tmps = in.nextLine().split("/");
                path = Arrays.copyOfRange(tmps, 0, tmps.length - 1);
                count = Integer.parseInt(tmps[tmps.length - 1]);

                state_gram.walk(path, count, 0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
