import java.util.*;

class Product{
    int sku;
    String name;
    Double price;

    public Product(int sku, String name, Double price){
        this.sku = sku;
        this.name = name;
        this.price = price;
    }
}

class ProductFactory{
    public Product createProduct(int sku){
        return new Product(sku, "Item " + sku, 100.0);
    }
}

abstract class InventoryStore{
    public abstract void addProduct(Product product, int quantity);
    public abstract void removeProduct(int sku, int quantity);
    public abstract int checkStock(int sku);
    public abstract ArrayList<Product> listAllProducts();
}

class DBInventoryStore extends InventoryStore{
    HashMap<Integer, Product> productsMap;
    HashMap<Integer, Integer> stockMap;

    public DBInventoryStore(){
        productsMap = new HashMap<>();
        stockMap = new HashMap<>();
    }

    public void addProduct(Product product, int quantity){
        if(!productsMap.containsKey(product.sku)){
            productsMap.put(product.sku, product);
        }

        stockMap.put(product.sku, stockMap.getOrDefault(product.sku, 0) + quantity);
    }

    public void removeProduct(int sku, int quantity){
        if(!productsMap.containsKey(sku) || stockMap.get(sku) < quantity || quantity <= 0){
            throw new RuntimeException("Invalid remove request");
        }

        if(quantity == stockMap.get(sku)){
            stockMap.remove(sku);
            productsMap.remove(sku);
        }

        else{
            stockMap.put(sku, stockMap.get(sku) - quantity);
        }
    }

    public int checkStock(int sku){
        if(!stockMap.containsKey(sku)){
            return 0;
        }

        return stockMap.get(sku);
    }

    public ArrayList<Product> listAllProducts(){
        ArrayList<Product> productList = new ArrayList<>();

        for(int key: productsMap.keySet()){
            productList.add(productsMap.get(key));
        }

        return productList;
    }
}

class InventoryManager{
    InventoryStore inventoryStore;
    ProductFactory productFactory;

    public InventoryManager(InventoryStore inventoryStore){
        this.inventoryStore = inventoryStore;
        this.productFactory = new ProductFactory();
    }

    public void addStock(int sku, int quantity){
        this.inventoryStore.addProduct(this.productFactory.createProduct(sku), quantity);
    }

    public void removeStock(int sku, int quantity){
        this.inventoryStore.removeProduct(sku, quantity);
    }

    public int checkStock(int sku){
        return this.inventoryStore.checkStock(sku);
    }

    public ArrayList<Product> getAvailableProducts(){
        return this.inventoryStore.listAllProducts();
    }
}

abstract class ReplenishStrategy{
    public abstract void replenish(InventoryManager inventoryManager, HashMap<Integer, Integer> stockMap);
}

class ThresholdReplenishStrategy extends ReplenishStrategy{
    int threshold;

    public ThresholdReplenishStrategy(int threshold){
        this.threshold = threshold;
    }

    public void replenish(InventoryManager inventoryManager, HashMap<Integer, Integer> stockMap){
        for(Integer sku: stockMap.keySet()){
            if(inventoryManager.checkStock(sku) < threshold){
                inventoryManager.addStock(sku, threshold - stockMap.get(sku));
            }
        }
    }
}

class WeeklyReplenishStrategy extends ReplenishStrategy{
    int quantity;

    public WeeklyReplenishStrategy(int quantity){
        this.quantity = quantity;
    }

    public void replenish(InventoryManager inventoryManager, HashMap<Integer, Integer> stockMap){
        for(Integer sku: stockMap.keySet()){
            inventoryManager.addStock(sku, this.quantity);
        }
    }
}

class DarkStore{
    String name;
    Double x;
    Double y;
    InventoryManager inventoryManager;
    ReplenishStrategy replenishStrategy;
    
    public DarkStore(String name, Double x, Double y, InventoryManager inventoryManager, ReplenishStrategy replenishStrategy){
        this.name = name;
        this.x = x;
        this.y = y;
        this.inventoryManager = inventoryManager;
        this.replenishStrategy = replenishStrategy;
    }

    public ArrayList<Product> getAllProducts(){
        return this.inventoryManager.getAvailableProducts();
    }

    public double distanceTo(Double ux, Double uy){
        return Math.sqrt(Math.pow((ux - x), 2) + Math.pow((uy - y), 2));
    }

    public void runReplenish(HashMap<Integer, Integer> stockMap){
        replenishStrategy.replenish(inventoryManager, stockMap);
    }
}

class DarkStoreManager{
    private static DarkStoreManager instance;
    ArrayList<DarkStore> darkStores;

    private DarkStoreManager(){
        this.darkStores = new ArrayList<>();
    }

