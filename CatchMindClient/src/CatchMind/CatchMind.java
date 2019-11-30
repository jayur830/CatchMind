package CatchMind;

import SendSocket.DataSendSocket;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CatchMind extends Application implements Initializable {

    private static Stage stage;

    @FXML
    private Canvas drawArea;
    @FXML
    static Label ans;
    @FXML
    private Label answerLabel;
    @FXML
    private TableView<ClientUserData> userTable;
    @FXML
    private TableColumn<ClientUserData, String> portColumn, nickNameColumn;
    @FXML
    private TextArea chatLog;
    @FXML
    private TextField chatTextField;
    @FXML
    private ChoiceBox<Object> colorChoiceBox;
    @FXML
    static Button btn;
    @FXML
    private Button gameActionButton, eraseButton, clearButton, sendButton;
    @FXML
    private Slider lineWidth;

    private ObservableList<ClientUserData> list = FXCollections.observableArrayList();
    private GraphicsContext context;

    static String port;
    public static String nickName;

    private static Stage stg = new Stage(StageStyle.DECORATED);

    static DataSendSocket socket;
    public static final String REGEX = "@%#";
    public static final String SERVERIP = "127.0.0.1";
    public static final int TCPSERVERPORT = 32768;

    private String answer = null;

    private double preX = -1.0, preY = -1.0;
    private Color stroke;

    static boolean isStarted = false;
    private boolean isStop = false, isQuestioner = false;

    private void sendDrawingLoc(MouseEvent event, boolean isDrawing) {
        double x = event.getX(), y = event.getY();
        Color stroke = (Color) this.context.getStroke();
        String colorData = null;
        if (stroke == Color.BLACK) colorData = "BLACK";
        else if (stroke == Color.RED) colorData = "RED";
        else if (stroke == Color.GREEN) colorData = "GREEN";
        else if (stroke == Color.BLUE) colorData = "BLUE";
        else if (stroke == Color.WHITE) colorData = "WHITE";
        socket.send("3" + REGEX + x + REGEX + y + REGEX +
                this.context.getLineWidth() + REGEX + colorData + REGEX + isDrawing + REGEX);
    }

    private void chatting() {
        String text = this.chatTextField.getText();
        if (text.contains(REGEX)) {
            StringBuilder s = new StringBuilder(text);
            s.replace(s.indexOf(REGEX), s.indexOf(REGEX) + 3, "@@%%##");
            text = s.toString();
        }

        if (isStarted) {
            if (this.isQuestioner) {
                if (text.equals(this.answer)) {
                    this.chatLog.appendText("[Warning] The questioner can't say the answer.\n");
                    this.chatTextField.clear();
                } else {
                    socket.send("2" + REGEX + nickName + REGEX + port + REGEX + text);
                    this.chatTextField.clear();
                }
            } else {
                if (text.equals(this.answer)) {
                    socket.send("7" + REGEX + nickName + REGEX + port + REGEX + text);
                    this.chatTextField.clear();
                } else {
                    socket.send("2" + REGEX + nickName + REGEX + port + REGEX + text);
                    this.chatTextField.clear();
                }
            }
        } else {
            socket.send("2" + REGEX + nickName + REGEX + port + REGEX + text);
            this.chatTextField.clear();
        }
    }

    private void gameSetting(boolean buttonDisable) {
        this.lineWidth.setDisable(buttonDisable);
        this.colorChoiceBox.setDisable(buttonDisable);
        this.eraseButton.setDisable(buttonDisable);
        this.clearButton.setDisable(buttonDisable);
    }

    @Override
    public void start(Stage _stage) throws ClassNotFoundException, IOException {
        stage = _stage;
        stage.setTitle("Let's Catch Mind!");
        stage.setScene(new Scene(FXMLLoader.load(
                Class.forName("CatchMind.CatchMind").getResource("main.fxml"))));
        stage.setResizable(false);
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        socket = new DataSendSocket(SERVERIP, TCPSERVERPORT);
        socket.send("0" + REGEX + nickName + REGEX + socket.getLocalPort());

        stage.setOnCloseRequest(event -> {
            isStop = true;
            socket.send("1" + REGEX + nickName + REGEX + port +
                    (isQuestioner ? REGEX + true + (isStarted ? REGEX + answer : REGEX + null) :
                            REGEX + false + (isStarted ? REGEX + answer : REGEX + null)));
        });

        Thread tcpThread = new Thread(() -> {
            String packet;
            while (!isStop) {
                if ((packet = socket.receive()).isEmpty()) continue;
                String[] data = packet.split(REGEX);
                switch (data[0]) {
                    case "0":
                        list.clear();
                        for (int i = 1; i < data.length; i += 2) {
                            list.add(new ClientUserData(data[i], data[i + 1]));
                            if (data[i].equals(nickName)) port = data[i + 1];
                        }
                        chatLog.appendText("[Client connected:" + data[data.length - 1] +
                                "] nickname: " + data[data.length - 2] + "\n");
                        if (list.get(0).getNickName().get().equals(nickName) &&
                                list.get(0).getPort().get().equals(port)) {
                            isQuestioner = true;
                            if (list.size() != 1) gameActionButton.setDisable(false);
                        }
                        break;
                    case "1":
                        chatLog.appendText("[Client disconnected:" + data[2] + "] nickname: " + data[1] + "\n");
                        for (ClientUserData user : list)
                            if (user.getNickName().get().equals(data[1]) &&
                                    user.getPort().get().equals(data[2])) {
                                list.remove(user);
                                break;
                            }
                        if (data[3].equals("true")) {
                            chatLog.appendText("[Changed questioner] The questioner is " + list.get(0).getNickName().get() + "\n");
                            if (list.get(0).getNickName().get().equals(nickName) &&
                                    list.get(0).getPort().get().equals(port)) {
                                isQuestioner = true;
                                if (!data[4].equals("null")) {
                                    socket.send("4");
                                    Platform.runLater(() -> {
                                        answerLabel.setText("answer: " + (answer = data[4]));
                                        gameActionButton.setText("Game end");
                                    });
                                    gameSetting(false);
                                }
                            }
                        }
                        if (isQuestioner) {
                            if (list.size() != 1) gameActionButton.setDisable(false);
                            else gameActionButton.setDisable(true);
                        }
                        break;
                    case "2":
                        if (data[3].contains("@@%%##")) {
                            StringBuilder s = new StringBuilder().append(data[3]);
                            data[3] = s.replace(s.indexOf("@@%%##"), s.indexOf("@@%%##") + 6, REGEX).toString();
                        }
                        this.chatLog.appendText("[" + data[1] + ":" + data[2] + "] " + data[3] + "\n");
                        this.chatLog.setScrollTop(chatLog.getLength());
                        break;
                    case "3":
                        double x = Double.parseDouble(data[1]),
                                y = Double.parseDouble(data[2]),
                                lineWidth = Double.parseDouble(data[3]);
                        switch (data[4]) {
                            case "BLACK": context.setStroke(Color.BLACK); break;
                            case "RED": context.setStroke(Color.RED); break;
                            case "GREEN": context.setStroke(Color.GREEN); break;
                            case "BLUE": context.setStroke(Color.BLUE); break;
                            case "WHITE": context.setStroke(Color.WHITE); break;
                        }
                        context.setLineWidth(lineWidth);
                        if (preX == -1.0 && preY == -1.0)
                            context.strokeLine(preX = x, preY = y, x, y);
                        else {
                            context.strokeLine(preX, preY, x, y);
                            preX = x; preY = y;
                        }
                        if (data[5].equals("false")) {
                            preX = -1.0; preY = -1.0;
                        }
                        break;
                    case "4":
                        context.clearRect(0, 0, drawArea.getWidth(), drawArea.getHeight());
                        break;
                    case "5":
                        this.chatLog.appendText("============= Game started =============\n");
                        this.chatLog.setScrollTop(chatLog.getLength());
                        char[] c = data[3].toCharArray();
                        int begin = 0, end = c.length - 1;
                        for (; end >= 0 && (int) c[end] == 0; --end);
                        answer = data[3].substring(begin, end + 1);
                        isStarted = true;
                        if (data[1].equals(nickName) && data[2].equals(port)) {
                            gameActionButton.setDisable(false);
                            gameSetting(false);
                        } else {
                            gameActionButton.setDisable(true);
                            gameSetting(true);
                        }
                        break;
                    case "6":
                        this.chatLog.appendText("============= Game ended =============\n" + data[1]);
                        this.chatLog.appendText("\n");
                        this.chatLog.setScrollTop(chatLog.getLength());
                        answer = null;
                        isStarted = false;
                        gameSetting(true);
                        break;
                    case "7":
                        chatLog.appendText("[Answerer] nickname: " + data[1] + ", answer: " + answer + "\n");
                        answer = null;
                        this.context.clearRect(0, 0, this.drawArea.getWidth(), this.drawArea.getHeight());
                        Platform.runLater(() -> {
                            this.answerLabel.setText("");
                            this.gameActionButton.setText("Game start");
                        });
                        if (this.isQuestioner) {
                            this.isQuestioner = false;
                            this.gameActionButton.setDisable(true);
                            gameSetting(true);
                        }
                        if (data[1].equals(nickName) && data[2].equals(port)) {
                            this.isQuestioner = true;
                            this.gameActionButton.setDisable(false);
                            Platform.runLater(() -> {
                                try {
                                    new Answer().start(stg);
                                } catch (ClassNotFoundException | IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                        break;
                }
            }
        });
        tcpThread.setDaemon(true);
        tcpThread.start();

        this.portColumn.setCellValueFactory(cell -> cell.getValue().getPort());
        this.nickNameColumn.setCellValueFactory(cell -> cell.getValue().getNickName());
        this.userTable.setItems(this.list);

        this.context = drawArea.getGraphicsContext2D();
        this.context.setStroke(Color.BLACK);
        this.context.setLineCap(StrokeLineCap.ROUND);
        this.context.setLineWidth(5.0);
        this.stroke = (Color) this.context.getStroke();

        ObservableList<Object> items = FXCollections.observableArrayList();
        items.add("Black"); items.add("Red"); items.add("Green"); items.add("Blue");
        this.colorChoiceBox.setItems(items);

        this.lineWidth.setOnMouseDragged(event -> context.setLineWidth((lineWidth.getValue() * 0.12) + 3.0));
        this.drawArea.setOnMousePressed(event -> {
            if (isStarted && isQuestioner) sendDrawingLoc(event, true);
        });
        this.drawArea.setOnMouseDragged(event -> {
            if (isStarted && isQuestioner) sendDrawingLoc(event, true);
        });
        this.drawArea.setOnMouseReleased(event -> {
            if (isStarted && isQuestioner) sendDrawingLoc(event, false);
        });

        ans = this.answerLabel;
        btn = this.gameActionButton;
        this.gameActionButton.setOnAction(event -> {
            if (!isStarted) {
                try {
                    new Answer().start(stg);
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }
            } else {
                socket.send("6");
                socket.send("4");
                gameActionButton.setText("Game start");
                answerLabel.setText("");
            }
        });
        this.clearButton.setOnAction(event -> socket.send("4"));
        this.colorChoiceBox.setOnAction(event -> {
            String color = colorChoiceBox.getValue().toString();
            switch (color) {
                case "Black": context.setStroke(stroke = Color.BLACK); break;
                case "Red": context.setStroke(stroke = Color.RED); break;
                case "Green": context.setStroke(stroke = Color.GREEN); break;
                case "Blue": context.setStroke(stroke = Color.BLUE); break;
            }
        });
        this.eraseButton.setOnAction(event -> context.setStroke(stroke = Color.WHITE));

        this.chatTextField.setOnKeyPressed(event -> {
            if (!chatTextField.getText().isEmpty() && event.getCode() == KeyCode.ENTER) chatting();
        });
        this.sendButton.setOnKeyPressed(event -> {
            if (!chatTextField.getText().isEmpty() && event.getCode() == KeyCode.ENTER) chatting();
        });
        this.sendButton.setOnAction(event -> {
            if (!chatTextField.getText().isEmpty()) chatting();
        });
    }
}
