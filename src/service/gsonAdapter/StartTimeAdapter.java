package service.gsonAdapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;

public class StartTimeAdapter extends TypeAdapter<LocalDateTime> {

    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime startTime) throws IOException {
        if (startTime == null) {
            jsonWriter.nullValue();
            return;
        }
        jsonWriter.value(startTime.toString());

    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        String result = jsonReader.nextString();

        if (result == null) {
            return null;
        }
        return LocalDateTime.parse(result);
    }
}
