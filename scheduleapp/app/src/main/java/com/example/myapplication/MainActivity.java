package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText groupCodeEditText;
    private Button loadScheduleButton;
    private EditText notesEditText;
    private Button saveButton;

    private static final String PREFS_NAME = "MyPrefs";
    private static final String NOTES_KEY = "notes";
    private static final String GROUP_CODE_KEY = "GROUP_CODE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        groupCodeEditText = findViewById(R.id.groupCodeEditText);
        loadScheduleButton = findViewById(R.id.loadScheduleButton);
        notesEditText = findViewById(R.id.notesEditText);
        saveButton = findViewById(R.id.saveButton);

        groupCodeEditText.setText(loadGroupCode());

        notesEditText.setText(loadNotes());

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String notes = notesEditText.getText().toString();
                saveNotes(notes);
            }
        });

        loadScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupCode = groupCodeEditText.getText().toString().trim();

                if (!TextUtils.isEmpty(groupCode) && groupCode.matches("\\d+")) {
                    saveGroupCode(groupCode);

                    String url = "https://lk2.stgau.ru/api/Rasp?idGroup=" + groupCode;

                    Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
                    intent.putExtra(GROUP_CODE_KEY, groupCode);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Введите корректный код группы", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveNotes(String notes) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(NOTES_KEY, notes);
        editor.apply();
    }

    private String loadNotes() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(NOTES_KEY, "");
    }

    private void saveGroupCode(String groupCode) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(GROUP_CODE_KEY, groupCode);
        editor.apply();
    }

    private String loadGroupCode() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(GROUP_CODE_KEY, "");
    }
}
