package app.personal.MVVM.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import app.personal.Utls.Constants;

@Entity(tableName = Constants.launchChecker)
public class LaunchChecker {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(defaultValue = "0")
    private int timesLaunched;

    @Ignore
    public LaunchChecker(int id, int timesLaunched) {
        this.id = id;
        this.timesLaunched = timesLaunched;
    }

    @Ignore
    public LaunchChecker(int timesLaunched) {
        this.timesLaunched = timesLaunched;
    }

    public LaunchChecker(){}

    public int getId() {
        return id;
    }

    public int getTimesLaunched() {
        return timesLaunched;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTimesLaunched(int timesLaunched) {
        this.timesLaunched = timesLaunched;
    }
}
