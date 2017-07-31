import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

public class Predictor {
    private final State state_gram = new State("START");
    private final Set<String> uninstall = new HashSet<>();

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

    public void next(String[] path) {
        List<State> states = state_gram.predict(path, 0, uninstall);

        // TODO print
        int total = 0;
        for (State s : states) {
            s.dump();
            total += s.getCount();
        }

        // TODO print probability
        for (State s : states) {
            s.dump(total);
        }
    }
}
