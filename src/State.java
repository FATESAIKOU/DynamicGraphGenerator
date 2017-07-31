import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class State implements Comparable<State> {
    private Map<String, State> routes;
    private final String name;
    private int count;

    public State(String name) {
        this.name = name;
    }

    public void walk(String[] path, int weight, int idx) {
        if (idx == path.length) {
            count += weight;
        } else {
            if (routes == null)
                routes = new HashMap<>();

            String key = path[idx];
            State state = routes.get(key);
            if (state == null) {
                state = new State(key);
                routes.put(key, state);
            }
            state.walk(path, weight, idx + 1);
        }
    }

    public List<State> predict(String[] path, int idx, Set<String> exclude) {
        if (routes == null)
            return new ArrayList<>();

        if (idx == path.length) {
            List<State> result = new ArrayList<>();
            for (State s : routes.values()) {
                if (exclude.contains(s.name))
                    continue;
                result.add(s);
            }
            Collections.sort(result);
            return result;
        } else {
            String key = path[idx];
            State state = routes.get(key);
            if (state == null)
                return new ArrayList<>();
            return state.predict(path, idx + 1, exclude);
        }
    }

    @Override
    public int compareTo(State s) {
        return Integer.compare(s.count, count);
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public String dump(String now_path) {
        String result = new String();
        if (count > 0) {
            result += now_path + count + '\n';
        }

        if (routes != null) {
            for (State s : routes.values()) {
                result += s.dump(now_path + s.getName() + "/");
            }
        }

        return result;
    }
    
    public String dumpJson(String padding) {
        String result = new String();

        result += padding + "{\n";
        result += padding + "\t\"name\": \"" + name + "\",\n";
        result += padding + "\t\"count\": " + count + ",\n";
        result += padding + "\t\"routes\": [\n";
        if (routes != null) {
            for (State s : routes.values()) {
                result += s.dumpJson(padding + "\t\t") + ",\n";
            }
            result = result.substring(0, result.length() - 2) + '\n';
        }
        result += padding + "\t]\n";
        result += padding + "}";

        return result;
    }
}
