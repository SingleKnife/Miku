package com.fyd.miku;

import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.fyd.miku.model.pmd.MikuModel;
import com.fyd.miku.model.pmd.PMDFile;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parsePmd();
            }
        });
    }

    private void parsePmd() {
        AssetManager assetManager = getAssets();
        try {
            PMDFile pmdParser = new PMDFile();
            InputStream inputStream = assetManager.open("Miku_Hatsune_Ver2.pmd");
            pmdParser.parse(inputStream);
            MikuModel mikuModel = new MikuModel(pmdParser);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
