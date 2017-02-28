package co.humaniq.models;

import retrofit2.Response;


public class ResultData<T> {
    private T data;

    public ResultData(Response<T> response) {
        this.data = response.body();
    }

    public ResultData(T data) {
        this.data = data;
    }

    public T data() { return data; }
}
