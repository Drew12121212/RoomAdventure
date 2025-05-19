import java.util.Scanner;
import java.util.HashMap;

public class RoomAdventure {
    private static Room currentRoom;
    private static String[] inventory = {null,null,null,null,null};
    private static String status;

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
        status = "I don't see that item";
        if (items.containsKey(noun)) {
            status = items.get(noun);
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

    private static void setupGame() {
        Room room1 = new Room("Room 1");
        Room room2 = new Room("Room 2");
        Room room3 = new Room("Room 3");
        Room room4 = new Room("Room 4");

        String[] room1ExitDirections = {"east", "north"};
        Room[] room1ExitDestinations = {room2, room3};
        String[] room1Grabbables = {"key"};
        room1.ad
        room1.setExitDirections(room1ExitDirections);
        room1.setExitDestinations(room1ExitDestinations);
        room1.addItem("chair", "It is a chair");
        room1.addItem("desk", "there is an old wooden desk with a key on top");
        room1.setGrabbables(room1Grabbables);

        String[] room2ExitDirections = {"west", "north"};
        Room[] room2ExitDestinations = {room1, room4};
        String[] room2Grabbables = {"coal"};
        room2.setExitDirections(room2ExitDirections);
        room2.setExitDestinations(room2ExitDestinations);
        room2.addItem("fireplace", "it is on fire");
        room2.addItem("rug", "There is a lump of coal on the rug");
        room2.setGrabbables(room2Grabbables);


        String[] room3ExitDirections = {"south", "east"};
        Room[] room3ExitDestinations = {room1, room4};
        String[] room3Grabbables = {"ke"};
        room3.setExitDirections(room3ExitDirections);
        room3.setExitDestinations(room3ExitDestinations);
        room3.addItem("wall", "it is a wall");
        room3.addItem("floor", "floor description");
        room3.setGrabbables(room3Grabbables);

        String[] room4ExitDirections = {"south", "west"};
        Room[] room4ExitDestinations = {room2, room3};
        String[] room4Grabbables = {"ke"};
        room4.setExitDirections(room4ExitDirections);
        room4.setExitDestinations(room4ExitDestinations);
        room4.addItem("car", "there is a broken car in the room");
        room4.addItem("person", "there is someone staring at you in the room");
        room4.setGrabbables(room4Grabbables);


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
                default:
                    status = DEFAULT_STATUS;
            }

            System.out.println(status);
        }
    }
}

class Room {
    private String name;
    private String[] exitDirections;
    private Room[] exitDestinations;
    private String[] grabbables;
    HashMap<String, Room> exitHashMap = new HashMap<String, Room>();
    HashMap<String, String> itemsHashMap = new HashMap<String, String>();
    
    public Room(String name) {
        this.name = name;
    }

    public void setExitDirections(String[] exitDirections) {
        this.exitDirections = exitDirections;
    }

    public String[] getExitDirections () {
        return this.exitDirections;
    }

    public void setExitDestinations(Room[] exitDestinations) {
        this.exitDestinations = exitDestinations;
    }

    public Room[] getExitDestinations() {
        return exitDestinations;
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
        for (String direction:exitDirections) {
            result += direction + " ";
        }
        return result + "\n";
    }
}