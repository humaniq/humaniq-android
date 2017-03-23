package co.humaniq.services;

import android.util.Log;
import co.humaniq.Client;
import co.humaniq.models.APIErrors;
import co.humaniq.models.ResultData;
import co.humaniq.views.ViewContext;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;


public class APIService {
    private static final String TAG = "APIService";

    private ViewContext context;

    public ViewContext getContext() {
        return context;
    }

    public APIService(ViewContext context) {
        this.context = context;
    }

    static <E> void doRequest(APIService service, Call<E> call,
                              final int requestCode)
    {
        final ViewContext context = service.getContext();

        if (context != null && !Client.isOnline(context)) {
            context.onApiConnectionError(requestCode);
            return;
        }

        if (context != null)
            context.onApiShowProgressbar(requestCode);

        call.enqueue(new Callback<E>() {
            @Override
            public void onResponse(Call<E> call, Response<E> response) {
                if (context != null)
                    context.onApiHideProgressbar(requestCode);

                if (!service.hasStatusError(response, requestCode)) {
                    Log.d(TAG, Integer.toString(response.code()));
                    Log.d(TAG, response.body().toString());

                    if (context != null)
                        context.onApiSuccess(new ResultData<>(response), requestCode);
                }
            }

            @Override
            public void onFailure(Call<E> call, Throwable t) {
                if (context != null)
                    context.onApiHideProgressbar(requestCode);

                t.printStackTrace();
                Log.e(TAG, t.toString());

                if (context != null)
                    context.onApiCriticalError(new APIErrors(), requestCode);
            }
        });
    }

    private boolean hasStatusError(Response response, final int requestCode) {
        final ViewContext context = getContext();

        if (response.isSuccessful())
            return false;

        if (context == null)
            return true;

        switch (response.code()) {
            case 400: // Bad request
                context.onApiValidationError(new APIErrors(response), requestCode);
                break;

            case 401: // Unauthorized
                context.onApiAuthorizationError(new APIErrors(response), requestCode);
                break;

            case 403: // Forbidden
            case 406: // Not acceptable
                context.onApiPermissionError(new APIErrors(response), requestCode);
                break;

            case 429: // Too many requests
            case 408: // Timeout
                context.onApiConnectionError(requestCode);
                break;

            case 504: // Gateway timeout
            case 502: // Bad gateway
                context.onApiConnectionError(requestCode);
                break;

            default:
                Log.e(TAG, Integer.toString(response.code()));
                Log.e(TAG, response.message());
                context.onApiCriticalError(new APIErrors(), requestCode);
        }

        try {
            Log.d(TAG, "Response message: "+response.message());
            Log.d(TAG, "Response status code: " + Integer.toString(response.code()));
            Log.d(TAG, "Response status error: " + response.errorBody().string());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }
}
