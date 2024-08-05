/**
 * @program: jeecg-boot
 * @description: HTTP Utility class for making HTTP requests
 * @author: TestNet
 **/
package testnet.common.utils;


import testnet.common.entity.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;


public class HttpUtils {

    public static HttpResponse get(String url) throws IOException {
        return sendRequest(url, "GET", null, null, 0);
    }

    public static HttpResponse get(String url, Map<String, String> headers) throws IOException {
        return sendRequest(url, "GET", headers, null, 0);
    }

    public static HttpResponse get(String url, Map<String, String> headers, int timeout) throws IOException {
        return sendRequest(url, "GET", headers, null, timeout);
    }


    public static HttpResponse post(String url, String body) throws IOException {
        return sendRequest(url, "POST", null, body, 0);
    }

    public static HttpResponse post(String url, Map<String, String> headers, String body) throws IOException {
        return sendRequest(url, "POST", headers, body, 0);
    }

    public static HttpResponse post(String url, String body, int timeout) throws IOException {
        return sendRequest(url, "POST", null, body, timeout);
    }

    public static HttpResponse post(String url, Map<String, String> headers, String body, int timeout) throws IOException {
        return sendRequest(url, "POST", headers, body, timeout);
    }


    public static HttpResponse delete(String url) throws IOException {
        return sendRequest(url, "DELETE", null, null, 0);
    }

    public static HttpResponse delete(String url, Map<String, String> headers) throws IOException {
        return sendRequest(url, "DELETE", headers, null, 0);
    }

    public static HttpResponse put(String url, String body) throws IOException {
        return sendRequest(url, "PUT", null, body, 0);
    }

    public static HttpResponse put(String url, Map<String, String> headers, String body) throws IOException {
        return sendRequest(url, "PUT", headers, body, 0);
    }

    public static HttpResponse put(String url, String body, int timeout) throws IOException {
        return sendRequest(url, "PUT", null, body, timeout);
    }

    public static HttpResponse put(String url, Map<String, String> headers, String body, int timeout) throws IOException {
        return sendRequest(url, "PUT", headers, body, timeout);
    }

    public static HttpResponse sendRequest(String url, String method, Map<String, String> headers, String body, int timeout) throws IOException {
        HttpURLConnection connection = null;
        try {
            URL urlObj = new URL(url);
            connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod(method);

            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            if (body != null) {
                connection.setDoOutput(true);
                try (OutputStream outputStream = connection.getOutputStream()) {
                    outputStream.write(body.getBytes("UTF-8"));
                }
            }

            if (timeout > 0) {
                connection.setConnectTimeout(timeout);
                connection.setReadTimeout(timeout);
            }

            int responseCode = connection.getResponseCode();
            Map<String, List<String>> responseHeaders = connection.getHeaderFields();
            String responseBody;

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    responseBody = response.toString();
                }
            } else {
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String errorLine;
                    while ((errorLine = errorReader.readLine()) != null) {
                        errorResponse.append(errorLine);
                    }
                    responseBody = errorResponse.toString();
                }
            }

            return new HttpResponse(responseCode, responseHeaders, responseBody);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }


}


