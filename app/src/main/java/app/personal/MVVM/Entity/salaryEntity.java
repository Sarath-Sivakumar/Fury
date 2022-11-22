package app.personal.MVVM.Entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import app.personal.Utls.Constants;

@Entity(tableName = Constants.salaryTable)
public class salaryEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String incName;
    private int salary;
    private int incType;  //Daily = 1, Monthly = 0, Hourly = -1(To be implemented in a future update).

    @Ignore
    public salaryEntity(int salary, String incName, int incType) {
        this.salary = salary;
        this.incName = incName;
        this.incType = incType;
    }

    @Ignore
    public salaryEntity(int id, int salary, String incName, int incType) {
        this.id = id;
        this.incName = incName;
        this.salary = salary;
        this.incType = incType;
    }

    public salaryEntity() {}

    public int getId() {
        return id;
    }

    public String getIncName() {
        return incName;
    }

    public int getSalary() {
        return salary;
    }

    public int getIncType() {
        return incType;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIncName(String incName) {
        this.incName = incName;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public void setIncType(int incType) {
        this.incType = incType;
    }
}
