package it.ownmarket.android.rest;


import it.ownmarket.android.util.Util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.text.Html;

/**
 * From <a href=
 * "http://lukencode.com/2010/04/27/calling-web-services-in-android-using-httpclient/"
 * >Lukencode</a>
 * Non l'ha scritto proprio bene c'è qualche modifica riguardante il threadsafe
 * ed i protocolli.
 * 
 * @author Pasquale Paola
 * 
 */
public class RestClient {

    private ArrayList<NameValuePair> params;
    private ArrayList<NameValuePair> filesParam;
    private ArrayList<NameValuePair> headers;
    private HttpPost requestPost;
    private HttpGet requestGet;

    private String url;

    private int responseCode;
    private String message;

    private String response;

    public String getResponse() {
        return response;
    }

    public String getErrorMessage() {
        return message;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public RestClient(String url) {
        this.url = url;
        params = new ArrayList<NameValuePair>();
        headers = new ArrayList<NameValuePair>();
        filesParam = new ArrayList<NameValuePair>();
    }

    public void addParam(String name, String value) {
        params.add(new BasicNameValuePair(name, value));
    }

    public void addFile(String name, String path) {
        filesParam.add(new BasicNameValuePair(name, path));
    }

    public void addHeader(String name, String value) {
        headers.add(new BasicNameValuePair(name, value));
    }

    public void execute(RequestMethod method) throws Exception {
        switch (method) {
            case GET:
                // add parameters
                String combinedParams = "";
                if (!params.isEmpty()) {
                    combinedParams += "?";
                    for (NameValuePair p : params) {
                        String paramString = p.getName() + "="
                                        + URLEncoder.encode(p.getValue(), "UTF-8");
                        if (combinedParams.length() > 1) {
                            combinedParams += "&" + paramString;
                        } else {
                            combinedParams += paramString;
                        }
                    }
                }
                requestGet = new HttpGet(url + combinedParams);

                // add headers
                for (NameValuePair h : headers) {
                    requestGet.addHeader(h.getName(), h.getValue());
                }

                executeRequest(requestGet, url);

                break;

            case POST:
                requestPost = new HttpPost(url);

                // add headers
                for (NameValuePair h : headers) {
                    requestPost.addHeader(h.getName(), h.getValue());
                }

                if (!params.isEmpty()) {

                    requestPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

                }

                executeRequest(requestPost, url);

                break;

            case POSTMULTIPART:
                requestPost = new HttpPost(url);

                // add headers
                for (NameValuePair h : headers) {
                    requestPost.addHeader(h.getName(), h.getValue());
                }
                SimpleMultipartEntity multip = null;
                if (!params.isEmpty()) {

                    multip = new SimpleMultipartEntity();
                    for (NameValuePair pair : params)
                        multip.addPart(pair.getName(), pair.getValue());

                }
                if (!filesParam.isEmpty()) {
                    if (multip == null)
                        multip = new SimpleMultipartEntity();
                    for (NameValuePair pair : filesParam) {
                        File ff = new File(pair.getValue());
                        if (ff.exists()) {
                            FileInputStream fin = new FileInputStream(ff);
                            multip.addPart(pair.getName(), ff.getName(), fin);
                        }
                    }
                }

                if (multip != null)
                    requestPost.setEntity(multip);

                executeRequest(requestPost, url);

                break;

            default:
                break;

        }
    }

    private void executeRequest(HttpUriRequest request, String url) {
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "utf-8");
        params.setBooleanParameter("http.protocol.expect-continue", false);
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(params, registry);

        HttpClient client = new DefaultHttpClient(manager, params);

        HttpResponse httpResponse;

        try {
            httpResponse = client.execute(request);
            responseCode = httpResponse.getStatusLine().getStatusCode();
            message = httpResponse.getStatusLine().getReasonPhrase();

            HttpEntity entity = httpResponse.getEntity();

            if (entity != null) {

                InputStream instream = entity.getContent();
                response = convertStreamToString(instream);

                // Closing the input stream will trigger connection release
                instream.close();
                // entity.consumeContent();
            }

        } catch (ClientProtocolException e) {
            client.getConnectionManager().shutdown();
            e.printStackTrace();
        } catch (IOException e) {
            client.getConnectionManager().shutdown();
            e.printStackTrace();
        } finally {
            if (manager != null)
                manager.shutdown();
        }
    }

    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static void put(String targetURL, File file, String username, String password)
                    throws Exception {

        String BOUNDRY = "==================================";
        HttpURLConnection conn = null;

        try {

            // These strings are sent in the request body. They provide
            // information about the file being uploaded
            String contentDisposition = "Content-Disposition: form-data; name=\"userfile\"; filename=\""
                            + file.getName() + "\"";
            String contentType = "Content-Type: application/octet-stream";

            // This is the standard format for a multipart request
            StringBuffer requestBody = new StringBuffer();
            requestBody.append("--");
            requestBody.append(BOUNDRY);
            requestBody.append('\n');
            requestBody.append(contentDisposition);
            requestBody.append('\n');
            requestBody.append(contentType);
            requestBody.append('\n');
            requestBody.append('\n');
            requestBody.append(new String(Util.getBytesFromFile(file)));
            requestBody.append("--");
            requestBody.append(BOUNDRY);
            requestBody.append("--");

            // Make a connect to the server
            URL url = new URL(targetURL);
            conn = (HttpURLConnection) url.openConnection();

            // Put the authentication details in the request
            if (username != null) {
                String usernamePassword = username + ":" + password;
                String encodedUsernamePassword = Base64.encodeBytes(usernamePassword.getBytes());
                conn.setRequestProperty("Authorization", "Basic " + encodedUsernamePassword);
            }

            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDRY);

            // Send the body
            DataOutputStream dataOS = new DataOutputStream(conn.getOutputStream());
            dataOS.writeBytes(requestBody.toString());
            dataOS.flush();
            dataOS.close();

            // Ensure we got the HTTP 200 response code
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new Exception(String.format("Received the response code %d from the URL %s",
                                responseCode, url));
            }

            // Read the response
            InputStream is = conn.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(bytes)) != -1) {
                baos.write(bytes, 0, bytesRead);
            }
            byte[] bytesReceived = baos.toByteArray();
            baos.close();

            is.close();
            String response = new String(bytesReceived);

            // TODO: Do something here to handle the 'response' string

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

    }

}
