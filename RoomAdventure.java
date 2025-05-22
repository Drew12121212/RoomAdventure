import java.util.Scanner;
import java.util.HashMap;

public class RoomAdventure {
    private static Room currentRoom;
    private static String[] inventory = {null,null,null,null,null, null, null};
    private static String status;
    private static boolean puzzleSolved = false;
    private static boolean running = true;

    final private static String DEFAULT_STATUS = 
        "Sorry, I do not understand. Try [verb] [noun]. Valid verbs include 'go', 'look', 'eat', 'answer', 'talk' , 'take','combine', and 'use'.";
    
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
            if (currentRoom.getEnemy() != null) {
            Globals.health -= currentRoom.getEnemyDamage();
            status += "\nA deadly " + currentRoom.getEnemy() + " jumps out and attacks you! You lose " + currentRoom.getEnemyDamage() + " health!\nYou need to use something to fight it off!";
            }
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

    private static void handleUse(String noun) {
        status = "You can't use that.";
        for (String item : inventory) {
            if (item != null && noun.equals(item)) {
                if (noun.equals("key") && currentRoom.getName().equals("Labratory")) {
                    currentRoom.addItem("note", "A glowing note that reads: 'The answer is echo.'");


                    ///no clue what this is doing but im going to just add a note to the inventory instead
                    /*
                    String[] currentGrabbables = currentRoom.getGrabbables();
                    String[] newGrabbables = new String[currentGrabbables.length + 1];
                    for (int i = 0; i < currentGrabbables.length; i++) {
                        newGrabbables[i] = currentGrabbables[i];
                    }
                    newGrabbables[currentGrabbables.length] = "note";
                    currentRoom.setGrabbables(newGrabbables);
                    */

                    for (int i = 0; i <inventory.length; i ++) {
                    if (inventory[i] == null) {
                        inventory[i] = "note";
                        break;
                    }
                }

                    status = "You used the key to unlock a drawer. A glowing note appears!";
                } else if (noun.equals("coal") && currentRoom.getName().equals("Riddle Room")) {
                    status = "You used the coal on the tablet: '...without a mouth...' appears.";
                } else if (noun.equals("flaming_stick") && currentRoom.getName().equals("Armory") && puzzleSolved) {
                    System.out.println("You wave the flaming stick. A hidden passage opens behind the wall!");
                    System.out.println("You have escaped the House\nYou Win");
                    running = false;
                    status = "";
                    //currentRoom.addExit("secret", new Room("Secret Chamber"));
                } else if (noun.equals("decoded_note") && currentRoom.getName().equals("Riddle Room")) {
                    status = "You read the decoded note: 'Echo is the answer.' The stone tablet crumbles.";
                    puzzleSolved = true;
                } else {
                    status = "You used the " + noun + ", but nothing happened.";
                }
                return;
            }
        }
    }



// Feature added by Ashish 'combine' command
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
            } else if ((noun1.equals("stick") && noun2.equals("firekey")) || (noun1.equals("firekey") && noun2.equals("stick"))) {
                inventory[firstIndex] = "flaming_stick";
                inventory[secondIndex] = null;
                status = "You combined the sword with the firekey. It becomes a flaming sword!";
            } else if ((noun1.equals("microscope") && noun2.equals("note")) || (noun1.equals("note") && noun2.equals("microscope"))) {
                inventory[firstIndex] = "decoded_note";
                inventory[secondIndex] = null;
                status = "You used the microscope to examine the note. You now have a decoded note!";
            } else if ((noun1.equals("coal") && noun2.equals("patty")) || (noun1.equals("patty") && noun2.equals("coal"))) {
                inventory[firstIndex] = "burnt_patty";
                inventory[secondIndex] = null;
                status = "You burned the patty using coal. It's now a burnt patty!";
            } else {
                status = "Those items can't be combined.";
            }
        } else {
            status = "You don't have both items.";
        }
    } 

    private static void handleAttack() {
        if (currentRoom.getEnemy() == null) {
            status = "There's nothing to attack here.";
            return;
        }

        for (String item : inventory) {
            if ("newspaper".equals(item)) {
                currentRoom.setEnemy(null, 0);
                status = "You smack the spider with the newspaper. It's dead!";
                return;
            }
        }

        status = "You have nothing to attack with!";
    }

    private static String redText(String text) {
        return Globals.RED + text + Globals.RESET;
    }

    private static void setupGame() {
        System.out.println("Welcome to Room Adventure!");
        System.out.println("Your goal is to explore rooms, collect items, and survive.");
        System.out.println("Commands (use [verb] [noun]):");
        System.out.println("- go [direction]           → move to a different room");
        System.out.println("- look [item]              → inspect something in the room");
        System.out.println("- take [item]              → pick up an item");
        System.out.println("- eat [item]               → consume an edible item");
        System.out.println("- use [item]               → use item from inventory");
        System.out.println("- talk [person]            → talk to someone");
        System.out.println("- attack [name]            → attack enemy");
        System.out.println("- combine [item] [item]    → creates a new item");
        System.out.println("- quit                     → exit the game");
        System.out.println();
        System.out.println("Health reaching 0 means game over.");
        System.out.println();
        
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

        String[] foyerGrabbables = {"coal", "stick"};
        foyer.addExit("west", livingRoom);
        foyer.addExit("north", labratory);
        foyer.addItem("fireplace", "there is a fireplace with a log that can be used as a " + redText("stick"));
        foyer.addItem("rug", "There is a lump of " + redText("coal") + " on the rug");
        foyer.setGrabbables(foyerGrabbables);
        foyer.setEnemy("spider", 10);


        String[] armoryGrabbables = {"newspaper"};
        Edible beefPatty = new Edible("patty", "a old moldy beef patty", "You at the beef patty and instantly feel sick", -50);
        Edible[] edibles3 = {beefPatty};
        armory.addExit("south", livingRoom);
        armory.addExit("east", labratory);
        armory.addItem("wall", "A solid stone wall with a " + redText("patty") + " stuck on it");
        armory.addItem("dresser", "\tThere’s an old dresser here, with a rolled-up " + redText("newspaper") + " on top.\n\tIt looks sturdy — maybe useful for swatting something...");
        armory.addEdibles(edibles3);
        armory.setGrabbables(armoryGrabbables);


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
        Scanner s = new Scanner(System.in);

        do {
        Globals.health = 100; // Reset health on restart
        inventory = new String[]{null, null, null, null, null, null, null};
        puzzleSolved = false;
        running = true;
        setupGame();

        while (running) {
            System.out.print(currentRoom.toString());
            System.out.print("Inventory: ");

            for (int i = 0; i < inventory.length; i++) {
                System.out.print(inventory[i] + " ");
            }
            
            System.out.println("\nWhat would you like to do?");
            String input = s.nextLine();
            if (input.equals("quit")) {
                running = false;
                continue;
            }
            String[] words = input.split(" ");

            if (!(words.length == 2 || (words.length == 3 && words[0].equals("combine")))) {
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
                case "use":
                    handleUse(noun);
                    break;
                case "attack":
                  handleAttack();
                  break;
                case "combine":
                  handleCombine(words[1], words[2]);
                  break;
                default:
                    status = DEFAULT_STATUS;
            }
/////////// Kim's change - game over and option to play again
            System.out.println(status);
            if (Globals.health <= 0) {
                System.out.println("You have no health left... Game over.");
                running = false;
            break;
            }
        }
     System.out.println("Would you like to play again? (yes/no)");
    } while (s.nextLine().trim().equalsIgnoreCase("yes"));

    System.out.println("Thanks for playing!");
    s.close();  
}
}

class Room {
    private String name;
    private String[] grabbables;
    HashMap<String, Room> exitHashMap = new HashMap<String, Room>();
    HashMap<String, String> itemsHashMap = new HashMap<String, String>();
    private Edible[] edibles;
    private String enemy;
    private int enemyDamage;

    
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
    public void setEnemy(String name, int damage) {
    this.enemy = name;
    this.enemyDamage = damage;
    }
    public String getEnemy() { return enemy; }
    public int getEnemyDamage() { return enemyDamage; }

    @Override
    public String toString() {
        String result = "\nLocation:" + name;
        result += "\nYou have " + Globals.health + " remaining" ;
        result += "\nYou See ";
        for (String item: itemsHashMap.keySet()) {
            result +=item + " ";
        }
        if (enemy != null) {
        result += "\nA deadly " + enemy + " is here, and it's ready to attack!";
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