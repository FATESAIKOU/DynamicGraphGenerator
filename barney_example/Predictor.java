import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Predictor {

    private final State biState = new State("START");
    private final State triState = new State("START");
    private final Set<String> uninstall = new HashSet<>();

    public void walk(String[] path) {
        if (path.length == 2)
            biState.walk(path, 0);
        else if (path.length == 3) {
            //triState.walk(path, 0);
            biState.walk(path, 0);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public void install(String packageName) {
        uninstall.remove(packageName);
    }

    public void uninstall(String packageName) {
        uninstall.add(packageName);
    }

    public void next(String[] path) {
        List<State> states = null;
        if (path.length == 1)
            states = biState.predict(path, 0, uninstall);
        else if (path.length == 2) {
            //states = triState.predict(path, 0, uninstall);
            states = biState.predict(path, 0, uninstall);
            System.out.println(states);
        } else {
            throw new UnsupportedOperationException();
        }

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

    public static void main(String[] args) {
        Predictor p = new Predictor();
        p.walk(new String[] { "A", "B" });
        p.walk(new String[] { "B", "A" });
        p.walk(new String[] { "A", "C" });
        p.walk(new String[] { "A", "C" });
        p.walk(new String[] { "A", "C" });
        p.walk(new String[] { "A", "C" });
        p.next(new String[] { "A" });

        p.walk(new String[] { "A", "C", "Z" });
        p.walk(new String[] { "A", "C", "Z" });
        p.walk(new String[] { "A", "C", "Z" });
        p.walk(new String[] { "A", "C", "Z" });
        p.walk(new String[] { "A", "C", "Z" });
        p.walk(new String[] { "A", "C", "Z" });
        p.next(new String[] { "A", "Z" });
        p.next(new String[] { "A", "C" });
        p.next(new String[] { "A", "Z" });

        System.out.println("...");
        p.uninstall("C");
        p.next(new String[] { "A" });
        System.out.println("...");
        p.install("C");
        p.next(new String[] { "A" });
    }

}
