package app.personal.MVVM.Entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import app.personal.Utls.Constants;

@Entity(tableName = Constants.budgetTable)
public class budgetEntity {

    @PrimaryKey(autoGenerate = false)
    private int id;
    private int percent;

    @Ignore
    public budgetEntity(int id, int percent) {
        this.id = id;
        this.percent = percent;
    }

    public budgetEntity(){}

    public int getId() {
        return id;
    }

    public int getPercent() {
        return percent;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }
}