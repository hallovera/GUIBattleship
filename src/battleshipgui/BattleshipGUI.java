package battleshipgui;

import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.Random;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.geometry.Insets;
import javafx.event.EventHandler;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class BattleshipGUI extends Application {

    public static void checkIfSunk(char[][] board, char hitShip, String initialText, Label messageText) {
        String[] shipNames = {"Aircraft Carrier", "Battleship", "Submarine", "Destroyer", "Patrol Boat"};
        loop:
        while (true) {
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    if (board[i][j] == hitShip) {
                        break loop;
                    }
                }
            }
            messageText.setText(messageText.getText() + "\n" + initialText + shipNames["CBSDP".indexOf(hitShip)] + "!");
            break loop;
        }
    }

    public static void winCheck(char[][] board, Button[][] buttonBoard, String player, Button[][] enemyBoard) {
        loop:
        while (true) {
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    if (board[i][j] != '*' && board[i][j] != 'M' && board[i][j] != 'H') {
                        break loop;
                    }
                }
            }
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    buttonBoard[i][j].setDisable(true);
                    enemyBoard[i][j].setDisable(true);
                }
            }
            Alert winMessage = new Alert(AlertType.INFORMATION);
            winMessage.setTitle("Message");
            winMessage.setHeaderText(null);
            winMessage.setContentText("The " + player + " has won the game!");
            winMessage.showAndWait();
            break loop;
        }
    }

    public static void loadFile(char[][] board, File boardFile) {
        try {
            Scanner file = new Scanner(boardFile);
            String nextLine;

            for (int i = 0; file.hasNextLine(); i++) {
                nextLine = file.nextLine();
                board[i] = nextLine.replace(" ", "").toCharArray();
            }
        } catch (FileNotFoundException exception) {
            System.out.println("Error opening file");
        }
    }

    public static void printBoards(GridPane grid, Button[][] buttonBoard, char[][] charBoard, boolean filesImported, boolean isCPU) {
        for (int i = 0; i < buttonBoard.length; i++) {
            for (int j = 0; j < buttonBoard[i].length; j++) {
                if (filesImported) {
                    buttonBoard[i][j].setStyle("-fx-font-weight: bold; background-size:100% 100%; -fx-background-radius: 0; -fx-stroke: blue");
                    buttonBoard[i][j].setFont(new Font("Arial", 20));
                    buttonBoard[i][j].setDisable(false);
                    if (isCPU) {
                        buttonBoard[i][j].setTextFill(Color.RED);
                    } else {
                        buttonBoard[i][j].setTextFill(Color.BLUE);
                    }
                    if (isCPU && charBoard[i][j] != 'H' && charBoard[i][j] != 'M' && charBoard[i][j] != '*') {
                        buttonBoard[i][j].setText("*");
                    } else {
                        buttonBoard[i][j].setText(String.valueOf(charBoard[i][j]));
                    }
                } else {
                    buttonBoard[i][j] = new Button();
                    buttonBoard[i][j].setMinSize(50, 50);
                    buttonBoard[i][j].setMaxSize(50, 50);
                    buttonBoard[i][j].setStyle("-fx-background-radius: 0");
                    buttonBoard[i][j].setDisable(true);
                    grid.add(buttonBoard[i][j], j, i);
                }
            }
        }
    }

    public static void createImage(String url, String style, int translateAmount, int col, int row, GridPane mainPane) {
        Label image = new Label();
        image.setGraphic(new ImageView(new Image(url)));
        image.setStyle(style);
        image.setTranslateX(translateAmount);
        mainPane.add(image, col, row);
    }

    public static void createMessageBox(VBox messages, StackPane outerLayer, Rectangle outline, Label heading, Label message, Color color) {
        heading.setFont(new Font("Arial", 24));
        heading.setStyle("-fx-background-color: #F4F4F4; -fx-font-weight: bold");
        message.setFont(new Font("Arial", 28));
        message.setStyle("-fx-background-color: #FFFFFF");
        outline.setFill(Color.TRANSPARENT);
        outline.setStyle("-fx-stroke: black; -fx-stroke-width: 1;");
        heading.setTextFill(color);
        message.setTextFill(color);

        outerLayer.getChildren().add(outline);
        outerLayer.getChildren().add(heading);
        outerLayer.getChildren().add(message);
        outerLayer.setAlignment(heading, Pos.TOP_LEFT);
        outerLayer.setAlignment(message, Pos.CENTER_LEFT);
        heading.setTranslateX(35);
        heading.setTranslateY(-15);
        message.setTranslateX(35);
        messages.getChildren().add(outerLayer);
    }

    public static void changeButton(Label message, Button[][] board, char[][] charBoard, int row, int col, String url, char hitOrMiss, String hitMessage) {
        board[row][col].setText("");
        ImageView img = new ImageView(new Image(url));
        img.setFitHeight(48);
        img.setFitWidth(48);
        img.setPreserveRatio(true);
        board[row][col].setGraphic(img);
        charBoard[row][col] = hitOrMiss;
        message.setText(hitMessage);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        GridPane mainPane = new GridPane();
        Scene scene = new Scene(mainPane);
        HBox playerSide = new HBox(15);
        HBox cpuSide = new HBox(15);
        GridPane playerGrid = new GridPane();
        GridPane cpuGrid = new GridPane();
        VBox messages = new VBox(20);
        StackPane playerMessages = new StackPane();
        StackPane cpuMessages = new StackPane();
        char[][] player = new char[10][10];
        char[][] cpu = new char[10][10];
        Button[][] boardCPU = new Button[10][10];
        Button[][] boardPlayer = new Button[10][10];
        Random randomNumber = new Random();
        Label letters1 = new Label();
        Label letters2 = new Label();
        Rectangle outline = new Rectangle(1150, 100);
        Rectangle outlineCPU = new Rectangle(1150, 100);
        Label heading = new Label("Player Messages");
        Label message = new Label("Please open the PLAYER.txt file! (File > Open)");
        Label headingCPU = new Label("CPU Messages");
        Label messageCPU = new Label("Please open the CPU.txt file! (File > Open)");
        String[] shipNames = {"Aircraft Carrier", "Battleship", "Submarine", "Destroyer", "Patrol Boat"};
        Alert error = new Alert(AlertType.ERROR);
        error.setHeaderText(null);

        playerSide.setPadding(new Insets(10));
        cpuSide.setPadding(new Insets(10));
        messages.setPadding(new Insets(20, 5, 10, 5));
        messages.setStyle("-fx-border-color: black; -fx-border-insets: 0 10 10 10; -fx-border-width: 1");
        playerSide.setStyle("-fx-border-color: black; -fx-border-insets: 5 0 0 10; -fx-border-width: 1");
        cpuSide.setStyle("-fx-border-color: black; -fx-border-insets: 5 10 0 0; -fx-border-width: 1");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Text Files", "*.txt"));

        // Creating options menu bar
        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
        Menu file = new Menu("_File");
        MenuItem open = new MenuItem("Open");
        MenuItem restart = new MenuItem("Restart Game");
        MenuItem exit = new MenuItem("Exit");
        open.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));     // Keyboard shortcuts for menu items
        restart.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
        exit.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN));
        file.getItems().addAll(open, restart, new SeparatorMenuItem(), exit);
        menuBar.getMenus().add(file);
        menuBar.setStyle("-fx-padding: 0 0 10 0; -fx-background-color: #F4F4F4");

        letters1.setGraphic(new ImageView(new Image("Images/rows.png")));
        letters2.setGraphic(new ImageView(new Image("Images/rows.png")));
        createImage("Images/cols.png", "-fx-border-insets: 10 0 0 0", 85, 0, 1, mainPane);
        createImage("Images/cols.png", "-fx-border-insets: 10 0 0 0", 75, 1, 1, mainPane);
        printBoards(playerGrid, boardPlayer, null, false, false);
        printBoards(cpuGrid, boardCPU, null, false, true);
        createMessageBox(messages, playerMessages, outline, heading, message, Color.BLUE);
        createMessageBox(messages, cpuMessages, outlineCPU, headingCPU, messageCPU, Color.RED);

        playerSide.getChildren().add(letters1);
        playerSide.getChildren().add(playerGrid);
        cpuSide.getChildren().add(letters2);
        cpuSide.getChildren().add(cpuGrid);
        mainPane.add(menuBar, 0, 0, 2, 1);
        mainPane.add(playerSide, 0, 2, 1, 1);
        mainPane.add(cpuSide, 1, 2, 1, 1);
        mainPane.add(messages, 0, 3, 2, 1);

        primaryStage.setResizable(false);
        primaryStage.setTitle("Battleship GUI");
        primaryStage.setScene(scene);
        primaryStage.show();

        exit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                primaryStage.close();
            }
        });

        restart.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                for (int i = 0; i < boardCPU.length; i++) {
                    for (int j = 0; j < boardCPU[i].length; j++) {
                        boardCPU[i][j].setGraphic(null);
                        boardPlayer[i][j].setGraphic(null);
                        cpu[i][j] = '\u0000';
                        player[i][j] = '\u0000';
                        boardPlayer[i][j].setText("");
                        boardCPU[i][j].setText("");
                        boardCPU[i][j].setDisable(true);
                        boardPlayer[i][j].setDisable(true);
                    }
                }
                message.setText("Please open the PLAYER.txt file! (File > Open)");
                messageCPU.setText("Please open the CPU.txt file! (File > Open)");
            }
        });

        open.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if (player[1][1] == '\u0000' || cpu[1][1] == '\u0000') {
                    File selectedFile = fileChooser.showOpenDialog(primaryStage);
                    if (selectedFile != null) {
                        if (selectedFile.getName().equals("PLAYER.txt")) {
                            message.setText("File imported successfully!");
                            loadFile(player, selectedFile);
                            printBoards(playerGrid, boardPlayer, player, true, false);
                        } else if (selectedFile.getName().equals("CPU.txt")) {
                            messageCPU.setText("File imported successfully!");
                            loadFile(cpu, selectedFile);
                            printBoards(cpuGrid, boardCPU, cpu, true, true);
                        } else {
                            error.setTitle("Wrong File!");
                            error.setContentText("This is an incompatible file. Please open only 'CPU.txt' or 'PLAYER.txt'.");
                            error.showAndWait();
                        }
                    }
                } else {
                    error.setTitle("Files Already Imported!");
                    error.setContentText("All necessary files have already been imported. If you want to restart, go to File > Restart Game.");
                    error.showAndWait();
                }
            }
        });

        for (int i = 0; i < boardCPU.length; i++) {
            int row = i;
            for (int j = 0; j < boardCPU[i].length; j++) {
                int col = j;
                boardCPU[row][col].setOnAction((ActionEvent e) -> {
                    if (cpu[1][1] != '\u0000' && player[1][1] != '\u0000' && cpu[row][col] != 'H' && cpu[row][col] != 'M') {
                        char letterAtHitSpot = '\u0000';
                        if (cpu[row][col] != 'H' && cpu[row][col] != 'M' && cpu[row][col] != '*') {
                            letterAtHitSpot = cpu[row][col];
                            changeButton(message, boardCPU, cpu, row, col, "Images/H.png", 'H', "Direct hit, congratulations!");
                            checkIfSunk(cpu, letterAtHitSpot, "You've sunk the computer's ", message);
                        } else if (cpu[row][col] == '*') {
                            changeButton(message, boardCPU, cpu, row, col, "Images/M.png", 'M', "You missed!");
                        }
                        winCheck(cpu, boardCPU, "player", boardPlayer);

                        loopCPU:
                        while (boardCPU[1][1].isDisable() != true) {
                            String input = String.valueOf("ABCDEFGHIJ".charAt(randomNumber.nextInt(10))) + randomNumber.nextInt(10);
                            if (player[input.charAt(0) - 'A'][Integer.valueOf(input.substring(1, 2))] == 'H' || player[input.charAt(0) - 'A'][Integer.valueOf(input.substring(1, 2))] == 'M') {
                                continue loopCPU;
                            } else if (player[input.charAt(0) - 'A'][Integer.valueOf(input.substring(1, 2))] != 'H' && player[input.charAt(0) - 'A'][Integer.valueOf(input.substring(1, 2))] != 'M' && player[input.charAt(0) - 'A'][Integer.valueOf(input.substring(1, 2))] != '*') {
                                letterAtHitSpot = player[input.charAt(0) - 'A'][Integer.valueOf(input.substring(1, 2))];
                                changeButton(messageCPU, boardPlayer, player, input.charAt(0) - 'A', Integer.valueOf(input.substring(1, 2)), "Images/H.png", 'H', ("The computer has attacked " + input + " and hit your " + shipNames["CBSDP".indexOf(player[input.charAt(0) - 'A'][Integer.parseInt(input.substring(1, 2))])] + "!"));
                                checkIfSunk(player, letterAtHitSpot, "The computer has sunk your ", messageCPU);
                                break loopCPU;
                            } else if (player[input.charAt(0) - 'A'][Integer.valueOf(input.substring(1, 2))] == '*') {
                                changeButton(messageCPU, boardPlayer, player, input.charAt(0) - 'A', Integer.valueOf(input.substring(1, 2)), "Images/M.png", 'M', ("The computer has attacked " + input + " and missed!"));
                                break loopCPU;
                            }
                        }
                        winCheck(player, boardPlayer, "computer", boardCPU);
                    }
                });
            }
        }
    }
}
