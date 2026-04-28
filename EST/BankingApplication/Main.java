package EST.BankingApplication;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        boolean flag = true; 
        while(flag == true){
            System.out.println("Menu: \n1.Account Services\n2.User Services\n3.Cashier\n4.Query Management\nExit");
            System.out.println("Enter your choice: ");
            int choice = sc.nextInt()
            switch(choice){
                case 1: {
                    System.out.print("Account Created");
                    break;
                }
                case 2: {
                    System.out.print("User Created");
                    break;
                }
                case 3: {
                    Cashier ob = new Cashier();
                    System.out.println("Enter amount : ");
                    double amt = sc.nextInt();
                    ob.deposit("Anish", amt);
                }
                case 4:{
                    flag = false;
                }

            }
        }
        
    }
}
