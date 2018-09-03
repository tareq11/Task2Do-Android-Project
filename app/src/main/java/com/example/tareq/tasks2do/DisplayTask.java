package com.example.tareq.tasks2do;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.ToggleButton;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DisplayTask extends AppCompatActivity {
   protected TaskDatabase myDb;
    String defaultCategory;

    Button update ;
    Button finish ;
    Button edit ;
    String name;
    String date;
    String time;
    String duration;
    String description;
    String category ;
    boolean isValid;
    Toast toast;
    TextView titleTv;
    TextView displayerTv;
    TextView nameTv;
    TextView dateTv;
    TextView timeTv;
    TextView durationTv;
    TextView descriptionTv;
    Spinner categoryS ;
    String taskID;
    private CalenderEvent calenderEvent;
    private String taskOldName;
    ToggleButton playPause;
    ProgressDialog progressDialog;
    protected Utils utils;
    protected Task curTask;
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
        setContentView(R.layout.display_task);
        utils = new Utils();
        calenderEvent=new CalenderEvent(getContentResolver(),this);
        taskID = getIntent().getStringExtra("TASK_ID");
        myDb = new TaskDatabase(this);
        titleTv=(TextView)findViewById(R.id.title_display);
        playPause = (ToggleButton) findViewById(R.id.playPause);
        nameTv=(TextView)findViewById(R.id.name_display);
        timeTv=(TextView)findViewById(R.id.time_display);
        dateTv=(TextView)findViewById(R.id.date_display);
        descriptionTv=(TextView)findViewById(R.id.description_display);
        durationTv=(TextView) findViewById(R.id.duration_display);
        playPause=(ToggleButton) findViewById(R.id.playPause);
        playPause.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try {
                        updateDuration(playPause.isChecked());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        categoryS=findViewById(R.id.category_display);
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

        // get Task details form DB
        curTask = myDb.getTaskByID(taskID);

        nameTv.setText(curTask.getName());
        timeTv.setText(curTask.getDateTime().split(" ")[1]);
        dateTv.setText(curTask.getDateTime().split(" ")[0]);
        descriptionTv.setText(curTask.getDescription());
        durationTv.setText(curTask.getDuration()+"");

        String[] category_array = getResources().getStringArray(R.array.Category_arrays);
        String a;
        a = utils.getCategoryAsString(curTask.getCategory(),getResources());
        for (int i=0;i<category_array.length;i++)//curTask.getCategory().getCategory()
            if(category_array[i].equalsIgnoreCase( utils.getCategoryAsString(curTask.getCategory(),getResources()) ) ){
                categoryS.setSelection(i);
                break;
            }

        displayerTv=(TextView)findViewById(R.id.displayer);
        try {
            displayerTv.setText(getDisplayerContent());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Set content Details
         titleTv.setText(getResources().getString(R.string.displayTaskDetails));
        nameTv.setVisibility(View.GONE);
        timeTv.setVisibility(View.GONE);
        dateTv.setVisibility(View.GONE);
        descriptionTv.setVisibility(View.GONE);
        durationTv.setVisibility(View.GONE);
        categoryS.setVisibility(View.GONE);
        update = (Button) findViewById(R.id.update);
        update.setVisibility(View.GONE);
        edit = (Button) findViewById(R.id.edit);
        finish = (Button) findViewById(R.id.finish);
        try {
            if (!isTimeHasCome()) {
                playPause.setVisibility(View.GONE);
                finish.setVisibility(View.GONE);
            }
            else
                playPause.setChecked(getStatus(taskID));
        } catch (ParseException e) {
            e.printStackTrace();
        }
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

    public void onUpdate(View view) throws ParseException {
        setName((EditText) findViewById(R.id.name_display));
        setDate((EditText) findViewById(R.id.date_display));
        setTime((EditText) findViewById(R.id.time_display));
        setDuration((EditText) findViewById(R.id.duration_display));
        setDescription((EditText) findViewById(R.id.description_display));
        setCategory( (Spinner) findViewById(R.id.category_display));
        isValid=true;

        boolean a =    isValidName(getName()) ;
        boolean ac =    isValidDuration(getDuration());
        boolean f = isValidTime(getTime());
        boolean as = isValidDate(getDate(),getTime()) ;
        boolean aw =isCategorySelected(getCategory());
        boolean v = isValidDescription(getDescription());
        if(isValid)

        {
            taskOldName=MainActivity.taskDB.getTaskByID(taskID).getName();                                                                                 //CategoryEnum.valueOf(getCategory().toUpperCase())
            MainActivity.taskDB.updateData(taskID,new Task(Integer.parseInt(taskID),getName(),getDate()+" "+getTime(),Integer.valueOf(getDuration()),utils.getCategoryAsEnum(getCategory().toUpperCase()),getDescription()));
            Toast.makeText(this, getResources().getString(R.string.Task_was_updated_successfully), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(DisplayTask.this,MainActivity.class);
            calenderEvent.deleteEvent(taskOldName);
            calenderEvent.addEvent(MainActivity.taskDB.getTaskByID(taskID));
            new LoadViewTask().execute();

            startActivity(intent);
        }
    }


    public void selectDate(View view)
    {
        DateDialog dateDialog =new DateDialog(view);

    }



    public  boolean validateString(String txt) {

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
        final String ERROR_NUM=getResources().getString(R.string.Duration_be_numbers_only);;
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



    public void onEdit(View view) throws ParseException {
        titleTv.setText(getResources().getString(R.string.EditTaskDetails));
        edit.setVisibility(View.GONE);
        displayerTv.setVisibility(View.GONE);
        nameTv.setVisibility(View.VISIBLE);
        timeTv.setVisibility(View.VISIBLE);
        dateTv.setVisibility(View.VISIBLE);
        descriptionTv.setVisibility(View.VISIBLE);
        durationTv.setVisibility(View.VISIBLE);
        categoryS.setVisibility(View.VISIBLE);
        update.setVisibility(View.VISIBLE);
        finish.setVisibility(View.GONE);
        playPause.setVisibility(View.GONE);
    }
    public void onFinish(View view) throws ParseException {
        int a= Integer.parseInt(MainActivity.durationDB.getDurationDetailsByID(taskID)[3]);
        AlertDialog.Builder alert = new AlertDialog.Builder(DisplayTask.this);

        // Setting Dialog Title
        alert.setTitle(getResources().getString(R.string.alert));

        // Setting Dialog Message
        alert.setMessage(getResources().getString(R.string.finish_alert_title));

        // Setting Positive "ok" Button
        alert.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            return;
            }
        });
        if (playPause.isChecked())
            alert.show();
       else if (a==0)
        {
                 alert.setMessage(getResources().getString(R.string.not_achieved_task_alert_title));
                 alert.show();
        }
        else {

            calenderEvent.deleteEvent(nameTv.getText().toString());
            long actualDuration = Long.parseLong(MainActivity.durationDB.getDurationDetailsByID(taskID)[3]);
            long expectedDuration = (MainActivity.taskDB.getTaskByID(taskID).getDuration())*60*60*1000;

            MainActivity.taskDB.deleteData(taskID);
            MainActivity.durationDB.deleteData(taskID);
            AlertDialog alertDialog = new Builder(getBaseContext()).create();

            long diff = actualDuration-expectedDuration;

            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);


            if (actualDuration <= expectedDuration){
                String msg=getResources().getString(R.string.Task_achieved_before_time);
                if (diffDays*-1>0)
                    msg=msg+"\n"+getResources().getString(R.string.days)+": "+diffDays*-1;
                if (diffHours*-1>0)
                    msg=msg+"\n"+getResources().getString(R.string.hours)+": "+diffHours*-1;
                if (diffMinutes*-1>0)
                    msg=msg+"\n"+getResources().getString(R.string.minutes)+": "+diffMinutes*-1;

                Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
                TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                v.setTextColor(Color.GREEN);
                toast.show();
            }
              //  Toast.makeText(this, "days: "+diffDays+" hours: "+diffHours+" Min: "+diffMinutes , Toast.LENGTH_SHORT).show();
             //   Toast.makeText(this, getResources().getString(R.string.Task_achieved_on_time), Toast.LENGTH_LONG).show();
            else {

             String msg=getResources().getString(R.string.Task_achieved_with_delay);
             if (diffDays>0)
                 msg=msg+"\n"+getResources().getString(R.string.days)+": "+diffDays;
             if (diffHours>0)
                 msg=msg+"\n"+getResources().getString(R.string.hours)+": "+diffHours;
             if (diffMinutes>0)
                 msg=msg+"\n"+getResources().getString(R.string.minutes)+": "+diffMinutes;

                Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
                TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                v.setTextColor(Color.RED);
                toast.show();
            }
            new LoadViewTask().execute();
            Intent intent = new Intent(view.getContext(), MainActivity.class);
            startActivity(intent);
        }
    }

    public void updateDuration (Boolean isProcessed) throws ParseException {

        String [] details = MainActivity.durationDB.getDurationDetailsByID(taskID);
        String id = details[0];
        String status = details[1];
        String lastStart = details[2];
        int overAll = Integer.parseInt(details[3]);

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date date = new Date();
        String todayDate = formatter.format(date);

        boolean newStatus;
        String newLastStart;
        long newOverAll;

        if (isProcessed){ // to make it in progress
            // lastStart=current dateTime
            newLastStart=todayDate;
            // overAll --> no changes
            newOverAll=overAll;
            // status = true (inProgress)
            newStatus=true;
        }
        else{
            // lastStart=0
            newLastStart="0";
            // overAll = overAll + (currentDate - LastStart)
            Date lastUpdate=formatter.parse(lastStart);
            date=formatter.parse(todayDate);

            long now = date.getTime();
            long last = lastUpdate.getTime();
            long overallnew = now - last;

            newOverAll=overAll+(date.getTime()-lastUpdate.getTime());
            newStatus=false;
        }

        MainActivity.durationDB.updateData(id,newStatus,newLastStart,newOverAll);
    }

    public boolean getStatus(String id){

        String status=MainActivity.durationDB.getDurationDetailsByID(id)[1];
        String b =utils.getPlayPauseString(getResources().getString(R.string.processed));
        if (status.equalsIgnoreCase(b))
            return true;
        return false;
    }

    public boolean isTimeHasCome() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String  taskDate = MainActivity.taskDB.getTaskByID(taskID).getDateTime();
        Date taskDateTime=formatter.parse(taskDate);
        Date date = new Date();
        long now = date.getTime();
        long taskDateInMSec = taskDateTime.getTime();
        boolean isTimeHasCome= now>=taskDateInMSec;
        return isTimeHasCome;
    }

    private class LoadViewTask extends AsyncTask<Void, Integer, Void>
    {
        protected void onPreExecute()
        {
            progressDialog = new ProgressDialog(DisplayTask.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle(getResources().getString(R.string.loading));
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
                    while(counter <= 100)
                    {
                        //Wait 850 milliseconds
                        this.wait(22850);
                        Thread.sleep(10*1000);
                        //Increment the counter
                        counter++;
                        //Set the current progress.
                        //This value is going to be passed to the onProgressUpdate() method.
                        publishProgress(counter*2225);
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
    public String getDisplayerContent() throws ParseException {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date taskDateTime=formatter.parse(curTask.getDateTime());
        DateFormat format2=new SimpleDateFormat("EEEE");
        String finalDay=format2.format(taskDateTime);

        String content="";

        content=getResources().getString(R.string.taskName) +" : "+ curTask.getName()+"\n\n";
        content=content + getResources().getString(R.string.time) +": " +curTask.getDateTime()+" "+finalDay +"\n\n";
        content=content +getResources().getString(R.string.duration) +": " +curTask.getDuration()+"\n\n";
        content=content +getResources().getString(R.string.category) +": "+ utils.getCategoryAsString(curTask.getCategory(),getResources())+"\n\n";
        content=content +getResources().getString(R.string.description) +": \n" +curTask.getDescription()+"\n\n";

       return content;
    }

}








