package org.gunivers.shutdownplugin;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Pterodactyl {

    private final String url;
    private final String apiKey;

    public Pterodactyl(FileConfiguration config) throws IOException {
        apiKey = config.getString("apiKey");
        String hostname = config.getString("hostname");
        String serverHash = config.getString("serverHash");
        url = "https://" + hostname + "/api/client/servers/" + serverHash + "/";

        testConnection();
    }

    private void addParameters(HttpURLConnection con) throws IOException {

        Map<String, String> parameters = new HashMap<>();
        parameters.put("Accept", "application/json");
        parameters.put("Content-Type", "application/json");
        parameters.put("Authorization", "Bearer " + apiKey);

        con.setDoOutput(true);
        String paramString = parameters.keySet().stream()
                .map(key -> key + ":" + parameters.get(key))
                .collect(Collectors.joining("\n"));
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        out.writeBytes(paramString);
        out.flush();
        out.close();
    }

    private void testConnection() throws IOException {

        URL u = new URL(url);
        HttpURLConnection con = (HttpURLConnection) u.openConnection();
        con.setRequestMethod("GET");
        addParameters(con);

        int status = con.getResponseCode();
        if(status > 299)
            throw new IOException("Received status code " + status);
    }

    public void shutdownServer() {

        try {

            URL u = new URL(url + "power");
            HttpURLConnection con = (HttpURLConnection) u.openConnection();
            con.setRequestMethod("POST");
            addParameters(con);

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.write("\"signal\": \"stop\"".getBytes(StandardCharsets.UTF_8));

            int status = con.getResponseCode();
            if (status > 299)
                throw new IOException("Received status code " + status);
        }
        catch(IOException e) { e.printStackTrace(); }
    }
}
