package com.example.notekeeper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ListActivity extends AppCompatActivity {
    ListView listItem;
    FloatingActionButton fab;
    List<NoteInfo> notes;
    private ArrayAdapter<NoteInfo> mNoteInfoArrayAdapter;

    @Override
    protected void onPostResume() {
        super.onPostResume();
        mNoteInfoArrayAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);



        initializeDisplayContent();
        createNewNote();
    }

    private void initializeDisplayContent() {

        listItem = findViewById(R.id.list_items);
        notes = DataManager.getInstance().getNotes();
        mNoteInfoArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,notes);
        listItem.setAdapter(mNoteInfoArrayAdapter);

        //implement the item click listener
        listItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra(MainActivity.NOTE_POSITION, position);
                view.getContext().startActivity(intent);
            }
        });

    }

    public void createNewNote(){
        fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}