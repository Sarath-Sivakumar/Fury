package app.personal.MVVM.Entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import app.personal.Utls.Constants;

@Entity(tableName = Constants.debtTable)
public class debtEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String Source, date, finalDate, status;
    private float Amount;

    @Ignore
    public debtEntity(int id, String Source, String date, String finalDate, float Amount, String status) {
        this.id = id;
        this.Source = Source;
        this.date = date;
        this.finalDate = finalDate;
        this.Amount = Amount;
        this.status = status;
    }

    @Ignore
    public debtEntity(String Source, String date, String finalDate, float Amount, String status) {
        this.Source = Source;
        this.date = date;
        this.finalDate = finalDate;
        this.Amount = Amount;
        this.status = status;
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

    public String getFinalDate() {
        return finalDate;
    }

    public float getAmount() {
        return Amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAmount(float amount) {
        Amount = amount;
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

    public void setFinalDate(String finalDate) {
        this.finalDate = finalDate;
    }
}