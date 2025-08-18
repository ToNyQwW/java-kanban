package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import service.gsonAdapter.DurationAdapter;
import service.gsonAdapter.TimeAdapter;

import java.time.Duration;
import java.time.LocalDateTime;

public final class GsonTask {

    private static final Gson gson;

    static {
        gson = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new TimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    private GsonTask() {
    }

    public static Gson getGson() {
        return gson;
    }
}

