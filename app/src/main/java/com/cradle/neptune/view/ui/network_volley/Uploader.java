package com.cradle.neptune.view.ui.network_volley;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.cradle.neptune.dagger.MyApp;
import com.cradle.neptune.view.LoginActivity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Uploader {
    private static final String TAG = "Uploader";
    // http related values
    private final String twoHyphens = "--";
    private final String lineEnd = "\r\n";
    private final String boundary = "apiclient-" + System.currentTimeMillis();
    private final String mimeType = "multipart/form-data;boundary=" + boundary;
    // what to send
    private String urlString;
    private String userName;
    private String userPassword;
    private byte[] multipartBody;
    private String token;

    public Uploader(String urlString, String userName, String userPassword, String token) {
        this.urlString = urlString;
        this.userName = userName;
        this.userPassword = userPassword;
        this.token=token;
    }

    public void doUpload(String jsonStringForBody, Response.Listener<NetworkResponse> callbackOk, Response.ErrorListener callbackFail) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        try {
            // the text
            buildTextPart(dos, "upload_file", ".");
            buildTextPart(dos, "user_name", userName);
            buildTextPart(dos, "user_pass", userPassword);

            // the file
            // do this on the main thread because client may delete file immediately on return
            buildFilePart(dos, jsonStringForBody);

            // send multipart form data necessary after file data
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // pass to multipart body
            multipartBody = bos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String,String> header = new HashMap<>();
        header.put(LoginActivity.TOKEN,token);
        MultipartRequest multipartRequest = new MultipartRequest(
                urlString,
                header,
                mimeType,
                jsonStringForBody,
                callbackOk, callbackFail);

        // add to volley queue
        RequestQueue queue = Volley.newRequestQueue(MyApp.getInstance());
        queue.add(multipartRequest);


    }

    private void buildTextPart(DataOutputStream dataOutputStream, String parameterName, String parameterValue) throws IOException {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + parameterName + "\"" + lineEnd);
        dataOutputStream.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd);
        dataOutputStream.writeBytes(lineEnd);
        dataOutputStream.writeBytes(parameterValue + lineEnd);
    }

    private void buildFilePart(DataOutputStream dataOutputStream, String sourceFileName) throws IOException {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"userDataFile\"; filename=\""
                + sourceFileName + "\"" + lineEnd);
        dataOutputStream.writeBytes(lineEnd);

        // create buffer
        File sourceFile = new File(sourceFileName);
        FileInputStream fileInputStream = new FileInputStream(sourceFile);
        int bytesAvailable = fileInputStream.available();

        int maxBufferSize = 1024 * 1024;
        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
        byte[] buffer = new byte[bufferSize];

        // read file and write it into form...
        int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

        while (bytesRead > 0) {
            dataOutputStream.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }

        dataOutputStream.writeBytes(lineEnd);
    }

}