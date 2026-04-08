package com.aggelos;

import java.io.*;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class Api {
    private static Socket socket;

    {
        try {
            socket = new Socket("localhost", 8080);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class ApiResponse {
        @SerializedName("data")
        private List<String> data;

        @SerializedName("status")
        private String status;

        @SerializedName("message")
        private String message;


        public List<String> getData() { return data; }
        public String getStatus() { return status; }
        public String getMessage() { return message; }
    }

    static ApiResponse ConvertToJson(String resp){
        Gson gson = new Gson();
        return gson.fromJson(resp, ApiResponse.class);
    }

    public ApiResponse Get(String path) throws IOException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        out.println("{\"method\": \"get\", \"path\": \"" + path + "\"}");
        return ConvertToJson(in.readLine());
    }

    public void Post(String path, Map<String, Object> body) throws IOException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        Map<String, Object> payload = new HashMap<>();
        payload.put("method", "post");
        payload.put("path", path);
        payload.put("body", body);

        Gson gson = new Gson();
        String json = gson.toJson(payload);
        out.println(json);

        String t = in.readLine().toString();
        System.out.println("T " + t);
    }

    public void closeConnection() throws IOException{
        System.out.println("Closing connection to server...");
        socket.close();
    }
}
