import java.util.Scanner;
import java.util.HashMap;

public class RoomAdventure {
    private static Room currentRoom;
    private static String[] inventory = {null,null,null,null,null};
    private static String status;
    private static boolean puzzleSolved = false;
    private static boolean running = true;

    final private static String DEFAULT_STATUS = 
        "Sorry, I do not understand. Try [verb] [noun]. Valid verbs include 'go', 'look', 'eat', 'answer', 'talk' and 'take'.";
    
    private static void handleGo(String noun) {
        HashMap<String, Room> exitHashMap = currentRoom.getExits();
        status = "I don't see that room";

        if (exitHashMap.containsKey(noun)) {
            Room nextRoom = exitHashMap.get(noun);

            //if in Riddle Room and puzzle not solved, block leaving
            if (currentRoom.getName().equals("Riddle Room") && !puzzleSolved) {
                status = "You are stuck here until you answer the riddle.";
            } else {
                currentRoom = nextRoom;
                status = "Changed Room";
            }
        }
    }


    private static void handleLook(String noun) {
        HashMap<String,String> items = currentRoom.itemsHashMap;
        Edible[] edibles = currentRoom.getEdibles();
        status = "I don't see that item";
        if (items.containsKey(noun)) {
            status = items.get(noun);
        }
        else {
            for (Edible item: edibles) {
                if (noun.equals(item.edibleName)) {
                    status = item.edibleDescription;
                }
            }
        }
    }


    //feature added by aayusha
    private static void handleTalk(String noun) {
        status = "There's no one to talk to.";

        if (noun.equals("person") && currentRoom.getItemsHashMap().containsKey("person")) {
            switch (currentRoom.getName()) {
                case "Labratory":
                    status = "The person stares blankly and whispers: 'You must solve the puzzle to escape...'";
                    break;
                case "Armory":
                    status = "The person smiles softly: 'You found me... I’ve been waiting.'";
                    break;
                default:
                    status = "The person blinks silently. No response.";
            }
        } else if (noun.equals("person")) {
            status = "Theres no person here to talk to.";
        }
    }


    private static void handleTake(String noun) {
        String[] grabbables = currentRoom.getGrabbables();
        status = "I can't grab that";
        for (String item : grabbables) {
            if (noun.equals(item)) {
                for (int i = 0; i <inventory.length; i ++) {
                    if (inventory[i] == null) {
                        inventory[i] = noun;
                        status = "Added it to the inventory";
                        break;
                    }
                }
            }
        }
    }

