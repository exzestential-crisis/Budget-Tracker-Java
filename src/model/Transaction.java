package model;

import java.util.Date;

public class Transaction {
    private Account account;
    private String category;
    private String type; // "Expense" or "Income"
    private double amount;
    private Date date;
    private String notes; // properly store notes

    // Constructor including optional notes
    public Transaction(Account account, String category, String type, double amount, Date date, String notes) {
        this.account = account;
        this.category = category;
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.notes = notes;
    }

    // Constructor without notes (notes default to empty string)
    public Transaction(Account account, String category, String type, double amount, Date date) {
        this(account, category, type, amount, date, "");
    }

    // Getters
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

    public String getNotes() {
        return notes;
    }

    // Setters
    public void setAccount(Account account) {
        this.account = account;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
