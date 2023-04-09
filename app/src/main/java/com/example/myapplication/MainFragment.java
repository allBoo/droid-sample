package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.MainFragmentDirections.ActionMainFragmentToViewFragment;
import com.example.myapplication.database.student.Student;
import com.example.myapplication.paging.ViewModelFactory;
import com.example.myapplication.paging.student.StudentAdapter;
import com.example.myapplication.paging.student.StudentComparator;
import com.example.myapplication.paging.student.StudentViewHolder;
import com.example.myapplication.paging.student.StudentViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class MainFragment extends Fragment {
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    private StudentViewModel studentViewModel;
    private StudentAdapter studentAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (studentViewModel == null) {
            studentViewModel = new ViewModelFactory(requireActivity())
                    .create(StudentViewModel.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);

        studentAdapter = new StudentAdapter(new StudentComparator(), (p, d) -> studentOnClick(d));
        final RecyclerView recyclerView = view.findViewById(R.id.students_list);
        recyclerView.setAdapter(studentAdapter);

        FloatingActionButton addStudentButton = view.findViewById(R.id.student_add_button);
        addStudentButton.setOnClickListener((v) -> studentAddOnCLick());

        final SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            studentAdapter.refresh();
            swipeRefreshLayout.setRefreshing(false);
        });

        initSwipeToDelete(recyclerView);

        setupMenu();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mDisposable.add(studentViewModel.getStudents()
                .subscribe(pagingData -> studentAdapter.submitData(getLifecycle(), pagingData),
                        ErrorUtils.onError(getView()))
        );
    }

    @Override
    public void onStop() {
        super.onStop();
        mDisposable.clear();
    }

    private void initSwipeToDelete(RecyclerView recyclerView) {
        new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView,
                                        @NonNull RecyclerView.ViewHolder viewHolder) {
                final StudentViewHolder studentViewHolder = (StudentViewHolder) viewHolder;
                final Student student = studentViewHolder.getBinding().getStudent();
                if (student != null) {
                    return makeMovementFlags(0, ItemTouchHelper.LEFT);
                } else {
                    return makeMovementFlags(0, 0);
                }
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder,
                                 int direction) {
                final StudentViewHolder studentViewHolder = (StudentViewHolder) viewHolder;
                final Student student = studentViewHolder.getBinding().getStudent();
                if (student != null) {
                    mDisposable.add(studentViewModel.delete(student)
                            .subscribe(() -> studentAdapter.refresh(),
                                    ErrorUtils.onError(getView())));
                }
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void setupMenu() {
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.main_fragment_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_refresh) {
                    studentAdapter.refresh();
                }
                return true;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void navigateToViewFragment(View view, Student student) {
        final ActionMainFragmentToViewFragment action =
                MainFragmentDirections.actionMainFragmentToViewFragment();
        action.setStudent(student);
        Navigation.findNavController(view).navigate(action);
    }

    public void studentOnClick(Student student) {
        navigateToViewFragment(getView(), student);
    }

    public void studentAddOnCLick() {
        navigateToViewFragment(getView(), null);
    }
}