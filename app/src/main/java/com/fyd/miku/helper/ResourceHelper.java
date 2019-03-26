package com.fyd.miku.helper;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

public class ResourceHelper {
    public static String getRawResourceString(Context context, int id) {
        char[] buf = new char[1024];
        StringWriter stringWriter = new StringWriter();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(id)))) {
            int numRead;
            while((numRead = reader.read(buf)) != -1) {
                stringWriter.write(buf, 0, numRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return stringWriter.toString();
    }
}
