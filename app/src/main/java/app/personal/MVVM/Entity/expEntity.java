package app.personal.MVVM.Entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import app.personal.Utls.Constants;

@Entity(tableName = Constants.expTable)
public class expEntity implements Parcelable{

    @PrimaryKey(autoGenerate = true)
    private int id;
    private float ExpenseAmt;
    private String ExpenseName, Date, Time;

    @Ignore
    public expEntity(float expenseAmt, String expenseName,
                     String Date, String Time) {
        ExpenseAmt = expenseAmt;
        ExpenseName = expenseName;
        this.Date = Date;
        this.Time = Time;
    }

    @Ignore
    public expEntity(int id, float expenseAmt, String expenseName,
                     String date, String time) {
        this.id = id;
        ExpenseAmt = expenseAmt;
        ExpenseName = expenseName;
        Date = date;
        Time = time;
    }

    public expEntity() {}

    public int getId() {
        return id;
    }

    public float getExpenseAmt() {
        return ExpenseAmt;
    }

    public String getExpenseName() {
        return ExpenseName;
    }

    public String getDate() {
        return Date;
    }

    public String getTime() {
        return Time;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setExpenseAmt(float expenseAmt) {
        ExpenseAmt = expenseAmt;
    }

    public void setExpenseName(String expenseName) {
        ExpenseName = expenseName;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setTime(String time) {
        Time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int arg1) {
        dest.writeInt(id);
        dest.writeString(ExpenseName);
        dest.writeFloat(ExpenseAmt);
        dest.writeString(Date);
        dest.writeString(Time);
    }

    public expEntity(Parcel in) {
        id = in.readInt();
        ExpenseName = in.readString();
        ExpenseAmt = in.readFloat();
        Date = in.readString();
        Time = in.readString();
    }

    public static final Parcelable.Creator<expEntity> CREATOR = new Parcelable.Creator<expEntity>() {
        public expEntity createFromParcel(Parcel in) {
            return new expEntity(in);
        }

        public expEntity[] newArray(int size) {
            return new expEntity[size];
        }
    };
}
