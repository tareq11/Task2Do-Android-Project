package com.example.tareq.tasks2do;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.PopupMenu.OnMenuItemClickListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;

import android.view.Gravity;
import android.widget.Button;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static TaskDatabase taskDB;
    public static DurationDatabase durationDB;
    private PopupWindow mPopupWindow;
    private Button save;
    private Button cancel;
    private Button languages;
    private Button themes;
    private Button btn;
    int theme=0;
    String language="";
    ProgressDialog progressDialog;
    private android.support.v7.widget.LinearLayoutCompat layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskDB = new TaskDatabase(this);
        durationDB = new DurationDatabase(this);

        if (Utils.theme!=0)
            setTheme(Utils.theme);
        if (!Utils.language.isEmpty()) {
            Locale locale1 = new Locale(Utils.language);
            Locale.setDefault(locale1);
            Configuration config1 = new Configuration();
            config1.locale = locale1;
            getBaseContext().getResources().updateConfiguration(config1,getBaseContext().getResources().getDisplayMetrics());
        }
        setContentView(R.layout.activity_main);


        layout = (android.support.v7.widget.LinearLayoutCompat) findViewById(R.id.rl);

        btn=(Button) findViewById(R.id.all);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.add, menu);
        Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
       // startActivity(intent);
        return super.onCreateOptionsMenu(menu);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:

                Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public TaskDatabase getData() {
        return this.taskDB;
    }

    public void showListOfTasks(View view) throws ParseException {
        Intent intent = new Intent(MainActivity.this, TasksViewer.class);

        switch (view.getId())  //get the id of the view clicked. (in this case button)
        {
            case R.id.all: // if its button1
                intent.putExtra("Category", "All");
                break;
            case R.id.home:
                intent.putExtra("Category", "Home");
                break;
            case R.id.work: // if its button1
                intent.putExtra("Category", "Work");
                break;
            case R.id.college:
                intent.putExtra("Category", "College");
                break;
            case R.id.others:
                intent.putExtra("Category", "Others");
                break;
        }
        startActivity(intent);

    }


    public void settingFragment(final View view) {



        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.setting_fragment,null);


        languages = (Button) customView.findViewById(R.id.languages);
        languages.setText(getResources().getString(R.string.languages));


        themes = (Button) customView.findViewById(R.id.themes);
        themes.setText(getResources().getString(R.string.themes));

        cancel = (Button) customView.findViewById(R.id.cancel);
        cancel.setText(getResources().getString(R.string.cancel));


        save = (Button) customView.findViewById(R.id.save);
        save.setText(getResources().getString(R.string.save));

        mPopupWindow = new PopupWindow(
                customView,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );


        if(Build.VERSION.SDK_INT>=21){
            mPopupWindow.setElevation(5.0f);
        }

        mPopupWindow.showAtLocation(layout, Gravity.CENTER,-200,400);
        save.setOnClickListener(new View.OnClickListener(){
            @Override
            //On click function
            public void onClick(View view) {
                //Create the intent to start another activity
               if (theme!=0)
                    Utils.theme=theme;
               if (!language.isEmpty())
                    Utils.language=language;
                new LoadViewTask().execute();
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        themes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(getBaseContext(), view);

                /** Adding menu items to the popumenu */
                popup.getMenuInflater().inflate(R.menu.themes_menu, popup.getMenu());

                /** Defining menu item click listener for the popup menu */
                popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                            if (item.getTitle().equals(getResources().getString(R.string.gray)))
                                theme =R.style.GrayTheme;
                            else
                                theme =R.style.AppTheme;
                            return true;
                    }
                });
            popup.show();    //showing popup menu
            }
        });
        languages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(getBaseContext(), view);

                /** Adding menu items to the popumenu */
                popup.getMenuInflater().inflate(R.menu.lang_menu, popup.getMenu());

                /** Defining menu item click listener for the popup menu */
                popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {

                    public boolean onMenuItemClick(MenuItem item) {
                        language  = ""; // your language
                        if (item.getTitle().equals("English"))
                            language  = "en";
                        else if (item.getTitle().equals("العربية"))
                            language  = "ar";
                        else if (item.getTitle().equals("עברית"))
                            language  = "iw";


                        return true;
                    }
                });

                popup.show();    //showing popup menu
            }
        });
        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            //On click function
            public void onClick(View view) {
                //Create the intent to start another activity
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

    }


    private class LoadViewTask extends AsyncTask<Void, Integer, Void>
    {

        //Before running code in separate thread
        @Override
        protected void onPreExecute()
        {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle(getResources().getString(R.string.saving));
            progressDialog.setMessage(getResources().getString(R.string.please_wait));

            progressDialog.show();
        }

        //The code to be executed in a background thread.
        @Override
        protected Void doInBackground(Void... params)
        {

            try
            {

                synchronized (this)
                {

                    int counter = 0;

                    while(counter <= 4)
                    {
                        //Wait 850 milliseconds
                        this.wait(850);
                        //Increment the counter
                        counter++;
                        //Set the current progress.
                        //This value is going to be passed to the onProgressUpdate() method.
                        publishProgress(counter*25);
                    }
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            return null;
        }

        //Update the progress
        @Override
        protected void onProgressUpdate(Integer... values)
        {
            //set the current progress of the progress dialog
            progressDialog.setProgress(values[0]);
        }

        //after executing the code in the thread
        @Override
        protected void onPostExecute(Void result)
        {
            //close the progress dialog
            progressDialog.dismiss();
            //initialize the View
         //  setContentView(R.layout.activity_main);
        }
    }
    public void time() throws ParseException {

        String dateStart = "15-1-2015 08:29";
        String dateStop = "16-1-2015 11:31";

//HH converts hour in 24 hours format (0-23), day calculation
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        Date d1 = null;
        Date d2 = null;

        d1 = formatter.parse(dateStart);
        d2 = formatter.parse(dateStop);

//in milliseconds
        long diff = d2.getTime() - d1.getTime();

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        System.out.print(diffDays + " days, ");
        System.out.print(diffHours + " hours, ");
        System.out.print(diffMinutes + " minutes, ");
        System.out.print(diffSeconds + " seconds.");
        Toast.makeText(this, "days: "+diffDays+" hours: "+diffHours+" Min: "+diffMinutes , Toast.LENGTH_SHORT).show();
    }

}

