package Snake;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

//FXML
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class SnakeController {
    @FXML private Button resume;
    @FXML private GridPane grid;
    @FXML private Label highscore;
    @FXML private Label score;
    @FXML private Label length;
    @FXML private Label speed;
    @FXML private Label errortext;
    @FXML private Label gameover;
    @FXML private Label gamepaused;
    @FXML private Label snake_text;
    @FXML private Label use_text;
    @FXML private Label to_turn_text;
    @FXML private Label press_text;
    @FXML private Label to_pause_text;
    @FXML private Pane letterW;
    @FXML private Pane letterA;
    @FXML private Pane letterS;
    @FXML private Pane letterD;
    @FXML private Pane letterP;
    @FXML private GridPane scoreboard;
    @FXML private Button startgame;
    @FXML private Button statsbutton;
    @FXML private Button playagain;
    @FXML private Button quitgame;
    @FXML private Button pausegame;
    
    private SnakeGame snakegame;
    private Snake snake;
    private Apple apple;
    private File file;
    private boolean StartMenuPassed = false;
    private boolean startup_approved = false;
    private boolean first_startup = true;
    private boolean existing_name = false;
    private boolean name_field_passed = false;
    private String playername;  
    private List<String> stats_from_file = new ArrayList<>();
    private List<Pane> bodypanes = new ArrayList<>();
    private ScoreBoardHandler scoreboardhandler = new ScoreBoardHandler();
    private int delay = 0;
    

    @FXML
    public void initialize() {
        HideOverlayElements();

        if (first_startup == true) {
            snakegame = new SnakeGame(this);
            snakegame.setFileAndRead();
            file = snakegame.getFilehandler().getFile();
            stats_from_file = snakegame.getStatsFromFile();
        }
        
        snakegame.generateSnakeAndApple();
        snake = snakegame.getSnake();
        apple = snakegame.getApple();

        try {
            if (startup_approved == false) {
                show_stats();
                if (name_field_passed == false) {
                    if (ShowNameInputField() == false) {
                        first_startup = false;
                        initialize();
                    }
                    else {
                        first_startup = false;
                        name_field_passed = true;
                        initialize();
                        scoreboardhandler.UpdateScoreBoard(scoreboard, stats_from_file, playername, snakegame);
                    }
                }
                else {
                    if (StartMenuPassed == false) {
                        ShowStartMenu();
                    }
                    else {
                        startup_approved = true;
                        start();
                        start_loop();
                    }
                }
            }
            else {
                start();
            }
        }
        catch (Exception e) { 
            System.out.println("Du må skrive inn et navn for å spille!");
            Platform.exit();
        }
    }
    
    public void start() {
        snake.generate_snake();
        snake.generateApple();
        this.apple = snake.getApple();
        drawgame();
        show_stats();
    }




    public void start_loop() {
        
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                if (snakegame.getGamePaused() == true) {

                } 
                if (delay == 0) {
                    snake.move();
                }
                if (delay > 0) {
                    delay -= 1;
                }
                
                
                Platform.runLater(new Runnable() {
                    @Override public void run() {
                        try {
                            if (snakegame.getGameStopped() == false && snakegame.getGamePaused() == false) {
                                drawgame();
                                String gamestatus = snakegame.update();
                                if (gamestatus.equals("RESTART")) {
                                    timer.cancel();
                                    initialize();
                                    start_loop();
                                }
                                else if (gamestatus.equals("APPLEEATEN")) {
                                    draw_snake(snake);
                                    timer.cancel();
                                    show_stats();
                                    scoreboardhandler.UpdateScoreBoard(scoreboard, stats_from_file, playername, snakegame);
                                    snakegame.getFilehandler().WriteToFile(file, stats_from_file);
                                    start_loop();
                                    delay += 1;
                                    
                                }
                                else if (gamestatus.equals("COLLISION")) {
                                    timer.cancel();
                                    initialize();
                                    start_loop();
                                }
                            }
                            else if (snakegame.getGamePaused() == true) {
                                ShowPauseMenu();
                                timer.cancel();
                                delay += 1;
                            }
                            
                            else {
                                ShowGameOverMenu();
                            }
                        }
                        catch (Exception e) {
                            snakegame.setGameStopped(true);
                            initialize();
                            snakegame.setLoopdelay(75);
                            snakegame.setSpeedvalue(1);
                            timer.cancel();
                            start_loop();
                        }
                    }
                });
                }
        };
        timer.scheduleAtFixedRate(task, 0, snakegame.getLoopdelay());
    }

    private void drawgame() {
        draw_snake(snake);
        draw_apple(apple);
    }

    //==============================================================================================================================================================================
    //Menyer og overlay
    @FXML
    private void HideOverlayElements() {
        snake_text.setVisible(false);
        use_text.setVisible(false);
        to_turn_text.setVisible(false);
        press_text.setVisible(false);
        to_pause_text.setVisible(false);
        letterW.setVisible(false);
        letterA.setVisible(false);
        letterS.setVisible(false);
        letterD.setVisible(false);
        letterP.setVisible(false);
        startgame.setVisible(false);
        statsbutton.setVisible(false);
        errortext.setVisible(false);
        gameover.setVisible(false);
        playagain.setVisible(false);
        quitgame.setVisible(false);
        gamepaused.setVisible(false);
        resume.setVisible(false);
        
    }

    @FXML
    private void ShowStartMenu() {
        snake_text.setVisible(true);
        use_text.setVisible(true);
        to_turn_text.setVisible(true);
        press_text.setVisible(true);
        to_pause_text.setVisible(true);
        letterW.setVisible(true);
        letterA.setVisible(true);
        letterS.setVisible(true);
        letterD.setVisible(true);
        letterP.setVisible(true);
        startgame.setVisible(true);
        statsbutton.setVisible(true);
    }
    
    @FXML
    private void ShowGameOverMenu() {
        gameover.setVisible(true);
        playagain.setVisible(true);
        quitgame.setVisible(true);
    }

    @FXML
    private void ShowPauseMenu() {
        gamepaused.setVisible(true);
        resume.setVisible(true);
    }

    @FXML
    private void handleStartGameClick() {
        StartMenuPassed = true;
        initialize();
        DisplayErrorCode("");
    }

    @FXML
    private void handleStatsButtonClick() {
        DisplayErrorCode("Statistikk blir lagret her: " + file.toString());
    }

    @FXML
    private void handlePlayAgainClick() {
        initialize();
        snakegame.setGameStopped(false);
        grid.requestFocus();
    }

    @FXML
    private void handleQuitGameClick() {
        Platform.exit();
        snakegame.getFilehandler().WriteToFile(file, stats_from_file);
    }

    @FXML
    private void handlePauseClick() {
        if (snakegame.getGameStopped() == false && StartMenuPassed == true) {
            snakegame.setGamePaused(true);
            grid.requestFocus();
        }
        
    }

    @FXML
    private void handleResumeClick() {
        snakegame.setGamePaused(false);
        gamepaused.setVisible(false);
        resume.setVisible(false);
        start_loop();
        grid.requestFocus();
    }

    private void DisplayErrorCode(String errormessage) {
        errortext.setText(errormessage);
        errortext.setVisible(true);
    }

