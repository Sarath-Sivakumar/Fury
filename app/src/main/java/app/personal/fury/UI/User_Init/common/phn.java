package app.personal.fury.UI.User_Init.common;

public class phn {

    public boolean checkNumber(String code, String phn){
        return checkValidity(code+phn);
    }

    private boolean checkValidity(String phn){
        return phn.length() <= 15 && phn.length() >= 4;
    }
}
