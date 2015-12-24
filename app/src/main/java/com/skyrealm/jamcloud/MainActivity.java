package com.skyrealm.jamcloud;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

import org.apache.http.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PlayerNotificationCallback, ConnectionStateCallback {
    android.support.v7.app.AlertDialog alert3;


    Button partycloud, syncplay, searchSongButton;
    JSONParser jsonParser = new JSONParser();
    String Trackname, Artist, albpic, trackid = "50kpGaPAhYJ3sGmk6vplg0";
    TextView Artistname, Tracker, leftcurrent, rightduration;
    String response= "";
    ImageButton playpause;
    ImageView albumpic;
    AlertDialog popup_search_song, popup_search_result;
    SeekBar progress;
    PlayerState playerstate;
    private int duration, currentspot;
    int progressTime;
    send_token_to_server send_token_to_server;
    SharedPreferences sharedPreferences;
    String user = "rockyfish";
    private BroadcastReceiver resumeReceiver, pauseReceiver;
    IntentFilter resumeFilter, pauseFilter;

    int playbut = 1;
    private static final int REQUEST_CODE = 1337;
    private String token;
    // TODO: Replace with your client ID
    private static final String CLIENT_ID = "dbeb02d1215d4d8fa5ceb552461f95de";
    // TODO: Replace with your redirect URI
    private static final String REDIRECT_URI = "jamcloud://callback";
    private String GetURL;
    ArrayList<ArrayList<String>> all_track_info;
    Timer t;
    int seekBarCounter;
    String track_num, pauseTime;


    Player mPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        playpause = (ImageButton) findViewById(R.id.playpause);
        Tracker = (TextView) findViewById(R.id.trackname);
        Artistname = (TextView) findViewById(R.id.Artist);
        albumpic = (ImageView) findViewById(R.id.Artistpicture);
        progress = (SeekBar) findViewById(R.id.seekBar);
        searchSongButton = (Button) findViewById(R.id.searchSongButton);
        int color = Color.parseColor("#ffffff");
        sharedPreferences = this.getSharedPreferences("GCM", MODE_PRIVATE);
        resumeFilter = new IntentFilter("PLAY");
        pauseFilter = new IntentFilter("PAUSE");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        resumeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(mPlayer != null)
                {
                    mPlayer.resume();
                }
            }
        };

        pauseReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mPlayer.pause();
            }
        };

        fab.setColorFilter(color);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
                android.support.v7.app.AlertDialog.Builder build = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
                View promptView = layoutInflater.inflate(R.layout.pupup, null);
                syncplay = (Button) promptView.findViewById(R.id.syncplay);
                partycloud = (Button) promptView.findViewById(R.id.party);


                syncplay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert3.dismiss();
                    }

                });
                partycloud.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert3.dismiss();
                    }

                });

                build.setView(promptView);
                alert3 = build.create();
                alert3.show();


            }
        });


        searchSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater searchInflater = LayoutInflater.from(MainActivity.this);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View view = searchInflater.inflate(R.layout.popup_search_song, null);
                builder.setView(view);
                popup_search_song = builder.create();
                popup_search_song.show();
                Button searchButton = (Button) view.findViewById(R.id.searchButton);
                final EditText songEditText = (EditText) view.findViewById(R.id.songEditText);
                searchButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        search_song search = new search_song();
                        search.execute(songEditText.getText().toString());
                    }
                });
            }
        });


        playpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playbut == 1) {
                    playpause.setImageResource(R.drawable.Play_icon);
                    playbut = 0;
                    mPlayer.pause();
                } else {
                    playpause.setImageResource(R.drawable.Pause_icon);
                    playbut = 1;
                    mPlayer.resume();
                }


            }
        });



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(MainActivity.this, REQUEST_CODE, request);

        progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {



            @Override
            public void onProgressChanged(SeekBar seekBar, final int progresValue, boolean fromUser) {
                mPlayer.seekToPosition(progresValue);
                seekBarCounter = progresValue / 1000;
                seekBarCounter++;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                progress.setMax(duration);

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        registerReceiver(resumeReceiver, resumeFilter);
        registerReceiver(pauseReceiver, pauseFilter);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        unregisterReceiver(pauseReceiver);
        unregisterReceiver(resumeReceiver);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public Player set_player()
    {
        return mPlayer;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_share) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                token = response.getAccessToken();
                Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
                    @Override
                    public void onInitialized(Player player) {
                        mPlayer = player;
                        mPlayer.addConnectionStateCallback(MainActivity.this);
                        mPlayer.addPlayerNotificationCallback(MainActivity.this);

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }






    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");
        new register_in_background().execute();
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable error) {
        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
        if(eventType.equals(EventType.PLAY)) {
            t = new Timer();
            progress.setMax(0);
            progress.setMax(playerState.durationInMs);

            // This will trigger itself every one second.
            t.schedule(new TimerTask() {
                @Override
                //turn the position into seconds and multiply the counter
                public void run() {
                    progress.setProgress(seekBarCounter * 1000);
                    if(seekBarCounter == 0)
                    {
                        seekBarCounter++;
                    }
                }
            }, 0, 1000);

            new send_song_info_to_server().execute(user, track_num, String.valueOf(playerState.positionInMs), "PLAY");
        } else if (eventType.equals(EventType.PAUSE))
        {
            send_song_info_to_server send_song_info_to_server = new send_song_info_to_server();
            send_song_info_to_server.execute(user, track_num, String.valueOf(playerState.positionInMs), "PAUSE");
            t.cancel();
        } else if (eventType.equals(EventType.TRACK_END))
        {
            seekBarCounter = 0;
            t.cancel();
        }
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String errorDetails) {
        Log.d("MainActivity", "Playback error received: " + errorType.name());
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    class Getinfo extends AsyncTask<String, String, String> {
        String tracknum = null;
        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // here Check for success tag
            trackid = args[0].replace("spotify:track:", "");

            String Forgot_URL = "https://api.spotify.com/v1/tracks/" + trackid;
            tracknum = args[0];


            try {

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("Accept", "application/json"));
                params.add(new BasicNameValuePair("Authorization Bearer ", token));

                JSONObject json = jsonParser.makeHttpRequest(
                        Forgot_URL, "GET", params);
                Trackname = json.getString("name");
                Artist = json.getJSONArray("artists").getJSONObject(0).getString("name");
                albpic = json.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url");
                duration = json.getInt("duration_ms");
                return json.getString("duration_ms");



            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
        protected void onPostExecute(String message) {
            if (message != null) {
                Artistname.setText(Artist);
                Tracker.setText(Trackname);
                new ImageLoadTask(albpic, albumpic).execute();

                //play the track
                mPlayer.play(tracknum);
                seekBarCounter = 0;
                progressTime = 0;
                //set the playpause button to show the pause icon
                playpause.setImageResource(R.drawable.Pause_icon);

            }else{
                Toast.makeText(MainActivity.this, "Return Null", Toast.LENGTH_LONG).show();
            }
        }

        public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

            private String url;
            private ImageView imageView;

            public ImageLoadTask(String url, ImageView imageView) {
                this.url = url;
                this.imageView = imageView;
            }

            @Override
            protected Bitmap doInBackground(Void... params) {
                try {
                    URL urlConnection = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) urlConnection
                            .openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(input);
                    return myBitmap;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                imageView.setImageBitmap(result);
            }

        }

    }

    //Class made to search for the song and populate a listview
    class search_song extends AsyncTask<String, Void, String>
    {

        JSONArray jsonArray = null;
        @Override
        protected String doInBackground(String... params) {
            params[0] = params[0].replace(" ", "+");
            String url = "http://ws.spotify.com/search/1/track.json?q=" + params[0];
            org.apache.http.HttpResponse response = null;
            String responseBody = null;
            HttpClient httpClient = new DefaultHttpClient();

            HttpGet httpPost = new HttpGet(url);
            try {
                response = httpClient.execute(httpPost);
                responseBody = EntityUtils.toString(response.getEntity());
                try {
                    all_track_info = new ArrayList<ArrayList<String>>();
                    JSONObject jsonObject = new JSONObject(responseBody);
                    JSONArray object = jsonObject.getJSONArray("tracks");
                    for(int i = 0; i < object.length(); i++)
                    {
                        ArrayList<String> temp = new ArrayList<String>();
                        //get the track number
                        String track_num = object.getJSONObject(i).getString("href");
                        //get the track name
                        String track_name = object.getJSONObject(i).getString("name");
                        JSONArray artists = object.getJSONObject(i).getJSONArray("artists");
                        String artist = artists.getJSONObject(0).getString("name");
                        temp.add(track_name);
                        temp.add(track_num);
                        temp.add(artist);
                        all_track_info.add(temp);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // writing response to log
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result)
        {
            SearchSongAdapter adapter = new SearchSongAdapter(MainActivity.this, all_track_info);
            //popup inflater for results
            LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View view = layoutInflater.inflate(R.layout.popup_search_result, null);
            builder.setView(view);
            popup_search_result = builder.create();
            popup_search_result.show();

            // get listview and set the adapter
            ListView listView = (ListView) view.findViewById(R.id.songListView);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                //on item click, call get_info and start the track
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    track_num = all_track_info.get(position).get(1);
                    new Getinfo().execute(track_num);
                    popup_search_song.dismiss();
                    popup_search_result.dismiss();
                }
            });
        }
    }
    class register_in_background extends AsyncTask<Void, Void, String>
    {

        @Override
        protected String doInBackground(Void... params) {
            String token = null;
            try {
                // [START register_for_gcm]
                // Initially this call goes out to the network to retrieve the token, subsequent calls
                // are local.
                // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
                // See https://developers.google.com/cloud-messaging/android/start for details on this file.
                // [START get_token]
                InstanceID instanceID = InstanceID.getInstance(MainActivity.this);
                token = instanceID.getToken("478628419848",
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                // [END get_token]
                Log.d("token","GCM Registration Token: " + token);


                // You should store a boolean that indicates whether the generated token has been
                // senrest to your server. If the boolean is false, send the token to your server,
                // otherwise your server should have already received the token.
                // [END register_for_gcm]
            } catch (Exception e) {
                Log.d("Tag", "Failed to complete token refresh", e);
                // If an exception happens while fetching the new token or updating our registration data
                // on a third-party server, this ensures that we'll attempt the update at a later time.
            }

            return token;
        }

        public void onPostExecute(String result)
        {
            Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
            send_token_to_server = new send_token_to_server();
            send_token_to_server.execute(result);
        }
    }

    class send_token_to_server extends AsyncTask<String, Void, String>
    {
        String token = null;
        String responseBody = null;

        @Override
        protected String doInBackground(String... params) {
            token = params[0];
            HttpResponse response;
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost("Http://www.skyrealmstudio.com/cgi-bin/JamCloud/SendToken.py");

            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            nameValuePair.add(new BasicNameValuePair("Username", user));
            nameValuePair.add(new BasicNameValuePair("Token", token));


            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                response = httpClient.execute(httpPost);
                responseBody = EntityUtils.toString(response.getEntity());
                // writing response to log
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseBody;
        }

        public void onPostExecute(String result)
        {
            Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
            sharedPreferences.edit().putString("Token", token).apply();
        }
    }

    class send_song_info_to_server extends AsyncTask<String, Void, String> {
        String type;

        @Override
        protected String doInBackground(String... params) {
            HttpResponse response;
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost("http://www.skyrealmstudio.com/cgi-bin/JamCloud/send_song_info_to_server.py");

            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            nameValuePair.add(new BasicNameValuePair("Username", params[0]));
            nameValuePair.add(new BasicNameValuePair("Track_Num", params[1]));
            nameValuePair.add(new BasicNameValuePair("Current_Position", params[2]));

            type = params[3];

            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                response = httpClient.execute(httpPost);

                // writing response to log
            } catch (IOException e) {
                e.printStackTrace();
            }
            return type;
        }

        protected void onPostExecute(String result) {
            if (result.equals("PLAY")) {

            } else if (result.equals("PAUSE")) {

            }
        }
    }

}
