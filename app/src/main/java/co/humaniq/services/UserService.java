package co.humaniq.services;

import co.humaniq.Client;
import co.humaniq.models.DummyModel;
import co.humaniq.models.Page;
import co.humaniq.models.User;
import co.humaniq.views.ViewContext;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


// TODO: Написать аннотацию, для автоматической генерации методов
public class UserService extends APIService {
    static final String TAG = "AuthService";

    interface RetrofitService {
        @GET("user/list/")
        Call<Page<User>> getList(@Query("page") int page);

        @GET("user/my/")
        Call<User> getMy();

        @GET("user/get_by_id/")
        Call<User> getById(@Query("id") int id);
    }

    private RetrofitService retrofitService;

    public UserService(ViewContext context) {
        super(context);
        retrofitService = Client.getAuthRetrofitInstance()
                .create(RetrofitService.class);
    }

    public void getList(final int page, final int requestCode) {
        Call<Page<User>> call = retrofitService.getList(page);
        APIService.doRequest(this, call, requestCode);
    }

    public void getMy(final int requestCode) {
        Call<User> call = retrofitService.getMy();
        APIService.doRequest(this, call, requestCode);
    }

    public void getById(final int requestCode, final int id) {
        Call<User> call = retrofitService.getById(id);
        APIService.doRequest(this, call, requestCode);
    }
}
