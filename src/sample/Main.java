package sample;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main extends Application {
    private static final String[][] template = {
            {"C", "x^y", "√x", "÷"},
            {"7", "8", "9", "×"},
            {"4", "5", "6", "-"},
            {"1", "2", "3", "+"},
            {"<<", "0", ".", "="}
    };


    private static String mainStr = "";
    private String highStr = "";
    private int counter = 0;


    private double firstNum = 0;
    private boolean firstDoindFlag = true;

    DecimalFormat decimalFormat = new DecimalFormat("#.#########");

    private final Map<String, Button> accelerators = new HashMap<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        final TextField secondary_Screen = createScreen(14);
        final TextField main_Screen = createScreen(24);
        final TilePane buttons = createButtons(main_Screen, secondary_Screen);

        ArrayList<String> doing = new ArrayList<String>();
        doing.add("+");
        doing.add("-");
        doing.add("*");
        doing.add("/");

        Scene scene = new Scene(createLayout(secondary_Screen, main_Screen, buttons));

        buttons.requestFocus();
        scene.setOnKeyPressed(event -> {
            String key = event.getText();
            if (key.equals("=")) {
                equalDoing(main_Screen, secondary_Screen);
            } else if (event.getCode() == KeyCode.BACK_SPACE) {
                eraseDoing(main_Screen);
            } else if (key.matches("[0-9]")) {

                if (lenCheck()) {
                    if (!mainStr.equals("0")) {
                        mainStr += key;
                        main_Screen.setText(mainStr);
                    }
                }
            } else if (doing.contains(key)) {
                if (key.equals("*")) key = "×";
                if (key.equals("/")) key = "÷";
                firstAction(key, main_Screen, secondary_Screen);
            } else if (key.equals(".") || key.equals(",")) {
                if (key.equals(",")) key = ".";
                pointDoing(main_Screen);
            }
        });

        stage.setTitle("calculator");
        stage.setHeight(497);
        stage.setWidth(336);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private VBox createLayout(TextField secondary_Screen, TextField main_Screen, TilePane buttons) {
        final VBox layout = new VBox(20);
        layout.setPadding(new Insets(4, 4, 4, 4));
        layout.setStyle("-fx-background-color: #E0E0E0;");
        Label lbl = new Label("");
        Label lbl2 = new Label("");
        layout.getChildren().setAll(lbl, lbl2, secondary_Screen, main_Screen, buttons);
        main_Screen.prefWidthProperty().bind(buttons.widthProperty());
        return layout;
    }

    private TextField createScreen(int Size) {
        final TextField main_textField = new TextField();
        main_textField.setAlignment(Pos.CENTER_RIGHT);
        main_textField.setStyle("-fx-background-color: #E0E0E0;");
        main_textField.setFont(Font.font("Arial", FontWeight.BOLD, Size));
        main_textField.setEditable(false);
        return main_textField;
    }

    private TilePane createButtons(TextField main_Screen, TextField secondary_Screen) {
        TilePane buttons = new TilePane();
        buttons.setVgap(4);
        buttons.setHgap(4);
        buttons.setPrefColumns(template[0].length);
        for (String[] r : template) {
            for (String s : r) {
                buttons.getChildren().add(createButton(s, main_Screen, secondary_Screen));
            }
        }
        return buttons;
    }

    private Button createButton(final String s, TextField main_Screen, TextField secondary_Screen) {
        Button button = makeStandardButton(s);

        if (s.matches("[0-9]")) {
            makeNumericButton(button, main_Screen);
        } else {
            if (s.equals("+") || s.equals("×") || s.equals("÷") || s.equals("x^y")) {
                makeOperandButton(button, main_Screen, secondary_Screen);
            }
            switch (s) {
                case "C":
                    CButton(button, main_Screen, secondary_Screen);
                    break;
                case "=":
                    equalButton(button, main_Screen, secondary_Screen);
                    break;
                case "<<":
                    eraseBotton(button, main_Screen);
                    break;
                case "√x":
                    sqrtButton(button, main_Screen, secondary_Screen);
                    break;
                case "-":
                    subtractionButton(button, main_Screen, secondary_Screen);
                    break;
                case ".":
                    pointButton(button, main_Screen);
                    break;
            }
        }
        return button;
    }

    private void makeOperandButton(Button button, TextField main_Screen, TextField secondary_Screen) {
        button.setStyle("-fx-background-color: #EBEBEB;-fx-font-size:18; "); //-fx-faint-focus-color: #0B0B0B;
        button.setOnAction(event -> firstAction(button.getText(), main_Screen, secondary_Screen));
    }

    private void firstAction(String action, TextField main_Screen, TextField secondary_Screen) {
        if (counter > 0) {
            if (action.equals("x^y")) action = "^";
            if (firstDoindFlag) {
                counter = 0;
                firstNum = Double.parseDouble(mainStr);
                highStr = mainStr + " " + action;
                secondary_Screen.setText(highStr);
                main_Screen.clear();
                mainStr = "";
                firstDoindFlag = false;
            } else {
                secondAction(main_Screen);
                highStr = commaKiller(decimalFormat.format(firstNum)) + " " + action;
                secondary_Screen.setText(highStr);
            }
        }
    }

    private String commaKiller(String str) {
        if (str.contains(",")) {
            str = str.replace(",", ".");
        }
        return str;
    }

    private void secondAction(TextField main_Screen) {
        char Achar = highStr.charAt(highStr.length() - 1);
        boolean flagArr = false;
        double secondNum = Double.parseDouble(mainStr);
        if (Achar == '+') {
            firstNum = firstNum + secondNum;
        } else if (Achar == '-') {
            firstNum = firstNum - secondNum;
        } else if (Achar == '÷') {
            if (secondNum == 0) {
                main_Screen.setText("Деление на 0!");
                flagArr = true;
            } else {
                firstNum = firstNum / secondNum;
            }

        } else if (Achar == '×') {
            firstNum = firstNum * secondNum;
        } else if (Achar == '^') {
            if (firstNum < 0 && secondNum > 0 && secondNum < 1) {
                main_Screen.setText("Корень от отриц. ч.!");
                flagArr = true;
            } else {
                firstNum = Math.pow(firstNum, secondNum);
            }

        }
        if (!flagArr)
            main_Screen.clear();
        mainStr = "";
        counter = 0;
    }

    private Button makeStandardButton(String s) {
        Button button = new Button(s);
        button.setPrefHeight(50);
        button.setPrefWidth(75);
        accelerators.put(s, button);
        return button;
    }

    private void makeNumericButton(Button button, TextField main_Screen) {
        button.setStyle("-fx-background-color: #FAFAFA;-fx-font-size:18;");
        button.setOnAction(event -> {
            if (lenCheck()) {
                if (!mainStr.equals("0")) {
                    mainStr += button.getText();
                    main_Screen.setText(mainStr);
                }
            }
        });
    }

    private boolean lenCheck() {
        if (counter < 15) {
            if (counter == 1 && mainStr.equals("0")) {
                counter = 0;
                mainStr = "";
            }
            counter++;
            return true;
        }
        return false;
    }

    private void sqrtButton(Button button, TextField main_Screen, TextField secondary_Screen) {
        button.setStyle("-fx-background-color: #EBEBEB;-fx-font-size:18;");
        button.setOnAction(event -> {
            if (counter > 0) {
                if (firstDoindFlag) {
                    firstNum = Double.parseDouble(mainStr);
                    highStr = "√" + mainStr;
                    secondary_Screen.setText(highStr);
                    if (firstNum >= 0) {
                        mainStr = commaKiller(decimalFormat.format(Math.sqrt(firstNum)));
                        main_Screen.setText(mainStr);
                        counter = mainStr.length();
                    } else {
                        main_Screen.setText("Корень от отриц. ч.!");
                        mainStr = "";
                        counter = 0;
                    }
                } else {
                    secondAction(main_Screen);
                    highStr = "√" + commaKiller(decimalFormat.format(firstNum));
                    secondary_Screen.setText(highStr);
                    if (firstNum >= 0) {
                        mainStr = commaKiller(decimalFormat.format(Math.sqrt(firstNum)));
                        main_Screen.setText(mainStr);
                        counter = mainStr.length();
                    } else {
                        main_Screen.setText("Корень от отриц. ч.!");
                        mainStr = "";
                        counter = 0;
                    }
                }
                firstDoindFlag = true;
            }
        });
    }

    private void equalButton(Button button, TextField main_Screen, TextField secondary_Screen) {
        button.setStyle("-fx-background-color: #80bce7;-fx-font-size:18;");
        button.setOnAction(event -> equalDoing(main_Screen, secondary_Screen));
    }

    private void equalDoing(TextField main_Screen, TextField secondary_Screen) {

        if (counter > 0 && !firstDoindFlag) {
            boolean arr = false;
            char Achar = highStr.charAt(highStr.length() - 1);
            if (Achar == '+')
                firstNum = firstNum + Double.parseDouble(mainStr);
            else if (Achar == '-')
                firstNum = firstNum - Double.parseDouble(mainStr);
            else if (Achar == '÷') {
                double secondNum = Double.parseDouble(mainStr);
                if (secondNum == 0) {
                    main_Screen.setText("Деление на 0!");
                    mainStr = "";
                    counter = 0;
                    arr = true;
                } else
                    firstNum = firstNum / secondNum;
            } else if (Achar == '×')
                firstNum = firstNum * Double.parseDouble(mainStr);
            else if (Achar == '^') {
                double secondNum = Double.parseDouble(mainStr);
                if (firstNum < 0 && secondNum != 0 && secondNum < 1 && secondNum > -1) {
                    main_Screen.setText("Корень от отриц. ч.!");
                    counter = 0;
                    arr = true;
                } else {
                    firstNum = Math.pow(firstNum, secondNum);
                }
            }
            if (!arr) {
                secondary_Screen.setText(highStr + " " + mainStr + " =");
                mainStr = commaKiller(decimalFormat.format(firstNum));
                main_Screen.setText(mainStr);

                firstDoindFlag = true;
                counter = mainStr.length();
            }

        }
    }

    private void subtractionButton(Button button, TextField main_Screen, TextField secondary_Screen) {
        button.setStyle("-fx-background-color: #EBEBEB;-fx-font-size:18;");
        button.setOnAction(event -> {
            if (counter == 0) {
                mainStr = "-";
                main_Screen.setText("-");
            } else
                firstAction(" -", main_Screen, secondary_Screen);
        });
    }

    private void eraseBotton(Button button, TextField main_Screen) {
        button.setStyle("-fx-background-color:  #FAFAFA;-fx-font-size:18;");
        button.setOnAction(event -> eraseDoing(main_Screen));
    }

    private void eraseDoing(TextField main_Screen) {
        if (counter > 0) {
            if (mainStr.charAt(mainStr.length() - 1) != '.')
                counter--;
            mainStr = main_Screen.getText().substring(0, main_Screen.getLength() - 1);
            main_Screen.setText(mainStr);
        } else if (mainStr.length() > 0 && mainStr.substring(0, 1).equals("-")) {
            mainStr = "";
            main_Screen.setText(mainStr);
        }
    }

    private void CButton(Button button, TextField main_Screen, TextField secondary_Screen) {
        button.setStyle("-fx-background-color: #EBEBEB;-fx-font-size:18;");
        button.setOnAction(event -> {
            mainStr = "";
            highStr = "";
            counter = 0;
            main_Screen.clear();
            secondary_Screen.clear();
            firstNum = 0;
            firstDoindFlag = true;
        });
    }

    private void pointButton(Button button, TextField main_Screen) {
        button.setStyle("-fx-background-color:  #FAFAFA;-fx-font-size:18;");
        button.setOnAction(event -> {
            pointDoing(main_Screen);
        });
    }

    private void pointDoing(TextField main_Screen) {
        if (mainStr.length() > 0 && !mainStr.contains(".") && counter != 0) {
            mainStr += ".";
            main_Screen.setText(mainStr);
        }
    }
}
