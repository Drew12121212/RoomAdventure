import java.util.Scanner;
import java.util.HashMap;

public class RoomAdventure {
    private static Room currentRoom;
    private static String[] inventory = {null,null,null,null,null};
    private static String status;
    private static boolean puzzleSolved = false;


    final private static String DEFAULT_STATUS = 
        "Sorry, I do not understand. Try [verb] [noun]. Valid verbs include 'go', 'look', and 'take'.";
    
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
        status = "I don't see that item";
        if (items.containsKey(noun)) {
            status = items.get(noun);
        }
    }


    //feature added by aayusha
    private static void handleTalk(String noun) {
    status = "There's no one to talk to.";

    if (noun.equals("person") && currentRoom.getItemsHashMap().containsKey("person")) {
        switch (currentRoom.toString()) {
            case "Room 4":
                status = "The person stares blankly and whispers: 'You must solve the puzzle to escape...'";
                break;
            case "Room 3":
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


    private static void setupGame() {
        Room room1 = new Room("Room 1");
        Room room2 = new Room("Room 2");
        Room room3 = new Room("Room 3");
        Room room4 = new Room("Room 4");




        String[] room1Grabbables = {"key"};
        room1.addExit("east", room2);
        room1.addExit("north", room3);
        room1.addItem("chair", "It is a chair");
        room1.addItem("desk", "there is an old wooden desk with a key on top");
        room1.setGrabbables(room1Grabbables);

        String[] room2Grabbables = {"coal"};
        room2.addExit("west", room1);
        room2.addExit("north", room4);
        room2.addItem("fireplace", "it is on fire");
        room2.addItem("rug", "There is a lump of coal on the rug");
        room2.setGrabbables(room2Grabbables);


        String[] room3Grabbables = {"ke"};
        room3.addExit("south", room1);
        room3.addExit("east", room4);
        room3.addItem("wall", "it is a wall");
        room3.addItem("floor", "floor description");
        room3.setGrabbables(room3Grabbables);


        String[] room4Grabbables = {"ke"};
        room4.addExit("south", room2);
        room4.addExit("west", room3);
        room4.addItem("car", "there is a broken car in the room");
        room4.addItem("person", "there is someone staring at you in the room");
        room4.setGrabbables(room4Grabbables);

        //adding riddle room by aayusha
        Room riddleRoom = new Room("Riddle Room");
        riddleRoom.addItem("tablet", "There is an ancient stone tablet with an inscription: 'I speak without a mouth and hear without ears. What am I?'");
        riddleRoom.setGrabbables(new String[]{});
        //connecting riddle room to room 3
        room3.addExit("north", riddleRoom);         
        riddleRoom.addExit("south", room3);
        currentRoom = room1;
    }

    @SuppressWarnings("java:S2189")
    public static void main(String[] args) {
        setupGame();

        while (true) {
            System.out.print(currentRoom.toString());
            System.out.print("Inventory: ");

            for (int i = 0; i < inventory.length; i++) {
                System.out.print(inventory[i] + " ");
            }
            
            System.out.println("\nWhat would you like to do?");
            Scanner s = new Scanner(System.in);
            String input = s.nextLine();
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
    
    public Room(String name) {
        this.name = name;
    }

    public String getName() {
    return name;
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