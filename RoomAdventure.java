import java.util.Scanner;
import java.util.HashMap;

public class RoomAdventure {
    private static Room currentRoom;
    private static String[] inventory = {null,null,null,null,null};
    private static String status;
    private static boolean running = true;

    final private static String DEFAULT_STATUS = 
        "Sorry, I do not understand. Try [verb] [noun]. Valid verbs include 'go', 'look', and 'take'.";
    
    private static void handleGo(String noun) {
        HashMap<String, Room> exitHashMap = currentRoom.getExits();
        status = "I don't see that room";
        if (exitHashMap.containsKey(noun)) {
            currentRoom = exitHashMap.get(noun);
            status = "Changed Room";
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

    private static String redText(String text) {
        return Globals.RED + text + Globals.RESET;
    }
    private static void setupGame() {
        Room room1 = new Room("Room 1");
        Room room2 = new Room("Room 2");
        Room room3 = new Room("Room 3");
        Room room4 = new Room("Room 4");

        String[] room1Grabbables = {"key"};
        Edible chickenLeg = new Edible("chicken", "a good looking grilled chicken leg", "you ate the chicken leg and feel restored", 20);
        Edible[] edibles1 = {chickenLeg};
        room1.addEdibles(edibles1);
        room1.addExit("east", room2);
        room1.addExit("north", room3);
        room1.addItem("chair", "It is a chair with a plate of " + redText("chicken") + " on it");
        room1.addItem("desk", "there is an old wooden desk with a " + redText("key") + " on top");
        room1.setGrabbables(room1Grabbables);

        String[] room2Grabbables = {"coal"};
        room2.addExit("west", room1);
        room2.addExit("north", room4);
        room2.addItem("fireplace", "it is on fire");
        room2.addItem("rug", "There is a lump of coal on the rug");
        room2.setGrabbables(room2Grabbables);


        String[] room3Grabbables = {"ke"};
        Edible beefPatty = new Edible("patty", "a old moldy beef patty", "You at the beef patty and instantly feel sick", -50);
        Edible[] edibles3 = {beefPatty};
        room3.addExit("south", room1);
        room3.addExit("east", room4);
        room3.addItem("wall", "It is a wall with a " + redText("patty") + " stuck on it");
        room3.addItem("floor", "floor description");
        room3.setGrabbables(room3Grabbables);
        room3.addEdibles(edibles3);


        String[] room4Grabbables = {"ke"};
        room4.addExit("south", room2);
        room4.addExit("west", room3);
        room4.addItem("car", "there is a broken car in the room");
        room4.addItem("person", "there is someone staring at you in the room");
        room4.setGrabbables(room4Grabbables);


        currentRoom = room1;
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
        result += "\n You have " + Globals.health + " remaining" ;
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