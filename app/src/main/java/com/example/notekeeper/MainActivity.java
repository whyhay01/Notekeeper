package com.example.notekeeper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int POSITION_NOT_SET = -1;
    public static final String TAG = MainActivity.class.getSimpleName();

    public static final String ORIGINAL_NOTE_COURSE_ID = "ORIGINAL_NOTE_COURSE_ID";
    public static final String ORIGINAL_NOTE_TEXT = "ORIGINAL_NOTE_TEXT";
    public static final String ORIGINAL_NOTE_TITLE = "ORIGINAL_NOTE_TITLE";

    private Spinner spinnerCourses;
    private EditText mNoteTitle;
    private EditText mNoteText;
    private List<CourseInfo> courses;
    CourseInfo courseInfo;
    private NoteInfo mNotes;
    final static String NOTE_POSITION = "NOTE_POSITION";
    private boolean mIsNewNote;
    private DataManager dataManager;
    private int mNotePosition;
    private boolean mIsCanceling;
    private String mOriginalCourseId;
    private String mOriginalNoteTitle;
    private String mOriginalNoteText;

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Requesting view reference

        spinnerCourses = findViewById(R.id.spinner_courses);
        mNoteTitle = findViewById(R.id.etNoteTitle);
        mNoteText = findViewById(R.id.etNoteText);

        courses = DataManager.getInstance().getCourses();

        //populating the spinner

        ArrayAdapter<CourseInfo> adapterCourses = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, courses);

        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerCourses.setAdapter(adapterCourses);


        //Getting the Intent passed from ListActivity class

            readDisplayStateValue();

//            if (savedInstanceState == null) {
//                saveOriginalNoteValue();
//            }else
////            {
////                restoreOriginalNoteValue(savedInstanceState);
////                Log.d(TAG,"This is the value in the NOTE_COURSE_ID ==>" +ORIGINAL_NOTE_COURSE_ID);
////                Log.d(TAG,"This is the value in the ORIGINAL_NOTE_TEXT ==>" +ORIGINAL_NOTE_TEXT);
////                Log.d(TAG,"This is the value in the ORIGINAL_NOTE_TITLE ==>" +ORIGINAL_NOTE_TITLE);
////            }
        if (!mIsNewNote) {
            displayNote(spinnerCourses, mNoteText, mNoteTitle);
            saveOriginalNoteValue();
        }
    }

//    private void restoreOriginalNoteValue(Bundle savedInstanceState) {
//        mOriginalCourseId = savedInstanceState.getString(ORIGINAL_NOTE_COURSE_ID);
//        mOriginalNoteText = savedInstanceState.getString(ORIGINAL_NOTE_TEXT);
//        mOriginalNoteTitle = savedInstanceState.getString(ORIGINAL_NOTE_TITLE);
//    }

//    @Override
//    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//
//        savedInstanceState.putString("ORIGINAL_NOTE_COURSE_ID",mNotes.getCourse().getCourseId());
////        savedInstanceState.putString(ORIGINAL_NOTE_COURSE_ID, mNotes.getCourse().getCourseId() );
//        savedInstanceState.putString("ORIGINAL_NOTE_COURSE_ID",mOriginalCourseId);
//        savedInstanceState.putString("ORIGINAL_NOTE_TEXT",mOriginalNoteText);
//        savedInstanceState.putString("ORIGINAL_NOTE_TITLE",mOriginalNoteTitle);
//
//    }

    private void saveOriginalNoteValue() {
        if (mIsNewNote)
            return;
        mOriginalCourseId = mNotes.getCourse().getCourseId();
        mOriginalNoteTitle = mNotes.getTitle();
        mOriginalNoteText = mNotes.getText();

    }

    private void displayNote(Spinner spinnerCourses, EditText mNoteText, EditText mNoteTitle) {
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        int courseIndex = courses.indexOf(mNotes.getCourse());
        spinnerCourses.setSelection(courseIndex);
        mNoteTitle.setText(mNotes.getTitle());
        mNoteText.setText(mNotes.getText());
    }

    private void readDisplayStateValue() {
        Intent intent = getIntent();
        int position = intent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET);

        mIsNewNote= position == POSITION_NOT_SET;

        if (mIsNewNote) {

            createNewNote();
        }else {

            mNotes = DataManager.getInstance().getNotes().get(position);
        }
    }

    private void createNewNote() {
        dataManager = DataManager.getInstance();
        mNotePosition = dataManager.createNewNote();
        mNotes = dataManager.getNotes().get(mNotePosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.send_note:
                CourseInfo course = (CourseInfo) spinnerCourses.getSelectedItem();
                String subject = mNoteTitle.getText().toString();
                String text = "Check what i learned in the pluralsight course \"" + course.getTitle()
                        + "\"\n" + mNoteText.getText().toString();

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc2822");
                intent.putExtra(Intent.EXTRA_SUBJECT,subject);
                intent.putExtra(Intent.EXTRA_TEXT,text);
                startActivity(intent);
                return true;

            case R.id.action_cancel:
                mIsCanceling = true;
                finish();


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mIsCanceling){
            if (mIsNewNote){
                dataManager.getInstance().removeNote(mNotePosition);
            }else{
                storePreviousNoteValue();
            }

        }else {
            saveNote();
        }
    }

    private void storePreviousNoteValue() {
        CourseInfo courseInfo = dataManager.getInstance().getCourse(mOriginalCourseId);
        mNotes.setCourse(courseInfo);
        mNotes.setText(mOriginalNoteText);
        mNotes.setTitle(mOriginalNoteTitle);
    }

    private void saveNote() {
        mNotes.setCourse((CourseInfo) spinnerCourses.getSelectedItem());
        mNotes.setTitle(mNoteTitle.getText().toString());
        mNotes.setText(mNoteText.getText().toString());
    }
}