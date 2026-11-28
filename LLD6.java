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
            initialTotal += cartItem.itemTotal();
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

abstract class DiscountStrategy{
    public abstract double calculate(double amount);
}

class FlatDiscountStrategy extends DiscountStrategy{
    public double calculate(double amount){
        return amount;
    }
}

class PercentDiscountStrategy extends DiscountStrategy{
    double percentage;

    public PercentDiscountStrategy(double percentage){
        this.percentage = percentage;
    }

    public double calculate(double amount){
        return amount * this.percentage;
    }
}

class PercentWithCapStrategy extends DiscountStrategy{
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

class DiscountStrategyManager{
    private static DiscountStrategyManager instance;

    private DiscountStrategyManager(){};

    public static synchronized DiscountStrategyManager getInstance(){
        if(instance == null){
            instance = new DiscountStrategyManager();
        }

        return instance;
    }

    public DiscountStrategy getStrategy(StrategyType strategyType, double percentage, double cap){
        if(strategyType == StrategyType.FLAT){
            return new FlatDiscountStrategy();
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

abstract class Coupon{
    Coupon nextCoupon;

    public void setNext(Coupon nextCoupon){
        this.nextCoupon = nextCoupon;
    }

    public abstract boolean isApplicable(Cart cart);
    public abstract double getDiscount(Cart cart);
    public abstract boolean isCombinable();

    public void applyDiscount(Cart cart){
        if(isApplicable(cart)){
            double discount = getDiscount(cart);
            cart.applyDiscount(discount);

            if(!isCombinable()){
                return;
            }
        }

        if(nextCoupon != null){
            nextCoupon.applyDiscount(cart);
        }
    }
}

class SeasonalOffer extends Coupon{
    double percent;
    String category;
    DiscountStrategyManager discountStrategyManager = DiscountStrategyManager.getInstance();
    
    public SeasonalOffer(double percent, String category){
        this.percent = percent;
        this.category = category;
    }

    public boolean isApplicable(Cart cart){
        for(CartItem item: cart.items){
            if(item.product.category.equals(this.category)){
                return true;
            }
        }

        return false;
    }

    public double getDiscount(Cart cart){
        discountStrategyManager.getStrategy(StrategyType.PERCENT, percent, 0.0);
    }

    public boolean isCombinable(){
        return true;
    }

    void setNext(LoyaltyDiscount);
}

class LoyaltyDiscount extends Coupon{
    
}