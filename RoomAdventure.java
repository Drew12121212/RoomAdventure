import java.util.Scanner;

public class RoomAdventure {
    private static Room currentRoom;
    private static String[] inventory = {null,null,null,null,null};
    private static String status;

    final private static String DEFAULT_STATUS = 
        "Sorry, I do not understand. Try [verb] [noun]. Valid verbs include 'go', 'look', and 'take'.";
    
    private static void handleGo(String noun) {
        String[] exitDirections = currentRoom.getExitDirections();
        Room[] exitDestinations = currentRoom.getExitDestinations();
        status = "I don't see that room";
        for (int i = 0; i < exitDirections.length; i ++) {
            if (noun.equals(exitDirections[i])) {
                currentRoom = exitDestinations[i];
                status = "Changed Room";
            }
        }
    }

    private static void handleLook(String noun) {
        String[] items = currentRoom.getItems();
        String[] itemDescriptions = currentRoom.getitemDescriptions();
        status = "I don't see that item";
        for (int i = 0; i < items.length; i++) {
            if (noun.equals(items[i])) {
                status = itemDescriptions[i];
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

    private static void setupGame() {
        Room room1 = new Room("Room 1");
        Room room2 = new Room("Room 2");

        String[] room1ExitDirections = {"east"};
        Room[] room1ExitDestinations = {room2};
        String[] room1Items = {"chair", "desk"};
        String[] room1ItemDescriptions = {
            "It is a chair",
            "it is a desk, there is a key on it",
        };
        String[] room1Grabbables = {"key"};
        room1.setExitDirections(room1ExitDirections);
        room1.setExitDestinations(room1ExitDestinations);
        room1.setItems(room1Items);
        room1.setItemDescriptions(room1ItemDescriptions);
        room1.setGrabbables(room1Grabbables);

        String[] room2ExitDirections = {"west"};
        Room[] room2ExitDestinations = {room1};
        String[] room2Items = {"fireplace", "rug",};
        String[] room2ItemDescriptions = {
            "It is on fire",
            "There s a lump of coal on the rug",
        };
        String[] room2Grabbables = {"coal"};
        room2.setExitDirections(room2ExitDirections);
        room2.setExitDestinations(room2ExitDestinations);
        room2.setItems(room2Items);
        room2.setItemDescriptions(room2ItemDescriptions);
        room2.setGrabbables(room2Grabbables);

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
    private String[] items;
    private String[] itemDescriptions;
    private String[] grabbables;

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

    public void setItems(String[] items) {
        this.items = items;
    }

    public String[] getItems() {
        return items;
    }

    public void setItemDescriptions(String[] itemDescriptions) {
        this.itemDescriptions = itemDescriptions;
    }

    public String[] getitemDescriptions() {
        return itemDescriptions;
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
        for (String item:items) {
            result +=item + " ";
        }
        result += "\nExits ";
        for (String direction:exitDirections) {
            result += direction + " ";
        }
        return result + "\n";
    }
}