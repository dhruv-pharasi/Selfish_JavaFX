
/**
 * javafx imports
 */
import javafx.animation.Animation;
import javafx.animation.FillTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * selfish package imports
 */
import selfish.GameEngine;
import selfish.GameException;

/**
 * other imports
 */
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.sound.sampled.*;
import java.awt.Desktop;

/**
 * Game's GUI application class
 */
public class GameApp extends Application {

    /**
     * class instance variables
     */
    public static final String GAMENAME = "Selfish Space Edition";
    public static final String FOLDER_PATH = "saved_games/";
    public static final String TEXTFIELD_CSS = "-fx-border-color: cyan; -fx-border-width: 3px; -fx-background-color: #B5FF00;";
    private int audioButtonClicks2 = 0;
    private Matcher matcher;
    private String toShow = ""; // players names to display on rectangle
    private int count = 0; // for showing player number
    public static ArrayList<String> playerNames = new ArrayList<>();
    private Rectangle2D screenCoords;
    private StartGame sg;
    private AlertBox alertBox;

    /**
     * instantiating StartGame and AlertBox
     * 
     * @throws GameException
     */
    public GameApp() throws GameException {
        this.sg = new StartGame();
        this.alertBox = new AlertBox();
        this.screenCoords = Screen.getPrimary().getVisualBounds();
    }

