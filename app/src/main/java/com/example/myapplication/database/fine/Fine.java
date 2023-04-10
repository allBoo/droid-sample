package com.example.myapplication.database.fine;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.myapplication.database.article.Article;

import java.io.Serializable;
import java.util.Objects;

@Entity(tableName = "fines", foreignKeys = {
        @ForeignKey(
                entity = Article.class,
                parentColumns = {"id"},
                childColumns = {"article_id"},
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE
        )
})
public class Fine implements Serializable {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @NonNull
    @ColumnInfo(name = "plate_number")
    private String plateNumber;

    @NonNull
    @ColumnInfo(name = "issue_date_time")
    private String issueDateTime;

    @ColumnInfo(name = "article_id", index = true)
    private int articleId;

    @NonNull
    private int fineAmount;

    @NonNull
    private String email;

    @Ignore
    public Fine() {
    }

    public Fine(@NonNull Integer id,
                @NonNull String plateNumber,
                @NonNull String issueDateTime,
                @NonNull int articleId,
                @NonNull int fineAmount,
                @NonNull String email) {
        this.id = id;
        this.plateNumber = plateNumber;
        this.issueDateTime = issueDateTime;
        this.articleId = articleId;
        this.fineAmount = fineAmount;
        this.email = email;
    }

    @Ignore
    public Fine(@NonNull String plateNumber,
                @NonNull String issueDateTime,
                Article article,
                @NonNull int fineAmount,
                @NonNull String email) {
        this.plateNumber = plateNumber;
        this.issueDateTime = issueDateTime;
        this.articleId = article.getId();
        this.fineAmount = fineAmount;
        this.email = email;
    }

    @NonNull
    public Integer getId() {
        return id;
    }

    @NonNull
    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(@NonNull String plateNumber) {
        this.plateNumber = plateNumber;
    }

    @NonNull
    public String getIssueDateTime() {
        return issueDateTime;
    }

    public void setIssueDateTime(@NonNull String issueDateTime) {
        this.issueDateTime = issueDateTime;
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public void setArticle(@NonNull Article article) {
        this.setArticleId(article.getId());
    }

    @NonNull
    public int getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(@NonNull int fineAmount) {
        this.fineAmount = fineAmount;
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
        Fine fine = (Fine) o;
        return id.equals(fine.id)
                && plateNumber.equals(fine.plateNumber)
                && issueDateTime.equals(fine.issueDateTime)
                && articleId == fine.articleId
                && fineAmount == fine.fineAmount
                && email.equals(fine.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, plateNumber, issueDateTime, articleId, fineAmount, email);
    }
}
