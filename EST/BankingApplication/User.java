package EST.BankingApplication;

public class User {
    String name;
    String number;
    String userStatus;
    public User(String name, String number, String userStatus){
        this.name = name;
        this.number = number;
        this.userStatus = userStatus;
    }
    String[] getDetails(String name){
        return new String[]{name, number, userStatus};
    }
}
