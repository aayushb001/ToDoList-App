package com.mycompany.aayushb.todolist;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase notesDatabase;
    EditText inputTextBox;
    ListView toDoTaskList;
    private String selectedForDeletion;

    public void createNote(View view){

       try {
           String noteToAdd;
           noteToAdd = inputTextBox.getText().toString();
           if(noteToAdd.isEmpty()) {
               Toast.makeText(getApplicationContext(), "No task to add", Toast.LENGTH_SHORT).show();
           } else{
               notesDatabase.execSQL("INSERT INTO myToDoList (notes) VALUES ('" + noteToAdd + "')"); //adding data to database
               inputTextBox.setText("");
           }
           showNotes();

       } catch(Exception e){

       }
    }

    public void showNotes(){

        ArrayList<String> myTaskList = new ArrayList<String>();
        myTaskList.clear();

        try {
            Cursor c = notesDatabase.rawQuery("SELECT * FROM myToDoList", null);
            int notesIndex = c.getColumnIndex("notes");
            c.moveToFirst();
            while (c != null) {
                myTaskList.add(c.getString(notesIndex));
                c.moveToNext();
            }
        }catch (Exception e){

        }
            ArrayAdapter taskListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, myTaskList);
            toDoTaskList.setAdapter(taskListAdapter);

    }

    public void deleteSelectedItem(String selectedTask){

        selectedForDeletion = selectedTask;

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        notesDatabase.execSQL("DELETE FROM myToDoList WHERE id IN (SELECT id FROM myToDoList WHERE notes = '" + selectedForDeletion + "' LIMIT 1)");
                        showNotes();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.getSupportActionBar().hide(); //hides the action bar
        setContentView(R.layout.activity_main);
        notesDatabase = this.openOrCreateDatabase("ToDoList", MODE_PRIVATE, null);
        notesDatabase.execSQL("CREATE TABLE IF NOT EXISTS myToDoList (notes VARCHAR, id INTEGER PRIMARY KEY)"); //creating a table
        inputTextBox = (EditText) findViewById(R.id.noteInputText);
        toDoTaskList = (ListView) findViewById(R.id.tasksList);
        showNotes();

        toDoTaskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedTask = (toDoTaskList.getItemAtPosition(position)).toString();
                deleteSelectedItem(selectedTask);
                showNotes();
            }
        });

    }
}
