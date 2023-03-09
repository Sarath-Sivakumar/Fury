package app.personal.MVVM.Entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import app.personal.Utls.Constants;

@Entity(tableName = Constants.CustomCategory)
public class CustomCategory {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String CategoryName;

    public CustomCategory(){}

    @Ignore
    public CustomCategory(String CategoryName){
        this.CategoryName = CategoryName;
    }

    @Ignore
    public CustomCategory(int id, String CategoryName){
        this.id = id;
        this.CategoryName = CategoryName;
    }

    public int getId() {
        return id;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }
}
