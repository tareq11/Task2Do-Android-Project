package com.example.tareq.tasks2do;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Mitch on 2016-05-13.
 */
public  class TasksViewer extends AppCompatActivity {
    CalenderEvent calenderEvent;
    TaskDatabase taskDB;
    DurationDatabase durationDB;
    protected List<String> listOfNames= new ArrayList<>();
    protected List<Integer> listOfIDs= new ArrayList<>();
    protected  ListView listView;
    protected CategoryEnum currentCategory;
    protected List<Task> theList;
    protected CategoryEnum category;
    protected TextView title;
    protected int pos=0;
    protected Utils utils;
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
        utils= new Utils();
        setContentView(R.layout.tasks_veiwer);
        category = CategoryEnum.valueOf(getIntent().getStringExtra("Category").toUpperCase());
        title = (TextView) findViewById(R.id.title_task_viewer);
      //  title.setText(title.getText()+" "+ category.getCategory());
        title.setText(title.getText()+" "+  utils.getCategoryAsString(category,getResources()));

        listView = (ListView) findViewById(R.id.listView);
        calenderEvent=new CalenderEvent(getContentResolver(),this);
        taskDB = new TaskDatabase(this);
        durationDB = new DurationDatabase(this);
        theList = taskDB.getAllTasksListByCategory(category);
        showTasks(theList);

    }

    protected void showTasks(final List<Task> theList) {
        SwipeMenuListView listView = (SwipeMenuListView) findViewById(R.id.listView); ;
        if(theList.size() == 0){
            Toast.makeText(this, getResources().getString(R.string.There_are_no_contents_in_this_list),Toast.LENGTH_LONG).show();
        }else{

            for(Task task : theList) {
                listOfNames.add(task.getName());
                listOfIDs.add(task.getId());
            }
                //   theList.add(taskDB.getString(1));
                ListAdapter listAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,listOfNames);
                listView.setAdapter(listAdapter);
                SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" add
                SwipeMenuItem openItem = new SwipeMenuItem( getApplicationContext());
                // set add background
                openItem.setBackground(new ColorDrawable(Color.rgb(0x2e, 0x66, 0xC1)));
                // set add width
                openItem.setWidth(300);
                // set add title
                openItem.setTitle(getResources().getString(R.string.open));
                // set add title fontsize
                openItem.setTitleSize(18);
                // set add title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" add
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set add background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xce, 0x1e, 0x1e)));
                // set add width
                deleteItem.setWidth(300);
                // set add title
                deleteItem.setTitle(getResources().getString(R.string.delete));
                // set add title fontsize
                deleteItem.setTitleSize(18);
                // set add title font color
                deleteItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
            listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                    switch (index) {
                        case 0:// on open
                            Intent intent = new Intent(TasksViewer.this,DisplayTask.class);
                            intent.putExtra("TASK_ID", listOfIDs.get(position).toString());

                            startActivity(intent);
                            break;
                        case 1:// on delete
                            pos=position;
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(TasksViewer.this);

                            // Setting Dialog Title
                            alertDialog.setTitle(getResources().getString(R.string.delete_alert_title));

                            // Setting Dialog Message
                            alertDialog.setMessage(getResources().getString(R.string.delete_alert_text));

                            // Setting Positive "Yes" Button
                            alertDialog.setPositiveButton(getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // User pressed YES button. Write Logic Here
                                    taskDB.deleteData(listOfIDs.get(pos).toString());
                                    calenderEvent.deleteEvent(listOfNames.get(pos)+"");
                                    taskDB.deleteData(listOfIDs.get(pos).toString());
                                    calenderEvent.deleteEvent(listOfNames.get(pos)+"");
                                    finish();
                                    startActivity(getIntent());
                                }
                            });

                            // Setting Negative "NO" Button
                            alertDialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            alertDialog.show();

                            break;
                    }

                    // false : close the menu; true : not close the menu
                    return false;
                }
            });
            listView.setMenuCreator(creator);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    }
                });

        }
    }


}

