import javafx.animation.TranslateTransition;
import javafx.stage.WindowEvent;
import javafx.animation.Animation;
import javafx.animation.FillTransition;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * selfish package imports
 */
import selfish.GameEngine;
import selfish.GameException;
import selfish.Astronaut;
import selfish.deck.Card;
import selfish.deck.GameDeck;
import selfish.deck.Oxygen;
import selfish.deck.SpaceDeck;

/**
 * other imports
 */
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.sound.sampled.*;
import java.util.List;

public class StartGame {
    private int actionsPlayed; // helps implement 2-player logic
    private ArrayList<CheckBox> checkBoxes;
    private AlertBox alBox;
    private int saveCount;
    private int discardCount; // for meteoroid implementation
    private int discardCount2; // for cosmic radiation implementation
    private boolean affected;
    private static int audioButtonClicks = 0;
    private GameEngine engine;
    private Label currentPlayerLabel;
    private Label allPlayersLabel;
    private Rectangle allPlayersRectangle;
    private int allPlayerRectangleHeight = 97;
    private Pattern pattern;
    private Matcher matcher;
    private boolean saved;
    private Stage mainGameStage;
    private Stage saveGameStage;
    private Stage astronautTurnSimulation;
    private Stage exitGameStage;
    private String savedFileName; // for storing file name in which game is saved
    private TextField tf; // For entering file name in save game menu
    private Label enterFileName; // For heading "Enter file name" in save game menu
    private Button drawCardB;
    private Button gameDiscardPileB;
    // Player buttons
    private Button purpleAstronautB;
    private Button greenAstronautB;
    private Button yellowAstronautB;
    private Button redAstronautB;
    private Button blueAstronautB;
    // Player name labels
    private Label purpleAstronautName;
    private Label greenAstronautName;
    private Label yellowAstronautName;
    private Label redAstronautName;
    private Label blueAstronautName;
    // Player Track lists;
    private ArrayList<Button> purpleAstronautTrackB;
    private ArrayList<Button> greenAstronautTrackB;
    private ArrayList<Button> yellowAstronautTrackB;
    private ArrayList<Button> redAstronautTrackB;
    private ArrayList<Button> blueAstronautTrackB;
    // Pane for all space cards
    private Pane spaceCardsPane;

    /**
     * StartGame class constructor
     * 
     * @throws GameException exception handling
     */
    public StartGame() throws GameException {
        this.actionsPlayed = 0;
        this.checkBoxes = new ArrayList<>();

        this.alBox = new AlertBox("Invalid card name");
        this.saveCount = 0;
        this.discardCount = 0;
        this.discardCount2 = 0;
        this.affected = false;

        this.engine = GameApp.setupGameEngine();
        this.currentPlayerLabel = new Label();
        this.allPlayersLabel = new Label();

        this.allPlayersRectangle = new Rectangle(1630, 270, 160, allPlayerRectangleHeight);
        this.allPlayersRectangle.setFill(Color.YELLOW);
        this.allPlayersRectangle.setId("rect");

        this.pattern = Pattern.compile("^[a-zA-Z0-9]+$");
        this.saved = false;

        this.mainGameStage = new Stage();
        this.mainGameStage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, e -> {
            e.consume();
        });

        this.saveGameStage = new Stage();
        this.saveGameStage.initOwner(this.mainGameStage);
        this.saveGameStage.initModality(Modality.APPLICATION_MODAL);

        this.exitGameStage = new Stage();
        this.exitGameStage.initOwner(this.mainGameStage);
        this.exitGameStage.initModality(Modality.APPLICATION_MODAL);

