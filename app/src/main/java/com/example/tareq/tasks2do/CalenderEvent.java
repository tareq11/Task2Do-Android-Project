package com.example.tareq.tasks2do;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.text.ParseException;
import java.util.Calendar;

/**
 * Created by Tareq on 1/5/2018.
 */

public class CalenderEvent {
    ContentResolver contentResolver;
    Activity activity;

    public CalenderEvent(ContentResolver contentResolver, Activity activity) {
        this.contentResolver = contentResolver;
        this.activity = activity;
    }

    public void deleteEvent(String eventId) {

        int id = ListSelectedCalendars(eventId);
        Uri deleteUri = null;
        deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, Long.parseLong(String.valueOf(id)));
        int rows = contentResolver.delete(deleteUri, null, null);
    }


    private int ListSelectedCalendars(String eventtitle) {


        Uri eventUri;
        if (android.os.Build.VERSION.SDK_INT <= 7) {
            // the old way

            eventUri = Uri.parse("content://calendar/events");
        } else {
            // the new way

            eventUri = Uri.parse("content://com.android.calendar/events");
        }

        int result = 0;
        String projection[] = {"_id", "title"};
        Cursor cursor = contentResolver.query(eventUri, null, null, null,
                null);

        if (cursor.moveToFirst()) {

            String calName;
            String calID;

            int nameCol = cursor.getColumnIndex(projection[1]);
            int idCol = cursor.getColumnIndex(projection[0]);
            do {
                calName = cursor.getString(nameCol);
                calID = cursor.getString(idCol);

                if (calName != null && calName.contains(eventtitle)) {
                    result = Integer.parseInt(calID);
                }

            } while (cursor.moveToNext());
            cursor.close();
        }

        return result;

    }

    public void addEvent(Task task,String a) throws ParseException {

        long calID = 3;
        long startMillis = 0;
        long endMillis = 0;
        String dateTime = task.getDateTime();
        dateTime = dateTime.replaceAll("[^\\d.]", ",");
        String[] date = dateTime.split(",");

        int day = Integer.parseInt(date[0]);
        int month = Integer.parseInt(date[1]);
        int year = Integer.parseInt(date[2]);
        int hour = Integer.parseInt(date[3]);
        int minute = Integer.parseInt(date[4]);
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(year, month - 1, day, hour, minute);
        startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();

        endMillis = startMillis + task.getDuration() * 60 * 60 * 1000;


        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, task.getName());
        values.put(CalendarContract.Events.DESCRIPTION, task.getCategory().getCategory() + " task: " + task.getDescription());
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Jerusalem");
        values.put(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, "3");

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR}, 123);


        }
        Uri uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values);

// get the event ID that is the last element in the Uri
        long eventID = Long.parseLong(uri.getLastPathSegment());
        //     ObjectAnimator.ofFloat(this, "alpha", 0f).start();
        Uri   deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, Long.parseLong(String.valueOf(eventID)));
    }

    public void addEvent(Task task) {


        long calID = 3;
        long startMillis = 0;
        long endMillis = 0;
        String dateTime = task.getDateTime();
        dateTime = dateTime.replaceAll("[^\\d.]", ",");
        String[] date = dateTime.split(",");

        int day = Integer.parseInt(date[0]);
        int month = Integer.parseInt(date[1]);
        int year = Integer.parseInt(date[2]);
        int hour = Integer.parseInt(date[3]);
        int minute = Integer.parseInt(date[4]);


        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR}, 123);

            ContentResolver cr = contentResolver;
            ContentValues contentValues = new ContentValues();

            Calendar beginTime = Calendar.getInstance();
            beginTime.set(year, month - 1, day, hour, minute);
            startMillis = beginTime.getTimeInMillis();

            endMillis = startMillis + task.getDuration() * 60 * 60 * 1000;


        String eventUriString = "content://com.android.calendar/events";
        ContentValues eventValues = new ContentValues();

        eventValues.put(CalendarContract.Events.CALENDAR_ID, 1);
        eventValues.put(CalendarContract.Events.TITLE, task.getName());
        eventValues.put(CalendarContract.Events.DESCRIPTION, task.getCategory().getCategory() + " task: " + task.getDescription());
        eventValues.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Jerusalem");
        eventValues.put(CalendarContract.Events.DTSTART, startMillis);
        eventValues.put(CalendarContract.Events.DTEND, endMillis);

        //eventValues.put(Events.RRULE, "FREQ=DAILY;COUNT=2;UNTIL="+endMillis);
        eventValues.put("eventStatus", 1);
//        eventValues.put("visibility", 3);
//        eventValues.put("transparency", 0);
//        eventValues.put(CalendarContract.Events.HAS_ALARM, 1);

        Uri eventUri = contentResolver.insert(Uri.parse(eventUriString), eventValues);
        long eventID = Long.parseLong(eventUri.getLastPathSegment());

        /***************** Event: Reminder(with alert) Adding reminder to event *******************/

        String reminderUriString = "content://com.android.calendar/reminders";

        ContentValues reminderValues = new ContentValues();

        reminderValues.put("event_id", eventID);
        reminderValues.put("minutes", 10);
        reminderValues.put("method", 1);

        Uri reminderUri = contentResolver.insert(Uri.parse(reminderUriString), reminderValues);
    }

}