package app.personal.MVVM.Entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import app.personal.Utls.Constants;

@Entity(tableName = Constants.inHandBalTable)
public class inHandBalEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int balance;
//    @ColumnInfo(defaultValue = "0")
//    private String amt;

    @Ignore
    public inHandBalEntity(int id, int balance) {
        this.id = id;
        this.balance = balance;
//        this.amt = amt;
    }

    @Ignore
    public inHandBalEntity(int balance) {
        this.balance = balance;
//        this.amt = amt;
    }

    public inHandBalEntity() {
    }

    //    Getter
    public int getId() {
        return id;
    }

//    public String getAmt() {
//        return amt;
//    }

    public int getBalance() {
        return balance;
    }

    //    Setter
    public void setId(int id) {
        this.id = id;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

//    public void setAmt(String amt) {
//        this.amt = amt;
//    }
}
