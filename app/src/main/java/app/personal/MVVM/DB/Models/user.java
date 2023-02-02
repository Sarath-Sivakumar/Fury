package app.personal.MVVM.DB.Models;

public class user {

    private String name, device, password, imgUrl;

    public user(String name, String device, String password, String imgUrl) {
        this.name = name;
        this.device = device;
        this.password = password;
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getDevice() {
        return device;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDevice(String device) {
        this.device = device;
    }


    public void setPassword(String password) {
        this.password = password;
    }

}
