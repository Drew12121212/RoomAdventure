
import java.util.Scanner;
import java.util.HashMap;

public class RoomAdventure {
    private static Room currentRoom;
    private static String[] inventory = {null, null, null, null, null};
    private static String status;
    private static boolean puzzleSolved = false;
    private static boolean running = true;

    final private static String DEFAULT_STATUS =
        "Sorry, I do not understand. Try [verb] [noun]. Valid verbs include 'go', 'look', 'eat', 'answer', 'talk', 'take', 'use', and 'combine'.";

    private static void handleGo(String noun) {
        HashMap<String, Room> exitHashMap = currentRoom.getExits();
        status = "I don't see that room";

        if (exitHashMap.containsKey(noun)) {
            Room nextRoom = exitHashMap.get(noun);
            if (currentRoom.getName().equals("Riddle Room") && !puzzleSolved) {
                status = "You are stuck here until you answer the riddle.";
            } else {
                currentRoom = nextRoom;
                status = "Changed Room";
            }
        }
    }

    private static void handleLook(String noun) {
        HashMap<String, String> items = currentRoom.itemsHashMap;
        Edible[] edibles = currentRoom.getEdibles();
        status = "I don't see that item";
        if (items.containsKey(noun)) {
            status = items.get(noun);
        } else {
            for (Edible item : edibles) {
                if (noun.equals(item.edibleName)) {
                    status = item.edibleDescription;
                }
            }
        }
    }

    private static void handleTalk(String noun) {
        status = "There's no one to talk to.";
        if (noun.equals("person") && currentRoom.getItemsHashMap().containsKey("person")) {
            switch (currentRoom.getName()) {
                case "Labratory":
                    status = "The person whispers: 'You must solve the puzzle to escape...'";
                    break;
                case "Armory":
                    status = "The person smiles: 'You found me... I've been waiting.'";
                    break;
                default:
                    status = "The person blinks silently. No response.";
            }
        }
    }

    private static void handleTake(String noun) {
        String[] grabbables = currentRoom.getGrabbables();
        status = "I can't grab that";
        for (String item : grabbables) {
            if (noun.equals(item)) {
                for (int i = 0; i < inventory.length; i++) {
                    if (inventory[i] == null) {
                        inventory[i] = noun;
                        status = "Added it to the inventory";
                        return;
                    }
                }
            }
        }
    }

    private static void handleEat(String noun) {
        Edible[] edibles = currentRoom.getEdibles();
        handleTake(noun);
        if (status.equals("I can't grab that")) {
            for (Edible item : edibles) {
                if (noun.equals(item.edibleName)) {
                    status = item.edibleMessage;
                    Globals.health += item.healthChange;
                    currentRoom.addEdibles(new Edible[0]);
                }
            }
        } else {
            status = "I can't eat that but I added it to the inventory";
        }
    }

    private static void handleAnswer(String noun) {
        if (currentRoom.getName().equals("Riddle Room")) {
            if (noun.equalsIgnoreCase("echo")) {
                puzzleSolved = true;
                status = "Correct! The tablet glows and a path opens.";
            } else {
                status = "Incorrect. The tablet remains silent.";
            }
        } else {
            status = "There’s no riddle to answer here.";
        }
    }

    private static void handleUse(String noun) {
        status = "You can't use that.";
        for (String item : inventory) {
            if (item != null && noun.equals(item)) {
                if (noun.equals("key") && currentRoom.getName().equals("Labratory")) {
                    currentRoom.addItem("note", "A glowing note that reads: 'The answer is echo.'");
                    status = "You used the key to unlock a drawer. A glowing note appears!";
                } else if (noun.equals("coal") && currentRoom.getName().equals("Riddle Room")) {
                    status = "You used the coal on the tablet: '...without a mouth...' appears.";
                } else {
                    status = "You used the " + noun + ", but nothing happened.";
                }
                return;
            }
        }
    }

    private static void handleCombine(String noun1, String noun2) {
        int firstIndex = -1, secondIndex = -1;
        for (int i = 0; i < inventory.length; i++) {
            if (noun1.equals(inventory[i])) firstIndex = i;
            if (noun2.equals(inventory[i])) secondIndex = i;
        }
        if (firstIndex != -1 && secondIndex != -1) {
            if ((noun1.equals("key") && noun2.equals("coal")) || (noun1.equals("coal") && noun2.equals("key"))) {
                inventory[firstIndex] = "firekey";
                inventory[secondIndex] = null;
                status = "You combined key and coal into a firekey!";
            } else {
                status = "Those items can't be combined.";
            }
        } else {
            status = "You don't have both items.";
        }
    }

