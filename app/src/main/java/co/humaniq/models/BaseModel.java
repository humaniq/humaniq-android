package co.humaniq.models;


public interface BaseModel {
    public class ViewType {
        static public final int LOADING = 0;
        static public final int DATA = 1;
    }

    int getViewType();
}