        this.astronautTurnSimulation = new Stage();
        this.astronautTurnSimulation.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, e -> {
            e.consume();
        });
        this.astronautTurnSimulation.initOwner(this.mainGameStage);
        this.astronautTurnSimulation.initModality(Modality.APPLICATION_MODAL);
        this.astronautTurnSimulation.setResizable(false);

        this.savedFileName = "";

        this.tf = new TextField();
        this.tf.setStyle(GameApp.TEXTFIELD_CSS);
        this.tf.setAlignment(Pos.CENTER);
        this.tf.setPrefSize(250, 40);
        this.tf.setFont(Font.font("Verdana", 18));
        this.tf.setMaxSize(250, 40);

        this.enterFileName = new Label("Enter File Name");
        enterFileName.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        enterFileName.setTextFill(Color.RED);
        this.drawCardB = new Button();
        this.gameDiscardPileB = new Button();

        // Buttons
        this.purpleAstronautB = new Button();
        this.greenAstronautB = new Button();
        this.yellowAstronautB = new Button();
        this.redAstronautB = new Button();
        this.blueAstronautB = new Button();

        // Labels for player names
        this.purpleAstronautName = new Label();
        this.greenAstronautName = new Label();
        this.yellowAstronautName = new Label();
        this.redAstronautName = new Label();
        this.blueAstronautName = new Label();

        // Arraylist for track buttons
        this.purpleAstronautTrackB = new ArrayList<>();
        this.greenAstronautTrackB = new ArrayList<>();
        this.yellowAstronautTrackB = new ArrayList<>();
        this.redAstronautTrackB = new ArrayList<>();
        this.blueAstronautTrackB = new ArrayList<>();

        this.spaceCardsPane = new Pane();
        this.spaceCardsPane.setLayoutX(150);
        this.spaceCardsPane.setLayoutY(0);
        this.spaceCardsPane.setPrefSize(1365, 1080);
    }

    /**
     * Method for starting the game
     */
    public void startGame() throws GameException, UnsupportedAudioFileException, IOException, LineUnavailableException {
        // GUI setup
        setUpGameElements();

        // Adding players to GameEngine
        for (String name : GameApp.playerNames) {
            engine.addPlayer(name);
        }

        // Starting the game
        this.engine.startGame();

        // Continuing cycle of the game
        checkGameConditions();
    }

    /**
     * Method for setting up all GUI elements
     * 
     * @throws GameException
     * @throws UnsupportedAudioFileException
     * @throws IOException
     * @throws LineUnavailableException
     */
    private void setUpGameElements()
            throws UnsupportedAudioFileException, IOException, LineUnavailableException {

        // Playing background music
        File startGameAudio = new File("Sound/startGameMusic.wav");
        AudioInputStream startGameStream = AudioSystem.getAudioInputStream(startGameAudio);
        Clip startGameAudioClip = AudioSystem.getClip();
        startGameAudioClip.open(startGameStream);
        startGameAudioClip.start();
        startGameAudioClip.loop(10);

        // Setting up Astronaut icons and buttons
        Image im1 = new Image("GUI_Images/purpleAstronaut.png");
        ImageView purpleAstronaut = new ImageView(im1);
        this.purpleAstronautB.setGraphic(purpleAstronaut);
        this.purpleAstronautB.setLayoutY(815);
        this.purpleAstronautB.setId("playerB");

        Image im2 = new Image("GUI_Images/greenAstronaut.png");
        ImageView greenAstronaut = new ImageView(im2);
        this.greenAstronautB.setGraphic(greenAstronaut);
        this.greenAstronautB.setLayoutY(815);
        this.greenAstronautB.setDisable(true);
        this.greenAstronautB.setId("playerB");

        Image im3 = new Image("GUI_Images/yellowAstronaut.png");
        ImageView yellowAstronaut = new ImageView(im3);
        this.yellowAstronautB.setGraphic(yellowAstronaut);
        this.yellowAstronautB.setLayoutY(815);
        this.yellowAstronautB.setVisible(false);
        this.yellowAstronautB.setDisable(true);
        this.yellowAstronautB.setId("playerB");

        Image im4 = new Image("GUI_Images/redAstronaut.png");
        ImageView redAstronaut = new ImageView(im4);
        this.redAstronautB.setGraphic(redAstronaut);
        this.redAstronautB.setLayoutY(815);
        this.redAstronautB.setVisible(false);
        this.redAstronautB.setDisable(true);
        this.redAstronautB.setId("playerB");

        Image im5 = new Image("GUI_Images/blueAstronaut.png");
        ImageView blueAstronaut = new ImageView(im5);
        this.blueAstronautB.setGraphic(blueAstronaut);
        this.blueAstronautB.setLayoutY(815);
        this.blueAstronautB.setVisible(false);
        this.blueAstronautB.setDisable(true);
        this.blueAstronautB.setId("playerB");

        // Setting up player names' labels
        try {
            this.purpleAstronautName.setText(GameApp.playerNames.get(0));
            this.greenAstronautName.setText(GameApp.playerNames.get(1));
            this.yellowAstronautName.setText(GameApp.playerNames.get(2));
            this.redAstronautName.setText(GameApp.playerNames.get(3));
            this.blueAstronautName.setText(GameApp.playerNames.get(4));
        } catch (IndexOutOfBoundsException ar) {
            System.out.println("No Problem");
        }

        this.purpleAstronautName.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        this.purpleAstronautName.setTextFill(Color.CYAN);
        this.purpleAstronautName.setLayoutY(950);

        this.greenAstronautName.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        this.greenAstronautName.setTextFill(Color.CYAN);
        this.greenAstronautName.setLayoutY(950);

        this.yellowAstronautName.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        this.yellowAstronautName.setTextFill(Color.CYAN);
        this.yellowAstronautName.setLayoutY(950);
        this.yellowAstronautName.setVisible(false);

        this.redAstronautName.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        this.redAstronautName.setTextFill(Color.CYAN);
        this.redAstronautName.setLayoutY(950);
        this.redAstronautName.setVisible(false);

        this.blueAstronautName.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        this.blueAstronautName.setTextFill(Color.CYAN);
        this.blueAstronautName.setLayoutY(950);
        this.blueAstronautName.setVisible(false);

        // Switch statement to display appropriate number of astronauts
        switch (GameApp.playerNames.size()) {
            case 3:
                this.yellowAstronautB.setVisible(true);
                this.yellowAstronautName.setVisible(true);
                break;
            case 4:
                this.yellowAstronautB.setVisible(true);
                this.yellowAstronautName.setVisible(true);
                this.redAstronautB.setVisible(true);
                this.redAstronautName.setVisible(true);
                break;
            case 5:
                this.yellowAstronautB.setVisible(true);
                this.yellowAstronautName.setVisible(true);
                this.redAstronautB.setVisible(true);
                this.redAstronautName.setVisible(true);
                this.blueAstronautB.setVisible(true);
                this.blueAstronautName.setVisible(true);
                break;
        }

        // Switch statement to center astronaut buttons based on player number
        switch (GameApp.playerNames.size()) {
            case 2:
                this.purpleAstronautB.setLayoutX(587);
                this.purpleAstronautName.setLayoutX(637);
                this.greenAstronautB.setLayoutX(897);
                this.greenAstronautName.setLayoutX(947);
                break;
            case 3:
                this.purpleAstronautB.setLayoutX(472);
                this.purpleAstronautName.setLayoutX(522);
                this.greenAstronautB.setLayoutX(742);
                this.greenAstronautName.setLayoutX(792);
                this.yellowAstronautB.setLayoutX(1012);
                this.yellowAstronautName.setLayoutX(1062);
                break;
            case 4:
                this.purpleAstronautB.setLayoutX(367);
                this.purpleAstronautName.setLayoutX(417);
                this.greenAstronautB.setLayoutX(617);
                this.greenAstronautName.setLayoutX(667);
                this.yellowAstronautB.setLayoutX(867);
                this.yellowAstronautName.setLayoutX(917);
                this.redAstronautB.setLayoutX(1117);
                this.redAstronautName.setLayoutX(1167);
                break;
            case 5:
                this.purpleAstronautB.setLayoutX(185);
                this.purpleAstronautName.setLayoutX(235);
                this.greenAstronautB.setLayoutX(465);
                this.greenAstronautName.setLayoutX(515);
                this.yellowAstronautB.setLayoutX(745);
                this.yellowAstronautName.setLayoutX(795);
                this.redAstronautB.setLayoutX(1025);
                this.redAstronautName.setLayoutX(1075);
                this.blueAstronautB.setLayoutX(1305);
                this.blueAstronautName.setLayoutX(1335);
                break;
        }

        // Game cards pile image
        Image imGC = new Image("GUI_Images/GameCardsFrontDesign.png");
        ImageView gameCardsFront = new ImageView(imGC);

        // Button for drawing card
        this.drawCardB.setGraphic(gameCardsFront);
        this.drawCardB.setId("drawCardB");
        this.drawCardB.setDisable(true);

        // Game cards discard pile image
        Image imGD = new Image("GUI_Images/GameDiscardFile.png");
        ImageView gameDiscardPile = new ImageView(imGD);

        // Setting graphic on restock empty deck button
        this.gameDiscardPileB.setGraphic(gameDiscardPile);
        this.gameDiscardPileB.setId("gameDiscardPileB");
        this.gameDiscardPileB.setDisable(true);
        this.gameDiscardPileB.setOnAction(e -> {
            try {
                GameApp.playCardShuffleSound();
            } catch (Exception av) {
                av.printStackTrace();
            }
            this.gameDiscardPileB.setDisable(true);
            this.engine.mergeDecks(this.engine.getGameDeck(), this.engine.getGameDiscard());
            checkGameConditions();
        });

        // Background rectangle
        Rectangle bgR = new Rectangle(0, 0, 1920, 1080);
        bgR.setFill(Color.BLACK);

        // Label for "race for survival..."
        Label l = new Label("And the race for survival begins...");
        l.setFont(Font.font("Verdana", FontPosture.ITALIC, 50));
        l.setTextFill(Color.AQUA);
        l.setLayoutX(0);
        l.setLayoutY(540);

        // Adding animation to label
        TranslateTransition transitionLabel = new TranslateTransition();
        transitionLabel.setDuration(Duration.seconds(8));
        transitionLabel.setToX(1940);
        transitionLabel.setNode(l);
        transitionLabel.play();

        // Save button
        Button saveB = new Button("Save Game");
        saveB.setPrefSize(120, 30);
        saveB.setFont(Font.font("Verdana", 14));
        saveB.setLayoutX(1650);
        saveB.setLayoutY(15);
        saveB.setOnAction(e -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception q) {
                q.printStackTrace();
            }
            if (!this.savedFileName.equals("")) {
                this.tf.setText(this.savedFileName.replace(".ser", ""));
                this.tf.setDisable(true);
                this.enterFileName.setText("Update Game File");
            }
            saveGameWindow();
        });

        // Mute/Unmute Audio
        Button stopAudio = new Button("Mute Audio");
        stopAudio.setFont(Font.font("Verdana", 14));
        stopAudio.setPrefSize(120, 30);
        stopAudio.setLayoutX(1650);
        stopAudio.setLayoutY(60);
        stopAudio.setOnAction(e -> {
            audioButtonClicks++;
            try {
                GameApp.playButtonSound();
            } catch (Exception q) {
                q.printStackTrace();
            }
            if (audioButtonClicks % 2 == 0) {
                stopAudio.setText("Mute Audio");
                startGameAudioClip.start();
            } else {
                startGameAudioClip.stop();
                stopAudio.setText("Unmute Audio");
            }
        });

        // Exit Game button
        Button mMenuB = new Button("Exit Game");
        mMenuB.setFont(Font.font("Verdana", 14));
        mMenuB.setPrefSize(120, 30);
        mMenuB.setLayoutX(1650);
        mMenuB.setLayoutY(105);
        mMenuB.setOnAction(e -> {
            try {
                GameApp.playButtonSound();
                exitGameButtonGUI();
            } catch (Exception c) {
                c.printStackTrace();
            }
        });

        // Rectangle for buttons' background
        Rectangle rButtons = new Rectangle(1630, 10, 160, 138);
        rButtons.setFill(Color.RED);

        // Rectangle for current player
        Rectangle rCP = new Rectangle(1630, 182, 160, 65);
        rCP.setFill(Color.YELLOW);
        rCP.setId("rect");

        // Side strip
        Image spstr = new Image("GUI_Images/gameBoardSideStrip.png");
        ImageView sideStrip = new ImageView(spstr);
        sideStrip.setX(1510);
        sideStrip.setY(40);

        // Current player heading label
        Label cPHeading = new Label("Current Player");
        cPHeading.setFont(Font.font("Verdana", FontWeight.BOLD, 17));
        cPHeading.setTextFill(Color.DARKCYAN);
        cPHeading.setLayoutX(1640);
        cPHeading.setLayoutY(190);

        // private attribute currentPlayerLabel configuration
        this.currentPlayerLabel.setFont(Font.font("Verdana", 16));
        this.currentPlayerLabel.setTextFill(Color.BLACK);
        this.currentPlayerLabel.setLayoutX(1680);
        this.currentPlayerLabel.setLayoutY(220);

        // All players heading label
        Label aPs = new Label("All Players");
        aPs.setFont(Font.font("Verdana", FontWeight.BOLD, 17));
        aPs.setTextFill(Color.DARKCYAN);
        aPs.setLayoutX(1658);
        aPs.setLayoutY(270);

        // private attribute allPlayersLabel configuration
        this.allPlayersLabel.setFont(Font.font("Verdana", 16));
        this.allPlayersLabel.setTextFill(Color.BLACK);
        this.allPlayersLabel.setLayoutX(1680);
        this.allPlayersLabel.setLayoutY(280);

        // Vbox for holding game card decks
        VBox gameCardDecks = new VBox(30, this.drawCardB, this.gameDiscardPileB);
        gameCardDecks.setAlignment(Pos.CENTER_LEFT);

        // Stackpane for holding background with other containers
        StackPane sp = new StackPane();
        sp.getChildren().addAll(bgR, gameCardDecks);

        // Group container for astronaut buttons
        Group groupButtons = new Group();
        groupButtons.getChildren().addAll(this.purpleAstronautB, this.greenAstronautB, this.yellowAstronautB,
                this.redAstronautB, this.blueAstronautB);

        // Group container for astronaut names
        Group groupNames = new Group();
        groupNames.getChildren().addAll(this.purpleAstronautName, this.greenAstronautName, this.yellowAstronautName,
                this.redAstronautName, this.blueAstronautName);

        // Pane root container
        Pane root = new Pane();
        root.getChildren().addAll(sp, this.spaceCardsPane, l, sideStrip, rButtons, rCP, saveB, stopAudio, mMenuB,
                cPHeading,
                this.currentPlayerLabel, this.allPlayersRectangle, aPs, this.allPlayersLabel,
                groupButtons, groupNames);

        // Setting stage and scene
        Scene scene = new Scene(root, 1920, 1080);
        scene.getStylesheets().add(getClass().getResource("CSS/mainGameStyles.css").toExternalForm());
        this.mainGameStage.setScene(scene);
        this.mainGameStage.setTitle(GameApp.GAMENAME);
        this.mainGameStage.setResizable(false);
        this.mainGameStage.show();
    }

    /**
     * private method.
     * For opening the GUI window where game is saved.
     */
    private void saveGameWindow() {

        // Background image
        Image imV = new Image("GUI_Images/space_1080_540_bcg.png");
        ImageView spaceBcg = new ImageView(imV);

        // Stackpane for background
        StackPane sp = new StackPane();
        sp.getChildren().add(spaceBcg);

        // Rectangle for elements
        Rectangle rElem = new Rectangle(390, 130, 300, 240);
        rElem.setFill(Color.BLACK);

        // Animation for rElem
        FillTransition ft2 = new FillTransition(Duration.seconds(3), rElem, Color.TRANSPARENT, Color.rgb(0, 32, 255));
        ft2.setCycleCount(Animation.INDEFINITE);
        ft2.setAutoReverse(true);
        ft2.play();

        // Label for displaying info about user's file name input
        Label userFileNameLabel = new Label();
        userFileNameLabel.setFont(Font.font("Verdana", 22));
        userFileNameLabel.setTextFill(Color.CYAN);

        // Label for save game
        Label saveGameLabel = new Label("Save Game");
        saveGameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 40));
        saveGameLabel.setTextFill(Color.CYAN);
        saveGameLabel.setLayoutX(430);
        saveGameLabel.setLayoutY(3);

        // Exit button
        Button exitB = new Button("Exit");
        exitB.setFont(Font.font("Verdana", 25));
        exitB.setPrefSize(85, 45);
        exitB.setLayoutX(50);
        exitB.setLayoutY(40);
        exitB.setOnAction(e -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception eg) {
                eg.printStackTrace();
            }
            this.tf.clear();
            this.saveGameStage.close();
        });

        // Save button
        Button saveB = new Button("Save");
        saveB.setFont(Font.font("Verdana", 20));
        saveB.setPrefSize(85, 40);
        saveB.setOnAction(e -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception vg) {
                vg.printStackTrace();
            }
            matcher = pattern.matcher(tf.getText());
            if (this.saved) {
                userFileNameLabel.setText("Game already saved");
            } else if (tf.getText().length() > 15) {
                userFileNameLabel.setText("File name too long");
                this.tf.clear();
            } else if (matcher.matches()) {
                String fileName = tf.getText() + ".ser";
                boolean result = true;
                if (this.saveCount == 0) {
                    result = checkFileDuplication(GameApp.FOLDER_PATH, fileName);
                }
                if (result) {
                    engine.saveState(GameApp.FOLDER_PATH + fileName);
                    userFileNameLabel.setText("Game saved!");
                    this.saved = true;
                    this.savedFileName = fileName;
                    this.saveCount++;
                } else {
                    userFileNameLabel.setText("File name in use");
                    this.tf.clear();
                }
            } else {
                userFileNameLabel.setText("Invalid file name");
                this.tf.clear();
            }
        });

        // VBox for center elements
        VBox v = new VBox(enterFileName, this.tf, saveB, userFileNameLabel);
        v.setAlignment(Pos.CENTER);
        v.setSpacing(15);

        // Stackpane for background and center elements
        StackPane spBcg = new StackPane(spaceBcg, rElem, v);

        // root container
        Group root = new Group(spBcg, saveGameLabel, exitB);

        // Setting stage and scene
        Scene scene = new Scene(root, 1080, 540);
        scene.getStylesheets().add(getClass().getResource("CSS/mainGameStyles.css").toExternalForm());

        this.saveGameStage.setScene(scene);
        this.saveGameStage.setTitle(GameApp.GAMENAME);
        this.saveGameStage.setResizable(false);
        this.saveGameStage.setX(287);
        this.saveGameStage.setY(240);
        this.saveGameStage.showAndWait();
    }

    /**
     * private Method.
     * For showing GUI window from where game can be exit.
     */
    private void exitGameButtonGUI() {

        // Background Image
        Image imVi = new Image("GUI_Images/space_1080_540_bcg.png");
        ImageView background = new ImageView(imVi);

        // Heading label
        Label headingL = new Label("Exit Game?");
        headingL.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        headingL.setTextFill(Color.CYAN);

        // What text to show below heading
        Label label = new Label();
        label.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        label.setTextFill(Color.LIMEGREEN);

        Label label2 = new Label();
        label2.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        label2.setTextFill(Color.LIMEGREEN);
        if (this.saved) {
            label.setText("Your game is saved!");
            label2.setText("You can safely exit the game.");
        } else {
            label.setText("Your game is unsaved!");
            label2.setText("You won't be able to resume later.");
        }

        // Cancel button
        Button cancelB = new Button("Cancel");
        cancelB.setFont(Font.font("Verdana", 25));
        cancelB.setOnAction(e -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception excp) {
                excp.printStackTrace();
            }
            this.exitGameStage.close();

        });

        // Exit button
        Button exitB = new Button("Exit");
        exitB.setFont(Font.font("Verdana", 25));
        exitB.setOnAction(e -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception k) {
                k.printStackTrace();
            }
            this.mainGameStage.close();
            this.exitGameStage.close();
            this.saveGameStage.close();
        });

        // HBox for buttons
        HBox hBox = new HBox(20, cancelB, exitB);
        hBox.setAlignment(Pos.BOTTOM_CENTER);

        // VBox for labels
        VBox vLabels = new VBox(5, label, label2);
        vLabels.setAlignment(Pos.CENTER);

        // VBox for heading, labels, and hBox
        VBox vBox = new VBox(50, headingL, vLabels, hBox);
        vBox.setAlignment(Pos.TOP_CENTER);

        // root StackPane for background with other elements
        StackPane root = new StackPane(background, vBox);

        // Setting scene and stage
        Scene scene = new Scene(root, 540, 280);
        scene.getStylesheets().add(getClass().getResource("CSS/menuAndNewGameStyles.css").toExternalForm());
        this.exitGameStage.setX(557);
        this.exitGameStage.setY(370);
        this.exitGameStage.setScene(scene);
        this.exitGameStage.setTitle(GameApp.GAMENAME);
        this.exitGameStage.setResizable(false);
        this.exitGameStage.showAndWait();
    }

    /**
     * Method to enable appropriate button
     */
    private void enableAstronautButton(String astronautName) {
        int index = GameApp.playerNames.indexOf(astronautName);
        switch (index) {
            case 0:
                this.purpleAstronautB.setDisable(false);
                this.purpleAstronautB.setOnAction(e -> {
                    try {
                        GameApp.playAstronautButtonSound();
                    } catch (Exception a) {
                        a.printStackTrace();
                    }
                    drawGameCard();
                });
                break;
            case 1:
                this.greenAstronautB.setDisable(false);
                this.greenAstronautB.setOnAction(e -> {
                    try {
                        GameApp.playAstronautButtonSound();
                    } catch (Exception a) {
                        a.printStackTrace();
                    }
                    drawGameCard();
                });
                break;
            case 2:
                this.yellowAstronautB.setDisable(false);
                this.yellowAstronautB.setOnAction(e -> {
                    try {
                        GameApp.playAstronautButtonSound();
                    } catch (Exception a) {
                        a.printStackTrace();
                    }
                    drawGameCard();
                });
                break;
            case 3:
                this.redAstronautB.setDisable(false);
                this.redAstronautB.setOnAction(e -> {
                    try {
                        GameApp.playAstronautButtonSound();
                    } catch (Exception a) {
                        a.printStackTrace();
                    }
                    drawGameCard();
                });
                break;
            case 4:
                this.blueAstronautB.setDisable(false);
                this.blueAstronautB.setOnAction(e -> {
                    try {
                        GameApp.playAstronautButtonSound();
                    } catch (Exception a) {
                        a.printStackTrace();
                    }
                    drawGameCard();
                });
                break;
        }
    }

    /**
     * Method to disable button
     * 
     * @param astronautName name of astronaut whose button needs to be disabled
     */
    private void disableAstronautButton(String astronautName) {
        int index = GameApp.playerNames.indexOf(astronautName);
        switch (index) {
            case 0:
                this.purpleAstronautB.setDisable(true);
                break;
            case 1:
                this.greenAstronautB.setDisable(true);
                break;
            case 2:
                this.yellowAstronautB.setDisable(true);
                break;
            case 3:
                this.redAstronautB.setDisable(true);
                break;
            case 4:
                this.blueAstronautB.setDisable(true);
                break;
        }
        this.drawCardB.setDisable(true);
    }

    /**
     * Method for continuing cycle of the game until it ends.
     */
    private void checkGameConditions() {

        if (this.engine.gameOver()) {

            System.out.println("Game Over!");
            this.currentPlayerLabel.setText("   \u2014");
            setupAllPlayersLabel();
            this.saved = false;

            // Disabling tracks and buttons of all players except winner
            if (this.engine.getWinner() != null) {
                int index = GameApp.playerNames.indexOf(this.engine.getWinner().toString());
                switch (index) {
                    case 0:
                        for (int i = 1; i < GameApp.playerNames.size(); i++) {
                            if (i == 1) {
                                for (Button b : this.greenAstronautTrackB) {
                                    b.setDisable(true);
                                }
                                this.greenAstronautB.setDisable(true);
                            } else if (i == 2) {
                                for (Button b : this.yellowAstronautTrackB) {
                                    b.setDisable(true);
                                }
                                this.yellowAstronautB.setDisable(true);
                            } else if (i == 3) {
                                for (Button b : this.redAstronautTrackB) {
                                    b.setDisable(true);
                                }
                                this.redAstronautB.setDisable(true);
                            } else if (i == 4) {
                                for (Button b : this.blueAstronautTrackB) {
                                    b.setDisable(true);
                                }
                                this.blueAstronautB.setDisable(true);
                            }
                        }
                        break;
                    case 1:
                        for (int i = 0; i < GameApp.playerNames.size(); i++) {
                            if (i == 0) {
                                for (Button b : this.purpleAstronautTrackB) {
                                    b.setDisable(true);
                                }
                                this.purpleAstronautB.setDisable(true);
                            } else if (i == 1) {
                                continue;
                            } else if (i == 2) {
                                for (Button b : this.yellowAstronautTrackB) {
                                    b.setDisable(true);
                                }
                                this.yellowAstronautB.setDisable(true);
                            } else if (i == 3) {
                                for (Button b : this.redAstronautTrackB) {
                                    b.setDisable(true);
                                }
                                this.redAstronautB.setDisable(true);
                            } else if (i == 4) {
                                for (Button b : this.blueAstronautTrackB) {
                                    b.setDisable(true);
                                }
                                this.blueAstronautB.setDisable(true);
                            }
                        }
                        break;
                    case 2:
                        for (int i = 0; i < GameApp.playerNames.size(); i++) {
                            if (i == 0) {
                                for (Button b : this.purpleAstronautTrackB) {
                                    b.setDisable(true);
                                }
                                this.purpleAstronautB.setDisable(true);
                            } else if (i == 1) {
                                for (Button b : this.greenAstronautTrackB) {
                                    b.setDisable(true);
                                }
                                this.greenAstronautB.setDisable(true);
                            } else if (i == 2) {
                                continue;
                            } else if (i == 3) {
                                for (Button b : this.redAstronautTrackB) {
                                    b.setDisable(true);
                                }
                                this.redAstronautB.setDisable(true);
                            } else if (i == 4) {
                                for (Button b : this.blueAstronautTrackB) {
                                    b.setDisable(true);
                                }
                                this.blueAstronautB.setDisable(true);
                            }
                        }
                        break;
                    case 3:
                        for (int i = 0; i < GameApp.playerNames.size(); i++) {
                            if (i == 0) {
                                for (Button b : this.purpleAstronautTrackB) {
                                    b.setDisable(true);
                                }
                                this.purpleAstronautB.setDisable(true);
                            } else if (i == 1) {
                                for (Button b : this.greenAstronautTrackB) {
                                    b.setDisable(true);
                                }
                                this.greenAstronautB.setDisable(true);
                            } else if (i == 2) {
                                for (Button b : this.yellowAstronautTrackB) {
                                    b.setDisable(true);
                                }
                                this.yellowAstronautB.setDisable(true);
                            } else if (i == 3) {
                                continue;
                            } else if (i == 4) {
                                for (Button b : this.blueAstronautTrackB) {
                                    b.setDisable(true);
                                }
                                this.blueAstronautB.setDisable(true);
                            }
                        }
                        break;
                    case 4:
                        for (int i = 0; i < GameApp.playerNames.size(); i++) {
                            if (i == 0) {
                                for (Button b : this.purpleAstronautTrackB) {
                                    b.setDisable(true);
                                }
                                this.purpleAstronautB.setDisable(true);
                            } else if (i == 1) {
                                for (Button b : this.greenAstronautTrackB) {
                                    b.setDisable(true);
                                }
                                this.greenAstronautB.setDisable(true);
                            } else if (i == 2) {
                                for (Button b : this.yellowAstronautTrackB) {
                                    b.setDisable(true);
                                }
                                this.yellowAstronautB.setDisable(true);
                            } else if (i == 3) {
                                for (Button b : this.redAstronautTrackB) {
                                    b.setDisable(true);
                                }
                                this.redAstronautB.setDisable(true);
                            } else if (i == 4) {
                                continue;
                            }
                        }
                        break;
                }
            } else { // Disabling all astronaut buttons when no winner
                this.purpleAstronautB.setDisable(true);
                this.greenAstronautB.setDisable(true);
                this.yellowAstronautB.setDisable(true);
                this.redAstronautB.setDisable(true);
                this.blueAstronautB.setDisable(true);
            }

        } else if (this.engine.getGameDeck().size() == 0) {

            this.gameDiscardPileB.setDisable(false);

        } else if (!this.engine.gameOver()) {

            this.saved = false;
            this.affected = false;
            this.discardCount = 0;
            this.discardCount2 = 0;
            this.actionsPlayed = 0;

            this.engine.startTurn();
            this.currentPlayerLabel.setText(this.engine.getCurrentPlayer().toString());
            setupAllPlayersLabel();
            enableAstronautButton(this.engine.getCurrentPlayer().toString());

        }
    }

    //////////////////// PHASE-1 ////////////////////////
    /**
     * Method for drawing card from game deck.
     * Simulates phase 1 of an astronaut's turn.
     */
    private void drawGameCard() {
        // Phase 1 of player's turn
        this.drawCardB.setDisable(false);

        this.drawCardB.setOnAction(e -> {
            try {
                GameApp.playDrawCardSound();
            } catch (Exception mm) {
                mm.printStackTrace();
            }
            // Drawing game card and adding to hand
            Card drawn = getGameCard();
            this.engine.getCurrentPlayer().addToHand(drawn);

            // Setting the stage and scene
            Scene sc = setupDrawCardScene(this.engine.getCurrentPlayer().toString(), drawn.toString());
            this.astronautTurnSimulation.setTitle("Draw Game Card");
            this.astronautTurnSimulation.setX(702);
            this.astronautTurnSimulation.setY(280);
            this.astronautTurnSimulation.setScene(sc);
            this.astronautTurnSimulation.showAndWait();

        });
    }

    //////////////////// INTERIM PHASE ////////////////////////
    /**
     * Phase for whether to play actions or skip to phase 3 of turn
     */
    private void interimPhase() {
        Scene interimScene = setupInterimPhase(this.engine.getCurrentPlayer().toString());
        this.astronautTurnSimulation.setTitle("All Game Cards");
        this.astronautTurnSimulation.setScene(interimScene);
        this.astronautTurnSimulation.setX(602);
        this.astronautTurnSimulation.setY(330);
    }

    //////////////////// PHASE-2 ////////////////////////
    /**
     * Play action cards simulator window.
     * Represents phase 2 of an astronauts turn.
     */
    private void playActionCards() {

        // Clear checkBoxes ArrayList
        this.checkBoxes.clear();

        // Background rectangle
        Rectangle rBcg = new Rectangle(0, 0, 450, 320);
        rBcg.setFill(Color.rgb(54, 69, 79));

        // Player name label
        Label playerNameLabel = new Label(this.engine.getCurrentPlayer().toString());
        playerNameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        playerNameLabel.setTextFill(Color.CYAN);

        // Label for "your action cards"
        Label yourActionsLabel = new Label("Your playable action cards:");
        yourActionsLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        yourActionsLabel.setTextFill(Color.RED);

        // Back button
        Button backB = new Button("Back");
        backB.setFont(Font.font("Verdana", 20));
        backB.setOnAction(e -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception gb) {
                gb.printStackTrace();
            }
            interimPhase();
        });

        // Play button
        Button playB = new Button("Play");
        playB.setFont(Font.font("Verdana", 20));
        playB.setOnAction(e -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception fgh) {
                fgh.printStackTrace();
            }

            // Checking if only 1 checkBox is selected
            int checkCount = 0;
            for (CheckBox c : this.checkBoxes) {
                if (c.isSelected()) {
                    checkCount++;
                }
            }
            if (checkCount != 1) {
                for (CheckBox cbx : this.checkBoxes) {
                    cbx.setSelected(false);
                }
                AlertBox a = new AlertBox("Invalid card selection!");
                a.showAlertBox();
            } else {
                boolean test = true;
                for (CheckBox c : this.checkBoxes) {
                    if (c.getText().equals(GameDeck.OXYGEN_SIPHON) && c.isSelected()
                            && this.engine.getAliveCount() > 1) {
                        oxygenSiphon();
                        break;
                    } else if (c.getText().equals(GameDeck.HACK_SUIT) && c.isSelected()
                            && this.engine.getAliveCount() > 1) {
                        hackSuit();
                        break;
                    } else if (c.getText().equals(GameDeck.TRACTOR_BEAM) && c.isSelected()
                            && this.engine.getAliveCount() > 1) {
                        tractorBeam();
                        break;
                    } else if (c.getText().equals(GameDeck.ROCKET_BOOSTER) && c.isSelected()) {
                        rocketBooster();
                        test = false;
                        break;
                    } else if (c.getText().equals(GameDeck.LASER_BLAST) && c.isSelected()
                            && this.engine.getAliveCount() > 1) {
                        laserBlast();
                        break;
                    } else if (c.getText().equals(GameDeck.HOLE_IN_SUIT) && c.isSelected()
                            && this.engine.getAliveCount() > 1) {
                        holeInSuit();
                        break;
                    } else if (c.getText().equals(GameDeck.TETHER) && c.isSelected()
                            && this.engine.getAliveCount() > 1) {
                        tether();
                        break;
                    }
                }

                if (this.engine.getAliveCount() <= 1 && test) {
                    for (CheckBox cx : this.checkBoxes) {
                        cx.setSelected(false);
                    }
                    AlertBox a = new AlertBox("Card not playable. Only you are alive!");
                    a.showAlertBox();
                }

            }
        });

        // HBoxes
        HBox hb1 = new HBox(20);
        hb1.setAlignment(Pos.CENTER);
        hb1.setMaxHeight(200);
        hb1.setMaxWidth(440);

        HBox hb2 = new HBox(20);
        hb2.setAlignment(Pos.CENTER);
        hb2.setMaxHeight(200);
        hb2.setMaxWidth(440);

        HBox hb3 = new HBox(20);
        hb3.setAlignment(Pos.CENTER);
        hb3.setMaxHeight(200);
        hb3.setMaxWidth(440);

        // Checkboxes
        int count = 0;
        for (Card card : this.engine.getCurrentPlayer().getActions()) {
            if (card.toString().equals(GameDeck.SHIELD)) {
                continue;
            } else if (count < 3) {
                count++;
                CheckBox cbx = new CheckBox(card.toString());
                cbx.setFont(Font.font("Verdana"));
                cbx.setTextFill(Color.YELLOW);
                this.checkBoxes.add(cbx);
                hb1.getChildren().add(cbx);
            } else if (count < 6) {
                count++;
                CheckBox cbx = new CheckBox(card.toString());
                cbx.setFont(Font.font("Verdana"));
                cbx.setTextFill(Color.YELLOW);
                this.checkBoxes.add(cbx);
                hb2.getChildren().add(cbx);
            } else {
                CheckBox cbx = new CheckBox(card.toString());
                cbx.setFont(Font.font("Verdana"));
                cbx.setTextFill(Color.YELLOW);
                this.checkBoxes.add(cbx);
                hb3.getChildren().add(cbx);
            }
        }

        // Hbox
        HBox buttonsHB = new HBox(20, backB, playB);
        buttonsHB.setAlignment(Pos.CENTER);

        // Vbox
        VBox v = new VBox(20, playerNameLabel, yourActionsLabel, hb1, hb2, hb3, buttonsHB);
        v.setAlignment(Pos.CENTER);

        // StackPane
        StackPane sPane = new StackPane(rBcg, v);

        // Setting the scene
        Scene s = new Scene(sPane, 450, 320);
        s.getStylesheets().add(getClass().getResource("CSS/mainGameStyles.css").toExternalForm());

        // Stage
        this.astronautTurnSimulation.setTitle("Play Action Cards");
        this.astronautTurnSimulation.setScene(s);
        this.astronautTurnSimulation.setX(627);
        this.astronautTurnSimulation.setY(230);

    }

    //////////////////// PHASE-3 ////////////////////////
    /**
     * Method to breathe or travel.
     * Represents phase 3 of astronaut turn.
     */
    private void breatheOrTravel() {
        Scene phase3Scene = setupPhase3Scene(this.engine.getCurrentPlayer().toString());
        this.astronautTurnSimulation.setTitle("Breathe or Travel");
        this.astronautTurnSimulation.setScene(phase3Scene);
        this.astronautTurnSimulation.setX(702);
        this.astronautTurnSimulation.setY(420);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////// HELPER METHODS BELOW ////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * helper method; prevents duplication of file name while saving
     * 
     * @param folder_path path to folder
     * @param fileName    name of file
     * @return true if name available; false if name is already in use
     */
    public boolean checkFileDuplication(String folder_path, String fileName) {
        File folder = new File(folder_path);
        File[] fileList = folder.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].isFile()) {
                if (fileList[i].getName().equals(fileName)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Helper method; displays names of all players in game
     */
    private void setupAllPlayersLabel() {
        ArrayList<String> asts = GameApp.playerNames;
        List<Astronaut> astros = this.engine.getAllPlayers();

        for (int i = 0; i < asts.size(); i++) {
            String toTest = asts.get(i) + " (is dead)";
            String normal = asts.get(i);
            for (Astronaut ast : astros) {
                if (toTest.equals(ast.toString())) {
                    asts.remove(i);
                    asts.add(i, normal + " \u00D7");
                }
            }
        }

        String allPlayers = "";
        for (String a : asts) {
            allPlayers += "\n" + a + "\n";
        }
        this.allPlayersLabel.setText(allPlayers);

        switch (asts.size()) {
            case 3:
                this.allPlayersRectangle.setHeight(allPlayerRectangleHeight + 45);
                break;
            case 4:
                this.allPlayersRectangle.setHeight(allPlayerRectangleHeight + 90);
                break;
            case 5:
                this.allPlayersRectangle.setHeight(allPlayerRectangleHeight + 120);
                break;
        }
    }

    /**
     * Helper method; returns card from game deck
     */
    private Card getGameCard() {
        return this.engine.getGameDeck().draw();
    }

    /**
     * Helper method; Sets the draw card scene based on these paramenters:
     * 
     * @param playerName      name of astronaut
     * @param cardName        name of drawn game card
     * @param cardDescription description of drawn game card
     * @return a scene
     */
    private Scene setupDrawCardScene(String playerName, String cardName) {

        // Rectangle for the background
        Rectangle r = new Rectangle(0, 0, 250, 400);
        r.setFill(Color.rgb(54, 69, 79));

        // Setting the player name
        Label currentPlayerName = new Label(playerName);
        currentPlayerName.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        currentPlayerName.setTextFill(Color.CYAN);

        // Setting Label for "Card drawn"
        Label cardDrawnLabel = new Label("Card Drawn");
        cardDrawnLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        cardDrawnLabel.setTextFill(Color.RED);

        // Setting the game card graphic
        ImageView gameCardImageView = giveCardImageView(cardName);

        // Next button
        Button nextB = new Button("Next");
        nextB.setFont(Font.font("Verdana", 17));
        nextB.setTextFill(Color.CYAN);
        nextB.setOnAction(e -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception adi) {
                adi.printStackTrace();
            }
            interimPhase();
        });

        // VBox for holding all elements
        VBox vB = new VBox(5, currentPlayerName, cardDrawnLabel, gameCardImageView, nextB);
        vB.setAlignment(Pos.CENTER);

        // StackPane to hold background with front elements
        StackPane sP = new StackPane(r, vB);

        // Setting the scene
        Scene s = new Scene(sP, 250, 400);
        s.getStylesheets().add(getClass().getResource("CSS/menuAndNewGameStyles.css").toExternalForm());

        return s;
    }

    /**
     * sets a custom interim scene for a player
     * 
     * @param playerName name of astronaut
     * @return a customized scene for the astronaut
     */
    private Scene setupInterimPhase(String playerName) {
        // Background rectangle
        Rectangle rBackground = new Rectangle(0, 0, 450, 250);
        rBackground.setFill(Color.rgb(54, 69, 79));

        // Player name label
        Label currentPlayerName = new Label(playerName);
        currentPlayerName.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        currentPlayerName.setTextFill(Color.CYAN);

        // Label for heading "Your game cards"
        Label headingLabel = new Label("Your game cards:");
        headingLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        headingLabel.setTextFill(Color.RED);
        // Label for players game cards
        Label playerGameCardsLabel = new Label(this.engine.getCurrentPlayer().getHandStr());
        playerGameCardsLabel.setPrefSize(430, 80); // CHECK THIS SIZE
        playerGameCardsLabel.setFont(Font.font("Verdana", FontWeight.SEMI_BOLD, 16));
        playerGameCardsLabel.setTextFill(Color.WHITE);
        playerGameCardsLabel.setWrapText(true);

        // Button for playing actions
        Button playActionsB = new Button("Play Actions");

        // When to enable and disable it
        if (this.engine.getAllPlayers().size() == 2 && this.actionsPlayed > 0) {
            playActionsB.setDisable(true);
        } else if (this.engine.getCurrentPlayer().getTrack().size() == 0) {
            playActionsB.setDisable(false);
        } else if (this.engine.getCurrentPlayer().hasMeltedEyeballs()) {
            playActionsB.setDisable(true);
        } else {
            playActionsB.setDisable(false);
        }
        if (this.engine.getCurrentPlayer().getActionsStr(true, true).equals("")) {
            playActionsB.setDisable(true);
        }
        playActionsB.setFont(Font.font("Verdana", 18));
        playActionsB.setOnAction(e -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception gg) {
                gg.printStackTrace();
            }
            playActionCards();

        });

        // Button for skipping
        Button skipB = new Button("Skip");
        skipB.setFont(Font.font("Verdana", 18));
        skipB.setOnAction(e -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception gg) {
                gg.printStackTrace();
            }
            breatheOrTravel();
        });

        // HBox for holding buttons
        HBox hB = new HBox(20, playActionsB, skipB);
        hB.setAlignment(Pos.BOTTOM_CENTER);

        // VBox for labels and buttons
        VBox vB = new VBox(25, currentPlayerName, headingLabel, playerGameCardsLabel, hB);
        vB.setAlignment(Pos.CENTER);

        // StackPane for holding background with all other forward elements
        StackPane sP = new StackPane(rBackground, vB);

        // Setting the scene
        Scene s = new Scene(sP, 450, 250);
        s.getStylesheets().add(getClass().getResource("CSS/menuAndNewGameStyles.css").toExternalForm());

        return s;

    }

    /**
     * Helper method; sets the scene for the final phase of an astronaut's turn.
     * 
     * @param playerName name of astronaut
     * @return customized scene
     */
    private Scene setupPhase3Scene(String playerName) {

        // Rectangle for the background
        Rectangle r = new Rectangle(0, 0, 300, 180);
        r.setFill(Color.rgb(54, 69, 79));

        // Setting the player name
        Label currentPlayerName = new Label(playerName);
        currentPlayerName.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        currentPlayerName.setTextFill(Color.CYAN);

        // Label for oxygen cards
        String[] cArray = this.engine.getCurrentPlayer().getHandStr().split(";");
        Label oxygenLabel = new Label(cArray[0]);
        oxygenLabel.setFont(Font.font("Verdana", 16));
        oxygenLabel.setTextFill(Color.WHITE);
        oxygenLabel.setWrapText(true);

        // Label for "you can now"
        Label choicesLabel = new Label("You can now:");
        choicesLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        choicesLabel.setTextFill(Color.RED);

        // Breathe button
        Button breatheB = new Button("Breathe");
        breatheB.setFont(Font.font("Verdana", 22));
        breatheB.setOnAction(e -> {
            try {
                GameApp.playBreatheSound();
            } catch (Exception ko) {
                ko.printStackTrace();
            }
            // astronaut breathing
            int oxygenRemaining = this.engine.getCurrentPlayer().breathe();

            // changing button to dead astronaut if dead after breathing
            if (oxygenRemaining <= 0) {
                setDeadAstronautGraphic(playerName);

                int index = GameApp.playerNames.indexOf(playerName);
                switch (index) {
                    case 0:
                        for (Button b : this.purpleAstronautTrackB) {
                            b.setDisable(true);
                        }
                        break;
                    case 1:
                        for (Button b : this.greenAstronautTrackB) {
                            b.setDisable(true);
                        }
                        break;
                    case 2:
                        for (Button b : this.yellowAstronautTrackB) {
                            b.setDisable(true);
                        }
                        break;
                    case 3:
                        for (Button b : this.redAstronautTrackB) {
                            b.setDisable(true);
                        }
                        break;
                    case 4:
                        for (Button b : this.blueAstronautTrackB) {
                            b.setDisable(true);
                        }
                        break;
                }
            }

            // ending turn
            disableAstronautButton(playerName);
            this.engine.endTurn();

            // closing astronautTurn simulator stage
            this.astronautTurnSimulation.close();

            // checking game conditions
            checkGameConditions();
        });

        // TRAVEL BUTTON
        Button travelB = new Button("Travel");
        travelB.setFont(Font.font("Verdana", 22));

        // When to enable and disable it
        if (this.engine.getCurrentPlayer().hasCard(GameDeck.OXYGEN_2) == 0
                && this.engine.getCurrentPlayer().hasCard(GameDeck.OXYGEN_1) < 2) {
            travelB.setDisable(true);
        } else {
            travelB.setDisable(false);
        }

        // Function of the button
        travelB.setOnAction(e -> {
            try {
                GameApp.playTravelSound();
            } catch (Exception ok) {
                ok.printStackTrace();
            }
            int index = GameApp.playerNames.indexOf(playerName);

            // astronaut travelling

            Card sCard = this.engine.getSpaceDeck().draw(); // space card drawn
            if (sCard.toString().equals(SpaceDeck.GRAVITATIONAL_ANOMALY)) {
                this.engine.getSpaceDiscard().add(sCard);
            } else {
                this.engine.getCurrentPlayer().addToTrack(sCard);
            }

            switch (index) {
                case 0:
                    moveAstronautB(this.purpleAstronautB, sCard, this.purpleAstronautTrackB, 0, 0);
                    if (!this.engine.getCurrentPlayer().isAlive() && !this.affected) {
                        this.purpleAstronautB.setDisable(true);
                        this.drawCardB.setDisable(true);
                        setDeadAstronautGraphic(playerName);
                        for (Button b : this.purpleAstronautTrackB) {
                            b.setDisable(true);
                        }
                    }
                    break;
                case 1:
                    moveAstronautB(this.greenAstronautB, sCard, this.greenAstronautTrackB, 0, 1);
                    if (!this.engine.getCurrentPlayer().isAlive() && !this.affected) {
                        this.greenAstronautB.setDisable(true);
                        this.drawCardB.setDisable(true);
                        setDeadAstronautGraphic(playerName);
                        for (Button b : this.greenAstronautTrackB) {
                            b.setDisable(true);
                        }
                    }
                    break;
                case 2:
                    moveAstronautB(this.yellowAstronautB, sCard, this.yellowAstronautTrackB, 0, 2);
                    if (!this.engine.getCurrentPlayer().isAlive() && !this.affected) {
                        this.yellowAstronautB.setDisable(true);
                        this.drawCardB.setDisable(true);
                        setDeadAstronautGraphic(playerName);
                        for (Button b : this.yellowAstronautTrackB) {
                            b.setDisable(true);
                        }
                    }
                    break;
                case 3:
                    moveAstronautB(this.redAstronautB, sCard, this.redAstronautTrackB, 0, 3);
                    if (!this.engine.getCurrentPlayer().isAlive() && !this.affected) {
                        this.redAstronautB.setDisable(true);
                        this.drawCardB.setDisable(true);
                        setDeadAstronautGraphic(playerName);
                        for (Button b : this.redAstronautTrackB) {
                            b.setDisable(true);
                        }
                    }
                    break;
                case 4:
                    moveAstronautB(this.blueAstronautB, sCard, this.blueAstronautTrackB, 0, 4);
                    if (!this.engine.getCurrentPlayer().isAlive() && !this.affected) {
                        this.blueAstronautB.setDisable(true);
                        this.drawCardB.setDisable(true);
                        setDeadAstronautGraphic(playerName);
                        for (Button b : this.blueAstronautTrackB) {
                            b.setDisable(true);
                        }
                    }
                    break;
            }

            if (this.engine.getCurrentPlayer().isAlive() && !this.affected) {
                disableAstronautButton(this.engine.getCurrentPlayer().toString());
            }

            if (!this.affected) {
                // ending turn
                this.engine.endTurn();

                // closing astronautTurn simulator stage
                this.astronautTurnSimulation.close();

                // checking game conditions
                checkGameConditions();
            }
        });

        // HBox to hold buttons
        HBox buttonsHB = new HBox(20, breatheB, travelB);
        buttonsHB.setAlignment(Pos.BOTTOM_CENTER);

        // VBox to hold labels and buttons
        VBox vBOX = new VBox(15, currentPlayerName, oxygenLabel, choicesLabel, buttonsHB);
        vBOX.setAlignment(Pos.CENTER);

        // StackPane to hold background with front elements
        StackPane sPane = new StackPane(r, vBOX);

        // Setting the scene
        Scene scene = new Scene(sPane, 300, 180);
        scene.getStylesheets().add(getClass().getResource("CSS/menuAndNewGameStyles.css").toExternalForm());

        return scene;

    }

    /**
     * Helper method; returns the appropriate ImageView object based on the card
     * drawn.
     * 
     * @param cardName name of card
     * @return imageView object
     */
    private ImageView giveCardImageView(String cardName) {
        ImageView imV = new ImageView();
        switch (cardName) {
            case "Oxygen(1)":
                Image i1 = new Image("GUI_Images/Game_Cards/Oxygen_1.png");
                imV.setImage(i1);
                return imV;
            case "Oxygen(2)":
                Image i2 = new Image("GUI_Images/Game_Cards/Oxygen_2.png");
                imV.setImage(i2);
                return imV;
            case "Hack suit":
                Image i3 = new Image("GUI_Images/Game_Cards/HackSuit.png");
                imV.setImage(i3);
                return imV;
            case "Hole in suit":
                Image i4 = new Image("GUI_Images/Game_Cards/HoleInSuit.png");
                imV.setImage(i4);
                return imV;
            case "Laser blast":
                Image i5 = new Image("GUI_Images/Game_Cards/LaserBlast.png");
                imV.setImage(i5);
                return imV;
            case "Oxygen siphon":
                Image i6 = new Image("GUI_Images/Game_Cards/OxygenSiphon.png");
                imV.setImage(i6);
                return imV;
            case "Rocket booster":
                Image i7 = new Image("GUI_Images/Game_Cards/RocketBooster.png");
                imV.setImage(i7);
                return imV;
            case "Shield":
                Image i8 = new Image("GUI_Images/Game_Cards/Shield.png");
                imV.setImage(i8);
                return imV;
            case "Tether":
                Image i9 = new Image("GUI_Images/Game_Cards/Tether.png");
                imV.setImage(i9);
                return imV;
            case "Tractor beam":
                Image i10 = new Image("GUI_Images/Game_Cards/TractorBeam.png");
                imV.setImage(i10);
                return imV;
        }
        return null;
    }

    /**
     * Helper method; moves the button when travelling
     * 
     * @param astronautButton button of astronaut
     * @param y               : 0 when called to travel, 1 when called by
     *                        hyperspace, 2 when called by rocketbooster/tether
     * 
     * @param ID              : 0 -> purple astronaut, 1 -> green astronaut, 2 ->
     *                        yellow astronaut, 3 -> red astronaut, 4 -> blue
     *                        astronaut
     */
    private void moveAstronautB(Button astronautButton, Card spaceC, ArrayList<Button> astronautButtonList, int y,
            int ID) {

        if (!spaceC.toString().equals(SpaceDeck.GRAVITATIONAL_ANOMALY)) {

            // Getting coords for button
            double xCoordsB = astronautButton.getLayoutX() - 145;
            double yCoordsB = astronautButton.getLayoutY() + 20;

            // Creating new button
            String name = spaceC.toString();
            String[] newName = name.split(" ");
            String toSet = "";
            for (String s : newName) {
                toSet += s + "\n";
            }
            Button b = new Button(toSet);
            b.setTextFill(Color.CYAN);
            b.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
            b.setPrefSize(170, 90);
            b.setLayoutX(xCoordsB);
            b.setLayoutY(yCoordsB);
            b.setId("spaceBs");
            b.setOnAction(e -> {
                try {
                    GameApp.playButtonSound();
                } catch (Exception v) {
                    v.printStackTrace();
                }
                spaceCardGUI(spaceC);
            });

            // Adding button to list and pane
            astronautButtonList.add(b);
            this.spaceCardsPane.getChildren().add(b);

            // Moving button up
            double Ycoords = astronautButton.getLayoutY();
            Ycoords -= 130;
            astronautButton.setLayoutY(Ycoords);

        }

        if (y == 0) {
            // Hacking oxygen worth value of atmost 2 to complete travel
            if (this.engine.getCurrentPlayer().hasCard(GameDeck.OXYGEN_2) > 0) {
                Card c = this.engine.getCurrentPlayer().hack(GameDeck.OXYGEN_2);
                this.engine.getGameDiscard().add(c);

            } else if (this.engine.getCurrentPlayer().hasCard(GameDeck.OXYGEN_1) > 1) {
                Card c1 = this.engine.getCurrentPlayer().hack(GameDeck.OXYGEN_1);
                Card c2 = this.engine.getCurrentPlayer().hack(GameDeck.OXYGEN_1);
                this.engine.getGameDiscard().add(c1);
                this.engine.getGameDiscard().add(c2);

            } else if (this.engine.getCurrentPlayer().hasCard(GameDeck.OXYGEN_1) > 0) {
                Card c1 = this.engine.getCurrentPlayer().hack(GameDeck.OXYGEN_1);
                this.engine.getGameDiscard().add(c1);
            }
        }
        if (y == 1 && spaceC.toString().equals(SpaceDeck.BLANK_SPACE)) {

            // Checking if alive or not after drawing
            if (!this.engine.getCurrentPlayer().isAlive()) {

                astronautButton.setDisable(true);
                this.drawCardB.setDisable(true);

                String name = this.engine.getCurrentPlayer().toString().replace(" (is dead)", "");
                setDeadAstronautGraphic(name);

                for (Button b : astronautButtonList) {
                    b.setDisable(true);
                }
            }

            // ending turn
            disableAstronautButton(this.engine.getCurrentPlayer().toString());
            this.engine.endTurn();

            // closing astronautTurn simulator stage
            this.astronautTurnSimulation.close();

            // checking game conditions
            checkGameConditions();
        }
        if (y == 2 && spaceC.toString().equals(SpaceDeck.BLANK_SPACE)) {
            if (this.engine.getCurrentPlayer().getTrack().size() != 6) {
                interimPhase();
            } else {
                // Checking if alive or not after drawing
                if (!this.engine.getCurrentPlayer().isAlive()) {

                    astronautButton.setDisable(true);
                    this.drawCardB.setDisable(true);

                    String name = this.engine.getCurrentPlayer().toString().replace(" (is dead)", "");
                    setDeadAstronautGraphic(name);

                    for (Button b : astronautButtonList) {
                        b.setDisable(true);
                    }
                }

                // disabling buttons
                disableAstronautButton(this.engine.getCurrentPlayer().toString());

                // ending turn
                this.engine.endTurn();

                // closing astronautTurn simulator stage
                this.astronautTurnSimulation.close();

                // checking game conditions
                checkGameConditions();
            }
        }

        switch (spaceC.toString()) {
            case SpaceDeck.USEFUL_JUNK:
                usefulJunk(astronautButton, astronautButtonList, y);
                break;
            case SpaceDeck.MYSTERIOUS_NEBULA:
                mysteriousNebula(astronautButton, astronautButtonList, y);
                break;
            case SpaceDeck.HYPERSPACE:
                if (this.engine.getCurrentPlayer().getTrack().size() != 6) {
                    hyperspace(astronautButton, astronautButtonList, y, ID);
                } else if (y == 2 && this.engine.getCurrentPlayer().getTrack().size() == 6) {

                    // Checking if alive or not after drawing
                    if (!this.engine.getCurrentPlayer().isAlive()) {

                        astronautButton.setDisable(true);
                        this.drawCardB.setDisable(true);

                        String name = this.engine.getCurrentPlayer().toString().replace(" (is dead)", "");
                        setDeadAstronautGraphic(name);

                        for (Button b : astronautButtonList) {
                            b.setDisable(true);
                        }
                    }

                    // disabling buttons
                    disableAstronautButton(this.engine.getCurrentPlayer().toString());

                    // ending turn
                    this.engine.endTurn();

                    // closing astronautTurn simulator stage
                    this.astronautTurnSimulation.close();

                    // checking game conditions
                    checkGameConditions();
                }
                break;
            case SpaceDeck.METEOROID:
                if (this.engine.getCurrentPlayer().isAlive())
                    meteoroid(astronautButton, astronautButtonList, y);
                break;
            case SpaceDeck.COSMIC_RADIATION:
                if (this.engine.getCurrentPlayer().isAlive())
                    cosmicRadiation(astronautButton, astronautButtonList, y);
                break;
            case SpaceDeck.ASTEROID_FIELD:
                if (this.engine.getCurrentPlayer().isAlive()) {
                    asteroidField(astronautButton, astronautButtonList, y);
                }
                break;
            case SpaceDeck.GRAVITATIONAL_ANOMALY:
                gravitationalAnomaly(astronautButton, astronautButtonList, y);
                break;
            case SpaceDeck.WORMHOLE:
                if (this.engine.getCurrentPlayer().isAlive()) {
                    wormhole(ID, y);
                }
                break;
            case SpaceDeck.SOLAR_FLARE:
                if (this.engine.getCurrentPlayer().isAlive()) {
                    solarFlare(y);
                }
                break;
        }

    }

    /**
     * GUI for space card on track.
     * 
     * @param spaceCard space card
     */
    private void spaceCardGUI(Card spaceCard) {

        Stage s = new Stage();
        s.initOwner(this.mainGameStage);
        s.initModality(Modality.APPLICATION_MODAL);
        s.initStyle(StageStyle.UTILITY);
        s.setTitle("Space Card Information");

        // Background
        Rectangle rB = new Rectangle(0, 0, 400, 250);
        rB.setFill(Color.rgb(54, 69, 79));

        // Heading
        Label hLabel = new Label(spaceCard.toString());
        hLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        hLabel.setTextFill(Color.CYAN);

        // Description
        Label description = new Label(spaceCard.getDescription());
        description.setWrapText(true);
        description.setFont(Font.font("Verdana", 16));
        description.setPrefSize(380, 165);
        description.setAlignment(Pos.CENTER);
        description.setTextFill(Color.YELLOW);

        // Close button
        Button closeB = new Button("Close");
        closeB.setFont(Font.font("Verdana", 20));
        closeB.setOnAction(e -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception error) {
                error.printStackTrace();
            }
            s.close();
        });

        // Vbox
        VBox v = new VBox(15, hLabel, description, closeB);
        v.setAlignment(Pos.CENTER);

        // Stackpane
        StackPane root = new StackPane(rB, v);

        // Scene
        Scene scn = new Scene(root, 400, 250);
        scn.getStylesheets().add(getClass().getResource("CSS/mainGameStyles.css").toExternalForm());

        s.setX(577);
        s.setY(240);
        s.setResizable(false);
        s.setScene(scn);
        s.show();
    }

    /**
     * Sets graphic of button to dead astronaut
     * 
     * @param playerName name of player
     */
    private void setDeadAstronautGraphic(String playerName) {
        int index = GameApp.playerNames.indexOf(playerName);

        switch (index) {
            case 0:
                this.purpleAstronautB.setGraphic(new ImageView(new Image("GUI_Images/deadAstronaut.png")));
                for (Button bt : this.purpleAstronautTrackB)
                    bt.setDisable(true);
                break;
            case 1:
                this.greenAstronautB.setGraphic(new ImageView(new Image("GUI_Images/deadAstronaut.png")));
                for (Button bt : this.greenAstronautTrackB)
                    bt.setDisable(true);
                break;
            case 2:
                this.yellowAstronautB.setGraphic(new ImageView(new Image("GUI_Images/deadAstronaut.png")));
                for (Button bt : this.yellowAstronautTrackB)
                    bt.setDisable(true);
                break;
            case 3:
                this.redAstronautB.setGraphic(new ImageView(new Image("GUI_Images/deadAstronaut.png")));
                for (Button bt : this.redAstronautTrackB)
                    bt.setDisable(true);
                break;
            case 4:
                this.blueAstronautB.setGraphic(new ImageView(new Image("GUI_Images/deadAstronaut.png")));
                for (Button bt : this.blueAstronautTrackB)
                    bt.setDisable(true);
                break;
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////// Space Cards Implementation ////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Implements useful junk space card's functionality
     */
    private void usefulJunk(Button astronautButton, ArrayList<Button> astronautButtonList, int y) {
        if (y != 2)
            this.affected = true;

        // Background
        Rectangle r = new Rectangle(0, 0, 450, 250);
        r.setFill(Color.rgb(54, 69, 79));

        // Player name
        Label playerNameLabel = new Label();
        if (this.engine.getCurrentPlayer().isAlive()) {
            playerNameLabel.setText(this.engine.getCurrentPlayer().toString());
        } else {
            playerNameLabel.setText(this.engine.getCurrentPlayer().toString().replace(" (is dead)", ""));
        }
        playerNameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        playerNameLabel.setTextFill(Color.CYAN);

        // Label
        Label titleL = new Label("You drew a Useful Junk space card. Draw an additional game card!");
        titleL.setFont(Font.font("Verdana", 23));
        titleL.setTextFill(Color.YELLOW);
        titleL.setWrapText(true);
        titleL.setMaxWidth(390);
        titleL.setAlignment(Pos.CENTER);

        // Draw button
        Button drawB = new Button("Draw");
        drawB.setFont(Font.font("Verdana", 22));
        drawB.setOnAction(e -> {
            try {
                GameApp.playDrawCardSound();
            } catch (Exception gg) {
                gg.printStackTrace();
            }

            Card toDraw = null;
            if (this.engine.getGameDeck().size() == 0) {
                toDraw = this.engine.getGameDiscard().draw();
            } else {
                toDraw = getGameCard();
            }
            this.engine.getCurrentPlayer().addToHand(toDraw);

            // Background
            Rectangle rB = new Rectangle(0, 0, 250, 400);
            rB.setFill(Color.rgb(54, 69, 79));

            // ImageView object for card drawn
            ImageView cardImv = giveCardImageView(toDraw.toString());

            // Player name
            Label playerNameL = new Label();
            if (this.engine.getCurrentPlayer().isAlive()) {
                playerNameL.setText(this.engine.getCurrentPlayer().toString());
            } else {
                playerNameL.setText(this.engine.getCurrentPlayer().toString().replace(" (is dead)", ""));
            }
            playerNameL.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
            playerNameL.setTextFill(Color.CYAN);

            // Label for card drawn
            Label cardDrawnLabel = new Label("Card Drawn");
            cardDrawnLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
            cardDrawnLabel.setTextFill(Color.RED);

            // End turn button
            Button endTurnB = new Button("End Turn");
            endTurnB.setFont(Font.font("Verdana", 20));
            endTurnB.setOnAction(ev -> {
                try {
                    GameApp.playButtonSound();
                } catch (Exception vm) {
                    vm.printStackTrace();
                }

                // Checking if alive or not after drawing
                if (!this.engine.getCurrentPlayer().isAlive()) {

                    astronautButton.setDisable(true);
                    this.drawCardB.setDisable(true);

                    String name = this.engine.getCurrentPlayer().toString().replace(" (is dead)", "");
                    setDeadAstronautGraphic(name);

                    for (Button b : astronautButtonList) {
                        b.setDisable(true);
                    }
                }

                // disabling buttons
                disableAstronautButton(this.engine.getCurrentPlayer().toString());

                // ending turn
                this.engine.endTurn();

                // closing astronautTurn simulator stage
                this.astronautTurnSimulation.close();

                // checking game conditions
                checkGameConditions();
            });

            // Next button (if called by Rocket booster)
            Button nextB = new Button("Next");
            nextB.setFont(Font.font("Verdana", 20));
            nextB.setOnAction(event -> {
                try {
                    GameApp.playButtonSound();
                } catch (Exception cz) {
                    cz.printStackTrace();
                }
                interimPhase();
            });

            // VBox
            VBox vBox = new VBox(5, playerNameL, cardDrawnLabel, cardImv);
            if (y == 2 && this.engine.getCurrentPlayer().getTrack().size() != 6)
                vBox.getChildren().add(nextB);
            else
                vBox.getChildren().add(endTurnB);
            vBox.setAlignment(Pos.CENTER);

            // StackPane
            StackPane sP = new StackPane(rB, vBox);

            Scene nScn = new Scene(sP, 250, 400);
            nScn.getStylesheets().add(getClass().getResource("CSS/mainGameStyles.css").toExternalForm());

            this.astronautTurnSimulation.setScene(nScn);
            this.astronautTurnSimulation.setTitle("Useful Junk");
            this.astronautTurnSimulation.setX(702);
            this.astronautTurnSimulation.setY(280);
        });

        // VBox
        VBox v = new VBox(25, playerNameLabel, titleL, drawB);
        v.setAlignment(Pos.CENTER);

        // StackPane
        StackPane s = new StackPane(r, v);

        // Setting the scene
        Scene sn = new Scene(s, 450, 250);
        sn.getStylesheets().add(getClass().getResource("CSS/mainGameStyles.css").toExternalForm());

        // Setting the stage
        this.astronautTurnSimulation.setScene(sn);
        this.astronautTurnSimulation.setTitle("Useful Junk");
        this.astronautTurnSimulation.setX(602);
        this.astronautTurnSimulation.setY(290);

    }

    /**
     * Implements mysterious nebula
     */
    private void mysteriousNebula(Button astronautButton, ArrayList<Button> astronautButtonList, int y) {
        if (y != 2)
            this.affected = true;

        // Background
        Rectangle r = new Rectangle(0, 0, 450, 250);
        r.setFill(Color.rgb(54, 69, 79));

        // Player name
        Label playerNameLabel = new Label();
        if (this.engine.getCurrentPlayer().isAlive()) {
            playerNameLabel.setText(this.engine.getCurrentPlayer().toString());
        } else {
            playerNameLabel.setText(this.engine.getCurrentPlayer().toString().replace(" (is dead)", ""));
        }
        playerNameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        playerNameLabel.setTextFill(Color.CYAN);

        // Label
        Label titleL = new Label("You drew a Mysterious Nebula space card. Draw 2 additional game cards!");
        titleL.setFont(Font.font("Verdana", 23));
        titleL.setTextFill(Color.YELLOW);
        titleL.setWrapText(true);
        titleL.setMaxWidth(390);
        titleL.setAlignment(Pos.CENTER);

        // Draw button
        Button drawB = new Button("Draw");
        drawB.setFont(Font.font("Verdana", 22));
        drawB.setOnAction(e -> {
            try {
                GameApp.playDrawCardSound();
            } catch (Exception gf) {
                gf.printStackTrace();
            }

            Card c1 = null;
            Card c2 = null;
            if (this.engine.getGameDeck().size() < 2) {
                c1 = this.engine.getGameDiscard().draw();
                c2 = this.engine.getGameDiscard().draw();
            } else {
                c1 = getGameCard();
                c2 = getGameCard();
            }
            this.engine.getCurrentPlayer().addToHand(c1);
            this.engine.getCurrentPlayer().addToHand(c2);

            // Background
            Rectangle rBack = new Rectangle(0, 0, 450, 400);
            rBack.setFill(Color.rgb(54, 69, 79));

            // ImageView objects for cards drawn
            ImageView imV1 = giveCardImageView(c1.toString());
            ImageView imV2 = giveCardImageView(c2.toString());

            // Player name
            Label playerNameL = new Label(this.engine.getCurrentPlayer().toString());
            playerNameL.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
            playerNameL.setTextFill(Color.CYAN);

            // Label for card drawn
            Label cardDrawnLabel = new Label("Cards Drawn");
            cardDrawnLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
            cardDrawnLabel.setTextFill(Color.RED);

            // End turn button
            Button endTurnB = new Button("End Turn");
            endTurnB.setFont(Font.font("Verdana", 20));
            endTurnB.setOnAction(ev -> {
                try {
                    GameApp.playButtonSound();
                } catch (Exception vm) {
                    vm.printStackTrace();
                }

                // Checking if alive or not after drawing
                if (!this.engine.getCurrentPlayer().isAlive()) {

                    astronautButton.setDisable(true);
                    this.drawCardB.setDisable(true);

                    String name = this.engine.getCurrentPlayer().toString().replace(" (is dead)", "");
                    setDeadAstronautGraphic(name);

                    for (Button b : astronautButtonList) {
                        b.setDisable(true);
                    }
                }

                // disabling buttons
                disableAstronautButton(this.engine.getCurrentPlayer().toString());

                // ending turn
                this.engine.endTurn();

                // closing astronautTurn simulator stage
                this.astronautTurnSimulation.close();

                // checking game conditions
                checkGameConditions();
            });

            // Next button (if called by Rocket booster)
            Button nextB = new Button("Next");
            nextB.setFont(Font.font("Verdana", 20));
            nextB.setOnAction(event -> {
                try {
                    GameApp.playButtonSound();
                } catch (Exception cz) {
                    cz.printStackTrace();
                }
                interimPhase();
            });

            // HBox for cards drawn
            HBox hB = new HBox(20, imV1, imV2);
            hB.setAlignment(Pos.CENTER);

            // VBox
            VBox vB = new VBox(5, playerNameL, cardDrawnLabel, hB);
            if (y == 2 && this.engine.getCurrentPlayer().getTrack().size() != 6)
                vB.getChildren().add(nextB);
            else
                vB.getChildren().add(endTurnB);
            vB.setAlignment(Pos.CENTER);

            // StackPane
            StackPane sP = new StackPane(rBack, vB);

            // Setting the scene
            Scene sne = new Scene(sP, 450, 400);
            sne.getStylesheets().add(getClass().getResource("CSS/mainGameStyles.css").toExternalForm());

            // Setting the stage
            this.astronautTurnSimulation.setScene(sne);
            this.astronautTurnSimulation.setTitle("Mysterious Nebula");
            this.astronautTurnSimulation.setX(602);
            this.astronautTurnSimulation.setY(290);

        });

        // VBox
        VBox v = new VBox(25, playerNameLabel, titleL, drawB);
        v.setAlignment(Pos.CENTER);

        // StackPane
        StackPane s = new StackPane(r, v);

        // Setting the scene
        Scene sn = new Scene(s, 450, 250);
        sn.getStylesheets().add(getClass().getResource("CSS/mainGameStyles.css").toExternalForm());

        // Setting the stage
        this.astronautTurnSimulation.setScene(sn);
        this.astronautTurnSimulation.setTitle("Mysterious Nebula");
        this.astronautTurnSimulation.setX(602);
        this.astronautTurnSimulation.setY(290);

    }

    /**
     * Method implementing hyperspace space card functionality
     */
    private void hyperspace(Button astronautButton, ArrayList<Button> astronautButtonList, int y, int ID) {
        if (y != 2)
            this.affected = true;

        // Background
        Rectangle r = new Rectangle(0, 0, 460, 210);
        r.setFill(Color.rgb(54, 69, 79));

        // Player name label
        Label playerNameLabel = new Label();
        if (this.engine.getCurrentPlayer().isAlive()) {
            playerNameLabel.setText(this.engine.getCurrentPlayer().toString());
        } else {
            playerNameLabel.setText(this.engine.getCurrentPlayer().toString().replace(" (is dead)", ""));
        }
        playerNameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        playerNameLabel.setTextFill(Color.CYAN);

        // Label
        Label titleL = new Label("You drew a Hyperspace space card. Move forward 1 space without discarding oxygen!");
        titleL.setFont(Font.font("Verdana", 23));
        titleL.setTextFill(Color.YELLOW);
        titleL.setWrapText(true);
        titleL.setMaxWidth(430);
        titleL.setAlignment(Pos.CENTER);

        // Move button
        Button moveB = new Button("Move");
        moveB.setFont(Font.font("Verdana", 22));
        moveB.setOnAction(e -> {
            try {
                GameApp.playTravelSound();
            } catch (Exception fg) {
                fg.printStackTrace();
            }
            Card spaceCard = this.engine.getSpaceDeck().draw();

            if (spaceCard.toString().equals(SpaceDeck.GRAVITATIONAL_ANOMALY)) {
                this.engine.getSpaceDiscard().add(spaceCard);
            } else {
                this.engine.getCurrentPlayer().addToTrack(spaceCard);
            }
            moveAstronautB(astronautButton, spaceCard, astronautButtonList, y, ID);
        });

        // VBox
        VBox vB = new VBox(15, playerNameLabel, titleL, moveB);
        vB.setAlignment(Pos.CENTER);

        // StackPane
        StackPane sPane = new StackPane(r, vB);

        // Setting the scene
        Scene scee = new Scene(sPane, 460, 210);
        scee.getStylesheets().add(getClass().getResource("CSS/mainGameStyles.css").toExternalForm());

        // Setting the stage
        this.astronautTurnSimulation.setScene(scee);
        this.astronautTurnSimulation.setTitle("Hyperspace");
        this.astronautTurnSimulation.setX(597);
        this.astronautTurnSimulation.setY(370);

    }

    /**
     * Implements meteoroid function
     */
    private void meteoroid(Button astronautButton, ArrayList<Button> astronautButtonList, int y) {
        if (y != 2)
            this.affected = true;

        // Background
        Rectangle r = new Rectangle(0, 0, 450, 330);
        r.setFill(Color.rgb(54, 69, 79));

        // Player name
        Label playerNameLabel = new Label();
        if (this.engine.getCurrentPlayer().isAlive()) {
            playerNameLabel.setText(this.engine.getCurrentPlayer().toString());
        } else {
            playerNameLabel.setText(this.engine.getCurrentPlayer().toString().replace(" (is dead)", ""));
        }
        playerNameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        playerNameLabel.setTextFill(Color.CYAN);

        // Label
        Label titleL = new Label("You drew a Meteoroid space card and have 6 or more game cards! Discard 2 cards.");
        titleL.setFont(Font.font("Verdana", 20));
        titleL.setTextFill(Color.YELLOW);
        titleL.setWrapText(true);
        titleL.setMaxWidth(390);
        titleL.setAlignment(Pos.CENTER);

        // Label for game cards
        Label cardsL = new Label(this.engine.getCurrentPlayer().getHandStr());
        cardsL.setFont(Font.font("Verdana", 16));
        cardsL.setTextFill(Color.WHITE);
        cardsL.setWrapText(true);
        cardsL.setMaxWidth(390);
        cardsL.setAlignment(Pos.CENTER);

        // Textfield for removing card
        TextField cardInput = new TextField();
        cardInput.setFont(Font.font("Verdana", 15));
        cardInput.setAlignment(Pos.CENTER);
        cardInput.setStyle(GameApp.TEXTFIELD_CSS);
        cardInput.setMaxWidth(190);

        // Discard button
        Button discardB = new Button("Discard");
        discardB.setFont(Font.font("Verdana", 17));
        discardB.setOnAction(e -> {
            try {
                if (discardB.getText().equals("End Turn") || discardB.getText().equals("End Turn ")
                        || discardB.getText().equals("Next ") || discardB.getText().equals("Next")) {
                    GameApp.playButtonSound();
                } else {
                    GameApp.playDrawCardSound();
                }
            } catch (Exception bv) {
                bv.printStackTrace();
            }

            if (discardB.getText().equals("End Turn ") || discardB.getText().equals("Next ")) {

                // Checking if alive or not after discarding
                if (!this.engine.getCurrentPlayer().isAlive()) {

                    astronautButton.setDisable(true);
                    this.drawCardB.setDisable(true);

                    String name = this.engine.getCurrentPlayer().toString().replace(" (is dead)", "");
                    setDeadAstronautGraphic(name);

                    for (Button b : astronautButtonList) {
                        b.setDisable(true);
                    }
                }

                // disabling buttons
                disableAstronautButton(this.engine.getCurrentPlayer().toString());

                // ending turn
                this.engine.endTurn();

                // closing astronautTurn simulator stage
                this.astronautTurnSimulation.close();

                // checking game conditions
                checkGameConditions();
            }

            else if (discardCount < 2 && this.engine.getCurrentPlayer().isAlive()) {
                String cardToRemove = cardInput.getText();
                boolean b = false;
                for (Card c : this.engine.getCurrentPlayer().getHand()) {
                    if (cardToRemove.equals(c.toString())) {
                        b = true;
                    }
                }

                if (b) {
                    discardCount++;
                    if (cardToRemove.equals(GameDeck.OXYGEN_1) || cardToRemove.equals(GameDeck.OXYGEN_2)) {
                        if (this.engine.getCurrentPlayer().hasCard(GameDeck.OXYGEN_2) == 1
                                && this.engine.getCurrentPlayer().hasCard(GameDeck.OXYGEN_1) == 0) {
                            Card card = this.engine.getCurrentPlayer().hack(cardToRemove); // this internally kills
                                                                                           // player on hack
                            this.engine.getGameDiscard().add(card);
                            // PLAYER DEAD
                            cardInput.clear();
                            cardInput.setDisable(true);
                            cardsL.setText("Oxygen cards exhausted!");
                            if (y == 2)
                                discardB.setText("Next ");
                            else
                                discardB.setText("End Turn ");

                        } else if (this.engine.getCurrentPlayer().hasCard(GameDeck.OXYGEN_2) == 0
                                && this.engine.getCurrentPlayer().hasCard(GameDeck.OXYGEN_1) == 1) {
                            Card card = this.engine.getCurrentPlayer().hack(cardToRemove); // this internally kills
                                                                                           // player on hack
                            this.engine.getGameDiscard().add(card);
                            // PLAYER DEAD
                            cardInput.clear();
                            cardInput.setDisable(true);
                            cardsL.setText("Oxygen cards exhausted!");
                            if (y == 2)
                                discardB.setText("Next ");
                            else
                                discardB.setText("End Turn ");

                        } else {
                            Card card = this.engine.getCurrentPlayer().hack(cardToRemove); // this time player not
                                                                                           // killed
                            this.engine.getGameDiscard().add(card);
                            cardInput.clear();
                            cardsL.setText(this.engine.getCurrentPlayer().getHandStr());
                        }
                    } else {
                        Card card = this.engine.getCurrentPlayer().hack(cardToRemove);
                        this.engine.getGameDiscard().add(card);
                        cardInput.clear();
                        cardsL.setText(this.engine.getCurrentPlayer().getHandStr());
                    }
                } else {
                    alBox.showAlertBox();
                    cardInput.clear();
                }

            }

            if (discardCount == 2 && this.engine.getCurrentPlayer().isAlive()) {
                if (y == 2)
                    discardB.setText("Next");
                else
                    discardB.setText("End Turn");
                cardInput.setDisable(true);
            }

            else if (discardCount > 2 && this.engine.getCurrentPlayer().isAlive()) {

                if (y == 2 && this.engine.getCurrentPlayer().getTrack().size() != 6) {
                    discardCount = 0;
                    interimPhase();

                } else {

                    // Checking if alive or not after discarding
                    if (!this.engine.getCurrentPlayer().isAlive()) {

                        astronautButton.setDisable(true);
                        this.drawCardB.setDisable(true);

                        String name = this.engine.getCurrentPlayer().toString().replace(" (is dead)", "");
                        setDeadAstronautGraphic(name);

                        for (Button b : astronautButtonList) {
                            b.setDisable(true);
                        }
                    }

                    // disabling buttons
                    disableAstronautButton(this.engine.getCurrentPlayer().toString());

                    // ending turn
                    this.engine.endTurn();

                    // closing astronautTurn simulator stage
                    this.astronautTurnSimulation.close();

                    // checking game conditions
                    checkGameConditions();
                }

            }

            if (discardCount == 2) {
                discardCount++;
            }
        });

        // VBox
        VBox v = new VBox(25, playerNameLabel, titleL, cardsL, cardInput, discardB);
        v.setAlignment(Pos.CENTER);

        // Stackpane
        StackPane sPane = new StackPane(r, v);

        if (this.engine.getCurrentPlayer().getHand().size() < 6) {

            titleL.setText(
                    "You drew a Meteoroid space card. Since you have less than 6 game cards, no penalty imposed!");
            titleL.setFont(Font.font("Verdana", 25));

            playerNameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 30));

            Button endTurnB = new Button("End Turn");
            endTurnB.setFont(Font.font("Verdana", 23));
            endTurnB.setOnAction(e -> {
                try {
                    GameApp.playButtonSound();
                } catch (Exception qw) {
                    qw.printStackTrace();
                }

                // disabling buttons
                disableAstronautButton(this.engine.getCurrentPlayer().toString());

                // ending turn
                this.engine.endTurn();

                // closing astronautTurn simulator stage
                this.astronautTurnSimulation.close();

                // checking game conditions
                checkGameConditions();

            });

            // Updating containers
            v.getChildren().clear();
            v.setSpacing(30);
            v.getChildren().addAll(playerNameLabel, titleL, endTurnB);

            sPane.getChildren().clear();
            sPane.getChildren().addAll(r, v);

        }

        // Setting the scene
        Scene sn = new Scene(sPane, 450, 330);
        sn.getStylesheets().add(getClass().getResource("CSS/mainGameStyles.css").toExternalForm());

        // Setting the stage
        this.astronautTurnSimulation.setScene(sn);
        this.astronautTurnSimulation.setTitle("Meteoroid");
        this.astronautTurnSimulation.setX(602);
        this.astronautTurnSimulation.setY(320);

    }

    /**
     * Implements cosmic radiation space card
     * 
     * @param astronautButton       button associated with astronaut
     * @param astronautButtonsTrack list of buttons associated with the astronaut
     */
    private void cosmicRadiation(Button astronautButton, ArrayList<Button> astronautButtonsTrack, int y) {
        if (y != 2)
            this.affected = true;

        // Background
        Rectangle r = new Rectangle(0, 0, 440, 270);
        r.setFill(Color.rgb(54, 69, 79));

        // Player name
        Label playerNameLabel = new Label(this.engine.getCurrentPlayer().toString());
        playerNameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        playerNameLabel.setTextFill(Color.CYAN);

        // Label
        Label titleL = new Label("You drew a Cosmic Radiation space card. Discard oxygen worth 1 value.");
        titleL.setFont(Font.font("Verdana", 19));
        titleL.setTextFill(Color.YELLOW);
        titleL.setWrapText(true);
        titleL.setMaxWidth(390);
        titleL.setAlignment(Pos.CENTER);

        // Label for oxygens
        String[] c = this.engine.getCurrentPlayer().getHandStr().split(";");
        Label oxLabel = new Label(c[0]);
        oxLabel.setFont(Font.font("Verdana", 17));
        oxLabel.setTextFill(Color.WHITE);

        // Label for "your oxygen cards"
        Label oxCards = new Label("Your oxygen cards:");
        oxCards.setTextFill(Color.RED);
        oxCards.setFont(Font.font("Verdana", FontWeight.BOLD, 20));

        // Discard button
        Button discardB = new Button("Discard");
        discardB.setFont(Font.font("Verdana", 20));
        discardB.setOnAction(e -> {
            try {
                if (discardB.getText().equals("Discard"))
                    GameApp.playDrawCardSound();
                else
                    GameApp.playButtonSound();
            } catch (Exception b) {
                b.printStackTrace();
            }

            if (discardCount2 < 1) {
                discardCount2++;
                if (this.engine.getCurrentPlayer().hasCard(GameDeck.OXYGEN_1) > 0) {
                    Oxygen o = (Oxygen) this.engine.getCurrentPlayer().hack(GameDeck.OXYGEN_1);
                    this.engine.getGameDiscard().add(o);
                } else if (this.engine.getCurrentPlayer().hasCard(GameDeck.OXYGEN_2) > 0) {
                    Oxygen dbl = (Oxygen) this.engine.getCurrentPlayer().hack(GameDeck.OXYGEN_2);
                    Oxygen[] ox = this.engine.splitOxygen(dbl);
                    this.engine.getCurrentPlayer().addToHand(ox[0]);
                    this.engine.getGameDiscard().add(ox[1]);
                }
                if (!this.engine.getCurrentPlayer().getHandStr().contains("(1)")
                        && !this.engine.getCurrentPlayer().getHandStr().contains("(2)")) {
                    oxLabel.setText("Oxygen cards exhausted!");
                } else {
                    oxLabel.setText(this.engine.getCurrentPlayer().getHandStr().split(";")[0]);
                }
                if (y == 2)
                    discardB.setText("Next");
                else
                    discardB.setText("End Turn");
            }

            if ((discardB.getText().equals("End Turn") || discardB.getText().equals("Next")) && discardCount2 > 1) {

                if (y == 2 && this.engine.getCurrentPlayer().isAlive() && discardB.getText().equals("Next")
                        && this.engine.getCurrentPlayer().getTrack().size() != 6) {
                    discardCount2 = 0;
                    interimPhase();
                } else {

                    // Checking if alive or not after discarding
                    if (!this.engine.getCurrentPlayer().isAlive()) {

                        astronautButton.setDisable(true);
                        this.drawCardB.setDisable(true);

                        String name = this.engine.getCurrentPlayer().toString().replace(" (is dead)", "");
                        setDeadAstronautGraphic(name);

                        for (Button b : astronautButtonsTrack) {
                            b.setDisable(true);
                        }
                    }

                    // disabling buttons
                    disableAstronautButton(this.engine.getCurrentPlayer().toString());

                    // ending turn
                    this.engine.endTurn();

                    // closing astronautTurn simulator stage
                    this.astronautTurnSimulation.close();

                    // checking game conditions
                    checkGameConditions();
                }
            }

            if (discardCount2 == 1) {
                discardCount2++;
            }

        });

        // VBox
        VBox v = new VBox(25, playerNameLabel, titleL, oxCards, oxLabel, discardB);
        v.setAlignment(Pos.CENTER);

        // StackPane
        StackPane sP = new StackPane(r, v);

        // Setting the scene
        Scene scene = new Scene(sP, 440, 270);
        scene.getStylesheets().add(getClass().getResource("CSS/mainGameStyles.css").toExternalForm());

        // Setting the stage
        this.astronautTurnSimulation.setScene(scene);
        this.astronautTurnSimulation.setTitle("Cosmic Radiation");
        this.astronautTurnSimulation.setX(602);
        this.astronautTurnSimulation.setY(320);

    }

    /**
     * Implementing space card asteroid field's functionality
     */
    private void asteroidField(Button astronautButton, ArrayList<Button> astronautButtonList, int y) {
        if (y != 2)
            this.affected = true;

        // Background
        Rectangle r = new Rectangle(0, 0, 440, 290);
        r.setFill(Color.rgb(54, 69, 79));

        // Player name
        Label playerNameLabel = new Label();
        if (this.engine.getCurrentPlayer().isAlive()) {
            playerNameLabel.setText(this.engine.getCurrentPlayer().toString());
        } else {
            playerNameLabel.setText(this.engine.getCurrentPlayer().toString().replace(" (is dead)", ""));
        }
        playerNameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        playerNameLabel.setTextFill(Color.CYAN);

        // Label
        Label titleL = new Label("You drew an Asteroid Field space card. Discard oxygen worth 2 value.");
        titleL.setFont(Font.font("Verdana", 20));
        titleL.setTextFill(Color.YELLOW);
        titleL.setWrapText(true);
        titleL.setMaxWidth(410);
        titleL.setAlignment(Pos.CENTER);

        // Label for "your oxygen cards"
        Label oxCards = new Label("Your oxygen cards:");
        oxCards.setTextFill(Color.RED);
        oxCards.setFont(Font.font("Verdana", FontWeight.BOLD, 20));

        // Label for oxygens
        String[] c = this.engine.getCurrentPlayer().getHandStr().split(";");
        Label oxLabel = new Label(c[0]);
        oxLabel.setFont(Font.font("Verdana", 17));
        oxLabel.setTextFill(Color.WHITE);

        // Button
        Button discardB = new Button("Discard");
        discardB.setFont(Font.font("Verdana", 20));
        discardB.setOnAction(e -> {
            try {
                if (discardB.getText().equals("Discard"))
                    GameApp.playDrawCardSound();
                else
                    GameApp.playButtonSound();
            } catch (Exception bg) {
                bg.printStackTrace();
            }

            if (discardB.getText().equals("End Turn") || discardB.getText().equals("Next")) {

                if (y == 2 && this.engine.getCurrentPlayer().isAlive()
                        && this.engine.getCurrentPlayer().getTrack().size() != 6) {
                    interimPhase();
                } else {
                    // Checking if alive or not
                    if (!this.engine.getCurrentPlayer().isAlive()) {

                        astronautButton.setDisable(true);
                        this.drawCardB.setDisable(true);

                        String name = this.engine.getCurrentPlayer().toString().replace(" (is dead)", "");
                        setDeadAstronautGraphic(name);

                        for (Button b : astronautButtonList) {
                            b.setDisable(true);
                        }
                    }

                    // Disabling astronaut button
                    disableAstronautButton(this.engine.getCurrentPlayer().toString());

                    // ending turn
                    this.engine.endTurn();

                    // closing astronautTurn simulator stage
                    this.astronautTurnSimulation.close();

                    // checking game conditions
                    checkGameConditions();
                }

            } else if (this.engine.getCurrentPlayer().hasCard(GameDeck.OXYGEN_2) > 0) {

                this.engine.getGameDiscard().add(this.engine.getCurrentPlayer().hack(GameDeck.OXYGEN_2));
                if (!this.engine.getCurrentPlayer().getHandStr().contains("(1)")
                        && !this.engine.getCurrentPlayer().getHandStr().contains("(2)")) {
                    oxLabel.setText("Oxygen cards exhausted!");
                } else {
                    oxLabel.setText(this.engine.getCurrentPlayer().getHandStr().split(";")[0]);
                }
                if (y == 2)
                    discardB.setText("Next");
                else
                    discardB.setText("End Turn");

            } else if (this.engine.getCurrentPlayer().hasCard(GameDeck.OXYGEN_1) > 1) {

                this.engine.getGameDiscard().add(this.engine.getCurrentPlayer().hack(GameDeck.OXYGEN_1));
                this.engine.getGameDiscard().add(this.engine.getCurrentPlayer().hack(GameDeck.OXYGEN_1));
                if (!this.engine.getCurrentPlayer().getHandStr().contains("(1)")
                        && !this.engine.getCurrentPlayer().getHandStr().contains("(2)")) {
                    oxLabel.setText("Oxygen cards exhausted!");
                } else {
                    oxLabel.setText(this.engine.getCurrentPlayer().getHandStr().split(";")[0]);
                }
                if (y == 2)
                    discardB.setText("Next");
                else
                    discardB.setText("End Turn");

            } else if (this.engine.getCurrentPlayer().hasCard(GameDeck.OXYGEN_1) > 0) {

                this.engine.getGameDiscard().add(this.engine.getCurrentPlayer().hack(GameDeck.OXYGEN_1));
                oxLabel.setText("Oxygen cards exhausted!");
                if (y == 2)
                    discardB.setText("Next");
                else
                    discardB.setText("End Turn");

            }
        });

        // VBox
        VBox v = new VBox(25, playerNameLabel, titleL, oxCards, oxLabel, discardB);
        v.setAlignment(Pos.CENTER);

        // StackPane
        StackPane sP = new StackPane(r, v);

        // Setting the scene
        Scene scene = new Scene(sP, 440, 270);
        scene.getStylesheets().add(getClass().getResource("CSS/mainGameStyles.css").toExternalForm());

        // Setting the stage
        this.astronautTurnSimulation.setScene(scene);
        this.astronautTurnSimulation.setTitle("Asteroid Field");
        this.astronautTurnSimulation.setX(602);
        this.astronautTurnSimulation.setY(320);
    }

    /**
     * Method to implement gravitational anomaly
     */
    private void gravitationalAnomaly(Button astronautButton, ArrayList<Button> astronautButtonList, int y) {
        if (y != 2)
            this.affected = true;

        // Background
        Rectangle r = new Rectangle(0, 0, 420, 180);
        r.setFill(Color.rgb(54, 69, 79));

        // Player name
        Label playerNameLabel = new Label();
        if (this.engine.getCurrentPlayer().isAlive()) {
            playerNameLabel.setText(this.engine.getCurrentPlayer().toString());
        } else {
            playerNameLabel.setText(this.engine.getCurrentPlayer().toString().replace(" (is dead)", ""));
        }
        playerNameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        playerNameLabel.setTextFill(Color.CYAN);

        // Title Label
        Label titleL = new Label("You drew a Gravitational Anomaly space card. You cannot move forward.");
        titleL.setFont(Font.font("Verdana", 20));
        titleL.setTextFill(Color.YELLOW);
        titleL.setWrapText(true);
        titleL.setMaxWidth(410);
        titleL.setAlignment(Pos.CENTER);

        // End Turn button
        Button endTurnB = new Button("End Turn");
        endTurnB.setFont(Font.font("Verdana", 20));
        endTurnB.setOnAction(e -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception as) {
                as.printStackTrace();
            }

            // Checking if alive or not
            if (!this.engine.getCurrentPlayer().isAlive()) {

                astronautButton.setDisable(true);
                this.drawCardB.setDisable(true);

                String name = this.engine.getCurrentPlayer().toString().replace(" (is dead)", "");
                setDeadAstronautGraphic(name);

                for (Button b : astronautButtonList) {
                    b.setDisable(true);
                }
            }

            // Disabling astronaut button
            disableAstronautButton(this.engine.getCurrentPlayer().toString());

            // ending turn
            this.engine.endTurn();

            // closing astronautTurn simulator stage
            this.astronautTurnSimulation.close();

            // checking game conditions
            checkGameConditions();

        });

        // Next button (if called by rocket booster)
        Button nextB = new Button("Next");
        nextB.setFont(Font.font("Verdana", 20));
        nextB.setOnAction(ev -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception bc) {
                bc.printStackTrace();
            }
            interimPhase();
        });

        // VBox
        VBox v = new VBox(15, playerNameLabel, titleL);
        if (y == 2)
            v.getChildren().add(nextB);
        else
            v.getChildren().add(endTurnB);
        v.setAlignment(Pos.CENTER);

        // StackPane
        StackPane sP = new StackPane(r, v);

        // Setting the scene
        Scene s = new Scene(sP, 420, 180);
        s.getStylesheets().add(getClass().getResource("CSS/mainGameStyles.css").toExternalForm());

        // Setting the stage
        this.astronautTurnSimulation.setScene(s);
        this.astronautTurnSimulation.setTitle("Gravitational Anomaly");
        this.astronautTurnSimulation.setX(637);
        this.astronautTurnSimulation.setY(265);
    }

    /**
     * Method implementing wormhole space card
     */
    private void wormhole(int ID, int y) {
        if (y != 2)
            this.affected = true;

        // Background
        Rectangle r = new Rectangle(0, 0, 450, 290);
        r.setFill(Color.rgb(54, 69, 79));

        // Player name
        Label playerNameLabel = new Label();
        if (this.engine.getCurrentPlayer().isAlive()) {
            playerNameLabel.setText(this.engine.getCurrentPlayer().toString());
        } else {
            playerNameLabel.setText(this.engine.getCurrentPlayer().toString().replace(" (is dead)", ""));
        }
        playerNameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        playerNameLabel.setTextFill(Color.CYAN);

        // Label
        Label titleL = new Label("You drew a Wormhole space card. Swap places with another astronaut.");
        titleL.setFont(Font.font("Verdana", 20));
        titleL.setTextFill(Color.YELLOW);
        titleL.setWrapText(true);
        titleL.setMaxWidth(390);
        titleL.setAlignment(Pos.CENTER);

        // Displaying all player names
        String allNames = "";
        List<Astronaut> allAs = this.engine.getAllPlayers();
        for (Astronaut a : allAs) {
            if (a.toString().equals(this.engine.getCurrentPlayer().toString())) {
                continue;
            } else if (a.toString().contains(" (is dead)")) {
                allNames += a.toString().replace(" (is dead)", "") + ", ";
            } else {
                allNames += a.toString() + ", ";
            }
        }
        allNames = allNames.substring(0, allNames.length() - 2);
        Label allPlayerNames = new Label(allNames);
        allPlayerNames.setFont(Font.font("Verdana", 20));
        allPlayerNames.setTextFill(Color.WHITE);
        allPlayerNames.setWrapText(true);
        allPlayerNames.setMaxWidth(390);
        allPlayerNames.setAlignment(Pos.CENTER);

        // Textfield to get input
        TextField userInputTF = new TextField();
        userInputTF.setFont(Font.font("Verdana", 15));
        userInputTF.setAlignment(Pos.CENTER);
        userInputTF.setStyle(GameApp.TEXTFIELD_CSS);
        userInputTF.setMaxWidth(190);

        // Swap button
        Button swapB = new Button("Swap");
        swapB.setFont(Font.font("Verdana", 20));
        swapB.setOnAction(e -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception fx) {
                fx.printStackTrace();
            }

            // Checking which astronaut to swap with
            String playerName = userInputTF.getText();
            boolean bool = false;
            Astronaut swapee = null;
            for (Astronaut a : this.engine.getAllPlayers()) {
                if (a.isAlive()) {
                    if (a.toString().equals(this.engine.getCurrentPlayer().toString())) {
                        continue;
                    } else if (a.toString().equals(playerName)) {
                        swapee = a;
                        bool = true;
                        break;
                    }
                } else {
                    if (a.toString().equals(this.engine.getCurrentPlayer().toString().replace(" (is dead)", ""))) {
                        continue;
                    } else if (a.toString().replace(" (is dead)", "").equals(playerName)) {
                        playerName += " \u00D7";
                        swapee = a;
                        bool = true;
                        break;
                    }
                }
            }

            if (y == 2 && swapB.getText().equals("Next")) {
                interimPhase();
            } else if (swapB.getText().equals("End Turn")) {

                // disabling buttons
                disableAstronautButton(this.engine.getCurrentPlayer().toString());

                // ending turn
                this.engine.endTurn();

                // closing astronautTurn simulator stage
                this.astronautTurnSimulation.close();

                // checking game conditions
                checkGameConditions();

            } else if (bool) {

                this.engine.getCurrentPlayer().swapTrack(swapee); // Tracks swapped internally

                // Swapping button and buttons ArrayLists
                int index = GameApp.playerNames.indexOf(playerName);

                switch (index) {

                    case 0: // swap with purple

                        if (ID == 1) { // green swap with purple
                            // Swapping buttons
                            double xCoordsGreen = this.greenAstronautB.getLayoutX();
                            double yCoordsGreen = this.greenAstronautB.getLayoutY();
                            this.greenAstronautB.setLayoutX(this.purpleAstronautB.getLayoutX());
                            this.greenAstronautB.setLayoutY(this.purpleAstronautB.getLayoutY());
                            this.purpleAstronautB.setLayoutX(xCoordsGreen);
                            this.purpleAstronautB.setLayoutY(yCoordsGreen);
                            // Swapping name labels
                            double xCoordsGreenName = this.greenAstronautName.getLayoutX();
                            this.greenAstronautName.setLayoutX(this.purpleAstronautName.getLayoutX());
                            this.purpleAstronautName.setLayoutX(xCoordsGreenName);
                            // Swapping button ArrayLists
                            ArrayList<Button> temp = this.greenAstronautTrackB;
                            this.greenAstronautTrackB = this.purpleAstronautTrackB;
                            this.purpleAstronautTrackB = temp;
                            // Enabling/disabling tracks
                            int m = 0;
                            for (Button btn : this.greenAstronautTrackB) {
                                if (btn.isDisable()) {
                                    btn.setDisable(false);
                                    m++;
                                }
                            }
                            if (m > 0) {
                                for (Button bt : this.purpleAstronautTrackB) {
                                    bt.setDisable(true);
                                }
                            }
                        } else if (ID == 2) { // yellow swap with purple
                            // Swapping buttons
                            double xCoordsYellow = this.yellowAstronautB.getLayoutX();
                            double yCoordsYellow = this.yellowAstronautB.getLayoutY();
                            this.yellowAstronautB.setLayoutX(this.purpleAstronautB.getLayoutX());
                            this.yellowAstronautB.setLayoutY(this.purpleAstronautB.getLayoutY());
                            this.purpleAstronautB.setLayoutX(xCoordsYellow);
                            this.purpleAstronautB.setLayoutY(yCoordsYellow);
                            // Swapping name labels
                            double xCoordsYellowName = this.yellowAstronautName.getLayoutX();
                            this.yellowAstronautName.setLayoutX(this.purpleAstronautName.getLayoutX());
                            this.purpleAstronautName.setLayoutX(xCoordsYellowName);
                            // Swapping button ArrayLists
                            ArrayList<Button> temp = this.yellowAstronautTrackB;
                            this.yellowAstronautTrackB = this.purpleAstronautTrackB;
                            this.purpleAstronautTrackB = temp;
                            // Enabling/disabling tracks
                            int m = 0;
                            for (Button btn : this.yellowAstronautTrackB) {
                                if (btn.isDisable()) {
                                    btn.setDisable(false);
                                    m++;
                                }
                            }
                            if (m > 0) {
                                for (Button bt : this.purpleAstronautTrackB) {
                                    bt.setDisable(true);
                                }
                            }
                        } else if (ID == 3) { // red swap with purple
                            // Swapping buttons
                            double xCoordsRed = this.redAstronautB.getLayoutX();
                            double yCoordsRed = this.redAstronautB.getLayoutY();
                            this.redAstronautB.setLayoutX(this.purpleAstronautB.getLayoutX());
                            this.redAstronautB.setLayoutY(this.purpleAstronautB.getLayoutY());
                            this.purpleAstronautB.setLayoutX(xCoordsRed);
                            this.purpleAstronautB.setLayoutY(yCoordsRed);
                            // Swapping name labels
                            double xCoordsRedName = this.redAstronautName.getLayoutX();
                            this.redAstronautName.setLayoutX(this.purpleAstronautName.getLayoutX());
                            this.purpleAstronautName.setLayoutX(xCoordsRedName);
                            // Swapping button ArrayLists
                            ArrayList<Button> temp = this.redAstronautTrackB;
                            this.redAstronautTrackB = this.purpleAstronautTrackB;
                            this.purpleAstronautTrackB = temp;
                            // Enabling/disabling tracks
                            int m = 0;
                            for (Button btn : this.redAstronautTrackB) {
                                if (btn.isDisable()) {
                                    btn.setDisable(false);
                                    m++;
                                }
                            }
                            if (m > 0) {
                                for (Button bt : this.purpleAstronautTrackB) {
                                    bt.setDisable(true);
                                }
                            }
                        } else if (ID == 4) { // blue swap with purple
                            // Swapping buttons
                            double xCoordsBlue = this.blueAstronautB.getLayoutX();
                            double yCoordsBlue = this.blueAstronautB.getLayoutY();
                            this.blueAstronautB.setLayoutX(this.purpleAstronautB.getLayoutX());
                            this.blueAstronautB.setLayoutY(this.purpleAstronautB.getLayoutY());
                            this.purpleAstronautB.setLayoutX(xCoordsBlue);
                            this.purpleAstronautB.setLayoutY(yCoordsBlue);
                            // Swapping name labels
                            double xCoordsBlueName = this.blueAstronautName.getLayoutX();
                            this.blueAstronautName.setLayoutX(this.purpleAstronautName.getLayoutX());
                            this.purpleAstronautName.setLayoutX(xCoordsBlueName);
                            // Swapping button ArrayLists
                            ArrayList<Button> temp = this.blueAstronautTrackB;
                            this.blueAstronautTrackB = this.purpleAstronautTrackB;
                            this.purpleAstronautTrackB = temp;
                            // Enabling/disabling tracks
                            int m = 0;
                            for (Button btn : this.blueAstronautTrackB) {
                                if (btn.isDisable()) {
                                    btn.setDisable(false);
                                    m++;
                                }
                            }
                            if (m > 0) {
                                for (Button bt : this.purpleAstronautTrackB) {
                                    bt.setDisable(true);
                                }
                            }
                        }
                        break;

                    case 1: // swap with green

                        if (ID == 0) { // purple swap with green
                            // Swapping buttons
                            double xCoordsPurple = this.purpleAstronautB.getLayoutX();
                            double yCoordsPurple = this.purpleAstronautB.getLayoutY();
                            this.purpleAstronautB.setLayoutX(this.greenAstronautB.getLayoutX());
                            this.purpleAstronautB.setLayoutY(this.greenAstronautB.getLayoutY());
                            this.greenAstronautB.setLayoutX(xCoordsPurple);
                            this.greenAstronautB.setLayoutY(yCoordsPurple);
                            // Swapping name labels
                            double xCoordsPurpleName = this.purpleAstronautName.getLayoutX();
                            this.purpleAstronautName.setLayoutX(this.greenAstronautName.getLayoutX());
                            this.greenAstronautName.setLayoutX(xCoordsPurpleName);
                            // Swapping button ArrayLists
                            ArrayList<Button> temp = this.purpleAstronautTrackB;
                            this.purpleAstronautTrackB = this.greenAstronautTrackB;
                            this.greenAstronautTrackB = temp;
                            // Enabling/disabling tracks
                            int m = 0;
                            for (Button btn : this.purpleAstronautTrackB) {
                                if (btn.isDisable()) {
                                    btn.setDisable(false);
                                    m++;
                                }
                            }
                            if (m > 0) {
                                for (Button bt : this.greenAstronautTrackB) {
                                    bt.setDisable(true);
                                }
                            }
                        } else if (ID == 2) { // yellow swap with green
                            // Swapping buttons
                            double xCoordsYellow = this.yellowAstronautB.getLayoutX();
                            double yCoordsYellow = this.yellowAstronautB.getLayoutY();
                            this.yellowAstronautB.setLayoutX(this.greenAstronautB.getLayoutX());
                            this.yellowAstronautB.setLayoutY(this.greenAstronautB.getLayoutY());
                            this.greenAstronautB.setLayoutX(xCoordsYellow);
                            this.greenAstronautB.setLayoutY(yCoordsYellow);
                            // Swapping name labels
                            double xCoordsYellowName = this.yellowAstronautName.getLayoutX();
                            this.yellowAstronautName.setLayoutX(this.greenAstronautName.getLayoutX());
                            this.greenAstronautName.setLayoutX(xCoordsYellowName);
                            // Swapping button ArrayLists
                            ArrayList<Button> temp = this.yellowAstronautTrackB;
                            this.yellowAstronautTrackB = this.greenAstronautTrackB;
                            this.greenAstronautTrackB = temp;
                            // Enabling/disabling tracks
                            int m = 0;
                            for (Button btn : this.yellowAstronautTrackB) {
                                if (btn.isDisable()) {
                                    btn.setDisable(false);
                                    m++;
                                }
                            }
                            if (m > 0) {
                                for (Button bt : this.greenAstronautTrackB) {
                                    bt.setDisable(true);
                                }
                            }
                        } else if (ID == 3) { // red swap with green
                            // Swapping buttons
                            double xCoordsRed = this.redAstronautB.getLayoutX();
                            double yCoordsRed = this.redAstronautB.getLayoutY();
                            this.redAstronautB.setLayoutX(this.greenAstronautB.getLayoutX());
                            this.redAstronautB.setLayoutY(this.greenAstronautB.getLayoutY());
                            this.greenAstronautB.setLayoutX(xCoordsRed);
                            this.greenAstronautB.setLayoutY(yCoordsRed);
                            // Swapping name labels
                            double xCoordsRedName = this.redAstronautName.getLayoutX();
                            this.redAstronautName.setLayoutX(this.greenAstronautName.getLayoutX());
                            this.greenAstronautName.setLayoutX(xCoordsRedName);
                            // Swapping button ArrayLists
                            ArrayList<Button> temp = this.redAstronautTrackB;
                            this.redAstronautTrackB = this.greenAstronautTrackB;
                            this.greenAstronautTrackB = temp;
                            // Enabling/disabling tracks
                            int m = 0;
                            for (Button btn : this.redAstronautTrackB) {
                                if (btn.isDisable()) {
                                    btn.setDisable(false);
                                    m++;
                                }
                            }
                            if (m > 0) {
                                for (Button bt : this.greenAstronautTrackB) {
                                    bt.setDisable(true);
                                }
                            }
                        } else if (ID == 4) { // blue swap with green
                            // Swapping buttons
                            double xCoordsBlue = this.blueAstronautB.getLayoutX();
                            double yCoordsBlue = this.blueAstronautB.getLayoutY();
                            this.blueAstronautB.setLayoutX(this.greenAstronautB.getLayoutX());
                            this.blueAstronautB.setLayoutY(this.greenAstronautB.getLayoutY());
                            this.greenAstronautB.setLayoutX(xCoordsBlue);
                            this.greenAstronautB.setLayoutY(yCoordsBlue);
                            // Swapping name labels
                            double xCoordsBlueName = this.blueAstronautName.getLayoutX();
                            this.blueAstronautName.setLayoutX(this.greenAstronautName.getLayoutX());
                            this.greenAstronautName.setLayoutX(xCoordsBlueName);
                            // Swapping button ArrayLists
                            ArrayList<Button> temp = this.blueAstronautTrackB;
                            this.blueAstronautTrackB = this.greenAstronautTrackB;
                            this.greenAstronautTrackB = temp;
                            // Enabling/disabling tracks
                            int m = 0;
                            for (Button btn : this.blueAstronautTrackB) {
                                if (btn.isDisable()) {
                                    btn.setDisable(false);
                                    m++;
                                }
                            }
                            if (m > 0) {
                                for (Button bt : this.greenAstronautTrackB) {
                                    bt.setDisable(true);
                                }
                            }
                        }
                        break;

                    case 2: // swap with yellow

                        if (ID == 0) { // purple swap with yellow
                            // Swapping buttons
                            double xCoordsPurple = this.purpleAstronautB.getLayoutX();
                            double yCoordsPurple = this.purpleAstronautB.getLayoutY();
                            this.purpleAstronautB.setLayoutX(this.yellowAstronautB.getLayoutX());
                            this.purpleAstronautB.setLayoutY(this.yellowAstronautB.getLayoutY());
                            this.yellowAstronautB.setLayoutX(xCoordsPurple);
                            this.yellowAstronautB.setLayoutY(yCoordsPurple);
                            // Swapping name labels
                            double xCoordsPurpleName = this.purpleAstronautName.getLayoutX();
                            this.purpleAstronautName.setLayoutX(this.yellowAstronautName.getLayoutX());
                            this.yellowAstronautName.setLayoutX(xCoordsPurpleName);
                            // Swapping button ArrayLists
                            ArrayList<Button> temp = this.purpleAstronautTrackB;
                            this.purpleAstronautTrackB = this.yellowAstronautTrackB;
                            this.yellowAstronautTrackB = temp;
                            // Enabling/disabling tracks
                            int m = 0;
                            for (Button btn : this.purpleAstronautTrackB) {
                                if (btn.isDisable()) {
                                    btn.setDisable(false);
                                    m++;
                                }
                            }
                            if (m > 0) {
                                for (Button bt : this.yellowAstronautTrackB) {
                                    bt.setDisable(true);
                                }
                            }
                        } else if (ID == 1) { // green swap with yellow
                            // Swapping buttons
                            double xCoordsGreen = this.greenAstronautB.getLayoutX();
                            double yCoordsGreen = this.greenAstronautB.getLayoutY();
                            this.greenAstronautB.setLayoutX(this.yellowAstronautB.getLayoutX());
                            this.greenAstronautB.setLayoutY(this.yellowAstronautB.getLayoutY());
                            this.yellowAstronautB.setLayoutX(xCoordsGreen);
                            this.yellowAstronautB.setLayoutY(yCoordsGreen);
                            // Swapping name labels
                            double xCoordsGreenName = this.greenAstronautName.getLayoutX();
                            this.greenAstronautName.setLayoutX(this.yellowAstronautName.getLayoutX());
                            this.yellowAstronautName.setLayoutX(xCoordsGreenName);
                            // Swapping button ArrayLists
                            ArrayList<Button> temp = this.greenAstronautTrackB;
                            this.greenAstronautTrackB = this.yellowAstronautTrackB;
                            this.yellowAstronautTrackB = temp;
                            // Enabling/disabling tracks
                            int m = 0;
                            for (Button btn : this.greenAstronautTrackB) {
                                if (btn.isDisable()) {
                                    btn.setDisable(false);
                                    m++;
                                }
                            }
                            if (m > 0) {
                                for (Button bt : this.yellowAstronautTrackB) {
                                    bt.setDisable(true);
                                }
                            }
                        } else if (ID == 3) { // red swap with yellow
                            // Swapping buttons
                            double xCoordsRed = this.redAstronautB.getLayoutX();
                            double yCoordsRed = this.redAstronautB.getLayoutY();
                            this.redAstronautB.setLayoutX(this.yellowAstronautB.getLayoutX());
                            this.redAstronautB.setLayoutY(this.yellowAstronautB.getLayoutY());
                            this.yellowAstronautB.setLayoutX(xCoordsRed);
                            this.yellowAstronautB.setLayoutY(yCoordsRed);
                            // Swapping name labels
                            double xCoordsRedName = this.redAstronautName.getLayoutX();
                            this.redAstronautName.setLayoutX(this.yellowAstronautName.getLayoutX());
                            this.yellowAstronautName.setLayoutX(xCoordsRedName);
                            // Swapping button ArrayLists
                            ArrayList<Button> temp = this.redAstronautTrackB;
                            this.redAstronautTrackB = this.yellowAstronautTrackB;
                            this.yellowAstronautTrackB = temp;
                            // Enabling/disabling tracks
                            int m = 0;
                            for (Button btn : this.redAstronautTrackB) {
                                if (btn.isDisable()) {
                                    btn.setDisable(false);
                                    m++;
                                }
                            }
                            if (m > 0) {
                                for (Button bt : this.yellowAstronautTrackB) {
                                    bt.setDisable(true);
                                }
                            }
                        } else if (ID == 4) { // blue swap with yellow
                            // Swapping buttons
                            double xCoordsBlue = this.blueAstronautB.getLayoutX();
                            double yCoordsBlue = this.blueAstronautB.getLayoutY();
                            this.blueAstronautB.setLayoutX(this.yellowAstronautB.getLayoutX());
                            this.blueAstronautB.setLayoutY(this.yellowAstronautB.getLayoutY());
                            this.yellowAstronautB.setLayoutX(xCoordsBlue);
                            this.yellowAstronautB.setLayoutY(yCoordsBlue);
                            // Swapping name labels
                            double xCoordsBlueName = this.blueAstronautName.getLayoutX();
                            this.blueAstronautName.setLayoutX(this.yellowAstronautName.getLayoutX());
                            this.yellowAstronautName.setLayoutX(xCoordsBlueName);
                            // Swapping button ArrayLists
                            ArrayList<Button> temp = this.blueAstronautTrackB;
                            this.blueAstronautTrackB = this.yellowAstronautTrackB;
                            this.yellowAstronautTrackB = temp;
                            // Enabling/disabling tracks
                            int m = 0;
                            for (Button btn : this.blueAstronautTrackB) {
                                if (btn.isDisable()) {
                                    btn.setDisable(false);
                                    m++;
                                }
                            }
                            if (m > 0) {
                                for (Button bt : this.yellowAstronautTrackB) {
                                    bt.setDisable(true);
                                }
                            }
                        }
                        break;

                    case 3: // swap with red

                        if (ID == 0) { // purple swap with red
                            // Swapping buttons
                            double xCoordsPurple = this.purpleAstronautB.getLayoutX();
                            double yCoordsPurple = this.purpleAstronautB.getLayoutY();
                            this.purpleAstronautB.setLayoutX(this.redAstronautB.getLayoutX());
                            this.purpleAstronautB.setLayoutY(this.redAstronautB.getLayoutY());
                            this.redAstronautB.setLayoutX(xCoordsPurple);
                            this.redAstronautB.setLayoutY(yCoordsPurple);
                            // Swapping name labels
                            double xCoordsPurpleName = this.purpleAstronautName.getLayoutX();
                            this.purpleAstronautName.setLayoutX(this.redAstronautName.getLayoutX());
                            this.redAstronautName.setLayoutX(xCoordsPurpleName);
                            // Swapping button ArrayLists
                            ArrayList<Button> temp = this.purpleAstronautTrackB;
                            this.purpleAstronautTrackB = this.redAstronautTrackB;
                            this.redAstronautTrackB = temp;
                            // Enabling/disabling tracks
                            int m = 0;
                            for (Button btn : this.purpleAstronautTrackB) {
                                if (btn.isDisable()) {
                                    btn.setDisable(false);
                                    m++;
                                }
                            }
                            if (m > 0) {
                                for (Button bt : this.redAstronautTrackB) {
                                    bt.setDisable(true);
                                }
                            }
                        } else if (ID == 1) { // green swap with red
                            // Swapping buttons
                            double xCoordsGreen = this.greenAstronautB.getLayoutX();
                            double yCoordsGreen = this.greenAstronautB.getLayoutY();
                            this.greenAstronautB.setLayoutX(this.redAstronautB.getLayoutX());
                            this.greenAstronautB.setLayoutY(this.redAstronautB.getLayoutY());
                            this.redAstronautB.setLayoutX(xCoordsGreen);
                            this.redAstronautB.setLayoutY(yCoordsGreen);
                            // Swapping name labels
                            double xCoordsGreenName = this.greenAstronautName.getLayoutX();
                            this.greenAstronautName.setLayoutX(this.redAstronautName.getLayoutX());
                            this.redAstronautName.setLayoutX(xCoordsGreenName);
                            // Swapping button ArrayLists
                            ArrayList<Button> temp = this.greenAstronautTrackB;
                            this.greenAstronautTrackB = this.redAstronautTrackB;
                            this.redAstronautTrackB = temp;
                            // Enabling/disabling tracks
                            int m = 0;
                            for (Button btn : this.greenAstronautTrackB) {
                                if (btn.isDisable()) {
                                    btn.setDisable(false);
                                    m++;
                                }
                            }
                            if (m > 0) {
                                for (Button bt : this.redAstronautTrackB) {
                                    bt.setDisable(true);
                                }
                            }
                        } else if (ID == 2) { // yellow swap with red
                            // Swapping buttons
                            double xCoordsYellow = this.yellowAstronautB.getLayoutX();
                            double yCoordsYellow = this.yellowAstronautB.getLayoutY();
                            this.yellowAstronautB.setLayoutX(this.redAstronautB.getLayoutX());
                            this.yellowAstronautB.setLayoutY(this.redAstronautB.getLayoutY());
                            this.redAstronautB.setLayoutX(xCoordsYellow);
                            this.redAstronautB.setLayoutY(yCoordsYellow);
                            // Swapping name labels
                            double xCoordsYellowName = this.yellowAstronautName.getLayoutX();
                            this.yellowAstronautName.setLayoutX(this.redAstronautName.getLayoutX());
                            this.redAstronautName.setLayoutX(xCoordsYellowName);
                            // Swapping button ArrayLists
                            ArrayList<Button> temp = this.yellowAstronautTrackB;
                            this.yellowAstronautTrackB = this.redAstronautTrackB;
                            this.redAstronautTrackB = temp;
                            // Enabling/disabling tracks
                            int m = 0;
                            for (Button btn : this.yellowAstronautTrackB) {
                                if (btn.isDisable()) {
                                    btn.setDisable(false);
                                    m++;
                                }
                            }
                            if (m > 0) {
                                for (Button bt : this.redAstronautTrackB) {
                                    bt.setDisable(true);
                                }
                            }
                        } else if (ID == 4) { // blue swap with red
                            // Swapping buttons
                            double xCoordsBlue = this.blueAstronautB.getLayoutX();
                            double yCoordsBlue = this.blueAstronautB.getLayoutY();
                            this.blueAstronautB.setLayoutX(this.redAstronautB.getLayoutX());
                            this.blueAstronautB.setLayoutY(this.redAstronautB.getLayoutY());
                            this.redAstronautB.setLayoutX(xCoordsBlue);
                            this.redAstronautB.setLayoutY(yCoordsBlue);
                            // Swapping name labels
                            double xCoordsBlueName = this.blueAstronautName.getLayoutX();
                            this.blueAstronautName.setLayoutX(this.redAstronautName.getLayoutX());
                            this.redAstronautName.setLayoutX(xCoordsBlueName);
                            // Swapping button ArrayLists
                            ArrayList<Button> temp = this.blueAstronautTrackB;
                            this.blueAstronautTrackB = this.redAstronautTrackB;
                            this.redAstronautTrackB = temp;
                            // Enabling/disabling tracks
                            int m = 0;
                            for (Button btn : this.blueAstronautTrackB) {
                                if (btn.isDisable()) {
                                    btn.setDisable(false);
                                    m++;
                                }
                            }
                            if (m > 0) {
                                for (Button bt : this.redAstronautTrackB) {
                                    bt.setDisable(true);
                                }
                            }
                        }
                        break;

                    case 4: // swap with blue

                        if (ID == 0) { // purple swap with blue
                            // Swapping buttons
                            double xCoordsPurple = this.purpleAstronautB.getLayoutX();
                            double yCoordsPurple = this.purpleAstronautB.getLayoutY();
                            this.purpleAstronautB.setLayoutX(this.blueAstronautB.getLayoutX());
                            this.purpleAstronautB.setLayoutY(this.blueAstronautB.getLayoutY());
                            this.blueAstronautB.setLayoutX(xCoordsPurple);
                            this.blueAstronautB.setLayoutY(yCoordsPurple);
                            // Swapping name labels
                            double xCoordsPurpleName = this.purpleAstronautName.getLayoutX();
                            this.purpleAstronautName.setLayoutX(this.blueAstronautName.getLayoutX());
                            this.blueAstronautName.setLayoutX(xCoordsPurpleName);
                            // Swapping button ArrayLists
                            ArrayList<Button> temp = this.purpleAstronautTrackB;
                            this.purpleAstronautTrackB = this.blueAstronautTrackB;
                            this.blueAstronautTrackB = temp;
                            // Enabling/disabling tracks
                            int m = 0;
                            for (Button btn : this.purpleAstronautTrackB) {
                                if (btn.isDisable()) {
                                    btn.setDisable(false);
                                    m++;
                                }
                            }
                            if (m > 0) {
                                for (Button bt : this.blueAstronautTrackB) {
                                    bt.setDisable(true);
                                }
                            }
                        } else if (ID == 1) { // green swap with blue
                            // Swapping buttons
                            double xCoordsGreen = this.greenAstronautB.getLayoutX();
                            double yCoordsGreen = this.greenAstronautB.getLayoutY();
                            this.greenAstronautB.setLayoutX(this.blueAstronautB.getLayoutX());
                            this.greenAstronautB.setLayoutY(this.blueAstronautB.getLayoutY());
                            this.blueAstronautB.setLayoutX(xCoordsGreen);
                            this.blueAstronautB.setLayoutY(yCoordsGreen);
                            // Swapping name labels
                            double xCoordsGreenName = this.greenAstronautName.getLayoutX();
                            this.greenAstronautName.setLayoutX(this.blueAstronautName.getLayoutX());
                            this.blueAstronautName.setLayoutX(xCoordsGreenName);
                            // Swapping button ArrayLists
                            ArrayList<Button> temp = this.greenAstronautTrackB;
                            this.greenAstronautTrackB = this.blueAstronautTrackB;
                            this.blueAstronautTrackB = temp;
                            // Enabling/disabling tracks
                            int m = 0;
                            for (Button btn : this.greenAstronautTrackB) {
                                if (btn.isDisable()) {
                                    btn.setDisable(false);
                                    m++;
                                }
                            }
                            if (m > 0) {
                                for (Button bt : this.blueAstronautTrackB) {
                                    bt.setDisable(true);
                                }
                            }
                        } else if (ID == 2) { // yellow swap with blue
                            // Swapping buttons
                            double xCoordsYellow = this.yellowAstronautB.getLayoutX();
                            double yCoordsYellow = this.yellowAstronautB.getLayoutY();
                            this.yellowAstronautB.setLayoutX(this.blueAstronautB.getLayoutX());
                            this.yellowAstronautB.setLayoutY(this.blueAstronautB.getLayoutY());
                            this.blueAstronautB.setLayoutX(xCoordsYellow);
                            this.blueAstronautB.setLayoutY(yCoordsYellow);
                            // Swapping name labels
                            double xCoordsYellowName = this.yellowAstronautName.getLayoutX();
                            this.yellowAstronautName.setLayoutX(this.blueAstronautName.getLayoutX());
                            this.blueAstronautName.setLayoutX(xCoordsYellowName);
                            // Swapping button ArrayLists
                            ArrayList<Button> temp = this.yellowAstronautTrackB;
                            this.yellowAstronautTrackB = this.blueAstronautTrackB;
                            this.blueAstronautTrackB = temp;
                            // Enabling/disabling tracks
                            int m = 0;
                            for (Button btn : this.yellowAstronautTrackB) {
                                if (btn.isDisable()) {
                                    btn.setDisable(false);
                                    m++;
                                }
                            }
                            if (m > 0) {
                                for (Button bt : this.blueAstronautTrackB) {
                                    bt.setDisable(true);
                                }
                            }
                        } else if (ID == 3) { // red swap with blue
                            // Swapping buttons
                            double xCoordsRed = this.redAstronautB.getLayoutX();
                            double yCoordsRed = this.redAstronautB.getLayoutY();
                            this.redAstronautB.setLayoutX(this.blueAstronautB.getLayoutX());
                            this.redAstronautB.setLayoutY(this.blueAstronautB.getLayoutY());
                            this.blueAstronautB.setLayoutX(xCoordsRed);
                            this.blueAstronautB.setLayoutY(yCoordsRed);
                            // Swapping name labels
                            double xCoordsRedName = this.redAstronautName.getLayoutX();
                            this.redAstronautName.setLayoutX(this.blueAstronautName.getLayoutX());
                            this.blueAstronautName.setLayoutX(xCoordsRedName);
                            // Swapping button ArrayLists
                            ArrayList<Button> temp = this.redAstronautTrackB;
                            this.redAstronautTrackB = this.blueAstronautTrackB;
                            this.blueAstronautTrackB = temp;
                            // Enabling/disabling tracks
                            int m = 0;
                            for (Button btn : this.redAstronautTrackB) {
                                if (btn.isDisable()) {
                                    btn.setDisable(false);
                                    m++;
                                }
                            }
                            if (m > 0) {
                                for (Button bt : this.blueAstronautTrackB) {
                                    bt.setDisable(true);
                                }
                            }
                        }
                        break;
                }

                if (y == 2)
                    swapB.setText("Next");
                else
                    swapB.setText("End Turn");

                userInputTF.clear();
                userInputTF.setDisable(true);

            } else {
                AlertBox aBox = new AlertBox("Invalid player name");
                aBox.showAlertBox();
                userInputTF.clear();
            }
        });

        // VBox
        VBox v = new VBox(25, playerNameLabel, titleL, allPlayerNames, userInputTF, swapB);
        v.setAlignment(Pos.CENTER);

        // Stackpane
        StackPane stackPane = new StackPane(r, v);

        // Setting the scene
        Scene scene = new Scene(stackPane, 450, 290);
        scene.getStylesheets().add(getClass().getResource("CSS/mainGameStyles.css").toExternalForm());

        // Setting the stage
        this.astronautTurnSimulation.setScene(scene);
        this.astronautTurnSimulation.setTitle("Wormhole");
        this.astronautTurnSimulation.setX(637);
        this.astronautTurnSimulation.setY(290);
    }

    /**
     * Implements solar flare space card functionality
     */
    private void solarFlare(int y) {
        if (y != 2)
            this.affected = true;

        // Background
        Rectangle rBcg = new Rectangle(0, 0, 460, 210);
        rBcg.setFill(Color.rgb(54, 69, 79));

        // Player name label
        Label playerNameLabel = new Label(this.engine.getCurrentPlayer().toString());
        playerNameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 27));
        playerNameLabel.setTextFill(Color.CYAN);

        // Card title label
        Label titleL = new Label(
                "You drew a Solar Flare space card. You cannot play any action cards while you are directly in front of it!");
        titleL.setWrapText(true);
        titleL.setFont(Font.font("Verdana", 20));
        titleL.setTextFill(Color.YELLOW);
        titleL.setMaxWidth(440);
        titleL.setAlignment(Pos.CENTER);

        // End turn button
        Button endTurnB = new Button("End Turn");
        endTurnB.setFont(Font.font("Verdana", 20));
        endTurnB.setOnAction(e -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception vb) {
                vb.printStackTrace();
            }

            // disabling buttons
            disableAstronautButton(this.engine.getCurrentPlayer().toString());

            // ending turn
            this.engine.endTurn();

            // closing astronautTurn simulator stage
            this.astronautTurnSimulation.close();

            // checking game conditions
            checkGameConditions();
        });

        // Next button (if called by rocket booster)
        Button nextB = new Button("Next");
        nextB.setFont(Font.font("Verdana", 20));
        nextB.setOnAction(ev -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception nb) {
                nb.printStackTrace();
            }
            interimPhase();
        });

        // VBox
        VBox vbox = new VBox(15, playerNameLabel, titleL);
        if (y == 2 && this.engine.getCurrentPlayer().getTrack().size() != 6)
            vbox.getChildren().add(nextB);
        else
            vbox.getChildren().add(endTurnB);
        vbox.setAlignment(Pos.CENTER);

        // Stackpane
        StackPane sP = new StackPane(rBcg, vbox);

        // Setting the scene
        Scene sce = new Scene(sP, 460, 210);
        sce.getStylesheets().add(getClass().getResource("CSS/mainGameStyles.css").toExternalForm());

        // Setting the stage
        this.astronautTurnSimulation.setScene(sce);
        this.astronautTurnSimulation.setTitle("Solar Flare");
        this.astronautTurnSimulation.setX(607);
        this.astronautTurnSimulation.setY(380);

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////// Action Cards Implementation ////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////// OXYGEN SIPHON ////////////////////////
    /**
     * Method implementing oxygen siphon action card
     */
    private void oxygenSiphon() {

        int rHeight = 200;

        // Background rectangle
        Rectangle rBcg = new Rectangle(0, 0, 430, rHeight);
        rBcg.setFill(Color.rgb(54, 69, 79));

        // Player name label
        Label playerNameLabel = new Label(this.engine.getCurrentPlayer().toString());
        playerNameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        playerNameLabel.setTextFill(Color.CYAN);

        // Card Label and info
        Label cardInfo = new Label(
                "Oxygen Siphon: Steal 2 oxygens from the player chosen! A single oxygen is stolen if the selected player doesn't have 2 oxygens.");
        cardInfo.setWrapText(true);
        cardInfo.setMaxWidth(415);
        cardInfo.setAlignment(Pos.CENTER);
        cardInfo.setTextFill(Color.YELLOW);
        cardInfo.setFont(Font.font("Verdana", 17));

        // Label for info
        Label infoL = new Label("Select a player");
        infoL.setTextFill(Color.RED);
        infoL.setFont(Font.font("Verdana", FontWeight.BOLD, 20));

        // Checkbox vbox
        VBox checkboxVB = new VBox(15);
        checkboxVB.setAlignment(Pos.CENTER);

        // Player Names checkbox
        ArrayList<CheckBox> chBox = new ArrayList<>();
        for (Astronaut a : this.engine.getAllPlayers()) {
            if (a.toString().equals(this.engine.getCurrentPlayer().toString())) {
                continue;
            } else if (!a.isAlive()) {
                continue;
            } else {
                CheckBox cb = new CheckBox(a.toString());
                rHeight += 50;
                cb.setFont(Font.font("Verdana", 17));
                cb.setTextFill(Color.YELLOW);
                chBox.add(cb);
                checkboxVB.getChildren().add(cb);
            }
        }
        rBcg.setHeight(rHeight);

        // Select button
        Button selectB = new Button("Select");
        selectB.setFont(Font.font("Verdana", 20));
        selectB.setOnAction(e -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception vq) {
                vq.printStackTrace();
            }

            int c = 0;
            for (CheckBox cb : chBox) {
                if (cb.isSelected()) {
                    c++;
                }
            }

            if (c != 1) {
                for (CheckBox checkbox : chBox) {
                    checkbox.setSelected(false);
                }
                AlertBox ax = new AlertBox("Invalid player selection.");
                ax.showAlertBox();
            } else {

                this.actionsPlayed++; // needed for two player logic

                // Oxygen Siphon discarded
                Card siphon = this.engine.getCurrentPlayer().hack(GameDeck.OXYGEN_SIPHON);
                this.engine.getGameDiscard().add(siphon);

                // Getting name of targeted player
                String name = null;
                for (CheckBox cb : chBox) {
                    if (cb.isSelected()) {
                        name = cb.getText();
                    }
                }

                // Getting Astronaut instance of targeted player
                Astronaut ast = null;
                for (Astronaut a : this.engine.getAllPlayers()) {
                    if (a.toString().equals(name)) {
                        ast = a;
                    }
                }

                // Checking if targeted astronaut has Shield card or not
                boolean hasShield = false;

                for (Card card : ast.getActions()) {
                    if (card.toString().equals(GameDeck.SHIELD)) {
                        hasShield = true;
                        hasShieldOxygenSiphon(name, ast);
                        break;
                    }
                }

                // When no shield, this code executes
                if (!hasShield) {
                    // Perform the task and call cool method
                    if (ast.hasCard(GameDeck.OXYGEN_1) == 1 && ast.hasCard(GameDeck.OXYGEN_2) == 0) {
                        this.engine.getCurrentPlayer().addToHand(ast.siphon()); // Last Oxygen(1), player is killed
                        noShieldOxygenSiphon(name, ast, 1);
                    } else {
                        this.engine.getCurrentPlayer().addToHand(ast.siphon());
                        this.engine.getCurrentPlayer().addToHand(ast.siphon()); // If last Oxygens, player killed
                        noShieldOxygenSiphon(name, ast, 2);
                    }
                }
            }
        });

        // Back button
        Button backB = new Button("Back");
        backB.setFont(Font.font("Verdana", 20));
        backB.setOnAction(ev -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception bv) {
                bv.printStackTrace();
            }
            playActionCards();
        });

        // Hbox for buttons
        HBox hb = new HBox(10, backB, selectB);
        hb.setAlignment(Pos.CENTER);

        // Main VBox
        VBox v = new VBox(15, playerNameLabel, cardInfo, infoL, checkboxVB, hb);
        v.setAlignment(Pos.TOP_CENTER);

        // StackPane
        StackPane s = new StackPane(rBcg, v);

        // Scene
        Scene sce = new Scene(s, 430, rHeight);
        sce.getStylesheets().add(getClass().getResource("CSS/mainGameStyles.css").toExternalForm());

        // Stage
        this.astronautTurnSimulation.setScene(sce);
        this.astronautTurnSimulation.setTitle("Oxygen Siphon");
        this.astronautTurnSimulation.setX(677);
        this.astronautTurnSimulation.setY(240);

    }

    /**
     * When opponent has no shield upon siphon being played against
     * 
     * @param oxygenNumber value of oxygen taken from targeted astronaut
     */
    private void noShieldOxygenSiphon(String targetName, Astronaut defender, int oxygenNumber) {

        // Background rectangle
        Rectangle rBcg = new Rectangle(0, 0, 440, 300);
        rBcg.setFill(Color.rgb(54, 69, 79));

        // Player name label
        Label playerNameLabel = new Label(this.engine.getCurrentPlayer().toString());
        playerNameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        playerNameLabel.setTextFill(Color.CYAN);

        // Card Label and info
        Label cardInfo = new Label(oxygenNumber + " oxygen(s) taken from " + targetName + "!");
        cardInfo.setWrapText(true);
        cardInfo.setAlignment(Pos.CENTER);
        cardInfo.setTextFill(Color.YELLOW);
        cardInfo.setFont(Font.font("Verdana", 20));

        // Updated game cards label
        Label updatedHandL = new Label("Your updated game cards:");
        updatedHandL.setFont(Font.font("Verdana", FontWeight.BOLD, 17));
        updatedHandL.setTextFill(Color.RED);

        // Game cards label
        Label gameCardL = new Label(this.engine.getCurrentPlayer().getHandStr());
        gameCardL.setWrapText(true);
        gameCardL.setAlignment(Pos.CENTER);
        gameCardL.setTextFill(Color.WHITE);
        gameCardL.setMaxWidth(390);
        gameCardL.setFont(Font.font("Verdana", 16));

        // Next button
        Button nextB = new Button("Next");
        nextB.setFont(Font.font("Verdana", 20));
        nextB.setOnAction(e -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception dv) {
                dv.printStackTrace();
            }
            if (!defender.isAlive()) {
                setDeadAstronautGraphic(targetName);
            }
            interimPhase();
        });

        // VBox
        VBox vbox = new VBox(20, playerNameLabel, cardInfo, updatedHandL, gameCardL, nextB);
        vbox.setAlignment(Pos.CENTER);

        // StackPane
        StackPane sp = new StackPane(rBcg, vbox);

        // Scene
        Scene scene = new Scene(sp, 440, 300);
        scene.getStylesheets().add(getClass().getResource("CSS/mainGameStyles.css").toExternalForm());

        // Stage
        this.astronautTurnSimulation.setTitle("Oxygen Siphon");
        this.astronautTurnSimulation.setScene(scene);
        this.astronautTurnSimulation.setX(677);
        this.astronautTurnSimulation.setY(240);

    }

    /**
     * When opponent shield upon siphon being played against
     */
    private void hasShieldOxygenSiphon(String targetName, Astronaut defender) {

        // Background rectangle
        Rectangle rBcg = new Rectangle(0, 0, 450, 220);
        rBcg.setFill(Color.rgb(54, 69, 79));

        // Target Name Label
        Label targetL = new Label(targetName);
        targetL.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        targetL.setTextFill(Color.CYAN);

        // Informational label
        Label infoL = new Label(
                this.engine.getCurrentPlayer().toString() + " played an Oxygen Siphon against you. Use a Shield?");
        infoL.setFont(Font.font("Verdana", FontWeight.BOLD, 17));
        infoL.setAlignment(Pos.CENTER);
        infoL.setMaxWidth(430);
        infoL.setTextFill(Color.RED);
        infoL.setWrapText(true);

        // Shield cards label
        Label shieldNumberL = new Label("You currently have " + defender.hasCard(GameDeck.SHIELD) + " Shield card(s).");
        shieldNumberL.setFont(Font.font("Verdana", 17));
        shieldNumberL.setTextFill(Color.YELLOW);
        shieldNumberL.setWrapText(true);

        // No button
        Button noB = new Button("No");
        noB.setFont(Font.font("Verdana", 20));
        noB.setOnAction(e -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception df) {
                df.printStackTrace();
            }
            if (defender.hasCard(GameDeck.OXYGEN_1) == 1 && defender.hasCard(GameDeck.OXYGEN_2) == 0) {
                this.engine.getCurrentPlayer().addToHand(defender.siphon()); // Last Oxygen(1), player is killed
                noShieldOxygenSiphon(targetName, defender, 1);
            } else {
                this.engine.getCurrentPlayer().addToHand(defender.siphon());
                this.engine.getCurrentPlayer().addToHand(defender.siphon()); // If last Oxygens, player killed
                noShieldOxygenSiphon(targetName, defender, 2);
            }
        });

        // Yes button
        Button yesB = new Button("Yes");
        yesB.setFont(Font.font("Verdana", 20));
        if (defender.getTrack().size() > 0) {
            if (defender.hasMeltedEyeballs()) {
                yesB.setDisable(true);
            }
        }
        yesB.setOnAction(e -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception df) {
                df.printStackTrace();
            }
            this.engine.getGameDiscard().add(defender.hack(GameDeck.SHIELD));
            commonShieldPlayedGUI(targetName, "Oxygen Siphon");
        });

        // Hbox
        HBox hb = new HBox(15, noB, yesB);
        hb.setAlignment(Pos.CENTER);

        // Vbox
        VBox v = new VBox(10, targetL, infoL, shieldNumberL, hb);
        v.setAlignment(Pos.CENTER);

        // StackPane
        StackPane sp = new StackPane(rBcg, v);

        // Scene
        Scene sce = new Scene(sp, 450, 220);
        sce.getStylesheets().add(getClass().getResource("CSS/mainGameStyles.css").toExternalForm());

        // Stage
        this.astronautTurnSimulation.setScene(sce);
        this.astronautTurnSimulation.setTitle("Play Shield?");
        this.astronautTurnSimulation.setX(677);
        this.astronautTurnSimulation.setY(240);

    }

    //////////////////// HACK SUIT ////////////////////////
    /**
     * hack suit
     */
    private void hackSuit() {
        int rHeight = 230;

        // Background rectangle
        Rectangle rBcg = new Rectangle(0, 0, 455, rHeight);
        rBcg.setFill(Color.rgb(54, 69, 79));

        // Player name label
        Label playerNameLabel = new Label(this.engine.getCurrentPlayer().toString());
        playerNameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        playerNameLabel.setTextFill(Color.CYAN);

        // Card Label and info
        Label cardInfo = new Label("Hack suit: Select and steal a card from opponent!");
        cardInfo.setWrapText(true);
        cardInfo.setMaxWidth(445);
        cardInfo.setAlignment(Pos.CENTER);
        cardInfo.setTextFill(Color.YELLOW);
        cardInfo.setFont(Font.font("Verdana", 17));

        // Label for info
        Label infoL = new Label("Select a player");
        infoL.setTextFill(Color.RED);
        infoL.setFont(Font.font("Verdana", FontWeight.BOLD, 20));

        // VBox
        VBox v = new VBox(20, playerNameLabel, cardInfo, infoL);
        v.setAlignment(Pos.CENTER);

        // Player Names checkbox
        ArrayList<CheckBox> chBox = new ArrayList<>();
        for (Astronaut a : this.engine.getAllPlayers()) {
            if (a.toString().equals(this.engine.getCurrentPlayer().toString())) {
                continue;
            } else if (!a.isAlive()) {
                continue;
            } else {
                CheckBox cb = new CheckBox(a.toString());
                rHeight += 30;
                cb.setFont(Font.font("Verdana", 17));
                cb.setTextFill(Color.YELLOW);
                chBox.add(cb);
                v.getChildren().add(cb);
            }
        }
        rBcg.setHeight(rHeight);

        // Select button
        Button selectB = new Button("Select");
        selectB.setFont(Font.font("Verdana", 20));
        selectB.setOnAction(e -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception vq) {
                vq.printStackTrace();
            }

            // Checking if only 1 player selected
            int c = 0;
            for (CheckBox cb : chBox) {
                if (cb.isSelected()) {
                    c++;
                }
            }

            if (c != 1) {
                for (CheckBox cx : chBox) {
                    cx.setSelected(false);
                }
                AlertBox ax = new AlertBox("Invalid player selection.");
                ax.showAlertBox();
            } else {

                this.actionsPlayed++; // implementing 2 player logic

                // Hack suit discarded
                Card actionCard = this.engine.getCurrentPlayer().hack(GameDeck.HACK_SUIT);
                this.engine.getGameDiscard().add(actionCard);

                // Getting name of targeted astronaut
                String name = null;
                for (CheckBox cb : chBox) {
                    if (cb.isSelected()) {
                        name = cb.getText();
                    }
                }

                // Getting astronaut instance of targeted player
                Astronaut targetAstronaut = null;
                for (Astronaut a : this.engine.getAllPlayers()) {
                    if (a.toString().equals(name)) {
                        targetAstronaut = a;
                    }
                }

                // Checking if targeted player has Shield
                boolean hasShield = false;

                for (Card card : targetAstronaut.getHand()) {
                    if (card.toString().equals(GameDeck.SHIELD)) {
                        hasShield = true;
                        hasShieldHackSuit(name, targetAstronaut);
                        break;
                    }
                }

                if (!hasShield) {
                    noShieldHackSuit(name, targetAstronaut);
                }
            }
        });

        // Back button
        Button backB = new Button("Back");
        backB.setFont(Font.font("Verdana", 20));
        backB.setOnAction(ev -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception bv) {
                bv.printStackTrace();
            }
            playActionCards();
        });

        // HBox
        HBox hb = new HBox(10, backB, selectB);
        hb.setAlignment(Pos.CENTER);

        // VBox
        v.getChildren().add(hb);

        // StackPane
        StackPane sp = new StackPane(rBcg, v);

        // Scene
        Scene nSce = new Scene(sp, 430, rHeight);
        nSce.getStylesheets().add(getClass().getResource("CSS/mainGameStyles.css").toExternalForm());

        // Stage
        this.astronautTurnSimulation.setScene(nSce);
        this.astronautTurnSimulation.setTitle("Hack Suit");
        this.astronautTurnSimulation.setX(612);
        this.astronautTurnSimulation.setY(200);

    }

    /**
     * GUI showing all cards of targeted player
     * 
     * @param targetName name of targeted player
     * @param defender   astronaut instance of targeted player
     */
    private void noShieldHackSuit(String targetName, Astronaut defender) {

        int initialHeight = 250;

        // Background
        Rectangle r = new Rectangle(0, 0, 400, initialHeight);
        r.setFill(Color.rgb(54, 69, 79));

        // Player name label
        Label playerNameLabel = new Label(this.engine.getCurrentPlayer().toString());
        playerNameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        playerNameLabel.setTextFill(Color.CYAN);

        // Label
        Label instructionL = new Label("Pick one card");
        instructionL.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        instructionL.setTextFill(Color.RED);
        instructionL.setAlignment(Pos.CENTER);

        // Label for target hand infoL
        Label infoL = new Label(targetName + "'s all game cards:");
        infoL.setFont(Font.font("Verdana", FontWeight.BOLD, 17));
        infoL.setTextFill(Color.YELLOW);
        infoL.setAlignment(Pos.CENTER);

        // CheckBox VBox
        VBox cbxVB = new VBox(20);
        cbxVB.setAlignment(Pos.CENTER);

        // Checkboxes of target's cards to display
        ArrayList<CheckBox> cboxList = new ArrayList<>();
        for (Card card : defender.getHand()) {
            CheckBox cBox = new CheckBox(card.toString());
            cBox.setFont(Font.font("Verdana"));
            cBox.setTextFill(Color.YELLOW);
            cboxList.add(cBox);
            cbxVB.getChildren().add(cBox);
            initialHeight += 30;
        }
        r.setHeight(initialHeight);

        // Pick button
        Button pickB = new Button("Pick");
        pickB.setFont(Font.font("Verdana", 20));
        pickB.setOnAction(e -> {
            try {
                GameApp.playDrawCardSound();
            } catch (Exception xc) {
                xc.printStackTrace();
            }

            // Checking if only 1 checkbox selected
            int cc = 0;
            for (CheckBox b : cboxList) {
                if (b.isSelected()) {
                    cc++;
                }
            }
            if (cc != 1) {
                for (CheckBox cx : cboxList) {
                    cx.setSelected(false);
                }
                AlertBox alertBOX = new AlertBox("Invalid selection");
                alertBOX.showAlertBox();
            } else {

                // Getting name of card selected
                String cardName = "";
                for (CheckBox b : cboxList) {
                    if (b.isSelected()) {
                        cardName = b.getText();
                    }
                }
                this.engine.getCurrentPlayer().addToHand(defender.hack(cardName));

                // Killing target if last oxygen hacked
                if (!defender.isAlive()) {
                    setDeadAstronautGraphic(targetName);
                }
                cardPickedGUI(targetName, cardName);
            }
        });

        // Main VBox
        VBox vB = new VBox(10, playerNameLabel, instructionL, infoL, cbxVB, pickB);
        vB.setAlignment(Pos.CENTER);

        // Stackpane
        StackPane sp = new StackPane(r, vB);

        // scene and stage
        Scene scn = new Scene(sp, 400, initialHeight);
        scn.getStylesheets().add(getClass().getResource("CSS/mainGameStyles.css").toExternalForm());

        this.astronautTurnSimulation.setTitle("Choose card");
        this.astronautTurnSimulation.setScene(scn);
        this.astronautTurnSimulation.setX(627);
        this.astronautTurnSimulation.setY(250);

    }

    /**
     * GUI for when target has shield
     * 
     * @param targetName name of targeted player
     * @param defender   astronaut instance of targeted player
     */
    private void hasShieldHackSuit(String targetName, Astronaut defender) {

        // Background rectangle
        Rectangle rBcg = new Rectangle(0, 0, 450, 220);
        rBcg.setFill(Color.rgb(54, 69, 79));

        // Target Name Label
        Label targetL = new Label(targetName);
        targetL.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        targetL.setTextFill(Color.CYAN);

        // Informational label
        Label infoL = new Label(
                this.engine.getCurrentPlayer().toString() + " played a Hack Suit against you. Use a Shield?");
        infoL.setFont(Font.font("Verdana", FontWeight.BOLD, 17));
        infoL.setAlignment(Pos.CENTER);
        infoL.setTextFill(Color.RED);
        infoL.setWrapText(true);

        // Shield cards label
        Label shieldNumberL = new Label("You currently have " + defender.hasCard(GameDeck.SHIELD) + " Shield card(s).");
        shieldNumberL.setFont(Font.font("Verdana", 17));
        shieldNumberL.setTextFill(Color.YELLOW);
        shieldNumberL.setWrapText(true);

        // No button
        Button noB = new Button("No");
        noB.setFont(Font.font("Verdana", 20));
        noB.setOnAction(e -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception df) {
                df.printStackTrace();
            }
            noShieldHackSuit(targetName, defender);
        });

        // Yes button
        Button yesB = new Button("Yes");
        yesB.setFont(Font.font("Verdana", 20));
        if (defender.getTrack().size() > 0) {
            if (defender.hasMeltedEyeballs()) {
                yesB.setDisable(true);
            }
        }
        yesB.setOnAction(e -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception df) {
                df.printStackTrace();
            }
            this.engine.getGameDiscard().add(defender.hack(GameDeck.SHIELD));
            commonShieldPlayedGUI(targetName, "Hack Suit");
        });

        // Hbox
        HBox hb = new HBox(15, noB, yesB);
        hb.setAlignment(Pos.CENTER);

        // Vbox
        VBox v = new VBox(10, targetL, infoL, shieldNumberL, hb);
        v.setAlignment(Pos.CENTER);

        // StackPane
        StackPane sp = new StackPane(rBcg, v);

        // Scene
        Scene sce = new Scene(sp, 450, 220);
        sce.getStylesheets().add(getClass().getResource("CSS/mainGameStyles.css").toExternalForm());

        // Stage
        this.astronautTurnSimulation.setScene(sce);
        this.astronautTurnSimulation.setTitle("Play Shield?");
        this.astronautTurnSimulation.setX(677);
        this.astronautTurnSimulation.setY(240);

    }

    /**
     * GUI shown when card is picked
     */
    private void cardPickedGUI(String targetName, String cardPicked) {

        // Background
        Rectangle r = new Rectangle(0, 0, 400, 300);
        r.setFill(Color.rgb(54, 69, 79));

        // Player name label
        Label playerNameLabel = new Label(this.engine.getCurrentPlayer().toString());
        playerNameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        playerNameLabel.setTextFill(Color.CYAN);

        // Cards taken label
        Label cardsTakenL = new Label(cardPicked + " taken from " + targetName + "!");
        cardsTakenL.setFont(Font.font("Verdana", 20));
        cardsTakenL.setTextFill(Color.YELLOW);

        // Updated hand Label
        Label updatedHandL = new Label("Your updated game cards:");
        updatedHandL.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        updatedHandL.setTextFill(Color.RED);

        // Current player hand
        Label currentPlayerHandL = new Label(this.engine.getCurrentPlayer().getHandStr());
        currentPlayerHandL.setFont(Font.font("Verdana", 16));
        currentPlayerHandL.setMaxWidth(385);
        currentPlayerHandL.setAlignment(Pos.CENTER);
        currentPlayerHandL.setTextFill(Color.WHITE);
        currentPlayerHandL.setWrapText(true);

        // Next button
        Button nextB = new Button("Next");
        nextB.setFont(Font.font("Verdana", 20));
        nextB.setOnAction(e -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception nm) {
                nm.printStackTrace();
            }
            interimPhase();
        });

        // VBox
        VBox v = new VBox(15, playerNameLabel, cardsTakenL, updatedHandL, currentPlayerHandL, nextB);
        v.setAlignment(Pos.CENTER);

        // StackPane
        StackPane sp = new StackPane(r, v);

        // Stage and scene
        Scene scn = new Scene(sp, 400, 300);
        scn.getStylesheets().add(getClass().getResource("CSS/mainGameStyles.css").toExternalForm());

        this.astronautTurnSimulation.setTitle("Hack Suit");
        this.astronautTurnSimulation.setScene(scn);
        this.astronautTurnSimulation.setX(627);
        this.astronautTurnSimulation.setY(250);

    }

    //////////////////// TRACTOR BEAM ////////////////////////
    /**
     * Method implementing tractor beam
     */
    private void tractorBeam() {

        int rHeight = 230;

        // Background rectangle
        Rectangle rBcg = new Rectangle(0, 0, 430, rHeight);
        rBcg.setFill(Color.rgb(54, 69, 79));

        // Player name label
        Label playerNameLabel = new Label(this.engine.getCurrentPlayer().toString());
        playerNameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        playerNameLabel.setTextFill(Color.CYAN);

        // Card Label and info
        Label cardInfo = new Label("Tractor Beam: Steal a random card from opponent!");
        cardInfo.setMaxWidth(410);
        cardInfo.setWrapText(true);
        cardInfo.setAlignment(Pos.CENTER);
        cardInfo.setTextFill(Color.YELLOW);
        cardInfo.setFont(Font.font("Verdana", 17));

        // Label for info
        Label infoL = new Label("Select a player");
        infoL.setTextFill(Color.RED);
        infoL.setFont(Font.font("Verdana", FontWeight.BOLD, 20));

        // VBox
        VBox v = new VBox(15, playerNameLabel, cardInfo, infoL);
        v.setAlignment(Pos.TOP_CENTER);

        // Player Names checkbox
        ArrayList<CheckBox> chBox = new ArrayList<>();
        for (Astronaut a : this.engine.getAllPlayers()) {
            if (a.toString().equals(this.engine.getCurrentPlayer().toString())) {
                continue;
            } else if (!a.isAlive()) {
                continue;
            } else {
                CheckBox cb = new CheckBox(a.toString());
                rHeight += 30;
                cb.setFont(Font.font("Verdana", 17));
                cb.setTextFill(Color.YELLOW);
                chBox.add(cb);
                v.getChildren().add(cb);
            }
        }
        rBcg.setHeight(rHeight);

        // Select button
        Button selectB = new Button("Select");
        selectB.setFont(Font.font("Verdana", 20));
        selectB.setOnAction(e -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception vq) {
                vq.printStackTrace();
            }

            // Checking if only 1 player selected
            int c = 0;
            for (CheckBox cb : chBox) {
                if (cb.isSelected()) {
                    c++;
                }
            }

            if (c != 1) {
                for (CheckBox cx : chBox) {
                    cx.setSelected(false);
                }
                AlertBox ax = new AlertBox("Invalid player selection.");
                ax.showAlertBox();
            } else {

                this.actionsPlayed++; // implementing 2 player logic

                // Tractor beam discarded
                Card actionCard = this.engine.getCurrentPlayer().hack(GameDeck.TRACTOR_BEAM);
                this.engine.getGameDiscard().add(actionCard);

                // Getting name of targeted astronaut
                String name = null;
                for (CheckBox cb : chBox) {
                    if (cb.isSelected()) {
                        name = cb.getText();
                    }
                }

                // Getting astronaut instance of targeted player
                Astronaut targetAstronaut = null;
                for (Astronaut a : this.engine.getAllPlayers()) {
                    if (a.toString().equals(name)) {
                        targetAstronaut = a;
                    }
                }

                // Checking if targeted player has Shield
                boolean hasShield = false;

                for (Card card : targetAstronaut.getHand()) {
                    if (card.toString().equals(GameDeck.SHIELD)) {
                        hasShield = true;
                        hasShieldTractorBeam(name, targetAstronaut);
                        break;
                    }
                }

                if (!hasShield) {
                    noShieldTractorBeam(name, targetAstronaut);
                }
            }
        });

        // Back button
        Button backB = new Button("Back");
        backB.setFont(Font.font("Verdana", 20));
        backB.setOnAction(ev -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception bv) {
                bv.printStackTrace();
            }
            playActionCards();
        });

        // HBox
        HBox hb = new HBox(10, backB, selectB);
        hb.setAlignment(Pos.CENTER);

        // VBox
        v.getChildren().add(hb);

        // StackPane
        StackPane sp = new StackPane(rBcg, v);

        // Scene
        Scene nSce = new Scene(sp, 430, rHeight);
        nSce.getStylesheets().add(getClass().getResource("CSS/mainGameStyles.css").toExternalForm());

        // Stage
        this.astronautTurnSimulation.setScene(nSce);
        this.astronautTurnSimulation.setTitle("Tractor Beam");
        this.astronautTurnSimulation.setX(612);
        this.astronautTurnSimulation.setY(200);

    }

    /**
     * When target has no shield
     */
    private void noShieldTractorBeam(String targetName, Astronaut defender) {

        // Background rectangle
        Rectangle rBcg = new Rectangle(0, 0, 280, 415);
        rBcg.setFill(Color.rgb(54, 69, 79));

        // Player name label
        Label playerNameLabel = new Label(this.engine.getCurrentPlayer().toString());
        playerNameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        playerNameLabel.setTextFill(Color.CYAN);

        // Card Label and info
        Label cardInfo = new Label("Card drawn from " + targetName + ":");
        cardInfo.setWrapText(true);
        cardInfo.setAlignment(Pos.CENTER);
        cardInfo.setTextFill(Color.RED);
        cardInfo.setFont(Font.font("Verdana", FontWeight.BOLD, 16));

        // Drawing random card and adding to current player's hand
        Card randomCard = defender.steal();
        this.engine.getCurrentPlayer().addToHand(randomCard);
        ImageView imV = giveCardImageView(randomCard.toString());

        // Next Button
        Button nextB = new Button("Next");
        nextB.setFont(Font.font("Verdana", 20));
        nextB.setOnAction(e -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception ds) {
                ds.printStackTrace();
            }
            // Killing defender if dead
            if (!defender.isAlive()) {
                setDeadAstronautGraphic(targetName);
            }
            interimPhase();
        });

        // VBox
        VBox v = new VBox(5, playerNameLabel, cardInfo, imV, nextB);
        v.setAlignment(Pos.CENTER);

        // StackPane
        StackPane sp = new StackPane(rBcg, v);

        // Scene
        Scene nSce = new Scene(sp, 280, 415);
        nSce.getStylesheets().add(getClass().getResource("CSS/mainGameStyles.css").toExternalForm());

        // Stage
        this.astronautTurnSimulation.setScene(nSce);
        this.astronautTurnSimulation.setTitle("Tractor Beam");
        this.astronautTurnSimulation.setX(612);
        this.astronautTurnSimulation.setY(200);

    }

    /**
     * GUI implementing Tractor Beam when the targeted player has Shield
     * 
     * @param targetName name of targeted astronaut
     * @param defender   astronaut instance of targeted player
     */
    private void hasShieldTractorBeam(String targetName, Astronaut defender) {

        // Background rectangle
        Rectangle rBcg = new Rectangle(0, 0, 450, 220);
        rBcg.setFill(Color.rgb(54, 69, 79));

        // Target Name Label
        Label targetL = new Label(targetName);
        targetL.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        targetL.setTextFill(Color.CYAN);

        // Informational label
        Label infoL = new Label(
                this.engine.getCurrentPlayer().toString() + " played a Tractor Beam against you. Use a Shield?");
        infoL.setFont(Font.font("Verdana", FontWeight.BOLD, 17));
        infoL.setMaxWidth(430);
        infoL.setAlignment(Pos.CENTER);
        infoL.setTextFill(Color.RED);
        infoL.setWrapText(true);

        // Shield cards label
        Label shieldNumberL = new Label("You currently have " + defender.hasCard(GameDeck.SHIELD) + " Shield card(s).");
        shieldNumberL.setFont(Font.font("Verdana", 17));
        shieldNumberL.setTextFill(Color.YELLOW);
        shieldNumberL.setWrapText(true);

        // No button
        Button noB = new Button("No");
        noB.setFont(Font.font("Verdana", 20));
        noB.setOnAction(e -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception df) {
                df.printStackTrace();
            }
            noShieldTractorBeam(targetName, defender);
        });

        // Yes button
        Button yesB = new Button("Yes");
        yesB.setFont(Font.font("Verdana", 20));
        if (defender.getTrack().size() > 0) {
            if (defender.hasMeltedEyeballs()) {
                yesB.setDisable(true);
            }
        }
        yesB.setOnAction(e -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception df) {
                df.printStackTrace();
            }
            this.engine.getGameDiscard().add(defender.hack(GameDeck.SHIELD));
            commonShieldPlayedGUI(targetName, "Tractor Beam");
        });

        // Hbox
        HBox hb = new HBox(15, noB, yesB);
        hb.setAlignment(Pos.CENTER);

        // Vbox
        VBox v = new VBox(10, targetL, infoL, shieldNumberL, hb);
        v.setAlignment(Pos.CENTER);

        // StackPane
        StackPane sp = new StackPane(rBcg, v);

        // Scene
        Scene sce = new Scene(sp, 450, 220);
        sce.getStylesheets().add(getClass().getResource("CSS/mainGameStyles.css").toExternalForm());

        // Stage
        this.astronautTurnSimulation.setScene(sce);
        this.astronautTurnSimulation.setTitle("Play Shield?");
        this.astronautTurnSimulation.setX(677);
        this.astronautTurnSimulation.setY(240);

    }

    //////////////////// ROCKET BOOSTER ////////////////////////
    /**
     * GUI for action card rocket booster
     */
    private void rocketBooster() {

        // Background rectangle
        Rectangle rBcg = new Rectangle(0, 0, 460, 190);
        rBcg.setFill(Color.rgb(54, 69, 79));

        // Player name label
        Label playerNameLabel = new Label(this.engine.getCurrentPlayer().toString());
        playerNameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        playerNameLabel.setTextFill(Color.CYAN);

        // Card Label and info
        Label cardInfo = new Label("Rocket Booster: Move forward without discarding oxygen!");
        cardInfo.setMaxWidth(440);
        cardInfo.setWrapText(true);
        cardInfo.setAlignment(Pos.CENTER);
        cardInfo.setTextFill(Color.YELLOW);
        cardInfo.setFont(Font.font("Verdana", 17));

        // Move button
        Button moveB = new Button("Move");
        moveB.setFont(Font.font("Verdana", 20));
        moveB.setOnAction(e -> {
            this.actionsPlayed++; // implementing 2-player logic
            try {
                GameApp.playTravelSound();
            } catch (Exception vf) {
                vf.printStackTrace();
            }
            // Discard rocket booster card
            this.engine.getGameDiscard().add(this.engine.getCurrentPlayer().hack(GameDeck.ROCKET_BOOSTER));
            // Call move method
            rocketBoosterMove();
        });

        // Back button
        Button backB = new Button("Back");
        backB.setFont(Font.font("Verdana", 20));
        backB.setOnAction(ev -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception bv) {
                bv.printStackTrace();
            }
            playActionCards();
        });

        // HBox
        HBox hb = new HBox(10, backB, moveB);
        hb.setAlignment(Pos.CENTER);

        // VBox
        VBox v = new VBox(15, playerNameLabel, cardInfo, hb);
        v.setAlignment(Pos.CENTER);

        // StackPane
        StackPane s = new StackPane(rBcg, v);

        // Stage and scene
        Scene scn = new Scene(s, 460, 190);
        scn.getStylesheets().add(getClass().getResource("CSS/mainGameStyles.css").toExternalForm());

        this.astronautTurnSimulation.setTitle("Rocket Booster");
        this.astronautTurnSimulation.setScene(scn);
        this.astronautTurnSimulation.setX(627);
        this.astronautTurnSimulation.setY(250);

    }

    /**
     * Implementing logic for rocket booster move functionality
     */
    private void rocketBoosterMove() {

        // Draw new space card
        Card spaceCard = this.engine.getSpaceDeck().draw();

        // Add new space card to track or discard
        if (spaceCard.toString().equals(SpaceDeck.GRAVITATIONAL_ANOMALY)) {
            this.engine.getSpaceDiscard().add(spaceCard);
        } else {
            this.engine.getCurrentPlayer().addToTrack(spaceCard);
        }

        int index = GameApp.playerNames.indexOf(this.engine.getCurrentPlayer().toString());

        // Switch statement to call move astronaut method
        switch (index) {
            case 0:
                moveAstronautB(this.purpleAstronautB, spaceCard, this.purpleAstronautTrackB, 2, 0);
                break;
            case 1:
                moveAstronautB(this.greenAstronautB, spaceCard, this.greenAstronautTrackB, 2, 1);
                break;
            case 2:
                moveAstronautB(this.yellowAstronautB, spaceCard, this.yellowAstronautTrackB, 2, 2);
                break;
            case 3:
                moveAstronautB(this.redAstronautB, spaceCard, this.redAstronautTrackB, 2, 3);
                break;
            case 4:
                moveAstronautB(this.blueAstronautB, spaceCard, this.blueAstronautTrackB, 2, 4);
                break;
        }

    }

    //////////////////// LASER BLAST ////////////////////////
    /**
     * Method implementing laser blast's functionality
     */
    private void laserBlast() {

        int rHeight = 255;

        // Background rectangle
        Rectangle rBcg = new Rectangle(0, 0, 430, rHeight);
        rBcg.setFill(Color.rgb(54, 69, 79));

        // Player name label
        Label playerNameLabel = new Label(this.engine.getCurrentPlayer().toString());
        playerNameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        playerNameLabel.setTextFill(Color.CYAN);

        // Card Label and info
        Label cardInfo = new Label(
                "Laser Blast: Pick another player and push them back 1 space. Cannot be played when the opponent is at the starting space.");
        cardInfo.setMaxWidth(410);
        cardInfo.setWrapText(true);
        cardInfo.setAlignment(Pos.CENTER);
        cardInfo.setTextFill(Color.YELLOW);
        cardInfo.setFont(Font.font("Verdana", 17));

        // Label for info
        Label infoL = new Label("Select a player");
        infoL.setTextFill(Color.RED);
        infoL.setFont(Font.font("Verdana", FontWeight.BOLD, 20));

        // VBox
        VBox v = new VBox(15, playerNameLabel, cardInfo, infoL);
        v.setAlignment(Pos.CENTER);

        // Player Names checkbox
        ArrayList<CheckBox> chBox = new ArrayList<>();
        for (Astronaut a : this.engine.getAllPlayers()) {
            if (a.toString().equals(this.engine.getCurrentPlayer().toString())) {
                continue;
            } else if (!a.isAlive()) {
                continue;
            } else {
                CheckBox cb = new CheckBox(a.toString());
                rHeight += 30;
                cb.setFont(Font.font("Verdana", 17));
                cb.setTextFill(Color.YELLOW);
                chBox.add(cb);
                v.getChildren().add(cb);
            }
        }
        rBcg.setHeight(rHeight);

        // Select button
        Button selectB = new Button("Select");
        selectB.setFont(Font.font("Verdana", 20));
        selectB.setOnAction(e -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception vq) {
                vq.printStackTrace();
            }

            // Checking if only 1 player selected
            int c = 0;
            for (CheckBox cb : chBox) {
                if (cb.isSelected()) {
                    c++;
                }
            }

            if (c != 1) {
                for (CheckBox cx : chBox) {
                    cx.setSelected(false);
                }
                AlertBox ax = new AlertBox("Invalid player selection.");
                ax.showAlertBox();
            } else {

                // Getting name of targeted astronaut
                String name = null;
                for (CheckBox cb : chBox) {
                    if (cb.isSelected()) {
                        name = cb.getText();
                    }
                }

                // Getting astronaut instance of targeted player
                Astronaut targetAstronaut = null;
                for (Astronaut a : this.engine.getAllPlayers()) {
                    if (a.toString().equals(name)) {
                        targetAstronaut = a;
                    }
                }

                // Checking is targeted astronaut is at starting space
                if (targetAstronaut.getTrack().size() == 0) {
                    for (CheckBox box : chBox) {
                        box.setSelected(false);
                    }
                    AlertBox aB = new AlertBox("Player is at starting space!");
                    aB.showAlertBox();
                }

                else {

                    this.actionsPlayed++; // implementing 2 player logic

                    // Laser blast discarded
                    Card actionCard = this.engine.getCurrentPlayer().hack(GameDeck.LASER_BLAST);
                    this.engine.getGameDiscard().add(actionCard);

                    // Checking if targeted player has Shield
                    boolean hasShield = false;

                    for (Card card : targetAstronaut.getHand()) {
                        if (card.toString().equals(GameDeck.SHIELD)) {
                            hasShield = true;
                            hasShieldLaserBlast(name, targetAstronaut);
                            break;
                        }
                    }

                    if (!hasShield) {

                        // Remove topmost card from defender's track internally
                        Card toRemove = targetAstronaut.laserBlast();
                        this.engine.getSpaceDiscard().add(toRemove);

                        int index = GameApp.playerNames.indexOf(name);
                        switch (index) {
                            case 0: // push purple back
                                double yCoordPurple = this.purpleAstronautTrackB
                                        .get(this.purpleAstronautTrackB.size() - 1)
                                        .getLayoutY() - 20;
                                this.spaceCardsPane.getChildren()
                                        .remove(this.purpleAstronautTrackB.get(this.purpleAstronautTrackB.size() - 1));
                                this.purpleAstronautTrackB.remove(this.purpleAstronautTrackB.size() - 1);
                                this.purpleAstronautB.setLayoutY(yCoordPurple);
                                break;

                            case 1: // push green back
                                double yCoordGreen = this.greenAstronautTrackB.get(this.greenAstronautTrackB.size() - 1)
                                        .getLayoutY() - 20;
                                this.spaceCardsPane.getChildren()
                                        .remove(this.greenAstronautTrackB.get(this.greenAstronautTrackB.size() - 1));
                                this.greenAstronautTrackB.remove(this.greenAstronautTrackB.size() - 1);
                                this.greenAstronautB.setLayoutY(yCoordGreen);
                                break;

                            case 2: // push yellow back
                                double yCoordYellow = this.yellowAstronautTrackB
                                        .get(this.yellowAstronautTrackB.size() - 1)
                                        .getLayoutY() - 20;
                                this.spaceCardsPane.getChildren()
                                        .remove(this.yellowAstronautTrackB.get(this.yellowAstronautTrackB.size() - 1));
                                this.yellowAstronautTrackB.remove(this.yellowAstronautTrackB.size() - 1);
                                this.yellowAstronautB.setLayoutY(yCoordYellow);
                                break;

                            case 3: // push red back
                                double yCoordRed = this.redAstronautTrackB.get(this.redAstronautTrackB.size() - 1)
                                        .getLayoutY() - 20;
                                this.spaceCardsPane.getChildren()
                                        .remove(this.redAstronautTrackB.get(this.redAstronautTrackB.size() - 1));
                                this.redAstronautTrackB.remove(this.redAstronautTrackB.size() - 1);
                                this.redAstronautB.setLayoutY(yCoordRed);
                                break;
                            case 4: // push blue back
                                double yCoordBlue = this.blueAstronautTrackB.get(this.blueAstronautTrackB.size() - 1)
                                        .getLayoutY() - 20;
                                this.spaceCardsPane.getChildren()
                                        .remove(this.blueAstronautTrackB.get(this.blueAstronautTrackB.size() - 1));
                                this.blueAstronautTrackB.remove(this.blueAstronautTrackB.size() - 1);
                                this.blueAstronautB.setLayoutY(yCoordBlue);
                                break;

                        }
                        noShieldLaserBlast(name, targetAstronaut);
                    }

                }

            }
        });

        // Back button
        Button backB = new Button("Back");
        backB.setFont(Font.font("Verdana", 20));
        backB.setOnAction(ev -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception cd) {
                cd.printStackTrace();
            }
            playActionCards();
        });

        // HBox
        HBox hb = new HBox(13, backB, selectB);
        hb.setAlignment(Pos.CENTER);

        // VBox
        v.getChildren().add(hb);

        // StackPane
        StackPane sp = new StackPane(rBcg, v);

        // Scene
        Scene nSce = new Scene(sp, 430, rHeight);
        nSce.getStylesheets().add(getClass().getResource("CSS/mainGameStyles.css").toExternalForm());

        // Stage
        this.astronautTurnSimulation.setScene(nSce);
        this.astronautTurnSimulation.setTitle("Laser Blast");
        this.astronautTurnSimulation.setX(612);
        this.astronautTurnSimulation.setY(200);

    }

    /**
     * GUI showing targeted player moved back.
     * 
     * @param targetName name of targeted player
     * @param defender   astronaut instance of targeted player
     */
    private void noShieldLaserBlast(String targetName, Astronaut defender) {

        // Background rectangle
        Rectangle rBcg = new Rectangle(0, 0, 400, 180);
        rBcg.setFill(Color.rgb(54, 69, 79));

        // Player name label
        Label playerNameLabel = new Label(this.engine.getCurrentPlayer().toString());
        playerNameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        playerNameLabel.setTextFill(Color.CYAN);

        // Card Label and info
        Label cardInfo = new Label(targetName + " moved 1 space back!");
        cardInfo.setWrapText(true);
        cardInfo.setAlignment(Pos.CENTER);
        cardInfo.setMaxWidth(380);
        cardInfo.setTextFill(Color.RED);
        cardInfo.setFont(Font.font("Verdana", FontWeight.BOLD, 20));

        // Next Button
        Button nextB = new Button("Next");
        nextB.setFont(Font.font("Verdana", 20));
        nextB.setOnAction(e -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception ds) {
                ds.printStackTrace();
            }
            interimPhase();
        });

        // VBox
        VBox v = new VBox(15, playerNameLabel, cardInfo, nextB);
        v.setAlignment(Pos.CENTER);

        // StackPane
        StackPane sp = new StackPane(rBcg, v);

        // Scene
        Scene nSce = new Scene(sp, 400, 180);
        nSce.getStylesheets().add(getClass().getResource("CSS/mainGameStyles.css").toExternalForm());

        // Stage
        this.astronautTurnSimulation.setScene(nSce);
        this.astronautTurnSimulation.setTitle("Laser Blast");
        this.astronautTurnSimulation.setX(612);
        this.astronautTurnSimulation.setY(200);

    }

    /**
     * GUI for when laser blast played and target has shield
     * 
     * @param targetName name of targeted player
     * @param defender   astronaut instance of targeted player
     */
    private void hasShieldLaserBlast(String targetName, Astronaut defender) {

        // Background rectangle
        Rectangle rBcg = new Rectangle(0, 0, 450, 220);
        rBcg.setFill(Color.rgb(54, 69, 79));

        // Target Name Label
        Label targetL = new Label(targetName);
        targetL.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        targetL.setTextFill(Color.CYAN);

        // Informational label
        Label infoL = new Label(
                this.engine.getCurrentPlayer().toString() + " played a Laser Blast against you. Use a Shield?");
        infoL.setFont(Font.font("Verdana", FontWeight.BOLD, 17));
        infoL.setMaxWidth(430);
        infoL.setAlignment(Pos.CENTER);
        infoL.setTextFill(Color.RED);
        infoL.setWrapText(true);

        // Shield cards label
        Label shieldNumberL = new Label("You currently have " + defender.hasCard(GameDeck.SHIELD) + " Shield card(s).");
        shieldNumberL.setFont(Font.font("Verdana", 17));
        shieldNumberL.setTextFill(Color.YELLOW);
        shieldNumberL.setWrapText(true);

        // No button
        Button noB = new Button("No");
        noB.setFont(Font.font("Verdana", 20));
        noB.setOnAction(e -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception df) {
                df.printStackTrace();
            }
            // Remove topmost card from defender's track internally
            Card toRemove = defender.laserBlast();
            this.engine.getSpaceDiscard().add(toRemove);

            int index = GameApp.playerNames.indexOf(targetName);
            switch (index) {
                case 0: // push purple back
                    double yCoordPurple = this.purpleAstronautTrackB
                            .get(this.purpleAstronautTrackB.size() - 1)
                            .getLayoutY() - 20;
                    this.spaceCardsPane.getChildren()
                            .remove(this.purpleAstronautTrackB.get(this.purpleAstronautTrackB.size() - 1));
                    this.purpleAstronautTrackB.remove(this.purpleAstronautTrackB.size() - 1);
                    this.purpleAstronautB.setLayoutY(yCoordPurple);
                    break;

                case 1: // push green back
                    double yCoordGreen = this.greenAstronautTrackB.get(this.greenAstronautTrackB.size() - 1)
                            .getLayoutY() - 20;
                    this.spaceCardsPane.getChildren()
                            .remove(this.greenAstronautTrackB.get(this.greenAstronautTrackB.size() - 1));
                    this.greenAstronautTrackB.remove(this.greenAstronautTrackB.size() - 1);
                    this.greenAstronautB.setLayoutY(yCoordGreen);
                    break;

                case 2: // push yellow back
                    double yCoordYellow = this.yellowAstronautTrackB
                            .get(this.yellowAstronautTrackB.size() - 1)
                            .getLayoutY() - 20;
                    this.spaceCardsPane.getChildren()
                            .remove(this.yellowAstronautTrackB.get(this.yellowAstronautTrackB.size() - 1));
                    this.yellowAstronautTrackB.remove(this.yellowAstronautTrackB.size() - 1);
                    this.yellowAstronautB.setLayoutY(yCoordYellow);
                    break;

                case 3: // push red back
                    double yCoordRed = this.redAstronautTrackB.get(this.redAstronautTrackB.size() - 1)
                            .getLayoutY() - 20;
                    this.spaceCardsPane.getChildren()
                            .remove(this.redAstronautTrackB.get(this.redAstronautTrackB.size() - 1));
                    this.redAstronautTrackB.remove(this.redAstronautTrackB.size() - 1);
                    this.redAstronautB.setLayoutY(yCoordRed);
                    break;
                case 4: // push blue back
                    double yCoordBlue = this.blueAstronautTrackB.get(this.blueAstronautTrackB.size() - 1)
                            .getLayoutY() - 20;
                    this.spaceCardsPane.getChildren()
                            .remove(this.blueAstronautTrackB.get(this.blueAstronautTrackB.size() - 1));
                    this.blueAstronautTrackB.remove(this.blueAstronautTrackB.size() - 1);
                    this.blueAstronautB.setLayoutY(yCoordBlue);
                    break;

            }
            noShieldLaserBlast(targetName, defender);
        });

        // Yes button
        Button yesB = new Button("Yes");
        yesB.setFont(Font.font("Verdana", 20));
        if (defender.getTrack().size() > 0) {
            if (defender.hasMeltedEyeballs()) {
                yesB.setDisable(true);
            }
        }
        yesB.setOnAction(e -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception df) {
                df.printStackTrace();
            }
            this.engine.getGameDiscard().add(defender.hack(GameDeck.SHIELD));
            commonShieldPlayedGUI(targetName, "Laser Blast");
        });

        // Hbox
        HBox hb = new HBox(15, noB, yesB);
        hb.setAlignment(Pos.CENTER);

        // Vbox
        VBox v = new VBox(10, targetL, infoL, shieldNumberL, hb);
        v.setAlignment(Pos.CENTER);

        // StackPane
        StackPane sp = new StackPane(rBcg, v);

        // Scene
        Scene sce = new Scene(sp, 450, 220);
        sce.getStylesheets().add(getClass().getResource("CSS/mainGameStyles.css").toExternalForm());

        // Stage
        this.astronautTurnSimulation.setScene(sce);
        this.astronautTurnSimulation.setTitle("Play Shield?");
        this.astronautTurnSimulation.setX(677);
        this.astronautTurnSimulation.setY(240);

    }

    //////////////////// HOLE IN SUIT ////////////////////////
    /**
     * Method implementing hole in suit's functionality
     */
    private void holeInSuit() {

        int rHeight = 245;

        // Background rectangle
        Rectangle rBcg = new Rectangle(0, 0, 430, rHeight);
        rBcg.setFill(Color.rgb(54, 69, 79));

        // Player name label
        Label playerNameLabel = new Label(this.engine.getCurrentPlayer().toString());
        playerNameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        playerNameLabel.setTextFill(Color.CYAN);

        // Card Label and info
        Label cardInfo = new Label("Hole in suit: Selected player discards 1 oxygen.");
        cardInfo.setMaxWidth(410);
        cardInfo.setWrapText(true);
        cardInfo.setAlignment(Pos.CENTER);
        cardInfo.setTextFill(Color.YELLOW);
        cardInfo.setFont(Font.font("Verdana", 17));

        // Label for info
        Label infoL = new Label("Select a player");
        infoL.setTextFill(Color.RED);
        infoL.setFont(Font.font("Verdana", FontWeight.BOLD, 20));

        // VBox
        VBox v = new VBox(15, playerNameLabel, cardInfo, infoL);
        v.setAlignment(Pos.CENTER);

        // Player Names checkbox
        ArrayList<CheckBox> chBox = new ArrayList<>();
        for (Astronaut a : this.engine.getAllPlayers()) {
            if (a.toString().equals(this.engine.getCurrentPlayer().toString())) {
                continue;
            } else if (!a.isAlive()) {
                continue;
            } else {
                CheckBox cb = new CheckBox(a.toString());
                rHeight += 30;
                cb.setFont(Font.font("Verdana", 17));
                cb.setTextFill(Color.YELLOW);
                chBox.add(cb);
                v.getChildren().add(cb);
            }
        }
        rBcg.setHeight(rHeight);

        // Select button
        Button selectB = new Button("Select");
        selectB.setFont(Font.font("Verdana", 20));
        selectB.setOnAction(e -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception vq) {
                vq.printStackTrace();
            }

            // Checking if only 1 player selected
            int c = 0;
            for (CheckBox cb : chBox) {
                if (cb.isSelected()) {
                    c++;
                }
            }

            if (c != 1) {
                for (CheckBox cx : chBox) {
                    cx.setSelected(false);
                }
                AlertBox ax = new AlertBox("Invalid player selection.");
                ax.showAlertBox();
            }

            else {

                this.actionsPlayed++; // implementing 2 player logic

                // Hole in suit discarded
                Card actionCard = this.engine.getCurrentPlayer().hack(GameDeck.HOLE_IN_SUIT);
                this.engine.getGameDiscard().add(actionCard);

                // Getting name of targeted astronaut
                String name = null;
                for (CheckBox cb : chBox) {
                    if (cb.isSelected()) {
                        name = cb.getText();
                    }
                }

                // Getting astronaut instance of targeted player
                Astronaut targetAstronaut = null;
                for (Astronaut a : this.engine.getAllPlayers()) {
                    if (a.toString().equals(name)) {
                        targetAstronaut = a;
                    }
                }

                // Checking if targeted player has Shield
                boolean hasShield = false;

                for (Card card : targetAstronaut.getHand()) {
                    if (card.toString().equals(GameDeck.SHIELD)) {
                        hasShield = true;
                        hasShieldHoleInSuit(name, targetAstronaut);
                        break;
                    }
                }

                if (!hasShield) {
                    noShieldHoleInSuit(name, targetAstronaut);
                }
            }
        });

        // Back button
        Button backB = new Button("Back");
        backB.setFont(Font.font("Verdana", 20));
        backB.setOnAction(ev -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception cd) {
                cd.printStackTrace();
            }
            playActionCards();
        });

        // HBox
        HBox hb = new HBox(13, backB, selectB);
        hb.setAlignment(Pos.CENTER);

        // VBox
        v.getChildren().add(hb);

        // StackPane
        StackPane sp = new StackPane(rBcg, v);

        // Scene
        Scene nSce = new Scene(sp, 430, rHeight);
        nSce.getStylesheets().add(getClass().getResource("CSS/mainGameStyles.css").toExternalForm());

        // Stage
        this.astronautTurnSimulation.setScene(nSce);
        this.astronautTurnSimulation.setTitle("Hole in Suit");
        this.astronautTurnSimulation.setX(612);
        this.astronautTurnSimulation.setY(200);

    }

    /**
     * Method implementing hole in suit when target has no shield
     * 
     * @param targetName name of targeted player
     * @param defender   astronaut instance of targeted player
     */
    private void noShieldHoleInSuit(String targetName, Astronaut defender) {

        // Perform the action
        if (defender.hasCard(GameDeck.OXYGEN_1) > 0) {
            this.engine.getGameDiscard().add(defender.hack(GameDeck.OXYGEN_1));

        } else if (defender.hasCard(GameDeck.OXYGEN_2) > 0) {

            Oxygen dbl = (Oxygen) defender.hack(GameDeck.OXYGEN_2);
            Oxygen[] oxys = this.engine.splitOxygen(dbl);
            this.engine.getGameDiscard().add(oxys[0]);
            defender.addToHand(oxys[1]);
        }

        // Background rectangle
        Rectangle rBcg = new Rectangle(0, 0, 400, 180);
        rBcg.setFill(Color.rgb(54, 69, 79));

        // Player name label
        Label playerNameLabel = new Label(this.engine.getCurrentPlayer().toString());
        playerNameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        playerNameLabel.setTextFill(Color.CYAN);

        // Card Label and info
        Label cardInfo = new Label("1 oxygen discarded from " + targetName + "'s cards!");
        cardInfo.setWrapText(true);
        cardInfo.setAlignment(Pos.CENTER);
        cardInfo.setMaxWidth(380);
        cardInfo.setTextFill(Color.RED);
        cardInfo.setFont(Font.font("Verdana", FontWeight.BOLD, 20));

        // Next Button
        Button nextB = new Button("Next");
        nextB.setFont(Font.font("Verdana", 20));
        nextB.setOnAction(e -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception ds) {
                ds.printStackTrace();
            }
            if (!defender.isAlive()) {
                setDeadAstronautGraphic(targetName);
            }
            interimPhase();
        });

        // VBox
        VBox v = new VBox(15, playerNameLabel, cardInfo, nextB);
        v.setAlignment(Pos.CENTER);

        // StackPane
        StackPane sp = new StackPane(rBcg, v);

        // Scene
        Scene nSce = new Scene(sp, 400, 180);
        nSce.getStylesheets().add(getClass().getResource("CSS/mainGameStyles.css").toExternalForm());

        // Stage
        this.astronautTurnSimulation.setScene(nSce);
        this.astronautTurnSimulation.setTitle("Hole in Suit");
        this.astronautTurnSimulation.setX(612);
        this.astronautTurnSimulation.setY(200);

    }

    /**
     * Method implementing hole in suit when target has shield
     * 
     * @param targetName name of targeted player
     * @param defender   astronaut instance of targeted player
     */
    private void hasShieldHoleInSuit(String targetName, Astronaut defender) {

        // Background rectangle
        Rectangle rBcg = new Rectangle(0, 0, 450, 220);
        rBcg.setFill(Color.rgb(54, 69, 79));

        // Target Name Label
        Label targetL = new Label(targetName);
        targetL.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        targetL.setTextFill(Color.CYAN);

        // Informational label
        Label infoL = new Label(
                this.engine.getCurrentPlayer().toString() + " played a Hole in Suit against you. Use a Shield?");
        infoL.setFont(Font.font("Verdana", FontWeight.BOLD, 17));
        infoL.setMaxWidth(430);
        infoL.setAlignment(Pos.CENTER);
        infoL.setTextFill(Color.RED);
        infoL.setWrapText(true);

        // Shield cards label
        Label shieldNumberL = new Label("You currently have " + defender.hasCard(GameDeck.SHIELD) + " Shield card(s).");
        shieldNumberL.setFont(Font.font("Verdana", 17));
        shieldNumberL.setTextFill(Color.YELLOW);
        shieldNumberL.setWrapText(true);

        // No button
        Button noB = new Button("No");
        noB.setFont(Font.font("Verdana", 20));
        noB.setOnAction(e -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception df) {
                df.printStackTrace();
            }
            noShieldHoleInSuit(targetName, defender);
        });

        // Yes button
        Button yesB = new Button("Yes");
        yesB.setFont(Font.font("Verdana", 20));
        if (defender.getTrack().size() > 0) {
            if (defender.hasMeltedEyeballs()) {
                yesB.setDisable(true);
            }
        }
        yesB.setOnAction(e -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception df) {
                df.printStackTrace();
            }
            this.engine.getGameDiscard().add(defender.hack(GameDeck.SHIELD));
            commonShieldPlayedGUI(targetName, "Hole in Suit");
        });

        // Hbox
        HBox hb = new HBox(15, noB, yesB);
        hb.setAlignment(Pos.CENTER);

        // Vbox
        VBox v = new VBox(10, targetL, infoL, shieldNumberL, hb);
        v.setAlignment(Pos.CENTER);

        // StackPane
        StackPane sp = new StackPane(rBcg, v);

        // Scene
        Scene sce = new Scene(sp, 450, 220);
        sce.getStylesheets().add(getClass().getResource("CSS/mainGameStyles.css").toExternalForm());

        // Stage
        this.astronautTurnSimulation.setScene(sce);
        this.astronautTurnSimulation.setTitle("Play Shield?");
        this.astronautTurnSimulation.setX(677);
        this.astronautTurnSimulation.setY(240);

    }

    //////////////////// TETHER ////////////////////////
    /**
     * Method implementing functionality of tether
     */
    private void tether() {

        int rHeight = 245;

        // Background rectangle
        Rectangle rBcg = new Rectangle(0, 0, 450, rHeight);
        rBcg.setFill(Color.rgb(54, 69, 79));

        // Player name label
        Label playerNameLabel = new Label(this.engine.getCurrentPlayer().toString());
        playerNameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        playerNameLabel.setTextFill(Color.CYAN);

        // Card Label and info
        Label cardInfo = new Label(
                "Tether: Move forward 1 space and knock another player back 1 space. Cannot be played if the opponent is at the starting space.");
        cardInfo.setMaxWidth(435);
        cardInfo.setWrapText(true);
        cardInfo.setAlignment(Pos.CENTER);
        cardInfo.setTextFill(Color.YELLOW);
        cardInfo.setFont(Font.font("Verdana", 17));

        // Label for info
        Label infoL = new Label("Select a player");
        infoL.setTextFill(Color.RED);
        infoL.setFont(Font.font("Verdana", FontWeight.BOLD, 20));

        // Checkbox vbox
        VBox chbVbox = new VBox(10);
        chbVbox.setAlignment(Pos.CENTER);

        // Player Names checkbox
        ArrayList<CheckBox> chBox = new ArrayList<>();
        for (Astronaut a : this.engine.getAllPlayers()) {
            if (a.toString().equals(this.engine.getCurrentPlayer().toString())) {
                continue;
            } else if (!a.isAlive()) {
                continue;
            } else {
                CheckBox cb = new CheckBox(a.toString());
                rHeight += 30;
                cb.setFont(Font.font("Verdana", 17));
                cb.setTextFill(Color.YELLOW);
                chBox.add(cb);
                chbVbox.getChildren().add(cb);
            }
        }
        rBcg.setHeight(rHeight);

        // Select button
        Button selectB = new Button("Select");
        selectB.setFont(Font.font("Verdana", 20));
        selectB.setOnAction(e -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception vq) {
                vq.printStackTrace();
            }

            // Checking if only 1 player selected
            int c = 0;
            for (CheckBox cb : chBox) {
                if (cb.isSelected()) {
                    c++;
                }
            }

            if (c != 1) {
                for (CheckBox cx : chBox) {
                    cx.setSelected(false);
                }
                AlertBox ax = new AlertBox("Invalid player selection.");
                ax.showAlertBox();
            }

            else {

                // Getting name of targeted astronaut
                String name = null;
                for (CheckBox cb : chBox) {
                    if (cb.isSelected()) {
                        name = cb.getText();
                    }
                }

                // Getting astronaut instance of targeted player
                Astronaut targetAstronaut = null;
                for (Astronaut a : this.engine.getAllPlayers()) {
                    if (a.toString().equals(name)) {
                        targetAstronaut = a;
                    }
                }

                if (targetAstronaut.getTrack().size() == 0) {
                    for (CheckBox ox : chBox) {
                        ox.setSelected(false);
                    }
                    AlertBox aox = new AlertBox("Player is at starting space!");
                    aox.showAlertBox();
                } else {

                    this.actionsPlayed++; // implementing 2 player logic

                    // Tether discarded
                    Card actionCard = this.engine.getCurrentPlayer().hack(GameDeck.TETHER);
                    this.engine.getGameDiscard().add(actionCard);

                    // Checking if targeted player has Shield
                    boolean hasShield = false;

                    for (Card card : targetAstronaut.getHand()) {
                        if (card.toString().equals(GameDeck.SHIELD)) {
                            hasShield = true;
                            hasShieldTether(name, targetAstronaut);
                            break;
                        }
                    }

                    if (!hasShield) {
                        noShieldTether(name, targetAstronaut);
                    }
                }
            }
        });

        // Back button
        Button backB = new Button("Back");
        backB.setFont(Font.font("Verdana", 20));
        backB.setOnAction(ev -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception cd) {
                cd.printStackTrace();
            }
            playActionCards();
        });

        // HBox
        HBox hb = new HBox(13, backB, selectB);
        hb.setAlignment(Pos.CENTER);

        // VBox
        VBox v = new VBox(15, playerNameLabel, cardInfo, infoL, chbVbox, hb);
        v.setAlignment(Pos.CENTER);

        // StackPane
        StackPane sp = new StackPane(rBcg, v);

        // Scene
        Scene nSce = new Scene(sp, 450, rHeight);
        nSce.getStylesheets().add(getClass().getResource("CSS/mainGameStyles.css").toExternalForm());

        // Stage
        this.astronautTurnSimulation.setScene(nSce);
        this.astronautTurnSimulation.setTitle("Tether");
        this.astronautTurnSimulation.setX(612);
        this.astronautTurnSimulation.setY(200);

    }

    /**
     * Method for when targeted player has no shield against Tether
     * 
     * @param targetName name of targeted player
     * @param defender   astronaut instance of targeted player
     */
    private void noShieldTether(String targetName, Astronaut defender) {

        // Internally remove topmost space card
        this.engine.getSpaceDiscard().add(defender.laserBlast());

        // Push defender back
        int index = GameApp.playerNames.indexOf(targetName);
        switch (index) {
            case 0: // push purple back
                double yCoordPurple = this.purpleAstronautTrackB
                        .get(this.purpleAstronautTrackB.size() - 1)
                        .getLayoutY() - 20;
                this.spaceCardsPane.getChildren()
                        .remove(this.purpleAstronautTrackB.get(this.purpleAstronautTrackB.size() - 1));
                this.purpleAstronautTrackB.remove(this.purpleAstronautTrackB.size() - 1);
                this.purpleAstronautB.setLayoutY(yCoordPurple);
                break;

            case 1: // push green back
                double yCoordGreen = this.greenAstronautTrackB.get(this.greenAstronautTrackB.size() - 1)
                        .getLayoutY() - 20;
                this.spaceCardsPane.getChildren()
                        .remove(this.greenAstronautTrackB.get(this.greenAstronautTrackB.size() - 1));
                this.greenAstronautTrackB.remove(this.greenAstronautTrackB.size() - 1);
                this.greenAstronautB.setLayoutY(yCoordGreen);
                break;

            case 2: // push yellow back
                double yCoordYellow = this.yellowAstronautTrackB
                        .get(this.yellowAstronautTrackB.size() - 1)
                        .getLayoutY() - 20;
                this.spaceCardsPane.getChildren()
                        .remove(this.yellowAstronautTrackB.get(this.yellowAstronautTrackB.size() - 1));
                this.yellowAstronautTrackB.remove(this.yellowAstronautTrackB.size() - 1);
                this.yellowAstronautB.setLayoutY(yCoordYellow);
                break;

            case 3: // push red back
                double yCoordRed = this.redAstronautTrackB.get(this.redAstronautTrackB.size() - 1)
                        .getLayoutY() - 20;
                this.spaceCardsPane.getChildren()
                        .remove(this.redAstronautTrackB.get(this.redAstronautTrackB.size() - 1));
                this.redAstronautTrackB.remove(this.redAstronautTrackB.size() - 1);
                this.redAstronautB.setLayoutY(yCoordRed);
                break;
            case 4: // push blue back
                double yCoordBlue = this.blueAstronautTrackB.get(this.blueAstronautTrackB.size() - 1)
                        .getLayoutY() - 20;
                this.spaceCardsPane.getChildren()
                        .remove(this.blueAstronautTrackB.get(this.blueAstronautTrackB.size() - 1));
                this.blueAstronautTrackB.remove(this.blueAstronautTrackB.size() - 1);
                this.blueAstronautB.setLayoutY(yCoordBlue);
                break;

        }

        // Background rectangle
        Rectangle rBcg = new Rectangle(0, 0, 400, 180);
        rBcg.setFill(Color.rgb(54, 69, 79));

        // Player name label
        Label playerNameLabel = new Label(this.engine.getCurrentPlayer().toString());
        playerNameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        playerNameLabel.setTextFill(Color.CYAN);

        // Card Label and info
        Label cardInfo = new Label(targetName + " moved 1 space back!");
        cardInfo.setWrapText(true);
        cardInfo.setAlignment(Pos.CENTER);
        cardInfo.setMaxWidth(380);
        cardInfo.setTextFill(Color.RED);
        cardInfo.setFont(Font.font("Verdana", FontWeight.BOLD, 20));

        // Next Button
        Button nextB = new Button("Next");
        nextB.setFont(Font.font("Verdana", 20));
        nextB.setOnAction(e -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception ds) {
                ds.printStackTrace();
            }

            // Draw new space card
            Card spaceCard = this.engine.getSpaceDeck().draw();

            // Add new space card to track or discard
            if (spaceCard.toString().equals(SpaceDeck.GRAVITATIONAL_ANOMALY)) {
                this.engine.getSpaceDiscard().add(spaceCard);
            } else {
                this.engine.getCurrentPlayer().addToTrack(spaceCard);
            }

            int idx = GameApp.playerNames.indexOf(this.engine.getCurrentPlayer().toString());

            // Switch statement to call move astronaut method
            switch (idx) {
                case 0:
                    moveAstronautB(this.purpleAstronautB, spaceCard, this.purpleAstronautTrackB, 2, 0);
                    break;
                case 1:
                    moveAstronautB(this.greenAstronautB, spaceCard, this.greenAstronautTrackB, 2, 1);
                    break;
                case 2:
                    moveAstronautB(this.yellowAstronautB, spaceCard, this.yellowAstronautTrackB, 2, 2);
                    break;
                case 3:
                    moveAstronautB(this.redAstronautB, spaceCard, this.redAstronautTrackB, 2, 3);
                    break;
                case 4:
                    moveAstronautB(this.blueAstronautB, spaceCard, this.blueAstronautTrackB, 2, 4);
                    break;
            }

        });

        // VBox
        VBox v = new VBox(15, playerNameLabel, cardInfo, nextB);
        v.setAlignment(Pos.CENTER);

        // StackPane
        StackPane sp = new StackPane(rBcg, v);

        // Scene
        Scene nSce = new Scene(sp, 400, 180);
        nSce.getStylesheets().add(getClass().getResource("CSS/mainGameStyles.css").toExternalForm());

        // Stage
        this.astronautTurnSimulation.setScene(nSce);
        this.astronautTurnSimulation.setTitle("Tether");
        this.astronautTurnSimulation.setX(612);
        this.astronautTurnSimulation.setY(200);

    }

    /**
     * Method for when targeted player has shield against tether
     * 
     * @param targetName name of targeted player
     * @param defender   astronaut instance of defender
     */
    private void hasShieldTether(String targetName, Astronaut defender) {

        // Background rectangle
        Rectangle rBcg = new Rectangle(0, 0, 450, 220);
        rBcg.setFill(Color.rgb(54, 69, 79));

        // Target Name Label
        Label targetL = new Label(targetName);
        targetL.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        targetL.setTextFill(Color.CYAN);

        // Informational label
        Label infoL = new Label(
                this.engine.getCurrentPlayer().toString() + " played a Tether against you. Use a Shield?");
        infoL.setFont(Font.font("Verdana", FontWeight.BOLD, 17));
        infoL.setMaxWidth(430);
        infoL.setAlignment(Pos.CENTER);
        infoL.setTextFill(Color.RED);
        infoL.setWrapText(true);

        // Shield cards label
        Label shieldNumberL = new Label("You currently have " + defender.hasCard(GameDeck.SHIELD) + " Shield card(s).");
        shieldNumberL.setFont(Font.font("Verdana", 17));
        shieldNumberL.setTextFill(Color.YELLOW);
        shieldNumberL.setWrapText(true);

        // No button
        Button noB = new Button("No");
        noB.setFont(Font.font("Verdana", 20));
        noB.setOnAction(e -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception df) {
                df.printStackTrace();
            }
            noShieldTether(targetName, defender);
        });

        // Yes button
        Button yesB = new Button("Yes");
        yesB.setFont(Font.font("Verdana", 20));
        if (defender.getTrack().size() > 0) {
            if (defender.hasMeltedEyeballs()) {
                yesB.setDisable(true);
            }
        }
        yesB.setOnAction(e -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception df) {
                df.printStackTrace();
            }
            this.engine.getGameDiscard().add(defender.hack(GameDeck.SHIELD));
            commonShieldPlayedGUI(targetName, "Tether");
        });

        // Hbox
        HBox hb = new HBox(15, noB, yesB);
        hb.setAlignment(Pos.CENTER);

        // Vbox
        VBox v = new VBox(10, targetL, infoL, shieldNumberL, hb);
        v.setAlignment(Pos.CENTER);

        // StackPane
        StackPane sp = new StackPane(rBcg, v);

        // Scene
        Scene sce = new Scene(sp, 450, 220);
        sce.getStylesheets().add(getClass().getResource("CSS/mainGameStyles.css").toExternalForm());

        // Stage
        this.astronautTurnSimulation.setScene(sce);
        this.astronautTurnSimulation.setTitle("Play Shield?");
        this.astronautTurnSimulation.setX(677);
        this.astronautTurnSimulation.setY(240);

    }

    //////////////////// COMMON SHIELD GUI ////////////////////////
    /**
     * GUI for when shield is played
     * 
     * @param targetName          astronaut targeted by current astronaut
     * @param cardToDefendAgainst non shield action card
     */
    private void commonShieldPlayedGUI(String targetName, String cardToDefendAgainst) {

        // Background rectangle
        Rectangle rBcg = new Rectangle(0, 0, 450, 200);
        rBcg.setFill(Color.rgb(54, 69, 79));

        // Heading target name
        Label headingL = new Label(targetName);
        headingL.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        headingL.setTextFill(Color.CYAN);

        // Info label
        Label infoL = new Label("You successfully defended yourself against " + cardToDefendAgainst + "!");
        infoL.setFont(Font.font("Verdana", FontWeight.BOLD, 17));
        infoL.setMaxWidth(430);
        infoL.setTextFill(Color.YELLOW);
        infoL.setWrapText(true);
        infoL.setAlignment(Pos.CENTER);

        // Next button
        Button nextB = new Button("Next");
        nextB.setFont(Font.font("Verdana", 20));
        nextB.setOnAction(e -> {
            try {
                GameApp.playButtonSound();
            } catch (Exception v) {
                v.printStackTrace();
            }
            interimPhase();
        });

        // VBox
        VBox v = new VBox(15, headingL, infoL, nextB);
        v.setAlignment(Pos.CENTER);

        // StackPane
        StackPane sp = new StackPane(rBcg, v);

        // Scene
        Scene sce = new Scene(sp, 450, 200);
        sce.getStylesheets().add(getClass().getResource("CSS/mainGameStyles.css").toExternalForm());

        // Stage
        this.astronautTurnSimulation.setScene(sce);
        this.astronautTurnSimulation.setTitle("Shield");
        this.astronautTurnSimulation.setX(677);
        this.astronautTurnSimulation.setY(240);

    }

}
