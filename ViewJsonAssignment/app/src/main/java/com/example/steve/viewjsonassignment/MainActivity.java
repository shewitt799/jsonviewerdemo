package com.example.steve.viewjsonassignment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Button mButton;
    private TextView mTextView;
    private ProgressBar mProgressBar;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.textview1);
        mButton = (Button) findViewById(R.id.button1);
        mProgressBar = (ProgressBar) findViewById(R.id.loading_view);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);



        mProgressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);


        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mButton.setVisibility(View.GONE);
                mTextView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);
                loadData();
            }
        });

    }

    private void onData(ServerData serverData) {

        mServerData = serverData;

        mButton.setVisibility(View.GONE);
        mTextView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);

        // setup recycler view with new adapter; better to do elsewhere, like in a new fragment, and also with reusing the adapter, but not much time

        // Requirement 3: Display your objects in a RecyclerView


        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mAdapter);




    }

    private void onError() {
        mButton.setVisibility(View.VISIBLE);
        mTextView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);

    }

    // being lazy just storing it here.
    private ServerData mServerData;

    private static final String EXAMPLE_URL = "https://guidebook.com/service/v2/upcomingGuides/";


    public void loadData() {

        OkHttpClient client = new OkHttpClient();


        Request request = new Request.Builder()
                .url(EXAMPLE_URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    onError();
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onError();
                                Log.e("MainActivity", "Unexpected code: " + response);
                            }
                        });

                    } else {

                        // let's just assume the activity is still around and active for this demo


                        // Requirement 1: Retrieve and print out the data received from the url above.
                        final String s = response.body().string();
                        Log.i("MainActivity", "server response: " + s);

                        // Requirement 2: Parse the data retrieved from the server into a list of Java objects
                        Gson gson = new Gson();
                        final ServerData serverData = gson.fromJson(s, ServerData.class);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (serverData != null) {
                                    onData(serverData);
                                } else {
                                    onError();
                                }

                            }
                        });


                    }
                }
            });

//            Response response = client.newCall(request).execute();
//            String s =  response.body().string();


    }


    public static class EventViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mName;
        public TextView mLine2;
        public ImageView mIcon;


        public EventViewHolder(View v) {
            super(v);
            mIcon = (ImageView) v.findViewById(R.id.event_icon);
            mName = (TextView) v.findViewById(R.id.event_name);
            mLine2 = (TextView) v.findViewById(R.id.event_line2);
        }
    }


    private class MyAdapter extends RecyclerView.Adapter<EventViewHolder> {

        @Override
        public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v =  LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.event_item_view, parent, false);
            EventViewHolder vh = new EventViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(EventViewHolder holder, int position) {

            Event event = mServerData.data.get(position);
            holder.mName.setText(event.name);
            holder.mLine2.setText("Ends " + event.endDate);

            Picasso.with(getApplicationContext())
                    .load(event.icon)
                    .error(R.drawable.ic_android_black_24dp)
                    .placeholder(R.drawable.ic_android_black_24dp)
                    .into(holder.mIcon);

        }

        @Override
        public int getItemCount() {
            return mServerData != null ? mServerData.data.size() : 0;
        }
    }
}
