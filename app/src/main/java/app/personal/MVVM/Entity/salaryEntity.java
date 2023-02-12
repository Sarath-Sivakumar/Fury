package app.personal.MVVM.Entity;

import androidx.room.ColumnInfo;
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
    private int incType;  //Daily = 1, Monthly = 0, Hourly = -1
    @ColumnInfo(defaultValue = "Not Set")
    private String creationDate;

    @Ignore
    public salaryEntity(int salary, String incName, int incType, String creationDate) {
        this.salary = salary;
        this.incName = incName;
        this.incType = incType;
    }


    @Ignore
    public salaryEntity(int id, int salary, String incName, int incType, String creationDate) {
        this.id = id;
        this.incName = incName;
        this.salary = salary;
        this.incType = incType;
        this.creationDate = creationDate;
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

    public String getCreationDate() {
        return creationDate;
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

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }
}
