import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

public class JsonUtils {
    private static final Gson gson = new Gson();

    public static String toJson(Object obj) { return gson.toJson(obj); }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    public static <T> List<T> fromJsonList(String json, Class<T> classOfT) {
        Type typeOfT = TypeToken.getParameterized(List.class, classOfT).getType();
        return gson.fromJson(json, typeOfT);
    }
}
