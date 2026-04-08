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

        public List<String> getData() {
            return data;
        }

        public String getStatus() {
            return status;
        }
    }

    static ApiResponse ConvertToJson(String resp){
        resp = resp.replace('=', ':');
        resp = resp.replaceAll("([a-zA-Z0-9]+)(?=[:,])", "\"$1\"");

        Gson gson = new Gson();

        return gson.fromJson(resp, (Type) ApiResponse.class);
    }

    public ApiResponse Get(String path) throws IOException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        out.println("{\"method\": \"get\", \"path\": \"" + path + "\"}");
        return ConvertToJson(in.readLine());
    }

    public ApiResponse Post(String path, List<String> body) throws IOException {
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

        return ConvertToJson(in.readLine());
    }

    public void closeConnection() throws IOException{
        System.out.println("Closing connection to server...");
        socket.close();
    }
}
