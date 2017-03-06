package co.humaniq.models;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Response;

import java.io.IOException;
import java.util.Objects;


public class APIErrors implements Errors {
    static public final String TAG = "APIErrors";

    private JSONObject errors = null;
    private Throwable throwable = null;
    private Response response;
    private String errorBody = "";

    public APIErrors() {
        errors = new JSONObject();
    }

    public APIErrors(Throwable t) {
        throwable = t;
    }

    public APIErrors(Response response) {
        try {
            errorBody = response.errorBody().string();
            Log.e(TAG, response.errorBody().string());
            errors = new JSONObject(response.errorBody().string());
            this.response = response;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getError() {
        return errors.optString("error_description");
    }

    @Override
    public String getError(String key) {
        if (errors == null)
            return "";

        JSONArray items = errors.optJSONArray(key);

        if (items == null)
            return "";

        return items.optString(0);
    }

    @Override
    public void setError(String key, String value) {
        try {
            JSONArray arr = new JSONArray();
            arr.put(value);
            errors.put(key, arr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        if (!errorBody.equals(""))
            return errorBody;

        return super.toString();
    }
}
