package com.example.myapplication.network;

public class FineService {
    private static volatile FineApi fineApi;

    private FineService() {
    }

    public static FineApi getFineApi() {
        if (fineApi == null) {
            synchronized (FineService.class) {
                if (fineApi == null) {
                    fineApi = RetrofitFactory.getRetrofit().create(FineApi.class);
                }
            }
        }
        return fineApi;
    }
}
