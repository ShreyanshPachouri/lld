import java.util.*;

abstract class INotification{
    abstract String getContent();
}

class SimpleNotification extends INotification{
    String text;

    public SimpleNotification(String text){
        this.text = text;
    }

    public String getContent(){
        return text;
    }
}

abstract class INotificationDecorator extends INotification{
    INotification notification;
}

class TimestampDecorator extends INotificationDecorator{
    INotification notification;
    String timestamp;

    public TimestampDecorator(INotification notification, String timestamp){
        this.notification = notification;
        this.timestamp = timestamp;
    }

    String getContent(){
        return notification.getContent() + " [" + timestamp + "]";
    }
}

class SignatureDecorator extends INotificationDecorator{
    INotification notification;
    String signature;

    public SignatureDecorator(INotification notification, String signature){
        this.notification = notification;
        this.signature = signature;
    }

    String getContent(){
        return notification.getContent() + " [" + signature + "]";
    }
}

abstract class IObserver{
    abstract void update();
}

class Logger extends IObserver{
    NotificationObservable observable;

    public Logger(NotificationObservable observable){
        this.observable = observable;

    }

    void update(){
        System.out.println("Logged event: " + observable.getNotification().getContent());
    }
}

class NotificationEngine extends IObserver{
    NotificationObservable observable;
    ArrayList<INotificationStrategy> strategies;

    public NotificationEngine(NotificationObservable observable, ArrayList<INotificationStrategy> strategies){
        this.observable = observable;
        this.strategies = strategies;
    }

    void update(){
        for(INotificationStrategy strategy : strategies){
            strategy.sendNotifcation(observable.getNotification().getContent());
        }
    }
}

abstract class IObservable{
    ArrayList<IObserver> observers;

    public IObservable(){
        observers = new ArrayList<>();
    }

    abstract public void addObserver(IObserver observer);
    abstract public void removeObserver(IObserver observer);
    abstract public void notifyObservers();
}

class NotificationObservable extends IObservable{
    INotification notification;

    public void addObserver(IObserver observer){
        observers.add(observer);
    }

    public void removeObserver(IObserver observer){
        observers.remove(observer);
    }

    public void notifyObservers(){
        for(IObserver observer : observers){
            observer.update();
        }
    }

    public void setNotification(INotification notification){
        this.notification = notification;
    }

    public INotification getNotification(){
        return notification;
    }
}

abstract class INotificationStrategy{
    abstract void sendNotifcation(String content);
}

class EmailStrategy extends INotificationStrategy{
    public void sendNotifcation(String content){
        System.out.println("Sending email notification: " + content);
    }
}

class SMSStrategy extends INotificationStrategy{
    public void sendNotifcation(String content){
        System.out.println("Sending SMS notification: " + content);
    }
}

class PopupStrategy extends INotificationStrategy{
    public void sendNotifcation(String content){
        System.out.println("Showing popup notification: " + content);
    }
}

class NotificationService{
    ArrayList<INotification> notifications;
    static NotificationService instance;
    NotificationObservable observable = new NotificationObservable();
    ArrayList<INotificationStrategy> strategies = new ArrayList<>(Arrays.asList(new EmailStrategy(), new SMSStrategy(), new PopupStrategy()));

    private NotificationService(ArrayList<INotification> notifications){
        this.notifications = notifications;
    }

    public static NotificationService getInstance(ArrayList<INotification> notifications){
        if(instance == null){
            instance = new NotificationService(notifications);
        }

        return instance;
    }

    void sendNotifications(){
        for(INotification notification: notifications){
            observable.setNotification(notification);
            observable.addObserver(new Logger(observable));
            observable.addObserver(new NotificationEngine(observable, strategies));
            observable.notifyObservers();
        }
    }
}