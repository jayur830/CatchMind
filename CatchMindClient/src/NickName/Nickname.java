package NickName;

import CatchMind.CatchMind;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Nickname extends Application implements Initializable {
    private static Stage stage;

    @FXML
    private TextField nickNameTextField;
    @FXML
    private Button okButton, cancelButton;

    private void showMessageBox(String msg) {
        try {
            MessageBox.message = msg;
            new MessageBox().start(new Stage(StageStyle.UTILITY));
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    private void connect() {
        String nickName = nickNameTextField.getText().trim();
        if (nickName.isEmpty()) {
            showMessageBox("Please enter your nickname.");
        } else if (nickName.contains(" ")) {
            showMessageBox("Don't contain the blanks in entering your nickname.");
        } else if (nickName.length() > 20) {
            showMessageBox("Please enter your nickname in 20 characters.");
        } else {
            Socket nickValidSock;
            try {
                nickValidSock = new Socket(CatchMind.SERVERIP, CatchMind.TCPSERVERPORT);
                nickValidSock.getOutputStream().write(("100" + CatchMind.REGEX + nickName).getBytes());
                InputStream in = nickValidSock.getInputStream();
                byte[] buf = new byte[10];
                while (in.read(buf) == -1);
                if (new String(buf).split(CatchMind.REGEX)[1].equals("true"))
                    showMessageBox("The nickname is already exist.");
                else {
                    CatchMind.nickName = nickName;
                    new CatchMind().start(stage);
                }
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void start(Stage _stage) throws ClassNotFoundException, IOException {
        stage = _stage;
        stage.setTitle("Enter your nickname");
        stage.setScene(new Scene(FXMLLoader.load(
                Class.forName("NickName.Nickname").getResource("nickname.fxml"))));
        stage.setResizable(false);
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.nickNameTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) connect();
        });
        this.okButton.setOnAction(event -> connect());
        this.cancelButton.setOnAction(event -> stage.close());
    }

    public static void main(String[] args) { launch(args); }
}