    private static void handleEat(String noun) {
        Edible[] edibles = currentRoom.getEdibles();
        handleTake(noun);
        if (status.equals("I can't grab that")) {
            for (Edible item: edibles) {
                if (noun.equals(item.edibleName)) {
                    status = item.edibleMessage;
                    Globals.health += item.healthChange;
                    Edible[] emptyEdible = new Edible[0];
                    currentRoom.addEdibles(emptyEdible);
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


    private static String redText(String text) {
        return Globals.RED + text + Globals.RESET;
    }

    private static void setupGame() {
        Room livingRoom = new Room("Living Room");
        Room foyer = new Room("Foyer");
        Room armory = new Room("Armory");
        Room labratory = new Room("Labratory");
        Room riddleRoom = new Room("Riddle Room");

        String[] livingRoomGrabbables = {"key"};
        Edible chickenLeg = new Edible("chicken", "a good looking grilled chicken leg", "you ate the chicken leg and feel restored", 20);
        Edible[] edibles1 = {chickenLeg};
        livingRoom.addEdibles(edibles1);
        livingRoom.addExit("east", foyer);
        livingRoom.addExit("north", armory);
        livingRoom.addItem("chair", "It is a chair with a plate of " + redText("chicken") + " on it");
        livingRoom.addItem("desk", "there is an old wooden desk with a " + redText("key") + " on top");
        livingRoom.setGrabbables(livingRoomGrabbables);

        String[] foyerGrabbables = {"coal"};
        foyer.addExit("west", livingRoom);
        foyer.addExit("north", labratory);
        foyer.addItem("fireplace", "it is on fire");
        foyer.addItem("rug", "There is a lump of " + redText("coal") + " on the rug");
        foyer.setGrabbables(foyerGrabbables);


        String[] armoryGrabbables = {"sword"};
        Edible beefPatty = new Edible("patty", "a old moldy beef patty", "You at the beef patty and instantly feel sick", -50);
        Edible[] edibles3 = {beefPatty};
        armory.addExit("south", livingRoom);
        armory.addExit("east", labratory);
        armory.addItem("wall", "A solid stone wall with a " + redText("patty") + " stuck on it");
        armory.addItem("stand", "An ornate stand with a foreign " + redText("sword") + " layed upon in");
        armory.setGrabbables(armoryGrabbables);
        armory.addEdibles(edibles3);


        String[] labratoryGrabbables = {"microscope"};
        labratory.addExit("south", foyer);
        labratory.addExit("west", armory);
        labratory.addItem("cabinent", "There is a chemical cabinent with a " + redText("microscope") + " in it");
        labratory.addItem("person", "there is someone staring at you in the room");
        labratory.setGrabbables(labratoryGrabbables);

        //adding riddle room by aayusha
        riddleRoom.addItem("tablet", "There is an ancient stone tablet with an inscription: 'I speak without a mouth and hear without ears. What am I?'");
        riddleRoom.setGrabbables(new String[]{});
        //connecting riddle room to room 3
        armory.addExit("north", riddleRoom);         
        riddleRoom.addExit("south", armory);
        currentRoom = livingRoom;
    }

    @SuppressWarnings("java:S2189")
    public static void main(String[] args) {
        setupGame();

        while (running) {
            System.out.print(currentRoom.toString());
            System.out.print("Inventory: ");

            for (int i = 0; i < inventory.length; i++) {
                System.out.print(inventory[i] + " ");
            }
            
            System.out.println("\nWhat would you like to do?");
            Scanner s = new Scanner(System.in);
            String input = s.nextLine();
            if (input.equals("quit")) {
                running = false;
                s.close();
                continue;
            }
            String[] words = input.split(" ");

            if (words.length != 2) {
                status = DEFAULT_STATUS;
                System.out.println(status);
                continue;
            }

            String verb = words[0];
            String noun = words[1];

            switch (verb) {
                case "go":
                    handleGo(noun);
                    break;
                case "look": 
                    handleLook(noun);
                    break;
                case "take":
                    handleTake(noun);
                    break;
                case "talk":
                    handleTalk(noun);
                    break;
                case "answer":
                    handleAnswer(noun);
                    break;
                case "eat":
                    handleEat(noun);
                    break;
                default:
                    status = DEFAULT_STATUS;
            }

            System.out.println(status);
        }
    }
}

class Room {
    private String name;
    private String[] grabbables;
    HashMap<String, Room> exitHashMap = new HashMap<String, Room>();
    HashMap<String, String> itemsHashMap = new HashMap<String, String>();
    private Edible[] edibles;
    
    public Room(String name) {
        this.name = name;
    }

    public String getName() {
    return name;
    }

    public void addEdibles(Edible[] edibles) {
        this.edibles = edibles;
    }

    public Edible[] getEdibles() {
        return this.edibles;
    }

    public void addExit(String direction, Room roomName) {
        exitHashMap.put(direction, roomName);
    }

    public HashMap<String, Room> getExits() {
        return exitHashMap;
    }
    public void addItem(String item, String itemDescription) {
        itemsHashMap.put(item, itemDescription);
    }

    public HashMap<String, String> getItemsHashMap() {
        return itemsHashMap;
    }

    public void setGrabbables(String[] grabbables) {
        this.grabbables = grabbables;
    }

    public String[] getGrabbables() {
        return grabbables;
    }


    @Override
    public String toString() {
        String result = "\nLocation:" + name;
        result += "\nYou have " + Globals.health + " remaining" ;
        result += "\nYou See ";
        for (String item: itemsHashMap.keySet()) {
            result +=item + " ";
        }
        result += "\nExits ";
        for (String exit: exitHashMap.keySet()) {
            result += exit + " ";
        }
        return result + "\n";
    }
}

class Edible {
        public String edibleName;
        public String edibleDescription;
        public String edibleMessage;
        public int healthChange;
        public Edible(String edibleName, String edibleDescription, String edibleMessage, int healthChange) {
            this.edibleName = edibleName;
            this.edibleDescription = edibleDescription;
            this.edibleMessage = edibleMessage;
            this.healthChange = healthChange;
        }
    }