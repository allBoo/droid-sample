package com.example.myapplication.database.fine;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.myapplication.database.article.Article;
import com.example.myapplication.database.converters.DateTimeConverter;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
    private final static DateTimeFormatter shortDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final static DateTimeFormatter shortDateFormatterIn = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.of("UTC"));

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @NonNull
    @ColumnInfo(name = "plate_number")
    private String plateNumber;

    @NonNull
    @ColumnInfo(name = "issue_date_time")
    @TypeConverters({DateTimeConverter.class})
    private OffsetDateTime issueDateTime;

    @ColumnInfo(name = "article_id", index = true)
    private int articleId;

    @NonNull
    private int fineAmount;

    @NonNull
    private String email;

    @NonNull
    private Boolean payed = false;

    @Ignore
    public Fine() {
    }

    public Fine(@NonNull Integer id,
                @NonNull String plateNumber,
                @NonNull OffsetDateTime issueDateTime,
                @NonNull int articleId,
                @NonNull int fineAmount,
                @NonNull String email,
                @NonNull Boolean payed) {
        this.id = id;
        this.plateNumber = plateNumber;
        this.issueDateTime = issueDateTime;
        this.articleId = articleId;
        this.fineAmount = fineAmount;
        this.email = email;
        this.payed = payed;
    }

    @Ignore
    public Fine(@NonNull String plateNumber,
                @NonNull OffsetDateTime issueDateTime,
                Article article,
                @NonNull int fineAmount,
                @NonNull String email,
                @NonNull Boolean payed) {
        this.plateNumber = plateNumber;
        this.issueDateTime = issueDateTime;
        this.articleId = article.getId();
        this.fineAmount = fineAmount;
        this.email = email;
        this.payed = payed;
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
    public OffsetDateTime getIssueDateTime() {
        return issueDateTime;
    }

    public void setIssueDateTime(@NonNull OffsetDateTime issueDateTime) {
        this.issueDateTime = issueDateTime;
    }

    @NonNull
    public String getIssueDateTimeString() {
        return issueDateTime == null ? OffsetDateTime.now().format(shortDateFormatter) : issueDateTime.format(shortDateFormatter);
    }

    public void setIssueDateTimeString(@NonNull String issueDateTimeString) {
        Log.i("FINE", "Updated datetime1 " + issueDateTimeString);

        try {
            issueDateTime = ZonedDateTime.parse(issueDateTimeString, shortDateFormatterIn).toInstant().atOffset(ZoneOffset.UTC);
        } catch (Exception e) {
            issueDateTime = OffsetDateTime.now();
        }
        Log.i("FINE", "Updated datetime2 " + issueDateTime);
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
    public String getFineAmountRub() {
        return String.valueOf(fineAmount / 100);
    }

    public void setFineAmountRub(@NonNull String fineAmountRub) {
        try {
            fineAmount = Integer.parseInt(fineAmountRub) * 100;
        } catch (NumberFormatException e) {
            fineAmount = 0;
        }
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

    @NonNull
    public Boolean getPayed() {
        return payed;
    }

    public void setPayed(@NonNull Boolean payed) {
        this.payed = payed;
    }
}
