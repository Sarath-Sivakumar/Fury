package app.personal.MVVM.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import app.personal.Utls.Constants;

@Entity(tableName = Constants.budgetTable)
public class budgetEntity {

    @PrimaryKey(autoGenerate = false)
    private int id;
    private int Amount;
    @ColumnInfo(defaultValue = "0")
    private int bal;

    @Ignore
    public budgetEntity(int id, int Amount, int bal) {
        this.id = id;
        this.Amount = Amount;
        this.bal = bal;
    }

    public budgetEntity(){}

    public int getId() {
        return id;
    }

    public int getBal() {
        return bal;
    }

    public int getAmount() {
        return Amount;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBal(int bal) {
        this.bal = bal;
    }

    public void setAmount(int amount) {
        this.Amount = amount;
    }
}