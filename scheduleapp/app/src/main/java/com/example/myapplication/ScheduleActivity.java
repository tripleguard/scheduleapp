package com.example.myapplication;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ScheduleActivity extends AppCompatActivity {
    private ListView fruitsList;
    private ProgressDialog dialog;
    private String groupCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_main);

        fruitsList = findViewById(R.id.fruitsList);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Загрузка...");
        dialog.show();

        groupCode = getIntent().getStringExtra("GROUP_CODE");
        String url = "https://lk2.stgau.ru/api/Rasp?idGroup=" + groupCode;

        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String string) {
                parseJsonData(string);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "Произошла ошибка!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(ScheduleActivity.this);
        rQueue.add(request);
    }

    private void parseJsonData(String jsonString) {
        try {
            Calendar calendar = Calendar.getInstance();
            Date today = calendar.getTime();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String currentDate = dateFormat.format(today);
            JSONObject object = new JSONObject(jsonString);
            JSONArray raspArray = object.getJSONObject("data").getJSONArray("rasp");
            ArrayList<String> scheduleList = new ArrayList<>();
            String groupName = raspArray.getJSONObject(0).getString("группа");
            scheduleList.add("Группа: " + groupName);
            scheduleList.add(currentDate);
            for (int i = 0; i < raspArray.length(); ++i) {
                JSONObject lesson = raspArray.getJSONObject(i);
                String date = lesson.getString("дата").split("T")[0];
                String dayOfWeek = lesson.getString("день_недели");
                String startTime = lesson.getString("начало");
                String endTime = lesson.getString("конец");
                String discipline = lesson.getString("дисциплина");
                String teacher = lesson.getString("преподаватель");
                String classroom = lesson.getString("аудитория");

                if (date.compareTo(currentDate) >= 0) {
                    if (!date.equals(currentDate)) {
                        scheduleList.add("\n" + date + " - " + dayOfWeek);
                        currentDate = date;
                    }

                    String scheduleItem = "Время: " + startTime + " - " + endTime + "\n" +
                            "Пара: " + discipline + "\nПреподаватель: " + teacher + "\nАудитория: " + classroom;
                    scheduleList.add(scheduleItem);
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, scheduleList);
            fruitsList.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        dialog.dismiss();
    }
}
