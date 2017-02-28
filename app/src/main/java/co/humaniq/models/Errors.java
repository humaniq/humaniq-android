package co.humaniq.models;


public interface Errors {
    String getError();
    String getError(final String key);
    void setError(final String key, final String value);
}