    public static synchronized DarkStoreManager getInstance(){
        if(instance == null){
            instance = new DarkStoreManager();
        }

        return instance;
    }

    public void addStore(DarkStore darkStore){
        darkStores.add(darkStore);
    }

    public ArrayList<DarkStore> getNearbyStores(Double ux, Double uy, Double maxDistance){
        ArrayList<DarkStore> nearStores = new ArrayList<>();

        for(DarkStore darkStore: darkStores){
            if(darkStore.distanceTo(ux, uy) <= maxDistance){
                nearStores.add(darkStore);
            }
        }

        return nearStores;
    }
}

class Pair<K,V> {
    private K key; 
    private V value;
    
    public Pair(K k, V v){ 
        key = k; 
        value = v; 
    }
    
    public K getKey(){ 
        return key; 
    }
    
    public V getValue(){ 
        return value; 
    }
}

class Cart{
    ArrayList<Pair<Product, Integer>> items;
    ProductFactory productFactory;

    public Cart(){
        this.items = new ArrayList<>();
        this.productFactory = new ProductFactory();
    }

    public void addItems(int sku, int quantity){
        this.items.add(new Pair<>(productFactory.createProduct(sku), quantity));
    }

    public Double getTotal(){
        Double total = 0.0;

        for(Pair<Product, Integer> pair: items){
            total += pair.getKey().price * pair.getValue();
        }

        return total;
    }

    public ArrayList<Pair<Product, Integer>> getItems(){
        return this.items;
    }
}

class User{
    String name;
    Double x;
    Double y;
    Cart cart;

    public User(String name, Double x, Double y, Cart cart){
        this.name = name;
        this.x = x;
        this.y = y;
        this.cart = cart;
    }
}

class DeliveryPartner{
    String name;

    public DeliveryPartner(String name){
        this.name = name;
    }
}

class Order {
    private static int nextId = 1;
    public int orderId;
    public User user;
    public List<Pair<Product, Integer>> items = new ArrayList<>();
    public List<DeliveryPartner> partners = new ArrayList<>();
    public double totalAmount;

    public Order(User u) {
        orderId = nextId ++;
        user = u;
        totalAmount = 0.0;
    }
}

class OrderManager{
    private static OrderManager instance;
    DarkStoreManager darkStoreManager;
    ArrayList<Order> orders;

    private OrderManager(){
        this.orders = new ArrayList<>();
        this.darkStoreManager = DarkStoreManager.getInstance();
    }
    
    public static synchronized OrderManager getInstance(){
        if(instance == null){
            instance = new OrderManager();
        }

        return instance;
    }

    public void placeOrder(User user, Cart cart){
        ArrayList<Pair<Product, Integer>> requestedItems = cart.getItems();
        ArrayList<Pair<Product, Integer>> fulfilledItems = new ArrayList<>();
        ArrayList<Order> orders = new ArrayList<>();
        
        Double maxDistance = 5.0;
        ArrayList<DarkStore> nearestStores = darkStoreManager.getNearbyStores(user.x, user.y, maxDistance);
       
        if(nearestStores.size() == 0){
            throw new RuntimeException("No nearby stores found");
        }

        DarkStore firstStore = nearestStores.get(0);
        
        for(Pair<Product, Integer> item: requestedItems){
            if(firstStore.inventoryManager.checkStock(item.getKey().sku) < item.getValue()){
                break;
            }

            firstStore.inventoryManager.removeStock(item.getKey().sku, item.getValue());
            fulfilledItems.add(item);
        }

        Order order = new Order(user);

        if(fulfilledItems.size() == requestedItems.size()){
            System.out.println("Only 1 agent required");
            order.totalAmount = cart.getTotal();
            order.partners.add(new DeliveryPartner("Partner 1"));
            orders.add(order);
            return;
        }

        System.out.println("Multiple agents are required");
        int partnerNumber = 1;

        for(int i = 0; i < nearestStores.size(); i ++){
            for(Pair<Product, Integer> item: requestedItems){
                if(nearestStores.get(i).inventoryManager.checkStock(item.getKey().sku) >= item.getValue()){
                    nearestStores.get(i).inventoryManager.removeStock(item.getKey().sku, item.getValue());
                    fulfilledItems.add(item);
                }
            }

            order.partners.add(new DeliveryPartner("Partner " + partnerNumber ++));
            System.out.println("Agent " + partnerNumber + " added");

            if(fulfilledItems.size() == requestedItems.size()){
                break;
            }
        }

        if(fulfilledItems.size() != requestedItems.size()){
            System.out.println("Order could not be fulfilled");
            return;
        }

        order.totalAmount = cart.getTotal();
        orders.add(order);
    }
}