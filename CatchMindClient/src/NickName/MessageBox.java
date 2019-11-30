package NickName;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.security.Key;
import java.util.ResourceBundle;

public class MessageBox extends Application implements Initializable {
    private static Stage stage;

    @FXML
    private Label messageLabel;
    @FXML
    private Button okButton;

    static String message;

    @Override
    public void start(Stage _stage) throws ClassNotFoundException, IOException {
        stage = _stage;
        stage.setTitle("Information");
        stage.setScene(new Scene(FXMLLoader.load(Class.forName("NickName.MessageBox").getResource("message_box.fxml"))));
        stage.setResizable(false);
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.messageLabel.setText(message);
        this.okButton.setOnAction(event -> stage.close());
        this.okButton.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) stage.close();
        });
    }
}
