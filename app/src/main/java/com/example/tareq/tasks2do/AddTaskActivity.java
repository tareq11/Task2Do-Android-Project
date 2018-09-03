package com.example.tareq.tasks2do;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AddTaskActivity extends AppCompatActivity {
    String defaultCategory;
    CalenderEvent calenderEvent;
    Button submit ;
    String name;
    String date;
    String time;
    String duration;
    String description;
    String category ;
    boolean isValid;
    Toast toast;
    TextView nameTv;
    TextView dateTv;
    TextView timeTv;
    TextView durationTv;
    TextView descriptionTv;
    Spinner categoryS ;
    private String SD_YeaR;
    private String SD_MontH;
    ProgressDialog progressDialog;
    protected Utils utils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Utils.theme!=0)
            setTheme(Utils.theme);
        if (!Utils.language.isEmpty()) {
            Locale locale1 = new Locale(Utils.language);
            Locale.setDefault(locale1);
            Configuration config1 = new Configuration();
            config1.locale = locale1;
            getBaseContext().getResources().updateConfiguration(config1,
                    getBaseContext().getResources().getDisplayMetrics());
        }
        utils = new Utils();
        setContentView(R.layout.activity_add);
        calenderEvent= new CalenderEvent(getContentResolver(),this);
        nameTv=(TextView)findViewById(R.id.name);
        timeTv=(TextView)findViewById(R.id.time);
        dateTv=(TextView)findViewById(R.id.date);
        descriptionTv=(TextView)findViewById(R.id.description);
        durationTv=(TextView) findViewById(R.id.duration);
        categoryS=findViewById(R.id.category);
        defaultCategory=categoryS.getSelectedItem().toString();
        timeTv.setInputType(0);


        dateTv.setInputType(0);

        dateTv.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View view, boolean hasfocus){
                if(hasfocus){
                    DateDialog dateDialog=new DateDialog(view);
                    FragmentTransaction ft =getFragmentManager().beginTransaction();
                    dateDialog.show(ft, "DatePicker");

                }
            }

        });
        timeTv.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View view, boolean hasfocus){
                if(hasfocus){
                    TimeDialog timeDialog=new TimeDialog(view);
                    FragmentTransaction ft =getFragmentManager().beginTransaction();
                    timeDialog.show(ft, "TimePicker");

                }
            }

        });


        submit = (Button) findViewById(R.id.submit);

    }





