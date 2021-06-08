package com.example.searchengine;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.health.SystemHealthManager;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;

    private RecyclerView recyclerView;
    private TableAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<CellItem> urlList;
    private ArrayList<CellItem> currentScreen;
    private RequestQueue mQueue;

    private Spinner dropdown;
    private AutoCompleteTextView editText;
    private ImageView voiceButton;

    private String baseURL = "http://10.0.2.2:5000/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mQueue = Volley.newRequestQueue(this);
        urlList = new ArrayList<>();
        currentScreen = new ArrayList<>();
        voiceButton = findViewById(R.id.search_voice_btn);

        editText = findViewById(R.id.actv);
        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        mAdapter = new TableAdapter(currentScreen);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);


        voiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.EXTRA_LANGUAGE_MODEL);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");

                try {
                    startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
                }
                catch (Exception e) {
                    //Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

//        @Override
//        onActivityResult(int requestCode, int resultCode, );


        mAdapter.setOnItemClickListener(new TableAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) { // goes to new URL
                if(!currentScreen.get(position).getUrl().equals(""))
                {
                    System.out.println("Hi there " + currentScreen.get(position).getUrl());
                    Intent intent = new Intent(MainActivity.this, webPage.class);
                    intent.putExtra("URL", currentScreen.get(position).getUrl() );
                    startActivity(intent);
                }
            }
        });

        //get the spinner from the xml.
        dropdown = findViewById(R.id.spinner1);
        String[] items = new String[]{"1", "2", "3"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //dropdown.setAdapter(adapter2);


        final Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Log.e("TAG", "Filling table");
                editText.clearFocus();
                String word = editText.getText().toString();
                queryWord(word);
                hideKeyboard();
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                System.out.println(editText.getText().toString());
                    //query for suggesting
                suggestingWords(editText.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    Log.e("TAG","Done pressed");
                }
                return false;
            }
        });

        editText.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                hideKeyboard();
                editText.clearFocus();
            }

        });

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                System.out.println("CLICKED , " + position);
                currentScreen.clear();
                int j=0;
                for(int i= position*10; i<urlList.size(); i++)
                {
                    currentScreen.add(urlList.get(i));
                    j++;
                    if(j==10)
                        break;;
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

        }
    }

    private void suggestingWords(final String word) {
        String url = baseURL + "suggest/" + word;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ArrayList<String> words = new ArrayList<>();
                    JSONArray jsonArray = response.getJSONArray("results");
                    for (int i=0; i<jsonArray.length(); i++) {
                        JSONObject cell = jsonArray.getJSONObject(i);
                        words.add(cell.getString("word"));
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, words);
                    editText.setAdapter(adapter);
                    System.out.println("adapter is" + words);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                urlList.add(new CellItem("No results found", "", ""));
//                mAdapter.notifyDataSetChanged();
                error.printStackTrace();
            }
        });

        mQueue.add(request);
    }

    private void queryWord(String word) {
        final String url = baseURL + word;


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    urlList.clear();
                    currentScreen.clear();
                    System.out.println("HELLO" + response.get("data").getClass().getName());
                    ArrayList<String> numbers = new ArrayList<>();
                    if(response.get("data").getClass().getName().equals("org.json.JSONArray"))
                    {
                        //System.out.println(response);
                        JSONArray jsonArray = response.getJSONArray("data");
                        int k = 1;
                        for (int i=0; i<jsonArray.length(); i++) {
                            JSONObject cell = jsonArray.getJSONObject(i);
                            urlList.add(new CellItem(cell.getString("title"), cell.getString("url"), cell.getString("description")));
//                            if(i<10)
//                            {
//                                currentScreen.add(new CellItem(cell.getString("title"), cell.getString("url"), cell.getString("description")));
//                            }
                            //update drop down list
                            if(i%10 == 0)
                            {
                                numbers.add(Integer.toString(k));
                                k++;
                            }

                        }
                        //update drop down list
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, numbers);
                        dropdown.setAdapter(adapter);
                    }
                    else if (response.get("data").getClass().getName().equals("org.json.JSONObject"))
                    {
                        JSONObject cell = response.getJSONObject("data");
                        urlList.add(new CellItem(cell.getString("title"), cell.getString("url"), cell.getString("description")));
                        //currentScreen.add(new CellItem(cell.getString("title"), cell.getString("url"), cell.getString("description")));
                        //update drop down list
                        numbers.add("1");
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, numbers);
                        dropdown.setAdapter(adapter);

                    }

                    //mAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //currentScreen.clear();
                urlList.clear();
                urlList.add(new CellItem("No results found", "", ""));
                ArrayList<String> numbers = new ArrayList<>();
                numbers.add("");
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, numbers);
                dropdown.setAdapter(adapter);
                //mAdapter.notifyDataSetChanged();
                error.printStackTrace();
            }
        });

        mQueue.add(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_SPEECH_INPUT: {
                if(resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    editText.setText(result.get(0));
                }
                break;
            }
        }
    }
}
