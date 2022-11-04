package app.personal.MVVM.Entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import app.personal.Utls.Constants;

@Entity(tableName = Constants.budgetTable)
public class budgetEntity {

    @PrimaryKey(autoGenerate = false)
    private int id;
    private float percent;

    @Ignore
    public budgetEntity(int id, float percent) {
        this.id = id;
        this.percent = percent;
    }

    public budgetEntity(){}

    public int getId() {
        return id;
    }

    public float getPercent() {
        return percent;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }
}