package cz.uhk.expirecheck.datamanager;

import cz.uhk.expirecheck.data.Item;

import java.io.IOException;
import java.util.List;

public interface DataManager {
    List<Item> load(String file) throws IOException;
    void save(String file, List<Item> items) throws IOException;
}
