package cz.uhk.expirecheck.datamanager;

import cz.uhk.expirecheck.data.Item;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class CsvDataManager implements DataManager {
    private static final String SEPARATOR = ";";
    @Override
    public List<Item> load(String file) throws IOException {
        List<Item> items = new ArrayList<>();

        try (BufferedReader input = new BufferedReader(new FileReader(file))) {
            String row = null;

            while ((row = input.readLine()) != null) {
                StringTokenizer tokenizer = new StringTokenizer(row, SEPARATOR);

                String name = tokenizer.nextToken();
                int qty = Integer.parseInt(tokenizer.nextToken());
                LocalDate expireDate = LocalDate.parse(tokenizer.nextToken());

                Item item = new Item(name, qty, expireDate);
                items.add(item);
            }
        }

        return items;
    }

    @Override
    public void save(String file, List<Item> items) throws IOException {
        try (PrintWriter output = new PrintWriter(file)) {
            for (var item: items) {
                output.print(item.getName());
                output.print(SEPARATOR);
                output.print(item.getQty());
                output.print(SEPARATOR);
                output.println(item.getExpire_date());
            }
        }
    }
}
