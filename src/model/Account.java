package model;

public class Account {
    private String name;
    private double balance;
    private String type; // add this

    // Updated constructor to optionally set type
    public Account(String name, double balance) {
        this.name = name;
        this.balance = balance;
        this.type = "Regular"; // default type
    }

    // Optional: constructor that sets type directly
    public Account(String name, double balance, String type) {
        this.name = name;
        this.balance = balance;
        this.type = type;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return name + " (" + type + ") - Balance: ₱" + String.format("%.2f", balance);
    }
}
