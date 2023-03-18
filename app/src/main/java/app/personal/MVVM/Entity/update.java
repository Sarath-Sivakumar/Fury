package app.personal.MVVM.Entity;

import androidx.room.Ignore;

public class update {
    private String Version;

    @Ignore
    public update(String version) {
        Version = version;
    }

    public update() {
    }

    public String getVersion() {
        return Version;
    }

    public void setVersion(String version) {
        Version = version;
    }
}
