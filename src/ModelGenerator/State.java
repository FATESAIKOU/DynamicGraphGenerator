import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.*;

public class State implements Comparable<State> {
    private Map<String, State> routes;
    private final String name;
    private int count;

    public State(String name) {
        this.name = name;
    }

    public State(JSONObject mdl) throws JSONException {
        name = mdl.getString("name");
        count = mdl.getInt("count");

        if (mdl.has("routes")) {
            routes = new HashMap<>();

            JSONArray mdl_arr = mdl.getJSONArray("routes");
            for (int i = 0; i < mdl_arr.length(); ++ i) {
                JSONObject sub_mdl = mdl_arr.getJSONObject(i);

                String key = sub_mdl.getString("name");
                routes.put(key, new State(sub_mdl));
            }
        }
    }

    public void walk(String[] path, int idx) {
        if (idx == path.length) {
            ++ count;
        } else {
            if (routes == null)
                routes = new HashMap<>();

            String key = path[idx];
            State state = routes.get(key);
            if (state == null) {
                state = new State(key);
                routes.put(key, state);
            }
            state.walk(path, idx + 1);
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

    public JSONObject toJSON() throws JSONException {
        JSONObject model = new JSONObject();

        model.put("name", name);
        model.put("count", count);
    
        if (routes != null) {
            JSONArray routes_json = new JSONArray();
            for (State s : routes.values()) {
                routes_json.put(s.toJSON());
            }
            model.put("routes", routes_json);
        }

        return model;
    }
}
