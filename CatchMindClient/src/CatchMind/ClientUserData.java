package CatchMind;

import javafx.beans.property.*;

public class ClientUserData {
    private String port, nickName;

    ClientUserData(String nickName, String port) {
        this.nickName = nickName;
        this.port = port;
    }

    StringProperty getPort() { return new SimpleStringProperty(this.port); }

    StringProperty getNickName() { return new SimpleStringProperty(this.nickName); }

    @Override
    public String toString() {
        return this.nickName + ", " + this.port;
    }
}