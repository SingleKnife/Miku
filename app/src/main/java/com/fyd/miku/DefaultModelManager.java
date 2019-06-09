package com.fyd.miku;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.fyd.miku.helper.AssetHelper;
import com.fyd.miku.helper.CacheHelper;
import com.fyd.miku.model.mmd.MikuModel;
import com.fyd.miku.model.pmd.PMDFile;
import com.fyd.miku.model.vmd.VMDFile;
import com.fyd.miku.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

public class DefaultModelManager {
    private static final String TOONS_DIR = "toons";       //toon文件目录
    private static final String MODEL_DIR = "model";       //模型文件目录
    private static final String MOTION_DIR = "motion";     //动作文件目录
    
    private static final String KEY_IS_MOTION_LOADED = "is_motion_loaded";
    private static final String KEY_IS_TOONS_LOADED = "is_toons_loaded";
    private static final String KEY_IS_MODEL_LOADED = "is_model_loaded";

    private static final String DEFAULT_MODEL_NAME = "Miku_Hatsune_Ver2.pmd";
    private static final String DEFAULT_MOTION_FILE = "motion.vmd";

    public static Observable<MikuModel> initAndLoadDefaultModel(final Context context) {
        return copyModelObservable(context)
                .flatMap(new Function<Boolean, ObservableSource<MikuModel>>() {
                    @Override
                    public ObservableSource<MikuModel> apply(Boolean initialize) throws Exception {
                        if(initialize) {
                            return loadModel(context);
                        } else {
                            throw new Exception("initialize model failed");
                        }
                    }
                });
    }

    public static Observable<VMDFile> initAndLoadDefaultMotion(final Context context) {
        return copyMotionObservable(context)
                .flatMap(new Function<Boolean, ObservableSource<VMDFile>>() {
                    @Override
                    public ObservableSource<VMDFile> apply(Boolean initialize) throws Exception {
                        if(initialize) {
                            return loadMotion(context);
                        } else {
                            throw new Exception("initialize motion failed");
                        }
                    }
                });
    }
    
    private static Observable<MikuModel> loadModel(final Context context) {
        return Observable.fromCallable(new Callable<MikuModel>() {
            @Override
            public MikuModel call() throws Exception {
                File fileDir = context.getFilesDir();
                String modelFilePath = fileDir.getAbsolutePath() + File.separator + MODEL_DIR
                        + File.separator + DEFAULT_MODEL_NAME;
                File modelFile = new File(modelFilePath);
                PMDFile pmdFile = new PMDFile();
                if(pmdFile.parse(modelFile)) {
                    return new MikuModel(pmdFile);
                } else {
                    throw new Exception("load model failed");
                }
            }
        });
    }

    private static Observable<VMDFile> loadMotion(final Context context) {
        return Observable.fromCallable(new Callable<VMDFile>() {
            @Override
            public VMDFile call() {
                File fileDir = context.getFilesDir();
                String motionFilePath = fileDir.getAbsolutePath() + File.separator + MOTION_DIR
                        + File.separator + DEFAULT_MOTION_FILE;
                File motionFile = new File(motionFilePath);
                VMDFile vmdFile = new VMDFile();
                vmdFile.parse(motionFile);

                return vmdFile;
            }
        });

    }


    private static Observable<Boolean> copyMotionObservable(final Context context) {
        return Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return isMotionLoaded(context) || copyMotionsToFileDir(context);
            }
        });
    }

    private static Observable<Boolean> copyToonsObservable(final Context context) {
        return Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return isToonsLoaded(context) || copyToonsToFileDir(context);
            }
        });
    }

    private static Observable<Boolean> copyModelObservable(final Context context) {
        return Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return isModelLoaded(context) || copyDefaultModelToFileDir(context);
            }
        });
    }

    private static boolean copyMotionsToFileDir(Context context) {
        AssetManager assetManager = context.getAssets();
        File fileDir = context.getFilesDir();
        String desDir = fileDir.getAbsolutePath() + File.separator + MOTION_DIR;
        boolean result;
        try {
            String[] motion = assetManager.list(MOTION_DIR);
            result = AssetHelper.copyAssetsToDir(assetManager, MOTION_DIR, motion, desDir);
            if(result) {
                CacheHelper.putBoolean(context, KEY_IS_MOTION_LOADED, true);
            }
        } catch (IOException e) {
            e.printStackTrace();
            FileUtils.deleteDir(desDir);
            result = false;
        }
        return result;
    }


    private static boolean copyToonsToFileDir(Context context) {
        AssetManager assetManager = context.getAssets();
        File fileDir = context.getFilesDir();
        String desDir = fileDir.getAbsolutePath() + File.separator + TOONS_DIR;
        boolean result;
        try {
            String[] toons = assetManager.list(TOONS_DIR);
            result = AssetHelper.copyAssetsToDir(assetManager, TOONS_DIR, toons, desDir);
            if(result) {
                CacheHelper.putBoolean(context, KEY_IS_TOONS_LOADED, true);
            }
        } catch (IOException e) {
            e.printStackTrace();
            FileUtils.deleteDir(desDir);
            result = false;
        }
        return result;
    }

    private static boolean copyDefaultModelToFileDir(Context context) {
        AssetManager assetManager = context.getAssets();
        File fileDir = context.getFilesDir();
        String desDir = fileDir.getAbsolutePath() + File.separator + MODEL_DIR;
        boolean result;
        try {
            String[] models = assetManager.list(MODEL_DIR);
            result = AssetHelper.copyAssetsToDir(assetManager, MODEL_DIR, models, desDir);
            if(result) {
                CacheHelper.putBoolean(context, KEY_IS_MODEL_LOADED, true);
            }
        } catch (IOException e) {
            e.printStackTrace();
            FileUtils.deleteDir(desDir);
            result = false;
        }
        Log.i("fyd", "copyDefaultModelToFileDir: " + result);
        return result;
    }

    private static boolean isMotionLoaded(Context context) {
        return CacheHelper.getBoolean(context, KEY_IS_MOTION_LOADED, false);
    }
    
    private static boolean isModelLoaded(Context context) {
        return CacheHelper.getBoolean(context, KEY_IS_MODEL_LOADED, false);
    }
    
    private static boolean isToonsLoaded(Context context) {
        return CacheHelper.getBoolean(context, KEY_IS_TOONS_LOADED, false);
    }
    
}
