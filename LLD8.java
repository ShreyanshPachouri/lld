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