package com.example;

public class TransactionDTO {
    private String id;
    private double amount;
    private String type; // Ejemplo: "DEPOSIT", "WITHDRAWAL"

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public TransactionDTO(String id, double amount, String type) {
        this.id = id;
        this.amount = amount;
        this.type = type;
    }
}
