import java.util.*;

class LLD2{
    String code;
    String name;
    int price;

    LLD2(String code, String name, int price){
        this.code = code;
        this.name = name;
        this.price = price;
    }
}

class Restaurant{
    static int restaurantId;
    String name;
    String location;
    ArrayList<LLD2> menu;

    Restaurant(String name, String location, ArrayList<LLD2> menu){
        this.name = name;
        this.location = location;
        this.menu = menu;
        restaurantId = ++ restaurantId;
    }
}

class RestaurantManager{
    static RestaurantManager manager;
    ArrayList<Restaurant> restaurants;

    private RestaurantManager(){
        restaurants = new ArrayList<>();
    }

    static synchronized RestaurantManager getRestaurantManager(){
        if(manager != null){
            return manager;
        }

        manager = new RestaurantManager();
        return manager;
    }

    void addRestaurant(Restaurant restaurant){
        restaurants.add(restaurant);
    }

    private Restaurant searchByLocation(String location){
        for(Restaurant r: restaurants){
            if(r.location.equals(location)){
                return r;
            }
        }

        return null;
    }
}

class Cart{
    Restaurant restaurant;
    ArrayList<LLD2> items;

    Cart (Restaurant r, ArrayList<LLD2> items){
        this.restaurant = r;
        this.items = items;
    }

    private void addItem(LLD2 item){
        items.add(item);
    }

    private int totalCost(){
        int sum = 0;

        for(LLD2 item: items){
            sum += item.price;
        }

        return sum;
    }

    boolean isEmpty(){
        return items.isEmpty();
    }
}

class User{
    int userId;
    String name;
    String address;
    Cart cart;

    User(int userId, String name, String adress, Cart cart){
        this.userId = userId;
        this.name = name;
        this.address = adress;
        this.cart = cart;
    }
}

interface IPaymentStrategy{
    void pay(int amount);
}

class CreditCard implements IPaymentStrategy{
    public void pay(int amount){
        System.out.println("Paid " + amount + " using credit card.");
    }
}

class NetBanking implements IPaymentStrategy{
    public void pay(int amount){
        System.out.println("Paid " + amount + " using net banking.");
    }
}

class UPI implements IPaymentStrategy{
    public void pay(int amount){
        System.out.println("Paid " + amount + " using UPI.");
    }
}

abstract class Order{
    int id;
    User user;
    Restaurant restaurant;
    ArrayList<LLD2> items;
    IPaymentStrategy paymentStrategy;
    Boolean isScheduled;

    Order(int id, User user, Restaurant restaurant, ArrayList<LLD2> items, IPaymentStrategy paymentStrategy, Boolean isScheduled){
        this.id = id;
        this.user = user;
        this.restaurant = restaurant;
        this.items = items;
        this.paymentStrategy = paymentStrategy;
        this.isScheduled = isScheduled;
    }

    abstract String getType();
}

class DeliveryOrder extends Order{
    String address;

    DeliveryOrder(int id, User user, Restaurant restaurant, ArrayList<LLD2> items, IPaymentStrategy paymentStrategy, String address, Boolean isScheduled){
        super(id, user, restaurant, items, paymentStrategy, isScheduled);
        this.address = address;
    }

    String getType(){
        return "Delivery";
    }
}

class PickupOrder extends Order{
    String ResAddress;

    PickupOrder(int id, User user, Restaurant restaurant, ArrayList<LLD2> items, IPaymentStrategy paymentStrategy, String address, Boolean isScheduled){
        super(id, user, restaurant, items, paymentStrategy, isScheduled);
        this.ResAddress = address;
    }

    String getType(){
        return "Pickup";
    }
}

interface IOrderFactory{
    Order createOrder(int id, User user, Restaurant restaurant, ArrayList<LLD2> items, IPaymentStrategy paymentStrategy, Boolean isScheduled, String orderType);
}

class ScheduleOrderFactory implements IOrderFactory{
    String scheduleTime;

    ScheduleOrderFactory(String scheduleTime){
        this.scheduleTime = scheduleTime;
    }

    public Order createOrder(int id, User user, Restaurant restaurant, ArrayList<LLD2> items, IPaymentStrategy paymentStrategy, Boolean isScheduled, String orderType){
        if(!isScheduled){
            return null;
        }

        if(orderType.equals("Delivery")){
            return new DeliveryOrder(id, user, restaurant, items, paymentStrategy, user.address, isScheduled);        
        }

        else if(orderType.equals("Pickup")){
            return new PickupOrder(id, user, restaurant, items, paymentStrategy, restaurant.location, isScheduled);
        }

        return null;
    }
}

class NowOrderFactory implements IOrderFactory{
    public Order createOrder(int id, User user, Restaurant restaurant, ArrayList<LLD2> items, IPaymentStrategy paymentStrategy, Boolean isScheduled, String orderType){
        if(isScheduled){
            return null;
        }
        
        if(orderType.equals("Delivery")){
            return new DeliveryOrder(id, user, restaurant, items, paymentStrategy, user.address, isScheduled);        
        }

        else if(orderType.equals("Pickup")){
            return new PickupOrder(id, user, restaurant, items, paymentStrategy, restaurant.location, isScheduled);
        }

        return null;
    }
}

class OrderManager{
    static OrderManager manager;
    ArrayList<Order> orderList;

    private OrderManager(){
        orderList = new ArrayList<>();
    }

    static synchronized OrderManager getOrderManager(){
        if(manager != null){
            return manager;
        }

        manager = new OrderManager();
        return manager;
    }

    private void addOrder(Order order){
        orderList.add(order);
    }

    private void listOrders(){
        for(Order order: orderList){
            System.out.println("Order ID: " + order.id + ", User: " + order.user.name + ", Restaurant: " + order.restaurant.name);
        }
    }
}

class NotificationService{
    Order order;
    
    void notifyUser(){
        System.out.println("This class is supposed to be a mockout for an external service.");
    }
}