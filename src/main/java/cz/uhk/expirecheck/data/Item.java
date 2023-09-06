package cz.uhk.expirecheck.data;

import java.time.LocalDate;


public class Item {
    protected String name;
    protected int qty;
    protected LocalDate expire_date;

    public Item(String name, int qty, LocalDate expire_date) {
        this.name = name;
        this.qty = qty;
        this.expire_date = expire_date;
    }

    public String getName() {
        return name;
    }

    public int getQty() {
        return qty;
    }

    public LocalDate getExpire_date() {
        return expire_date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public void setExpire_date(LocalDate expire_date) {
        this.expire_date = expire_date;
    }
}

