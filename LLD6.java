import java.util.*;

enum StrategyType{
    FLAT,
    PERCENT,
    PERCENT_WITH_CAP
}

class Product{
    String name;
    String category;
    double price;

    public Product(String name, String category, double price){
        this.name = name;
        this.category = category;
        this.price = price;
    }
}

class CartItem{
    Product product;
    int quantity;

    public CartItem(Product product, int quantity){
        this.product = product;
        this.quantity = quantity;
    }

    public double itemTotal(){
        return this.quantity * this.product.price;
    }
}

class Cart{
    ArrayList<CartItem> items;
    boolean loyaltyMember;
    double initialTotal;
    double finalTotal;

    public Cart(boolean loyaltyMember){
        this.items = new ArrayList<>();
        this.loyaltyMember = loyaltyMember;
    }

    public void addProduct(Product product, int quantity){
        items.add(new CartItem(product, quantity));
    }

    public double getInitialTotal(){
        for(CartItem cartItem: items){
            initialTotal += (cartItem.product.price * cartItem.quantity);
        }

        System.out.println("The initial total is: " + initialTotal);
        return initialTotal;
    }

    public void applyDiscount(double amount){
        finalTotal = Math.max(0, getInitialTotal() - amount);
        System.out.println("Applied discount of " + amount);
    }

    public double getFinalTotal(){
        System.out.println("The final total is: " + finalTotal);
        return finalTotal;
    }
}

interface DiscountStrategy{
    public double calculate(double amount);
}

class FlatDiscountStrategy implements DiscountStrategy{
    double amount;

    public FlatDiscountStrategy(double amount){
        this.amount = amount;
    }

    public double calculate(double amount){
        return this.amount;
    }
}

class PercentDiscountStrategy implements DiscountStrategy{
    double percentage;

    public PercentDiscountStrategy(double percentage){
        this.percentage = percentage;
    }

    public double calculate(double amount){
        return amount * this.percentage;
    }
}

class PercentWithCapStrategy implements DiscountStrategy{
    double percent;
    double cap;

    public PercentWithCapStrategy(double percent, double cap){
        this.percent = percent;
        this.cap = cap;
    }

    public double calculate(double amount){
        return Math.min(cap, amount * percent);
    }
}

class DiscountStrategyManager{
    private static DiscountStrategyManager instance;

    private DiscountStrategyManager(){};

    public static synchronized DiscountStrategyManager getInstance(){
        if(instance == null){
            instance = new DiscountStrategyManager();
        }

        return instance;
    }

    public DiscountStrategy getStrategy(StrategyType strategyType, double amount, double percentage, double cap){
        if(strategyType == StrategyType.FLAT){
            return new FlatDiscountStrategy(amount);
        }

        else if(strategyType == StrategyType.PERCENT){
            return new PercentDiscountStrategy(percentage);
        }

        else if(strategyType == StrategyType.PERCENT_WITH_CAP){
            return new PercentWithCapStrategy(percentage, cap);
        }

        throw new RuntimeException("Invalid strategy type");
    }
}

