package CatchMind;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Answer extends Application implements Initializable {
    private static Stage stage;

    @FXML
    private TextField answerTextField;
    @FXML
    private Button okButton;

    private void setAnswer() {
        CatchMind.socket.send("5" + CatchMind.REGEX +
                CatchMind.nickName + CatchMind.REGEX +
                CatchMind.port + CatchMind.REGEX + this.answerTextField.getText().trim());
        CatchMind.ans.setText("answer: " + this.answerTextField.getText().trim());
        CatchMind.btn.setText("Game end");
        stage.close();
    }

    @Override
    public void start(Stage _stage) throws ClassNotFoundException, IOException {
        stage = _stage;
        stage.setTitle("Make a question");
        stage.setScene(new Scene(FXMLLoader.load(Class.forName("CatchMind.Answer").getResource("answer_dialog.fxml"))));
        stage.setResizable(false);
        stage.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        stage.setOnCloseRequest(event -> {
            if (CatchMind.isStarted)
                CatchMind.socket.send("6");
            CatchMind.isStarted = false;
        });

        this.answerTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) setAnswer();
        });
        this.okButton.setOnAction(event -> setAnswer());
    }
}
