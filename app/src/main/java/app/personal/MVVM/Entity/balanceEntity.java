package app.personal.MVVM.Entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import app.personal.Utls.Constants;

@Entity(tableName = Constants.balanceTable)
public class balanceEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String balance;

    @Ignore
    public balanceEntity(String balance) {
        this.balance = balance;
    }

    @Ignore
    public balanceEntity(int id, String balance) {
        this.id = id;
        this.balance = balance;
    }

    public balanceEntity() {}

    public int getId() {
        return id;
    }

    public String getBalance() {
        return balance;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}