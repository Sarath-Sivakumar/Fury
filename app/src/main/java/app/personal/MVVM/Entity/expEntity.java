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
    private int ExpenseAmt;
    private int day;
    private String ExpenseName, Date, Time;

    @Ignore
    public expEntity(int expenseAmt, String expenseName,
                     String Date, String Time, int day) {
        ExpenseAmt = expenseAmt;
        ExpenseName = expenseName;
        this.Date = Date;
        this.Time = Time;
        this.day = day;
    }

    @Ignore
    public expEntity(int id, int expenseAmt, String expenseName,
                     String date, String time, int day) {
        this.id = id;
        ExpenseAmt = expenseAmt;
        ExpenseName = expenseName;
        Date = date;
        Time = time;
        this.day = day;
    }

    public expEntity() {}

    public int getId() {
        return id;
    }

    public int getDay() {
        return day;
    }

    public int getExpenseAmt() {
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

    public void setDay(int day) {
        this.day = day;
    }

    public void setExpenseAmt(int expenseAmt) {
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
        ExpenseAmt = in.readInt();
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