    private static void setupGame() {
        Room livingRoom = new Room("Living Room");
        Room foyer = new Room("Foyer");
        Room armory = new Room("Armory");
        Room labratory = new Room("Labratory");
        Room riddleRoom = new Room("Riddle Room");

        livingRoom.addItem("desk", "Desk with a shiny key");
        livingRoom.setGrabbables(new String[]{"key"});
        livingRoom.addExit("east", foyer);

        foyer.addItem("rug", "Rug hiding a lump of coal");
        foyer.setGrabbables(new String[]{"coal"});
        foyer.addExit("west", livingRoom);
        foyer.addExit("north", labratory);

        labratory.addItem("cabinet", "Cabinet with a microscope");
        labratory.setGrabbables(new String[]{"microscope"});
        labratory.addExit("south", foyer);
        labratory.addExit("west", armory);

        armory.addItem("stand", "Sword on a stand");
        armory.setGrabbables(new String[]{"sword"});
        armory.addExit("east", labratory);
        armory.addExit("north", riddleRoom);
        armory.addExit("south", livingRoom);

        riddleRoom.addItem("tablet", "Tablet with a riddle");
        riddleRoom.addExit("south", armory);

        currentRoom = livingRoom;
    }

    public static void main(String[] args) {
        setupGame();
        Scanner s = new Scanner(System.in);

        while (running) {
            System.out.print(currentRoom.toString());
            System.out.print("Inventory: ");
            for (String item : inventory) System.out.print(item + " ");
            System.out.println("\nWhat would you like to do?");
            String input = s.nextLine();
            if (input.equals("quit")) break;

            String[] words = input.split(" ");
            if (!(words.length == 2 || (words.length == 3 && words[0].equals("combine")))) {
                status = DEFAULT_STATUS;
                System.out.println(status);
                continue;
            }
            String verb = words[0];

            switch (verb) {
                case "go": handleGo(words[1]); break;
                case "look": handleLook(words[1]); break;
                case "take": handleTake(words[1]); break;
                case "talk": handleTalk(words[1]); break;
                case "answer": handleAnswer(words[1]); break;
                case "eat": handleEat(words[1]); break;
                case "use": handleUse(words[1]); break;
                case "combine": handleCombine(words[1], words[2]); break;
                default: status = DEFAULT_STATUS;
            }
            System.out.println(status);
        }
        s.close();
    }
}

class Room {
    private String name;
    private String[] grabbables;
    HashMap<String, Room> exitHashMap = new HashMap<>();
    HashMap<String, String> itemsHashMap = new HashMap<>();
    private Edible[] edibles = new Edible[0];

    public Room(String name) { this.name = name; }
    public String getName() { return name; }
    public void addItem(String item, String desc) { itemsHashMap.put(item, desc); }
    public void addExit(String dir, Room room) { exitHashMap.put(dir, room); }
    public HashMap<String, Room> getExits() { return exitHashMap; }
    public HashMap<String, String> getItemsHashMap() { return itemsHashMap; }
    public void setGrabbables(String[] g) { this.grabbables = g; }
    public String[] getGrabbables() { return grabbables; }
    public void addEdibles(Edible[] edibles) { this.edibles = edibles; }
    public Edible[] getEdibles() { return edibles; }

    public String toString() {
        String res = "\nLocation: " + name + "\nYou see: ";
        for (String item : itemsHashMap.keySet()) res += item + " ";
        res += "\nExits: ";
        for (String exit : exitHashMap.keySet()) res += exit + " ";
        return res + "\n";
    }
}

class Edible {
    public String edibleName, edibleDescription, edibleMessage;
    public int healthChange;
    public Edible(String n, String d, String m, int h) {
        this.edibleName = n; this.edibleDescription = d;
        this.edibleMessage = m; this.healthChange = h;
    }
}

class Globals {
    public static int health = 100;
    public static final String RED = "\u001B[31m";
    public static final String RESET = "\u001B[0m";
}
