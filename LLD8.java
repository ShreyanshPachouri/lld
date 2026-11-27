import java.time.LocalTime;
import java.util.*;

abstract class NotificationObserver{
    public abstract void update(String message);
}

class UserNotificationObserver extends NotificationObserver{
    String userID;

    public UserNotificationObserver(String userID){
        this.userID = userID;
    }

    public void update(String message){
        System.out.println("User " + userID + " has been notified of the message " + message);
    }
}

class NotificationService{
    private static NotificationService instance;
    HashMap<String, NotificationObserver> observers;

    private NotificationService(){
        observers = new HashMap<>();
    }

    public static synchronized NotificationService getInstance(){
        if(instance == null){
            instance = new NotificationService();
        }

        return instance;
    }

    public void add(String userID, NotificationObserver notificationObserver){
        if(observers.containsKey(userID)){
            System.out.println("User already exists");
            return;
        }

        observers.put(userID, notificationObserver);
        System.out.println("User added");
    }

    public void remove(String userID){
        if(!observers.containsKey(userID)){
            System.out.println("User not found");
            return;
        }

        observers.remove(userID);
        System.out.println("User removed");
    }

    public void notify(String userID, String message){
        if(!observers.containsKey(userID)){
            System.out.println("User not found");
            return;
        }

        observers.get(userID).update(message);
    }

    public void notifyAll(String message){
        for(String userID: observers.keySet()){
            observers.get(userID).update(message);
        }
    }
}

class Location{
    double latitude;
    double longitude;

    public Location(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double distanceInKM(Location location){
        return Math.sqrt(Math.pow((location.latitude - this.latitude), 2) + Math.pow((location.longitude - this.longitude), 2));
    }
}

abstract class LocationStrategy{
    public abstract ArrayList<User> findNearbyUsers(Location location, double maxDistance, ArrayList<User> allUsers);
}

class BasicLocationStrategy extends LocationStrategy{
    public ArrayList<User> findNearbyUsers(Location location, double maxDistance, ArrayList<User> allUsers){
        ArrayList<User> nearbyUsers = new ArrayList<>();

        for(User user: allUsers){
            if(location.distanceInKM(user.userProfile.location) <= maxDistance){
                nearbyUsers.add(user);
            }
        }

        return nearbyUsers;
    }
}

class LocationService{
    LocationStrategy locationStrategy;
    private static LocationService instance;

    private LocationService(){}

    public static synchronized LocationService getInstance(){
        if(instance == null){
            instance = new LocationService();
        }

        return instance;
    }

    private void setStrategy(LocationStrategy locationStrategy){
        this.locationStrategy = locationStrategy;
    }

    public ArrayList<User> findNearbyUser(Location location, double maxDistance, ArrayList<User> allUsers){
        return this.locationStrategy.findNearbyUsers(location, maxDistance, allUsers);
    }
}

enum Gender{
    MALE,
    FEMALE,
    OTHER
}

class Interest{
    String name;
    String category;

    public Interest(String name, String category){
        this.name = name;
        this.category = category;
    }
}

class Preference{
    int minAge;
    int maxAge;
    double maxDistance;
    ArrayList<String> interests = new ArrayList<>();
    ArrayList<Gender> interestedIn = new ArrayList<>();

    public Preference(int minAge, int maxAge, double maxDistance){
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.maxDistance = maxDistance;
    }

    private void addInterest(String interest){
        interests.add(interest);
        System.out.println("Interest " + interest + " added");
    }

    private void addGender(Gender gender){
        interestedIn.add(gender);
        System.out.println("Gender " + gender + " added");
    }
}

class UserProfile{
    String name;
    int age;
    Gender gender;
    String bio = "";
    ArrayList<String> photos = new ArrayList<>();
    ArrayList<Interest> interests = new ArrayList<>();
    Location location;

    public UserProfile(String name, int age, Gender gender, Location location){
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.location = location;
    }

    private void display(){
        System.out.println("Name: " + name);
        System.out.println("Age: " + age);
        System.out.println("Gender: " + gender);
        System.out.println("Location: " + location.longitude + " " + location.latitude);

        for(int i = 1; i < photos.size(); i ++){
            System.out.println("Photo " + i + " " + photos.get(i));
        }

        for(int i = 1; i < interests.size(); i ++){
            System.out.println("Interest " + i + " " + interests.get(i));
        }
    }
}

class Message{
    String senderID;
    String content;
    String time;

    public Message(String senderID, String content, String time){
        this.senderID = senderID;
        this.content = content;
        this.time = time;
    }
}

class ChatRoom{
    String ID;
    ArrayList<String> personIDs;
    ArrayList<Message> messages;

    public void addMessage(String senderID, String content){
        messages.add(new Message(senderID, content, LocalTime.now().toString()));
    }

    public void displayChat(){
        for(Message message: messages){
            System.out.println(message.content);
        }
    }
}

enum MatcherType{
    BASIC,
    INTEREST,
    LOCATION
}

enum SwipeAction{
    LEFT,
    RIGHT
}

