package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.database.group.Group;
import com.example.myapplication.database.student.Student;
import com.example.myapplication.databinding.FragmentViewBinding;
import com.example.myapplication.paging.ViewModelFactory;
import com.example.myapplication.paging.group.GroupViewModel;
import com.example.myapplication.paging.student.StudentViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class ViewFragment extends Fragment {

    private final CompositeDisposable mDisposable = new CompositeDisposable();

    private StudentViewModel studentViewModel;
    private GroupViewModel groupViewModel;
    private final List<Group> groupList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (studentViewModel == null) {
            studentViewModel = new ViewModelFactory(requireActivity())
                    .create(StudentViewModel.class);
        }
        if (groupViewModel == null) {
            groupViewModel = new ViewModelFactory(requireActivity())
                    .create(GroupViewModel.class);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewFragmentArgs args = ViewFragmentArgs.fromBundle(getArguments());
        final Student student = args.getStudent();
        final FragmentViewBinding binding =
                FragmentViewBinding.inflate(inflater, container, false);
        binding.setStudent(Optional.ofNullable(student)
                .orElse(new Student()));
        binding.setIsCreation(student == null);
        binding.setHandler(this);

        final Spinner groupsView = binding.getRoot().findViewById(R.id.groups);
        final ArrayAdapter<Group> groupAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, groupList);
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupsView.setAdapter(groupAdapter);

        mDisposable.add(groupViewModel.getGroups()
                .subscribe(dbGroups -> {
                    groupList.clear();
                    groupList.add(new Group(0, ""));
                    groupList.addAll(dbGroups);
                    groupAdapter.notifyDataSetChanged();
                    if (student != null) {
                        groupsView.setSelection(getGroupPosition(groupList, student.getGroupId()));
                    }
                }, ErrorUtils.onError(getView())));

        return binding.getRoot();
    }

    @Override
    public void onStop() {
        super.onStop();
        mDisposable.clear();
    }

    private static void setError(TextView view, String error) {
        view.requestFocus();
        view.setError(error);
    }

    private int getGroupPosition(List<Group> groups, int groupId) {
        for (int i = 0; i < groups.size(); i++) {
            if (Objects.equals(groups.get(i).getId(), groupId)) {
                return i;
            }
        }
        return 0;
    }

    private static boolean isEmptyValues(View view, String error) {
        if (view instanceof ViewGroup) {
            if (view instanceof Spinner) {
                final Spinner spinner = (Spinner) view;
                if (spinner.getSelectedItemPosition() == 0) {
                    setError(((TextView) spinner.getSelectedView()), error);
                    return true;
                }
            }
            final ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                final View child = viewGroup.getChildAt(i);
                if (isEmptyValues(child, error)) {
                    return true;
                }
            }
        } else if (view instanceof TextInputEditText) {
            final TextInputEditText input = (TextInputEditText) view;
            if (input.getEditableText().toString().trim().isEmpty()) {
                setError(input, error);
                return true;
            }
        }
        return false;
    }

    private Student checkAndSetGroup(Student student) {
        if (student.getGroupId() == 0) {
            throw new IllegalArgumentException("GroupId is 0");
        }
        student.setGroup(groupList.get(student.getGroupId()));
        return student;
    }

    public void onSave(Student student) {
        if (isEmptyValues(getView(), getString(R.string.empty_value))) {
            return;
        }
        mDisposable.add(studentViewModel.insert(checkAndSetGroup(student))
                .subscribe(() -> Navigation.findNavController(getView()).popBackStack(),
                        ErrorUtils.onError(getView())));
    }

    public void onUpdate(Student student) {
        if (isEmptyValues(getView(), getString(R.string.empty_value))) {
            return;
        }
        mDisposable.add(studentViewModel.update(checkAndSetGroup(student))
                .subscribe(() -> Navigation.findNavController(getView()).popBackStack(),
                        ErrorUtils.onError(getView())));
    }
}