//==============================================================================================================================================================================

    @FXML 
    private boolean ShowNameInputField() {
        TextInputDialog popup = new TextInputDialog();
        if (first_startup == true) {
            popup.setTitle("Registrering");
            popup.setHeaderText("Registrering av navn");
            popup.setContentText("Skriv inn navnet ditt: ");
        }
        else if (existing_name == true && first_startup == false) {
            popup.setTitle("Registrering");
            popup.setHeaderText("Registrering av navn");
            popup.setContentText("Det navnet finnes allerede i statistikken, skriv inn et ekstra kjennemerke: ");
            existing_name = false;
        }
        else {
            popup.setTitle("Registrering");
            popup.setHeaderText("Registrering av navn");
            popup.setContentText("Ugyldig navn, prøv på nytt: ");
        }
        
        String input = popup.showAndWait().get();

        if (ValidNameInput(input) == true && TestExistingName(input) == false) {
            snake.setPlayerName(input);
            this.playername = input;
            popup.close();
            return true;
        }
        else if (ValidNameInput(input) == false && TestExistingName(input) == true) {
            existing_name = true;
            return false;
        }
        else if (ValidNameInput(input) == true && TestExistingName(input) == true) {
            existing_name = true;
            return false;
        }
        else {
            existing_name = false;
            return false;
        }
    }

    public boolean ValidNameInput(String name) {
        return name.matches("^([a-zA-ZæøåÆØÅ]+\s)*[a-zA-ZæøåÆØÅ]+$");
    }

    @FXML
    public void draw_snake(Snake snake) {
        // Tømmer grid-et men beholder rutenettet (som er element med indeks 0 i grid.getChildren()-listen)
        Node node = grid.getChildren().get(0);
        grid.getChildren().clear();
        grid.getChildren().add(0,node);
        
        for (BodyPart bodypart : snake.getSnake_body()) {
            Pane pane = new Pane();
            pane.setStyle("-fx-background-color: green;");
            grid.add(pane, bodypart.getX_Coordinate(), bodypart.getY_Coordinate());
            bodypanes.add(pane);
        }
    }

    @FXML
    public void draw_apple(Apple apple) {
        Pane pane = new Pane();
        pane.setStyle("-fx-background-color: red;");
        grid.add(pane, apple.getX_Coordinate(), apple.getY_Coordinate());
        
    }

    @FXML
    private void show_stats() {
        highscore.setText("Highscore: " + String.valueOf(snakegame.getHighScore()));
        score.setText("Score: " + String.valueOf(snake.getScore()));
        length.setText("Length: " + String.valueOf(snake.getSnake_body().size()));
        if (snakegame.getSpeedvalue() >= 15) {
            speed.setText("MAX SPEED!");
        }
        else {
            speed.setText("Speed: " + String.valueOf(snakegame.getSpeedvalue()));
        }
        
    }

    @FXML
    private void handleKeyPress(KeyEvent event) {
        if (snakegame.getGameStopped() == false) {
            try {
                switch(event.getCode()) {
                    case P:
                        if (snakegame.getGamePaused() == false) {
                            snakegame.setGamePaused(true);
                            grid.requestFocus();
                        }
                        else {
                            snakegame.setGamePaused(false);
                            gamepaused.setVisible(false);
                            resume.setVisible(false);
                            start_loop();
                            grid.requestFocus();
                        }
                    default:
                        break;
                }
                if (snakegame.getGamePaused() == false) {
                    snake.handleInput(event);
                    drawgame();
                }
                
            } catch (IllegalArgumentException e) {
                initialize();        
            }
        }
    }

    private boolean TestExistingName(String name) {
        for (String string : stats_from_file) {
            if (string.split(",")[0].equals(name)) {
                return true;
            }
        }
        return false;
    }

    public String getPlayerName() {
        return playername;
    }

    public void setApple(Apple apple) {
        this.apple = apple;
    }
}


