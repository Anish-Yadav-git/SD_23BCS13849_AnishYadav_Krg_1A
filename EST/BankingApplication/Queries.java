package EST.BankingApplication;

public class Queries {
    String query;
    public Queries(String query){
        this.query = query;
    }
    String queryStaus(String query){
        return "Query Closed";
    }
}
