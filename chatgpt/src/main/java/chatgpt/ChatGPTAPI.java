package chatgpt;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;


public class ChatGPTAPI {
    private static final String API_KEY = "sk-OsMMq65tXdfOIlTUYtocSL7NCsmA7CerN77OkEv29dODg1EA";
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public static String getResponse(String prompt) {
        try {
            OkHttpClient client = new OkHttpClient();
            JSONObject payload = new JSONObject();
            payload.put("model", "gpt-3.5-turbo");
            payload.put("messages", new JSONArray().put(new JSONObject()
                    .put("role", "user")
                    .put("content", prompt)));

            RequestBody body = RequestBody.create(payload.toString(), MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(API_URL)
                    .header("Authorization", "Bearer " + API_KEY)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            return new JSONObject(response.body().string())
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getString("content");
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Unable to connect to ChatGPT.";
        }
    }
}
