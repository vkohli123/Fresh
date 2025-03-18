package com.example.check;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Handler;
import android.os.Looper;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MyTest {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Context context;

    public MyTest(Context context) {
        this.context = context;
    }
    public interface Callback {
        void onResult(String adId);
    }

    public interface IpCallback {
        void onResult(String ip);
    }

    public class IpResponse {
        @SerializedName("status")
        private String status;

        @SerializedName("message")
        private String message;

        @SerializedName("data")
        private IpData data;

        public IpData getData() {
            return data;
        }
    }

    public class IpData {
        @SerializedName("ip")
        private String ip;

        public String getIp() {
            return ip;
        }
    }

    public void performAction() {
        System.out.println("BasicPlugin action performed");
    }

    public String getNetworkType(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") Network network = connectivityManager.getActiveNetwork();
        @SuppressLint("MissingPermission") NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);

        if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            return "WiFi";
        } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            return "Cellular";
        } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
            return "Ethernet";
        } else {
            return "Unknown";
        }
    }

    public String getTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        return tz.getID();
    }

    public void getAdvertisingId(Context context, Callback callback) {
        executorService.submit(() -> {
            try {
                AdvertisingIdClient.Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
                String adId = adInfo != null ? adInfo.getId() : null;
                handler.post(() -> callback.onResult(adId));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void getIpAddress(Context context, IpCallback callback) {
        String apiUrl = "https://utils.follow.whistle.mobi/ip_utils_loop.php";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, apiUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Use Gson to parse JSON
                        Gson gson = new Gson();
                        IpResponse ipResponse = gson.fromJson(response.toString(), IpResponse.class);
                        callback.onResult(ipResponse.getData().getIp());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Error: Unable to fetch IP address. " + error.getMessage());
                        callback.onResult("Unknown");
                    }
                });

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        // Add the request to the RequestQueue.
        queue.add(request);
    }
    public String getPackageName(Context context) {
        return context.getPackageName();
    }
    public String getDeviceModel() {
        return android.os.Build.MODEL;
    }

    public String getDate() {
        Date current = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return formatter.format(current);
    }

    public String getTime() {
        Date current = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return formatter.format(current);
    }
    public String getPlatform() {
        return "Android";
    }
    public void whistleLoopEvents(String eventName, String extraParameters) {
        getAdvertisingId(context,new Callback() {
            @Override
            public void onResult(String deviceId) {
                String packageName = "com.example.staging_loop_example";
                String networkType = getNetworkType(context);
                getIpAddress(context,new IpCallback() {
                    @Override
                    public void onResult(String ipAddress) {
                        String userAgent = getDeviceModel();
                        String clickedTime = getTime();
                        String clickedDate = getDate();
                        String platformOs = getPlatform();
                        String timeZone = getTimeZone();
                        String token = "FcxbwOmR5MgutSv810zNDjaIP9LJChTX"; // Replace with your token

                        String url = "https://utils.follow.whistle.mobi/events_sdk_app.php?" +
                                "device_id=" + deviceId + "&" +
                                "event_name=" + eventName + "&" +
                                "package_name=" + packageName + "&" +
                                "network_type=" + networkType + "&" +
                                "ip=" + ipAddress + "&" +
                                "user_agent=" + userAgent + "&" +
                                "clicked_time=" + clickedTime + "&" +
                                "clicked_date=" + clickedDate + "&" +
                                "platform_os=" + platformOs + "&" +
                                "time_zone=" + timeZone + "&" +
                                "extraparams=" + extraParameters + "&" +
                                "token=" + token;

                        System.out.println("POST " + url);

                        StringRequest stringRequest = new StringRequest(
                                Request.Method.POST, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        System.out.println("Response is: " + response);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        System.out.println("That didn't work! Error: " + error);
                                    }
                                });

                        // Add the request to the RequestQueue.
                        RequestQueue queue = Volley.newRequestQueue(context);
                        queue.add(stringRequest);
                    }
                });
            }
        });
    }
}