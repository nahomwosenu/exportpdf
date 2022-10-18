package com.nahompro.testapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.BuildCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity {

    private static final int PERM_RQ = 194;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btnExport).setOnClickListener(v->{handleExport();});
        if(Build.VERSION.SDK_INT<=Build.VERSION_CODES.Q && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERM_RQ);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && false == Environment.isExternalStorageManager()) {
            Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
            startActivity(new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri));
        }
    }

    private void handleExport() {
        String name=((EditText)findViewById(R.id.etName)).getText().toString();
        String age=((EditText)findViewById(R.id.etAge)).getText().toString();
        try {
            PdfPTable table = new PdfPTable(2);
            Stream.of("Name","Age").forEach(c->{
                PdfPCell title=new PdfPCell();
                title.setPhrase(new Phrase(c));
                table.addCell(title);
            });
            table.addCell(name);
            table.addCell(age);
            /* Hey Erzik, if you want you can loop into student records to create a longer table. Let me show you below with fake info */
            for(int i=0;i<10;i++){
                table.addCell("name"+i);
                table.addCell(""+(int)(Math.random()*100));
            }
            File path=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            Document document=new Document();
            PdfWriter.getInstance(document,new FileOutputStream(path.getAbsolutePath()+"/export.pdf"));
            document.open();
            document.add(table);
            document.close();
            Toast.makeText(MainActivity.this,"PDF exported to "+path.getAbsolutePath()+"/export.pdf",Toast.LENGTH_LONG).show();
        }catch (Exception e){
            Log.e("error","error with pdf export",e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERM_RQ && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            Toast.makeText(MainActivity.this,"You can now export to PDF",Toast.LENGTH_LONG).show();
        }
    }
}