    /**
     * main method
     * 
     * @param args String[] args
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Overriding Application class's start method
     */
    @Override
    public void start(Stage stage) throws Exception {

        // Exception handling

        Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
            System.out.println("Handler caught exception: " + throwable.getMessage());
        });

        setupStartPage(stage);
    }

    /**
     * Used to set-up game's start page
     * 
     * @param stage parameter
     * @throws UnsupportedAudioFileException exception handling
     * @throws IOException                   exception handling
     * @throws LineUnavailableException      exception handling
     */
    public void setupStartPage(Stage stage)
            throws UnsupportedAudioFileException, IOException, LineUnavailableException {

        // Adding heading to game
        Label gameName = new Label(GAMENAME);
        gameName.setFont(Font.font("Verdana", FontWeight.BOLD, 100));
        gameName.setLayoutX(335);
        gameName.setLayoutY(1);
        gameName.setTextFill(Color.CYAN);

        // Adding background music for start page
        File audioFile = new File("Sound/startPageMusic.wav");
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
        Clip audioClip = AudioSystem.getClip();
        audioClip.open(audioStream);
        audioClip.start();
        audioClip.loop(15);

        // New Game button
        Button newGameB = new Button("New Game");
        newGameB.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        newGameB.setPrefSize(190, 40);
        newGameB.setOnAction(e -> {
            try {
                playButtonSound();
                audioStream.close();
                setupNewGamePage();
            } catch (Exception g) {
                g.printStackTrace();
            }
            audioClip.stop();
            audioClip.close();
            stage.close();
        });

        // Load saved game button
        Button loadGameB = new Button("Load Game");
        loadGameB.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        loadGameB.setPrefSize(190, 40);
        loadGameB.setOnAction(e -> {
            try {
                playButtonSound();
                audioStream.close();
            } catch (Exception exp) {
                exp.printStackTrace();
            }
            loadSavedGame();
            stage.close();
            audioClip.stop();
            audioClip.close();
        });

        // Game Rules button
        Button rulesB = new Button("Game Rules");
        rulesB.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        rulesB.setPrefSize(200, 40);
        rulesB.setOnAction(e -> {
            try {
                playButtonSound();
                Desktop.getDesktop().browse(new URL(
                        "https://thefriendlyboardgamer.wordpress.com/2019/01/16/selfish-space-edition/#:~:text=Selfish%20Space%20is%20a%202,and%20it%20is%20game%20over.")
                        .toURI());
            } catch (Exception exp) {
                exp.printStackTrace();
            }
        });

        // Quit Game button
        Button exitGameB = new Button("Quit Game");
        exitGameB.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        exitGameB.setPrefSize(190, 40);
        exitGameB.setOnAction(e -> {
            try {
                playButtonSound();
                audioStream.close();
            } catch (Exception c) {
                c.printStackTrace();
            }
            audioClip.stop();
            audioClip.close();
            stage.close();
        });

        // Blue astronaut image
        Image imView3 = new Image("GUI_Images/astronaut_blue.png");
        ImageView astBlue = new ImageView(imView3);
        astBlue.setX(10);

        // Blue astronaut animation
        TranslateTransition transitionAstronaut = new TranslateTransition();
        transitionAstronaut.setDuration(Duration.seconds(3));
        transitionAstronaut.setToY(40);
        transitionAstronaut.setAutoReverse(true);
        transitionAstronaut.setCycleCount(Animation.INDEFINITE);
        transitionAstronaut.setNode(astBlue);
        transitionAstronaut.play();

        // Space background image
        Image imView = new Image("GUI_Images/startPageSpace.jpeg");
        ImageView spaceBcg = new ImageView(imView);

        // Spaceship image
        Image imView2 = new Image("GUI_Images/spaceShip.png");
        ImageView spaceShip = new ImageView(imView2);
        spaceShip.setX(0);
        spaceShip.setY(700);

        // Spaceship animation
        TranslateTransition transitionSpaceship = new TranslateTransition();
        transitionSpaceship.setDuration(Duration.seconds(2));
        transitionSpaceship.setToX(1400);
        transitionSpaceship.setNode(spaceShip);
        transitionSpaceship.play();

        // Asteroid image
        Image imView4 = new Image("GUI_Images/mainMenuAsteroid.png");
        ImageView asteroidImg = new ImageView(imView4);
        asteroidImg.setX(30);
        asteroidImg.setY(820);

        // Spaceship sound
        try {
            playSpaceshipSound();
        } catch (Exception x) {
            x.printStackTrace();
        }

        // VBox for holding buttons
        VBox buttonVBox = new VBox(35);
        buttonVBox.setAlignment(Pos.CENTER);
        buttonVBox.getChildren().addAll(newGameB, loadGameB, rulesB, exitGameB);

        // StackPane for holding VBox and space image
        StackPane sp = new StackPane();
        sp.getChildren().addAll(spaceBcg, buttonVBox);

        // Group for root container
        Group root = new Group();
        root.getChildren().addAll(sp, gameName, astBlue, spaceShip, asteroidImg);

        // Setting the scene
        Scene scene = new Scene(root, 1920, 1080);
        scene.getStylesheets().add(getClass().getResource("CSS/menuAndNewGameStyles.css").toExternalForm());

        // Setting the stage
        stage.setScene(scene);
        stage.setTitle(GAMENAME);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Method for setting up the new game
     * Takes in player names
     * 
     * @throws IOException
     * @throws UnsupportedAudioFileException
     * @throws LineUnavailableException
     */
    public void setupNewGamePage() throws UnsupportedAudioFileException, IOException, LineUnavailableException {

        // Adding heading to game
        Label gameName = new Label(GAMENAME);
        gameName.setFont(Font.font("Verdana", FontWeight.BOLD, 100));
        gameName.setLayoutX(335);
        gameName.setLayoutY(1);
        gameName.setTextFill(Color.CYAN);

        // Getting dimensions of screen to center rectangles

        // Creating the stage
        Stage stage = new Stage();

        // Rectangle background for Text and TextField
        Rectangle r1 = new Rectangle(320, 190, Color.BLACK);
        r1.setX((screenCoords.getWidth() - r1.getWidth()) / 2);
        r1.setY((screenCoords.getHeight() - r1.getHeight()) / 2);

        // Rectangle for showing stored player names
        Rectangle r2 = new Rectangle(100, 360, 400, 375);
        r2.setFill(Color.BLACK);

        // Fill Transition animation for r2
        FillTransition ft2 = new FillTransition(Duration.seconds(4), r2, Color.BLACK, Color.rgb(0, 32, 255));
        ft2.setCycleCount(Animation.INDEFINITE);
        ft2.setAutoReverse(true);
        ft2.play();

        // Adding background music for start page
        File audioFile = new File("Sound/startPageMusic.wav");
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
        Clip audioClip = AudioSystem.getClip();
        audioClip.open(audioStream);
        audioClip.start();
        audioClip.loop(40);

        // Mute/Unmute Audio button
        Button stopAudio = new Button("Mute Audio");
        stopAudio.setFont(Font.font("Verdana", 15));
        stopAudio.setPrefSize(130, 40);
        stopAudio.setLayoutX(25);
        stopAudio.setLayoutY(70);
        stopAudio.setOnAction(e -> {
            audioButtonClicks2++;
            try {
                playButtonSound();
            } catch (Exception b) {
                b.printStackTrace();
            }
            if (audioButtonClicks2 % 2 == 0) {
                stopAudio.setText("Mute Audio");
                audioClip.start();
            } else {
                audioClip.stop();
                stopAudio.setText("Unmute Audio");
            }
        });

        // Back button
        Button backB = new Button("Back");
        backB.setFont(Font.font("Verdana", 18));
        backB.setPrefSize(80, 25);
        backB.setLayoutX(50);
        backB.setLayoutY(20);
        backB.setOnAction(e -> {
            try {
                playButtonSound();
                audioClip.stop();
                audioStream.close();
                Stage s = new Stage();
                setupStartPage(s);
                stage.close();
            } catch (Exception b) {
                b.printStackTrace();
            }
            audioClip.close();
        });

        // Text for indicating that player successfully added or insufficient players
        Text playerAdded = new Text();
        playerAdded.setFont(Font.font("Verdana", 18));

        // Text for showing players added
        Text allPlayers = new Text();
        allPlayers.setFont(Font.font("Verdana", FontWeight.NORMAL, 20));
        allPlayers.setFill(Color.RED);
        allPlayers.setX(160);
        allPlayers.setY(410);

        // Reset names button 270
        Button resetB = new Button("Reset");
        resetB.setFont(Font.font("Verdana", 15));
        resetB.setLayoutX(350);
        resetB.setLayoutY(695);
        resetB.setOnAction(e -> {
            try {
                playButtonSound();
            } catch (Exception h) {
                h.printStackTrace();
            }
            count = 0;
            toShow = "";
            allPlayers.setText(toShow);
            playerNames.clear();
        });

        // Remove last button
        Button removeLastB = new Button("Remove Last");
        removeLastB.setFont(Font.font("Verdana", 15));
        removeLastB.setLayoutX(170);
        removeLastB.setLayoutY(695);
        removeLastB.setOnAction(e -> {
            try {
                playButtonSound();
            } catch (Exception w) {
                w.printStackTrace();
            }
            if (count > 0) {
                count--;
                playerNames.remove(playerNames.size() - 1);
                String[] names = toShow.split("\n");
                System.out.println(names.length);
                String[] newNames = new String[names.length - 1];
                toShow = "";
                for (int i = 0; i < newNames.length; i++) {
                    newNames[i] = names[i];
                }
                int j = 0;
                for (String name : newNames) {
                    j++;
                    if (j % 2 == 0) {
                        toShow += "\n" + name + "\n";
                    }
                }
                allPlayers.setText(toShow);
            }
        });

        // Start Game button
        Button startGameB = new Button("Start Game");
        startGameB.setFont(Font.font("Verdana", 27));
        startGameB.setPrefSize(200, 50);
        startGameB.setLayoutX(860);
        startGameB.setLayoutY(650);
        // Adding Start Game button functionality
        startGameB.setOnAction(e -> {
            try {
                playButtonSound();
                if (count < 2) {
                    playerAdded.setText("Insufficient players");
                    playerAdded.setFill(Color.CYAN);
                } else {
                    this.sg.startGame();
                    audioClip.close();
                    audioStream.close();
                    stage.close();
                }
            } catch (Exception b) {
                b.printStackTrace();
            }
        });

        // Text for showing heading "Players Added"
        Text addedPlayersText = new Text("Players Added");
        addedPlayersText.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        addedPlayersText.setFill(Color.CYAN);
        addedPlayersText.setX(190);
        addedPlayersText.setY(395);

        // Text for guiding user about purpose of the textfield
        Text text = new Text("Enter player name");
        text.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        text.setFill(Color.CYAN);

        // Regex pattern for validating player names
        Pattern pattern = Pattern.compile("^[a-zA-Z]+$");

        // Creating textfield for storing player names
        TextField playerNameField = new TextField();
        playerNameField.setStyle(TEXTFIELD_CSS);
        playerNameField.setFont(Font.font("Verdana", 20));
        playerNameField.setAlignment(Pos.CENTER);
        playerNameField.setMaxSize(240, 65);

        // Button for adding player
        Button addPlayerButton = new Button("Add Player");
        addPlayerButton.setFont(Font.font("Verdana", 20));
        // Adding functionality to Add Player Button
        addPlayerButton.setOnAction(e -> {
            matcher = pattern.matcher(playerNameField.getText());
            boolean played = false;
            if (matcher.matches()) {
                String[] numStrings = { "1. ", "2. ", "3. ", "4. ", "5. " };
                if (count < 5) {
                    if (playerNames.contains(playerNameField.getText())) {
                        playerAdded.setText("Name already in use!");
                        playerAdded.setFill(Color.CYAN);
                    } else {
                        if (playerNameField.getText().length() > 10) {
                            playerAdded.setText("Name too long!");
                            playerAdded.setFill(Color.CYAN);
                        } else {
                            played = true;
                            count++;
                            playerNames.add(playerNameField.getText());
                            toShow += "\n" + numStrings[count - 1] + playerNameField.getText() + "\n";
                            allPlayers.setText(toShow);
                            allPlayers.setFill(Color.WHITE);
                            playerAdded.setText("Player added!");
                            playerAdded.setFill(Color.CYAN);
                        }
                    }
                } else {
                    playerAdded.setText("");
                    allPlayers.setText(toShow + "\n Player limit is 5 players!");
                    allPlayers.setFill(Color.WHITE);
                }
            } else {
                playerAdded.setText("Invalid player name");
                playerAdded.setFill(Color.CYAN);
            }
            playerNameField.clear();
            try {
                if (played) {
                    playAddPlayerAudio();
                } else {
                    playButtonSound();
                }
            } catch (Exception v) {
                v.printStackTrace();
            }
        });

        // Create image object for space jpeg
        Image imView = new Image("GUI_Images/startPageSpace.jpeg");
        ImageView spaceBcg = new ImageView(imView);

        // Group for all players added stuff
        Group group = new Group();
        group.getChildren().addAll(r2, addedPlayersText, playerNameField, allPlayers, startGameB);

        // VBox for center elements
        VBox vb = new VBox(15);
        vb.setAlignment(Pos.CENTER);
        vb.getChildren().addAll(text, playerNameField, addPlayerButton, r1, playerAdded);

        // StackPane for rectangle and textfield and text
        StackPane s = new StackPane();
        s.setAlignment(Pos.CENTER);
        s.getChildren().addAll(r1, vb);

        // StackPane for combining background with all previous elements
        StackPane sp = new StackPane();
        sp.getChildren().addAll(spaceBcg, s);

        // Group root container
        Group root = new Group(sp, group, backB, resetB, gameName, stopAudio, removeLastB);

        // Setting the scene and stage 1920 1080
        Scene scene2 = new Scene(root, screenCoords.getWidth(), screenCoords.getHeight());
        scene2.getStylesheets().add(getClass().getResource("CSS/menuAndNewGameStyles.css").toExternalForm());
        stage.setScene(scene2);
        stage.setTitle(GAMENAME);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * method implementing the logic behind saving game
     */
    public void loadSavedGame() {

        // setup GUI
        try {
            loadSavedGUI();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Implementing functionality

    }

    /**
     * load previously saved game
     * 
     * @throws IOException                   exception handling
     * @throws UnsupportedAudioFileException exception handling
     * @throws LineUnavailableException      exception handling
     */
    public void loadSavedGUI() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        Stage stage = new Stage();

        // Rectangle
        Rectangle r = new Rectangle(680, 280, 456, 230);
        r.setFill(Color.BLACK);

        // Rectangle for file names
        Rectangle r2 = new Rectangle(758, 540, 300, 430);
        r2.setFill(Color.BLACK);

        // r2 animation
        FillTransition ft2 = new FillTransition(Duration.seconds(4), r2, Color.BLACK, Color.rgb(0, 32, 255));
        ft2.setCycleCount(Animation.INDEFINITE);
        ft2.setAutoReverse(true);
        ft2.play();

        // Adding background music for start page
        File audioFile = new File("Sound/startPageMusic.wav");
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
        Clip audioClip = AudioSystem.getClip();
        audioClip.open(audioStream);
        audioClip.start();
        audioClip.loop(40);

        // Textfield for entering file name
        TextField enterFileNameTF = new TextField();
        enterFileNameTF.setFont(Font.font("Verdana", 20));
        enterFileNameTF.setAlignment(Pos.CENTER);
        enterFileNameTF.setStyle(TEXTFIELD_CSS);
        enterFileNameTF.setPrefSize(400, 40);
        enterFileNameTF.setLayoutX(712);
        enterFileNameTF.setLayoutY(380);

        // Space background image
        Image imView = new Image("GUI_Images/startPageSpace.jpeg");
        ImageView spaceBcg = new ImageView(imView);

        // Back button
        Button backB = new Button("Back");
        backB.setFont(Font.font("Verdana", 22));
        backB.setPrefSize(120, 35);
        backB.setLayoutX(60);
        backB.setLayoutY(30);
        backB.setOnAction(e -> {
            try {
                playButtonSound();
                audioClip.close();
                audioStream.close();
                stage.close();
                Stage s = new Stage();
                setupStartPage(s);
            } catch (Exception ef) {
                ef.printStackTrace();
            }
        });

        // Label to show file names
        Label filesNames = new Label();
        filesNames.setFont(Font.font("Verdana", 20));
        filesNames.setTextFill(Color.CYAN);
        filesNames.setLayoutX(775);
        filesNames.setLayoutY(520);

        // Button for showing all files
        Button allFilesB = new Button("Show All Files");
        allFilesB.setFont(Font.font("Verdana", 30));
        allFilesB.setPrefSize(260, 50);
        allFilesB.setLayoutX(700);
        allFilesB.setLayoutY(440);
        allFilesB.setOnAction(e -> {
            try {
                playButtonSound();
            } catch (Exception g) {
                g.printStackTrace();
            }
            filesNames.setText(displayFiles(FOLDER_PATH));
        });

        // Load game button
        Button loadB = new Button("Load");
        loadB.setFont(Font.font("Verdana", 30));
        loadB.setPrefSize(130, 50);
        loadB.setLayoutX(980);
        loadB.setLayoutY(440);
        loadB.setOnAction(e -> {
            try {
                playButtonSound();
            } catch (Exception fb) {
                fb.printStackTrace();
            }
            if (checkFileName(FOLDER_PATH, enterFileNameTF.getText())) {
                // String fileName = enterFileNameTF.getText() + ".ser"; // IMPLEMENT LOAD
                // FUNCTIONALITY
                try {
                    // GameEngine gEng = GameEngine.loadState(FOLDER_PATH + fileName);

                    audioClip.stop();
                    audioClip.close();
                    audioStream.close();
                    stage.close();
                } catch (Exception et) {
                    et.printStackTrace();
                }

            } else {
                enterFileNameTF.clear();
                alertBox.showAlertBox();
            }
        });

        // Clear button
        Button clearB = new Button("Clear");
        clearB.setFont(Font.font("Verdana", 16));
        clearB.setTextFill(Color.CYAN);
        clearB.setPrefSize(80, 25);
        clearB.setLayoutX(870);
        clearB.setLayoutY(930);
        clearB.setOnAction(e -> {
            try {
                playButtonSound();
            } catch (Exception za) {
                za.printStackTrace();
            }
            filesNames.setText("");
        });

        // Label for enter file name
        Label enterFileName = new Label("Enter File Name");
        enterFileName.setFont(Font.font("Verdana", FontWeight.BOLD, 40));
        enterFileName.setTextFill(Color.RED);
        enterFileName.setLayoutX(727);
        enterFileName.setLayoutY(300);

        // Heading label
        Label loadSavedLabel = new Label("Load Saved Game");
        loadSavedLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 100));
        loadSavedLabel.setLayoutX(405);
        loadSavedLabel.setLayoutY(1);
        loadSavedLabel.setTextFill(Color.CYAN);

        // root container
        Group root = new Group(spaceBcg, r, r2, backB, loadSavedLabel, allFilesB, enterFileNameTF, enterFileName, loadB,
                filesNames, clearB);

        // Setting stage and scene
        Scene scene = new Scene(root, 1920, 1080);
        scene.getStylesheets().add(getClass().getResource("CSS/menuAndNewGameStyles.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle(GAMENAME);
        stage.setResizable(false);
        stage.show();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////// HELPER METHODS BELOW ///////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Helper method; plays button sound effect
     * 
     * @throws UnsupportedAudioFileException exception handling
     * @throws IOException                   exception handling
     * @throws LineUnavailableException      exception handling
     */
    public static void playButtonSound() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        File buttonSoundFile = new File("Sound/buttonClickSound.wav");
        try (AudioInputStream buttonStream = AudioSystem.getAudioInputStream(buttonSoundFile)) {
            Clip buttonClip = AudioSystem.getClip();
            buttonClip.addLineListener(e -> {
                if (e.getType() == LineEvent.Type.STOP) {
                    buttonClip.stop();
                    buttonClip.close();
                    try {
                        buttonStream.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            });
            buttonClip.open(buttonStream);
            buttonClip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method for playing audio when player is added successfully
     * 
     * @throws UnsupportedAudioFileException exception handling
     * @throws IOException                   exception handling
     * @throws LineUnavailableException      exception handling
     */
    public static void playAddPlayerAudio()
            throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        File addPlayerFile = new File("Sound/addPlayerAudio.wav");
        try (AudioInputStream addPlayerStream = AudioSystem.getAudioInputStream(addPlayerFile)) {
            Clip addPlayerClip = AudioSystem.getClip();
            addPlayerClip.addLineListener(e -> {
                if (e.getType() == LineEvent.Type.STOP) {
                    addPlayerClip.stop();
                    addPlayerClip.close();
                    try {
                        addPlayerStream.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            });
            addPlayerClip.open(addPlayerStream);
            addPlayerClip.start();
        } catch (Exception er) {
            er.printStackTrace();
        }

    }

    /**
     * Method to play spaceship sound effect
     * 
     * @throws UnsupportedAudioFileException exception handling
     * @throws IOException                   exception handling
     * @throws LineUnavailableException      exception handling
     */
    public static void playSpaceshipSound()
            throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        File spaceshipAudio = new File("Sound/spaceshipSoundEffect.wav");
        try (AudioInputStream spaceshipStream = AudioSystem.getAudioInputStream(spaceshipAudio)) {
            Clip spaceshipClip = AudioSystem.getClip();
            spaceshipClip.addLineListener(e -> {
                if (e.getType() == LineEvent.Type.STOP) {
                    spaceshipClip.stop();
                    spaceshipClip.close();
                    try {
                        spaceshipStream.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            });
            spaceshipClip.open(spaceshipStream);
            spaceshipClip.start();
        } catch (Exception err) {
            err.printStackTrace();
        }

    }

    /**
     * Helper method; used to play sound effect when card is drawn
     * 
     * @throws UnsupportedAudioFileException exception handling
     * @throws IOException                   exception handling
     * @throws LineUnavailableException      exception handling
     */
    public static void playDrawCardSound() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        File drawCardFile = new File("Sound/drawCardSoundEffect.wav");
        try (AudioInputStream drawCardStream = AudioSystem.getAudioInputStream(drawCardFile)) {
            Clip drawCardClip = AudioSystem.getClip();
            drawCardClip.addLineListener(e -> {
                if (e.getType() == LineEvent.Type.STOP) {
                    drawCardClip.stop();
                    drawCardClip.close();
                    try {
                        drawCardStream.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            });
            drawCardClip.open(drawCardStream);
            drawCardClip.start();
        } catch (Exception f) {
            f.printStackTrace();
        }

    }

    /**
     * Helper method; used to play sound when astronaut button is clicked
     * 
     * @throws UnsupportedAudioFileException exception handling
     * @throws IOException                   exception handling
     * @throws LineUnavailableException      exception handling
     */
    public static void playAstronautButtonSound()
            throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        File astronautFile = new File("Sound/astronautButtonClick.wav");
        try (AudioInputStream aStream = AudioSystem.getAudioInputStream(astronautFile);) {
            Clip aClip = AudioSystem.getClip();
            aClip.addLineListener(e -> {
                if (e.getType() == LineEvent.Type.STOP) {
                    aClip.stop();
                    aClip.close();
                    try {
                        aStream.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            });
            aClip.open(aStream);
            aClip.start();
        } catch (Exception et) {
            et.printStackTrace();
        }
    }

    /**
     * Helper method; used to play sound when breathe button is clicked
     * 
     * @throws UnsupportedAudioFileException exception handling
     * @throws IOException                   exception handling
     * @throws LineUnavailableException      exception handling
     */
    public static void playBreatheSound() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        File breatheFile = new File("Sound/breatheEffect.wav");
        try (AudioInputStream bStream = AudioSystem.getAudioInputStream(breatheFile);) {
            Clip breClip = AudioSystem.getClip();
            breClip.addLineListener(e -> {
                if (e.getType() == LineEvent.Type.STOP) {
                    breClip.stop();
                    breClip.close();
                    try {
                        bStream.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            });
            breClip.open(bStream);
            breClip.start();
        } catch (Exception p) {
            p.printStackTrace();
        }
    }

    /**
     * Helper method; plays sound when astronaut travel
     * 
     * @throws UnsupportedAudioFileException exception handling
     * @throws IOException                   exception handling
     * @throws LineUnavailableException      exception handling
     */
    public static void playTravelSound() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        File travelFile = new File("Sound/travelAstronaut.wav");
        try (AudioInputStream travelStream = AudioSystem.getAudioInputStream(travelFile)) {
            Clip travelClip = AudioSystem.getClip();
            travelClip.addLineListener(e -> {
                if (e.getType() == LineEvent.Type.STOP) {
                    travelClip.stop();
                    travelClip.close();
                    try {
                        travelStream.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            });
            travelClip.open(travelStream);
            travelClip.start();
        } catch (Exception gb) {
            gb.printStackTrace();
        }
    }

    /**
     * Helper method; plays card shuffle sound
     * 
     * @throws UnsupportedAudioFileException exception handling
     * @throws IOException                   exception handling
     * @throws LineUnavailableException      exception handling
     */
    public static void playCardShuffleSound()
            throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        File shuffleFile = new File("Sound/cardShuffle.wav");
        try (AudioInputStream shuffleStream = AudioSystem.getAudioInputStream(shuffleFile)) {
            Clip shuffleClip = AudioSystem.getClip();
            shuffleClip.addLineListener(e -> {
                if (e.getType() == LineEvent.Type.STOP) {
                    shuffleClip.stop();
                    shuffleClip.close();
                    try {
                        shuffleStream.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            });
            shuffleClip.open(shuffleStream);
            shuffleClip.start();
        } catch (Exception b) {
            b.printStackTrace();
        }

    }

    /**
     * Helper method; used to set-up GameEngine instance
     */
    public static GameEngine setupGameEngine() throws GameException {
        Random r = new Random();

        GameEngine gEngine = new GameEngine(r.nextLong(),
                "ActionCards.txt",
                "SpaceCards.txt");

        return gEngine;
    }

    /**
     * helper method; to display file names
     */
    private String displayFiles(String folder_path) {
        String fileNames = "";
        File allFiles = new File(folder_path);
        File[] fileList = allFiles.listFiles();
        for (File f : fileList) {
            if (f.isFile()) {
                if (f.getName().endsWith("ser")) {
                    fileNames += "\n" + f.getName().replace(".ser", "") + "\n";
                }
            }
        }
        return fileNames;
    }

    /**
     * check if file name is valid
     * 
     * @param folder_path path
     * @return boolean
     */
    public boolean checkFileName(String folder_path, String file_name) {
        File f = new File(folder_path);
        File[] fileList = f.listFiles();
        for (File file : fileList) {
            if (file.isFile()) {
                if (file.getName().equals(file_name + ".ser")) {
                    return true;
                }
            }
        }
        return false;
    }
}
