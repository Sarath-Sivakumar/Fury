package app.personal.MVVM.Entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import app.personal.Utls.Constants;

@Entity(tableName = Constants.userTable)
public class userEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name, imgUrl;

    public userEntity(){}
    @Ignore
    public userEntity(String name, String imgUrl) {
        this.name = name;
        this.imgUrl = imgUrl;
    }

    @Ignore
    public userEntity(int id, String name, String imgUrl) {
        this.id = id;
        this.name = name;
        this.imgUrl = imgUrl;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
