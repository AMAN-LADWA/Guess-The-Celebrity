package com.viber.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> celebUrls =new ArrayList<String>();
    ArrayList<String> celebNames =new ArrayList<String>();
    ImageView imageView;
    int chosenCeleb = 0;
    Button button0;
    Button button1;
    Button button2;
    Button button3;
    String[] answers = new String[4];
    int locationCorrectAnswer = 0;

    public void celebChosen(View view) {
        if (view.getTag().toString().equals(Integer.toString(locationCorrectAnswer))) {
            Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT).show();
            newQuestion();
        }else {
            Toast.makeText(getApplicationContext(), "Incorrect! it is "+ celebNames.get(chosenCeleb).toString(), Toast.LENGTH_SHORT).show();
            newQuestion();
        }
    }

    public class DownloadTask extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try{
                url=new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in =urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data!=-1){
                    char current = (char) data;
                    result +=current;
                    data = reader.read();
                }
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
            return result;
        }
    }
    public class ImageDownloader extends AsyncTask<String,Void, Bitmap>{
        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }
    public void newQuestion() {
        try {
            Random random = new Random();
            chosenCeleb = random.nextInt(celebUrls.size());
            ImageDownloader imageTask = new ImageDownloader();
            Bitmap celebImg = imageTask.execute(celebUrls.get(chosenCeleb)).get();
            imageView.setImageBitmap(celebImg);
            locationCorrectAnswer = random.nextInt(4);
            int incorrectAnswerLocation;
            for (int i = 0; i < 4; i++) {
                if (i == locationCorrectAnswer) {
                    answers[i] = celebNames.get(chosenCeleb);
                } else {
                    incorrectAnswerLocation = random.nextInt(celebUrls.size());
                    while (incorrectAnswerLocation == chosenCeleb) {
                        incorrectAnswerLocation = random.nextInt(celebUrls.size());
                    }
                    answers[i] = celebNames.get(incorrectAnswerLocation);
                }
            }
            button0.setText(answers[0]);
            button1.setText(answers[1]);
            button2.setText(answers[2]);
            button3.setText(answers[3]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DownloadTask task = new DownloadTask();
        imageView = (ImageView) findViewById(R.id.imageView);
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        String result = null;
        try {
            result = task.execute("https://famousheights.net/5ft-5in-165-cm").get();
            String[] splitResult = result.split("</tbody>");
            Pattern p = Pattern.compile("\" src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);
            while (m.find()){
            celebUrls.add("https://famousheights.net"+m.group(1));
            }
            Pattern pp = Pattern.compile("<img alt=\"(.*?)\"");
            Matcher mm = pp.matcher(splitResult[0]);
            while (mm.find()){
                celebNames.add(mm.group(1));
            }
            newQuestion();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}