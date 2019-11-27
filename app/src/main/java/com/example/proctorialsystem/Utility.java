package com.example.proctorialsystem;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import static java.lang.System.out;

public class Utility {


    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isValidEmail(String email){
        Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }

    public static boolean isValidUSN(String usn){
        Pattern VALID_USN_REGEX = Pattern.compile("^[0-9][A-Z]{2}[0-9]{2}[A-Z]{2}[0-9]{3}$");
        Matcher matcher = VALID_USN_REGEX.matcher(usn);
        return matcher.find();
    }

    public static boolean isValidDOB(String usn){
        Pattern VALID_DOB_REGEX = Pattern.compile("^(0[1-9]|1[012])[/](0[1-9]|[12][0-9]|3[01])[/](19|20)\\d\\d$");
        Matcher matcher = VALID_DOB_REGEX.matcher(usn);
        return matcher.find();
    }


    public static void setPostRequestContent(HttpsURLConnection conn,
                                             JSONObject jsonObject) throws IOException {

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
        writer.write(jsonObject.toString());
        Log.i(ProctorMainActivity.class.toString(), jsonObject.toString());
        writer.flush();
        writer.close();
        os.close();
    }

    public static String fetchResponseHttps(HttpsURLConnection connection) {

        try {
            connection.setConnectTimeout(60000);
            connection.connect();
            InputStream in = new BufferedInputStream(connection.getInputStream());
            BufferedReader read = new BufferedReader(new InputStreamReader(in));

            StringBuffer res = new StringBuffer();
            String line;

            while ((line = read.readLine()) != null) {
                res.append(line);
            }
            return res.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String fetchResponseHttp(HttpURLConnection connection) {

        try {
            connection.setConnectTimeout(60000);
            connection.connect();
            InputStream in = new BufferedInputStream(connection.getInputStream());
            BufferedReader read = new BufferedReader(new InputStreamReader(in));

            StringBuffer res = new StringBuffer();
            String line;

            while ((line = read.readLine()) != null) {
                res.append(line);
            }
            out.println("RESPONSE:\n" + res);
            return res.toString();
        } catch (Exception e) {
            out.println("Failed..");
            e.printStackTrace();
        }

        return "";
    }


    public static boolean isOnline() {
        try {
            int timeoutMs = 1500;
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

            sock.connect(sockaddr, timeoutMs);
            sock.close();

            return true;
        } catch (IOException e) {
            return false;
        }
    }



}
