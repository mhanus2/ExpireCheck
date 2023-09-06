package cz.uhk.expirecheck.data;

import cz.uhk.expirecheck.datamanager.DataManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ItemList {
    private List<Item> items = new ArrayList<>();

    public void add(Item item) {
        items.add(item);
    }

    public void remove(Item item) {
        items.remove(item);
    }

    public void remove(int index) {
        items.remove(index);
    }

    public Item getItem(int index) {
        return items.get(index);
    }

    public int getItemsCount() {
        return items.size();
    }

    public void load(DataManager dataManager, String file) throws IOException {
        items = dataManager.load(file);
    }

    public void save(DataManager dataManager, String file) throws IOException {
        dataManager.save(file, items);
    }
}
