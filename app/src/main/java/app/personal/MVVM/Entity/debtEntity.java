package app.personal.MVVM.Entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import app.personal.Utls.Constants;

@Entity(tableName = Constants.debtTable)
public class debtEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String Source, date, time, finalDate;

    @Ignore
    public debtEntity(int id, String Source, String date, String time, String finalDate) {
        this.id = id;
        this.Source = Source;
        this.date = date;
        this.time = time;
        this.finalDate = finalDate;
    }

    @Ignore
    public debtEntity(String Source, String date, String time, String finalDate) {
        this.Source = Source;
        this.date = date;
        this.time = time;
        this.finalDate = finalDate;
    }

    public debtEntity() {}

    public int getId() {
        return id;
    }

    public String getSource() {
        return Source;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getFinalDate() {
        return finalDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSource(String Source) {
        this.Source = Source;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setFinalDate(String finalDate) {
        this.finalDate = finalDate;
    }
}