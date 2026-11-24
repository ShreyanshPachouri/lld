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
    public abstract void listAllProducts();
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
            throw new RuntimeException("Product does not exist OR quantity is greater than existing stock or quantity is less than 1");
        }
    }

    public int checkStock(int sku){
        if(!stockMap.containsKey(sku)){
            throw new RuntimeException("Invalid product");
        }

        return stockMap.get(sku);
    }

    public void listAllProducts(){
        for(int key: productsMap.keySet()){
            System.out.println(productsMap.get(key));
        }
    }
}

class InventoryManager{
    InventoryStore inventoryStore;
    ProductFactory productFactory;

    public InventoryManager(){
        this.inventoryStore = new DBInventoryStore();
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

    public void getAvailableProducts(){
        this.inventoryStore.listAllProducts();
    }
}

