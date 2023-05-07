import selfish.Astronaut;
import selfish.GameEngine;
import selfish.GameException;

import java.io.Console;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class GameDriver {

    /**
     * A helper function to centre text in a longer String.
     * 
     * @param width The length of the return String.
     * @param s     The text to centre.
     * @return A longer string with the specified text centred.
     */

    public static String centreString(int width, String s) {
        return String.format("%-" + width + "s", String.format("%" + (s.length() + (width - s.length()) / 2) + "s", s));
    }

    public GameDriver() {
    }

    public static void main(String[] args) throws GameException {
        GameEngine gameEngine;
        Random r = new Random();
        long sed = r.nextLong();

        try {
            gameEngine = new GameEngine(sed,
                    "/Users/dhruv/Desktop/GitRepos/comp16412-coursework-2__c79120dp/io/ActionCards.txt",
                    "/Users/dhruv/Desktop/GitRepos/comp16412-coursework-2__c79120dp/io/SpaceCards.txt");
        } catch (Exception e) {
            throw new GameException("File not found", new FileNotFoundException());
        }

        try { // Reading art.txt file
            FileReader reader = new FileReader(
                    "/Users/dhruv/Desktop/GitRepos/comp16412-coursework-2__c79120dp/io/art.txt");
            int num = reader.read();
            while (num != -1) {
                System.out.print((char) num);
                num = reader.read();
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException f) {
            f.printStackTrace();
        } // end of file read

        System.out.println("\n");

        Console console = System.console(); // For user input
        ArrayList<String> playerNames = new ArrayList<String>(); // To store player names
        // ArrayList<Astronaut> astronauts = new ArrayList<Astronaut>(); // To call
        // constructor and store the astronaut
        // // object
        String playerName;

        for (int i = 1; i <= 5; i++) { // Loop to store player names in arraylist playerNames

            if (i < 3) {
                System.out.print("Player " + i + " name? ");
                playerName = console.readLine();

                while (playerName.equals("")) {
                    System.out.println("Name cannot be blank");
                    System.out.print("Player " + i + " name? ");
                    playerName = console.readLine();
                }
                playerNames.add(playerName);
                gameEngine.addPlayer(playerName);
                Astronaut temp1 = new Astronaut(playerName, gameEngine);
                // astronauts.add(temp1);

            } else {
                System.out.print("Add another? [Y]es/[N]o: ");
                String addAnother = console.readLine();

                while (!(addAnother.equals("Y") || addAnother.equals("y") || addAnother.equals("N")
                        || addAnother.equals("n"))) {
                    System.out.println("Invalid input");
                    System.out.print("Add another? [Y]es/[N]o: ");
                    addAnother = console.readLine();
                }

                if (addAnother.equals("Y") || addAnother.equals("y")) {
                    System.out.print("Player " + i + " name? ");
                    playerName = console.readLine();

                    while (playerName.equals("")) {
                        System.out.println("Name cannot be blank");
                        System.out.print("Player " + i + " name? ");
                        playerName = console.readLine();
                    }
                    playerNames.add(playerName);
                    gameEngine.addPlayer(playerName);
                    // Astronaut temp2 = new Astronaut(playerName, gameEngine);
                    // astronauts.add(temp2);

                } else {
                    break;
                }
            }
        } // player names in arraylist stored

        System.out.print("\n"); // adding whitespace to terminal output

        System.out.print("After a dazzling (but doomed) space mission, ");

        switch (playerNames.size()) {
            case 2:
                System.out.print(playerNames.get(0) + " and " + playerNames.get(1));
                break;
            case 3:
                System.out.print(
                        playerNames.get(0) + ", " + playerNames.get(1) + " and " + playerNames.get(2));
                break;
            case 4:
                System.out.print(
                        playerNames.get(0) + ", " + playerNames.get(1) + ", " + playerNames.get(2) + " and "
                                + playerNames.get(3));
                break;
            case 5:
                System.out.print(
                        playerNames.get(0) + ", " + playerNames.get(1) + ", " + playerNames.get(2) + ", "
                                + playerNames.get(3) + " and " + playerNames.get(4));
                break;
        }

        System.out.print(
                " are floating in space and their Oxygen supplies are running low. Only the first back to the ship will survive!\n");
        System.out.println();

    }

}