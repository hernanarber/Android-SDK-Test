package com.forter.hernanarber.fortersdk;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class JsonTransmitter {

    private JsonTransmitter() {}

    public static void send(String json, String url) {
        HttpURLConnection connection = null;
        String response = "";
        try {
            // Setting up the Connection:
            URL urlObj = new URL(url);
            connection = (HttpURLConnection) urlObj.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Length", "" + Integer.toString(json.getBytes().length));
            connection.setUseCaches(false);
            // Sending the Data:
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(json);
            wr.flush();
            int serverResponseCode = connection.getResponseCode();
            if (serverResponseCode == 200) {
                System.out.println("ForterSDK: Data Events Received by Server");
            } else {
                System.out.println("ForterSDK: Data Events sending Failed");
            }
        } catch (Exception e) {
                e.printStackTrace();
                String err = "ForterSDK: Transmission failed: " + e;
                System.out.println(err);
        } finally {
                // cleanup
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
}


