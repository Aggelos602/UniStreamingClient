package com.aggelos;

import java.io.*;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.List;

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

    public ApiResponse getFormats() throws IOException {
        return Wrapper("getFormats");
    }

    public ApiResponse getResolutions() throws IOException {
        return Wrapper("getResolutions");
    }

    private ApiResponse Wrapper(String path) throws IOException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        out.println("{\"action\":\"" + path + "\"}");
        String response = in.readLine();

        response = response.replace('=', ':');
        response = response.replaceAll("([a-zA-Z0-9]+)(?=[:,])", "\"$1\"");

        Gson gson = new Gson();

        return gson.fromJson(response, (Type) ApiResponse.class);

    }

    public void closeConnection() throws IOException{
        System.out.println("Closing connection to server...");
        socket.close();
    }
}
