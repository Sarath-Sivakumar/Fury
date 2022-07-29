package app.personal.MVVM.Entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import app.personal.Utls.Constants;

@Entity(tableName = Constants.salaryTable)
public class salaryEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private float salary;

    @Ignore
    public salaryEntity(float balance) {
        this.salary = balance;
    }

    @Ignore
    public salaryEntity(int id, float salary) {
        this.id = id;
        this.salary = salary;
    }

    public salaryEntity() {}

    public int getId() {
        return id;
    }

    public float getSalary() {
        return salary;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSalary(float salary) {
        this.salary = salary;
    }
}
