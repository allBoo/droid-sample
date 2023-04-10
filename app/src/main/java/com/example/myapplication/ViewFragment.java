package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.database.article.Article;
import com.example.myapplication.database.fine.Fine;
import com.example.myapplication.databinding.FragmentViewBinding;
import com.example.myapplication.paging.ViewModelFactory;
import com.example.myapplication.paging.article.ArticleViewModel;
import com.example.myapplication.paging.fine.FineViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import android.content.Context;

public class ViewFragment extends Fragment {
    private final static DateTimeFormatter shortDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final CompositeDisposable mDisposable = new CompositeDisposable();

    private FineViewModel fineViewModel;
    private ArticleViewModel articleViewModel;
    private final List<Article> articleList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (fineViewModel == null) {
            fineViewModel = new ViewModelFactory(requireActivity())
                    .create(FineViewModel.class);
        }
        if (articleViewModel == null) {
            articleViewModel = new ViewModelFactory(requireActivity())
                    .create(ArticleViewModel.class);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewFragmentArgs args = ViewFragmentArgs.fromBundle(getArguments());
        final Fine fine = args.getFine();
        final FragmentViewBinding binding =
                FragmentViewBinding.inflate(inflater, container, false);
        binding.setFine(Optional.ofNullable(fine)
                .orElse(new Fine()));
        binding.setIsCreation(fine == null);
        binding.setHandler(this);

        final Spinner articlesView = binding.getRoot().findViewById(R.id.articles);
        final ArrayAdapter<Article> articleAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, articleList);
        articleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        articlesView.setAdapter(articleAdapter);

        mDisposable.add(articleViewModel.getArticles()
                .subscribe(dbArticles -> {
                    articleList.clear();
                    articleList.add(new Article(0, ""));
                    articleList.addAll(dbArticles);
                    articleAdapter.notifyDataSetChanged();
                    if (fine != null) {
                        articlesView.setSelection(getArticlePosition(articleList, fine.getArticleId()));
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

    private int getArticlePosition(List<Article> articles, int articleId) {
        for (int i = 0; i < articles.size(); i++) {
            if (Objects.equals(articles.get(i).getId(), articleId)) {
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

    private Fine checkAndSetArticle(Fine fine) {
        if (fine.getArticleId() == 0) {
            throw new IllegalArgumentException("ArticleId is 0");
        }
        fine.setArticle(articleList.get(fine.getArticleId()));
        return fine;
    }

    public void onSave(Fine fine) {
        if (isEmptyValues(getView(), getString(R.string.empty_value))) {
            return;
        }
        mDisposable.add(fineViewModel.insert(checkAndSetArticle(fine))
                .subscribe(() -> Navigation.findNavController(getView()).popBackStack(),
                        ErrorUtils.onError(getView())));
    }

    public void onUpdate(Fine fine) {
        if (isEmptyValues(getView(), getString(R.string.empty_value))) {
            return;
        }
        mDisposable.add(fineViewModel.update(checkAndSetArticle(fine))
                .subscribe(() -> Navigation.findNavController(getView()).popBackStack(),
                        ErrorUtils.onError(getView())));
    }

    public void showDateTimePicker(Fine fine) {
        OffsetDateTime dt = fine.getIssueDateTime() != null ? fine.getIssueDateTime() : OffsetDateTime.now();

        new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                OffsetDateTime dt1 = dt.withYear(year).withMonth(monthOfYear).withDayOfMonth(dayOfMonth);

                new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        OffsetDateTime dt2 = dt1.withHour(hourOfDay).withMinute(minute);
                        ((TextInputEditText) getView().findViewById(R.id.fine_date_time)).setText(dt2.format(shortDateFormatter));

                        Log.v("FINE", "The choosen one " + dt2);
                    }
                }, dt1.getHour(), dt1.getMinute(), true).show();
            }
        }, dt.getYear(), dt.getMonthValue(), dt.getDayOfMonth()).show();
    }
}
