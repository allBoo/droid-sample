package com.example.myapplication.network;

public class StudentService {
    private static volatile StudentApi studentApi;

    private StudentService() {
    }

    public static StudentApi getStudentApi() {
        if (studentApi == null) {
            synchronized (StudentService.class) {
                if (studentApi == null) {
                    studentApi = RetrofitFactory.getRetrofit().create(StudentApi.class);
                }
            }
        }
        return studentApi;
    }
}