///////////////  SETTERS  AND GETTERS   ///////////////

    public void setDate(EditText date) {this.date = date.getText().toString();}

    public String getDate() { return date; }

    public void setName(EditText name) { this.name = name.getText().toString(); }

    public String getName() {return name; }

    public void setCategory(Spinner category) {this.category = category.getSelectedItem().toString();}

    public String getCategory() {return category; }

    public void setTime(EditText time) {this.time = time.getText().toString();  }

    public String getTime() {return time; }

    public void setDescription(EditText description) {this.description = description.getText().toString();    }

    public String getDescription() {return description;  }

    public void setDuration(EditText duration) {this.duration = duration.getText().toString();  }

    public String getDuration() {return duration;}

    public void onSubmit(View view) throws ParseException {
        setName((EditText) findViewById(R.id.name));
        setDate((EditText) findViewById(R.id.date));
        setTime((EditText) findViewById(R.id.time));
        setDuration((EditText) findViewById(R.id.duration));
        setDescription((EditText) findViewById(R.id.description));
        setCategory( (Spinner) findViewById(R.id.category));
        isValid=true;

    boolean a =    isValidName(getName()) ;
        boolean ac =    isValidDuration(getDuration());
        boolean f = isValidTime(getTime());
        boolean as = isValidDate(getDate(),getTime()) ;
        boolean aw =isCategorySelected(getCategory());
        boolean v = isValidDescription(getDescription());
        if(isValid)

        {                                                                                                                        //CategoryEnum.valueOf(getCategory().toUpperCase())
            MainActivity.taskDB.insertData(new Task(0,getName(),getDate()+" "+getTime(),Integer.valueOf(getDuration()),utils.getCategoryAsEnum(getCategory().toUpperCase()),getDescription()));
            MainActivity.durationDB.addNewDuration(MainActivity.taskDB.getTaskByName(getName()).getId());
          //  Toast.makeText(this, MainActivity.taskDB.getAllData().toString(), Toast.LENGTH_LONG).show();
            calenderEvent.addEvent(new Task(0,getName(),getDate()+" "+getTime(),Integer.valueOf(getDuration()),utils.getCategoryAsEnum(getCategory().toUpperCase()),getDescription()));
            new LoadViewTask().execute();
            Intent intent = new Intent(AddTaskActivity.this,MainActivity.class);
            startActivity(intent);
        }
    }


    public void selectDate(View view)
    {
        DateDialog dateDialog =new DateDialog(view);

    }



    public  boolean validateString(String txt) {

       // String regx = "[a-z]+\\.?";
        String regx = "[a-zا-يא-ת]+.?";

        Pattern pattern = Pattern.compile(regx,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(txt);
        return matcher.find();

    }

    ////// Name Validation
    public boolean isValidName(String name){
        final String ERROR_EMPTY=getResources().getString(R.string.task_Name_Must_Not_Be_Empty);
        final String ERROR_FORMAT=getResources().getString(R.string.task_Name_Must_Start_With_A_Letter);

        if (name.isEmpty() || name.equals(ERROR_EMPTY)) {
            markAsError(nameTv,ERROR_EMPTY);
            isValid=false;
            return false;
        }
        else if(!validateString(name) || name.equals(ERROR_FORMAT))
        {
            markAsError(nameTv,ERROR_FORMAT);
            isValid=false;
            return false;
        }
        markAsPassed(nameTv);
        return true;
    }

    ////// Description Validation
    public boolean isValidDescription(String description){
        final String ERROR_MSG=getResources().getString(R.string.Task_Description_Must_Not_Be_Empty);
        final String ERROR_FORMAT= getResources().getString(R.string.Task_Description_Must_Start_With_A_Letter);
        if (description.isEmpty() || description.contains(ERROR_MSG)) {
            markAsError(descriptionTv,ERROR_MSG);
            isValid=false;
            return false;
        }
        else if(!validateString(description) || description.equals(ERROR_FORMAT))
        {
            markAsError(descriptionTv,ERROR_FORMAT);
            isValid=false;
            return false;
        }
        markAsPassed(descriptionTv);
        return true;
    }

    ////// Date Validation
    public boolean isValidDate(String date , String time) throws ParseException {
        final String ERROR_MSG=getResources().getString(R.string.Date_must_not_be_empty);
        final String ERROR_EXPIRED=getResources().getString(R.string.Date_must_not_be_expired);
        final String ERROR_TIME=getResources().getString(R.string.Time_must_not_be_empty);
        String dateTime=date+" "+time;
        if (date.trim().isEmpty() ) {
            markAsError(dateTv,ERROR_MSG);
            isValid=false;
            return false;
        }
        else if (time.isEmpty()){
            return false;
        }
        else if(!isActiveDate(dateTime) || date.equals(ERROR_EXPIRED))
        {
            markAsError(dateTv,ERROR_EXPIRED);
            isValid=false;
            return false;
        }

        markAsPassed(dateTv);
        return true;
    }

    ////// Duration Validation
    public boolean isValidDuration(String duration) throws ParseException {
        final String ERROR_MSG=getResources().getString(R.string.Duration_must_not_be_empty);
        final String ERROR_NUM=getResources().getString(R.string.Duration_be_numbers_only);
        if (duration.isEmpty() || duration.equals(ERROR_MSG)) {
            markAsError(durationTv,ERROR_MSG);
            isValid=false;
            return false;
        }
        else if(!isNumeric(duration) || duration.equals(ERROR_NUM))
        {
            markAsError(durationTv,ERROR_NUM);
            isValid=false;
            return false;
        }
        markAsPassed(durationTv);
        return true;
    }
    ////// Time Validation
    public boolean isValidTime(String time) throws ParseException {
        final String ERROR_MSG=getResources().getString(R.string.Time_must_not_be_empty);

        if (time.isEmpty() || time.equals(ERROR_MSG)) {
            markAsError(timeTv,ERROR_MSG);
            isValid=false;
            return false;
        }
        markAsPassed(timeTv);
        return true;
    }
    ////// Category Validation
    public boolean isCategorySelected(String category) throws ParseException {
        if (category.equals(defaultCategory)) {
            markAsError(categoryS);
            isValid=false;
            return false;
        }
        markAsPassed(categoryS);
        return true;
    }
    public boolean isActiveDate(String currentDate) throws ParseException {

//        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
//        Date strDate = sdf.parse(currentDate);
//        if (new Date().after(strDate))
//            return true;
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date now = new Date();
    int c = now.compareTo(formatter.parse(currentDate));
        if  (now.compareTo(formatter.parse(currentDate))<0) {
            markAsPassed(dateTv);
            return true;


        }
        isValid=false;
        return false;
    }


    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }
    public void markAsError(TextView txtview,String msg)
    {
        txtview.setBackgroundResource(R.drawable.bad_input);
        txtview.setHint(msg);
        txtview.setHintTextColor(Color.RED);
    }
    public void markAsError(Spinner spinner)
    {
        spinner.setBackgroundResource(R.drawable.bad_input);
    }
    public void markAsPassed(TextView txtview)
    {
        txtview.setBackgroundResource(R.drawable.good_input);
        txtview.setHintTextColor(Color.BLACK);
    }
    public void markAsPassed(Spinner spinner)
    {
        spinner.setBackgroundResource(R.drawable.good_input);
    }

    private class LoadViewTask extends AsyncTask<Void, Integer, Void>
    {

        //Before running code in separate thread
        @Override
        protected void onPreExecute()
        {
            progressDialog = new ProgressDialog(AddTaskActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle(getResources().getString(R.string.creating));
            progressDialog.setMessage(getResources().getString(R.string.please_wait));

            progressDialog.show();
        }

        //The code to be executed in a background thread.
        @Override
        protected Void doInBackground(Void... params)
        {
            /* This is just a code that delays the thread execution 4 times,
             * during 850 milliseconds and updates the current progress. This
             * is where the code that is going to be executed on a background
             * thread must be placed.
             */
            try
            {
                //Get the current thread's token
                synchronized (this)
                {
                    //Initialize an integer (that will act as a counter) to zero
                    int counter = 0;
                    //While the counter is smaller than four
                    while(counter <= 4)
                    {
                        //Wait 850 milliseconds
                        this.wait(1850);
                        //Increment the counter
                        counter++;
                        //Set the current progress.
                        //This value is going to be passed to the onProgressUpdate() method.
                        publishProgress(counter*50);
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


}



