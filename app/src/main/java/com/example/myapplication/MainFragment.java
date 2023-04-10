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
import com.example.myapplication.database.fine.Fine;
import com.example.myapplication.paging.ViewModelFactory;
import com.example.myapplication.paging.fine.FineAdapter;
import com.example.myapplication.paging.fine.FineComparator;
import com.example.myapplication.paging.fine.FineViewHolder;
import com.example.myapplication.paging.fine.FineViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class MainFragment extends Fragment {
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    private FineViewModel fineViewModel;
    private FineAdapter fineAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (fineViewModel == null) {
            fineViewModel = new ViewModelFactory(requireActivity())
                    .create(FineViewModel.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);

        fineAdapter = new FineAdapter(new FineComparator(), (p, d) -> fineOnClick(d));
        final RecyclerView recyclerView = view.findViewById(R.id.fines_list);
        recyclerView.setAdapter(fineAdapter);

        FloatingActionButton addFineButton = view.findViewById(R.id.fine_add_button);
        addFineButton.setOnClickListener((v) -> fineAddOnCLick());

        final SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            fineAdapter.refresh();
            swipeRefreshLayout.setRefreshing(false);
        });

        initSwipeToDelete(recyclerView);

        setupMenu();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mDisposable.add(fineViewModel.getFines()
                .subscribe(pagingData -> fineAdapter.submitData(getLifecycle(), pagingData),
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
                final FineViewHolder fineViewHolder = (FineViewHolder) viewHolder;
                final Fine fine = fineViewHolder.getBinding().getFine();
                if (fine != null) {
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
                final FineViewHolder fineViewHolder = (FineViewHolder) viewHolder;
                final Fine fine = fineViewHolder.getBinding().getFine();
                if (fine != null) {
                    mDisposable.add(fineViewModel.delete(fine)
                            .subscribe(() -> fineAdapter.refresh(),
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
                    fineAdapter.refresh();
                }
                return true;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void navigateToViewFragment(View view, Fine fine) {
        final ActionMainFragmentToViewFragment action =
                MainFragmentDirections.actionMainFragmentToViewFragment();
        action.setFine(fine);
        Navigation.findNavController(view).navigate(action);
    }

    public void fineOnClick(Fine fine) {
        navigateToViewFragment(getView(), fine);
    }

    public void fineAddOnCLick() {
        navigateToViewFragment(getView(), null);
    }
}