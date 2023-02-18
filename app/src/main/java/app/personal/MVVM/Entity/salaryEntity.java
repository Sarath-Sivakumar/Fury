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
//    Update this after every repetition.
    @ColumnInfo(defaultValue = "Not Set")
    private String creationDate;
    @ColumnInfo(defaultValue = "1")
    private int salMode;

    @Ignore
    public salaryEntity(int salary, String incName, int incType, String creationDate, int salMode) {
        this.salary = salary;
        this.incName = incName;
        this.incType = incType;
        this.creationDate = creationDate;
        this.salMode = salMode;
    }


    @Ignore
    public salaryEntity(int id, int salary, String incName, int incType, String creationDate, int salMode) {
        this.id = id;
        this.incName = incName;
        this.salary = salary;
        this.incType = incType;
        this.creationDate = creationDate;
        this.salMode = salMode;
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

    public int getSalMode() {
        return salMode;
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

    public void setSalMode(int salMode) {
        this.salMode = salMode;
    }
}
