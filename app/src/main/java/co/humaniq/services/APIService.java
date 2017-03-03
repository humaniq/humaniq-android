package co.humaniq.services;

import android.util.Log;
import co.humaniq.Client;
import co.humaniq.models.APIErrors;
import co.humaniq.models.DummyModel;
import co.humaniq.models.ResultData;
import co.humaniq.views.ViewContext;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;


public class APIService<T> extends BaseService<T> {
    private static final String TAG = "APIService";

    public APIService(ViewContext context) {
        super(context);
    }

    void requestCall(Call<T> call) {
        requestCall(call, ViewContext.DUMMY_REQUEST);
    }

    void requestCall(Call<T> call, final int requestCode) {
        APIService.doRequest(this, getContext(), call, requestCode);
    }

    void request(Call<DummyModel> call) {
        request(call, ViewContext.DUMMY_REQUEST);
    }

    void request(Call<DummyModel> call, final int requestCode) {
        APIService.doRequest(this, getContext(), call, requestCode);
    }

    private static <E> void doRequest(APIService service, ViewContext context, Call<E> call,
                                      final int requestCode)
    {
        if (!Client.isOnline(context)) {
            context.connectionError(requestCode);
            return;
        }

        context.showProgressbar(requestCode);
        call.enqueue(new Callback<E>() {
            @Override
            public void onResponse(Call<E> call, Response<E> response) {
                context.hideProgressbar(requestCode);

                if (!service.hasStatusError(response, requestCode))
                    context.success(new ResultData<>(response), requestCode);
            }

            @Override
            public void onFailure(Call<E> call, Throwable t) {
                context.hideProgressbar(requestCode);
                t.printStackTrace();
                context.criticalError(new APIErrors(), requestCode);
            }
        });
    }

    private boolean hasStatusError(Response response, final int requestCode) {
        final ViewContext context = getContext();

        if (response.isSuccessful())
            return false;

        switch (response.code()) {
            case 400: // Bad request
                context.validationError(new APIErrors(response), requestCode);
                break;

            case 401: // Unauthorized
                context.authorizationError(new APIErrors(response), requestCode);
                break;

            case 403: // Forbidden
            case 406: // Not acceptable
                context.permissionError(new APIErrors(response), requestCode);
                break;

            case 429: // Too many requests
            case 408: // Timeout
                context.connectionError(requestCode);
                break;

            case 504: // Gateway timeout
            case 502: // Bad gateway
                context.connectionError(requestCode);
                break;

            default:
                context.criticalError(new APIErrors(), requestCode);
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
