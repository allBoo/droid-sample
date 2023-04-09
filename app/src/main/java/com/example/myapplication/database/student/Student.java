package com.example.myapplication.database.student;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.myapplication.database.group.Group;

import java.io.Serializable;
import java.util.Objects;

@Entity(tableName = "students", foreignKeys = {
        @ForeignKey(
                entity = Group.class,
                parentColumns = {"id"},
                childColumns = {"group_id"},
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE
        )
})
public class Student implements Serializable {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    @NonNull
    @ColumnInfo(name = "first_name")
    private String firstName;
    @NonNull
    @ColumnInfo(name = "last_name")
    private String lastName;
    @ColumnInfo(name = "group_id", index = true)
    private int groupId;
    @NonNull
    private String phone;
    @NonNull
    private String email;

    @Ignore
    public Student() {
    }

    public Student(@NonNull Integer id,
                   @NonNull String firstName,
                   @NonNull String lastName,
                   @NonNull int groupId,
                   @NonNull String phone,
                   @NonNull String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.groupId = groupId;
        this.phone = phone;
        this.email = email;
    }

    @Ignore
    public Student(@NonNull String firstName,
                   @NonNull String lastName,
                   Group group,
                   @NonNull String phone,
                   @NonNull String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.groupId = group.getId();
        this.phone = phone;
        this.email = email;
    }

    @NonNull
    public Integer getId() {
        return id;
    }

    @NonNull
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(@NonNull String firstName) {
        this.firstName = firstName;
    }

    @NonNull
    public String getLastName() {
        return lastName;
    }

    public void setLastName(@NonNull String lastName) {
        this.lastName = lastName;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public void setGroup(@NonNull Group group) {
        this.setGroupId(group.getId());
    }

    @NonNull
    public String getPhone() {
        return phone;
    }

    public void setPhone(@NonNull String phone) {
        this.phone = phone;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    public void setEmail(@NonNull String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return id.equals(student.id)
                && firstName.equals(student.firstName)
                && lastName.equals(student.lastName)
                && groupId == student.groupId
                && phone.equals(student.phone)
                && email.equals(student.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, groupId, phone, email);
    }
}
