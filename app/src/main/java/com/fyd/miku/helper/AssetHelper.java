package com.fyd.miku.helper;

import android.content.res.AssetManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AssetHelper {

    public static boolean copyAssetsToDir(AssetManager assetManager, String assetDir, String[] assets, String desDirName) {
        boolean success = true;
        for(String asset : assets) {
            boolean copyResult = copyAssetToDir(assetManager, assetDir, asset, desDirName);
            if(!copyResult) {
                success = false;
                break;
            }
        }
        return success;
    }

    /**
     * 将asset文件写入缓存
     */
    private static boolean copyAssetToDir(AssetManager assetManager, String assetDir, String assetName, String desDirName){
        boolean result = true;
        File desDir =new File(desDirName);
        if (!desDir.exists()){
            desDir.mkdirs();
        }
        File outFile =new File(desDir,assetName + ".temp");
        try {
            InputStream is = assetManager.open(assetDir + File.separator + assetName);
            FileOutputStream fos = new FileOutputStream(outFile);
            byte[] buffer = new byte[1024];
            int byteCount;
            while ((byteCount = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();
            is.close();
            fos.close();
            if(!outFile.renameTo(new File(desDir, assetName))) {
                result = false;
            }
        }catch (IOException e) {
            result = false;
        }
        return result;
    }
}
