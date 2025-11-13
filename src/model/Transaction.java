package model;

import java.util.Date;

public class Transaction {
    private Account account;
    private String category;
    private String type; // "Expense" or "Income"
    private double amount;
    private Date date;

    public Transaction(Account account, String category, String type, double amount, Date date) {
        this.account = account;
        this.category = category;
        this.type = type;
        this.amount = amount;
        this.date = date;
    }

    public Account getAccount() {
        return account;
    }

    public String getCategory() {
        return category;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public Date getDate() {
        return date;
    }
}
