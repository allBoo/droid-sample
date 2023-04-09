package com.example.myapplication.network;

import com.example.myapplication.database.group.Group;
import com.example.myapplication.database.student.Student;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface StudentApi {
    @GET("/students")
    Observable<List<Student>> getStudents(@Query("_page") int page,
                                          @Query("_limit") int limit);

    @POST("/students")
    Observable<Student> createStudent(@Body Student student);

    @PUT("/students/{id}")
    Observable<Student> updateStudent(@Path("id") int id, @Body Student student);

    @DELETE("/students/{id}")
    Observable<Student> deleteStudent(@Path("id") int id);

    @GET("/groups")
    Observable<List<Group>> getGroups();
}
