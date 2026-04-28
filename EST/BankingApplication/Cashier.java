package EST.BankingApplication;

public class Cashier {
    String deposit(String accNo, double amt){
       String message =  "₹" + amt + " deposited successfully in " + accNo;
       return message;
    }
    String withdraw(String accNo, double amt){
        String message =  "₹" + amt + " withdrawn successfully from " + accNo;
        return message;
     }
}
