package Rest;

import java.util.ArrayList;

public class Greeting {

    private final long id;
    private final String content;
    private ArrayList<String> names;

    public Greeting(long id, String content) {
        this.id = id;
        this.content = content;
        names = new ArrayList<>();
        names.add("a");
        names.add("b");
        names.add("c");
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public ArrayList<String> getNames() {
        return names;
    }
}
