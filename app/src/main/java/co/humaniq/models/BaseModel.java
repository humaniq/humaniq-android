package co.humaniq.models;


public interface BaseModel {
    class ViewType {
        static public final int DATA = 0;
        static public final int LOADING = 1;
        static public final int HISTORY_HEADER = 2;
        static public final int HISTORY_RECEIVED = 3;
        static public final int HISTORY_TRANSFERRED = 4;
        static public final int HISTORY_BONUS = 5;
    }

    int getViewType();
}
