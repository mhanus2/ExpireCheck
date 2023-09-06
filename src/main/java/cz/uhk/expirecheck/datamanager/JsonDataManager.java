package cz.uhk.expirecheck.datamanager;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import cz.uhk.expirecheck.data.Item;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class JsonDataManager implements DataManager {
    @Override
    public List<Item> load(String file) throws IOException{
        List<Item> items;

        try (FileReader input = new FileReader(file)) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                    .create();
            items = gson.fromJson(input, new TypeToken<List<Item>>(){}.getType());
        }

        return items;
    }

    @Override
    public void save(String file, List<Item> items) throws IOException {
        try (FileWriter output = new FileWriter(file)) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                    .create();
            String json = gson.toJson(items);
            output.write(json);
        }
    }

    // This code was copied from https://howtodoinjava.com/gson/gson-typeadapter-for-inaccessibleobjectexception/
    public static class LocalDateTypeAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        @Override
        public JsonElement serialize(final LocalDate date, final Type typeOfSrc,
                                     final JsonSerializationContext context) {
            return new JsonPrimitive(date.format(formatter));
        }

        @Override
        public LocalDate deserialize(final JsonElement json, final Type typeOfT,
                                     final JsonDeserializationContext context) throws JsonParseException {
            return LocalDate.parse(json.getAsString(), formatter);
        }
    }
}
