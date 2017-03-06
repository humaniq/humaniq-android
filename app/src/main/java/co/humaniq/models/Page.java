package co.humaniq.models;

import java.util.List;


public class Page<T> {
    private int count;
    private String next;
    private String previous;
    private List<T> results;

    public int getCount() {
        return count;
    }

    public String getNext() {
        return next;
    }

    public String getPrevious() {
        return previous;
    }

    public List<T> getResults() {
        return results;
    }

    public Integer getNextPage() {
        if (next == null) {
            return null;
        }

        String[] params = next.split("&");

        for (String param : params) {
            String name = param.split("=")[0];
            String value = param.split("=")[1];

            if (name.equals("page"))
                return Integer.parseInt(value);
        }

        return null;
    }
}
