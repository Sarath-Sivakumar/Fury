package app.personal.MVVM.Entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import app.personal.Utls.Constants;

@Entity(tableName = Constants.inHandBalTable)
public class inHandBalEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String balance;

    @Ignore
    public inHandBalEntity(int id, String balance) {
        this.id = id;
        this.balance = balance;
    }

    @Ignore
    public inHandBalEntity(String balance) {
        this.balance = balance;
    }

    public inHandBalEntity(){}

//    Getter
    public int getId() {
        return id;
    }

    public String getBalance() {
        return balance;
    }

//    Setter
    public void setId(int id) {
        this.id = id;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}
