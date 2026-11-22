import java.util.*;

enum GatewayType{
    PAYTM,
    RAZORPAY;
}

class PaymentRequest{
    String sender;
    String receiver;
    String currency;
    double amount;

    public PaymentRequest(String sender, String receiver, String currency, double amount){
        this.sender = sender;
        this.receiver = receiver;
        this.currency = currency;
        this.amount = amount;
    }
}

abstract class BankingSystem{
    public abstract boolean processPayment(double amount);
}

class PaytmBankingSystem extends BankingSystem{
    public boolean processPayment(double amount){
        return Math.random() * 100 > 80;
    }
}

class RazorpayBankingSystem extends BankingSystem{
    public boolean processPayment(double amount){
        return Math.random() * 100 > 10;
    }
}

abstract class IPaymentGateway{
    public abstract boolean validatePayment(PaymentRequest paymentRequest);
    public abstract boolean initiatePayment(PaymentRequest paymentRequest);
    public abstract boolean confirmPayment(PaymentRequest paymentRequest);

    public boolean processPayment(PaymentRequest paymentRequest){
        boolean result = validatePayment(paymentRequest) && initiatePayment(paymentRequest) && confirmPayment(paymentRequest);

        if(!result){
            throw new RuntimeException("Payment request failed");
        }

        return result;
    }
}

class PaytmGateway extends IPaymentGateway{
    BankingSystem bankingSystem = new PaytmBankingSystem();

    public boolean validatePayment(PaymentRequest paymentRequest){
        boolean result = true;

        if(paymentRequest.amount < 0 || !paymentRequest.currency.equals("INR")){
            result = false;
        }

        if(!result){
            throw new RuntimeException("Either the amount is less than 0 or the currency is not INR");
        }

        System.out.println("Validating payment request of " + paymentRequest.amount + " rupees from " + paymentRequest.sender + " to " + paymentRequest.receiver);

        return true;
    }

    public boolean initiatePayment(PaymentRequest paymentRequest){
        boolean result = bankingSystem.processPayment(paymentRequest.amount);

        if(!result){
            throw new RuntimeException("Payment initialisation failed");
        }

        return result;
    }

    public boolean confirmPayment(PaymentRequest paymentRequest){
        System.out.println("Confirming payment");
        return true;
    }
}

class RazorpayGateway extends IPaymentGateway{
    BankingSystem bankingSystem = new RazorpayBankingSystem();
    

    public boolean validatePayment(PaymentRequest paymentRequest){
        boolean result = true;

        if(paymentRequest.amount < 0 || !paymentRequest.currency.equals("INR")){
            result = false;
        }

        if(!result){
            throw new RuntimeException("Either the amount is less than 0 or the currency is not INR");
        }

        System.out.println("Validating payment request of " + paymentRequest.amount + " rupees from " + paymentRequest.sender + " to " + paymentRequest.receiver);

        return true;
    }

    public boolean initiatePayment(PaymentRequest paymentRequest){
        boolean result = bankingSystem.processPayment(paymentRequest.amount);

        if(!result){
            throw new RuntimeException("Payment initialisation failed");
        }

        return result;
    }

    public boolean confirmPayment(PaymentRequest paymentRequest){
        System.out.println("Confirming payment");
        return true;
    }
}

class PaymentGatewayProxy extends IPaymentGateway{
    IPaymentGateway paymentGateway;
    int maxRetries;

    public PaymentGatewayProxy(IPaymentGateway paymentGateway, int maxRetries){
        this.paymentGateway = paymentGateway;
        this.maxRetries = maxRetries;
    }

    public boolean validatePayment(PaymentRequest paymentRequest){
        return paymentGateway.validatePayment(paymentRequest);
    }

    public boolean initiatePayment(PaymentRequest paymentRequest){
        boolean result = false;

        for(int attempt = 0; attempt < maxRetries; attempt ++){
            if(paymentGateway.processPayment(paymentRequest)){
                result =  true;
                break;
            }
        }

        if(!result){
            throw new RuntimeException("Payment failed after " + maxRetries + " retries");
        }

        return result;
    }

    public boolean confirmPayment(PaymentRequest paymentRequest){
        return paymentGateway.confirmPayment(paymentRequest);
    }
}

class GatewayFactory{
    private static GatewayFactory instance;

    private GatewayFactory(){};

    public static synchronized GatewayFactory getInstance(){
        if(instance == null){
            instance = new GatewayFactory();
        }

        return instance;
    }

    public PaymentGatewayProxy getGateway(GatewayType gatewayType){
        if(gatewayType.equals(GatewayType.PAYTM)){
            return new PaymentGatewayProxy(new PaytmGateway(), 10);
        }

        else if(gatewayType.equals(GatewayType.RAZORPAY)){
            return new PaymentGatewayProxy(new RazorpayGateway(), 10);
        }

        throw new RuntimeException("Invalid gateway type");
    }
}

class PaymentService{
    private static PaymentService instance;
    private PaymentGatewayProxy paymentGateway;

    private PaymentService(){};

    public static synchronized PaymentService getInstance(){
        if(instance == null){
            instance = new PaymentService();
        }

        return instance;
    }

    public void setGateway(PaymentGatewayProxy paymentGateway){
        this.paymentGateway = paymentGateway;
    }

    public boolean processPayment(PaymentRequest paymentRequest){
        return paymentGateway.processPayment(paymentRequest);
    }
}

class PaymentController{
    PaymentService paymentService;
    private static PaymentController instance;

    private PaymentController(){
        this.paymentService = PaymentService.getInstance();
    }

    public static synchronized PaymentController getInstance(){
        if(instance == null){
            instance = new PaymentController();
        }

        return instance;
    }

    public boolean handlePayment(GatewayType gatewayType, PaymentRequest paymentRequest){
        PaymentGatewayProxy paymentGateway = GatewayFactory.getInstance().getGateway(gatewayType);
        paymentService.setGateway(paymentGateway);

        return paymentService.processPayment(paymentRequest);
    }
}
