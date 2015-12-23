package com.skyrealm.jamcloud;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.PlayerStateCallback;
import com.spotify.sdk.android.player.Spotify;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PlayerNotificationCallback, ConnectionStateCallback {
    android.support.v7.app.AlertDialog alert3;


    Button partycloud, syncplay;
    JSONParser jsonParser = new JSONParser();
    String Trackname, Artist, albpic, trackid = "50kpGaPAhYJ3sGmk6vplg0";
    TextView Artistname, Tracker, leftcurrent, rightduration;
    String response= "";
    ImageButton playpause;
    ImageView albumpic;
    SeekBar progress;
    private int duration, currentspot;
    int playbut = 1;
    private static final int REQUEST_CODE = 1337;
    private String token;
    // TODO: Replace with your client ID
    private static final String CLIENT_ID = "dbeb02d1215d4d8fa5ceb552461f95de";
    // TODO: Replace with your redirect URI
    private static final String REDIRECT_URI = "jamcloud://callback";
    private String GetURL;


    private Player mPlayer;
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
        int color = Color.parseColor("#ffffff");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

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



        playpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playbut == 1) {
                    playpause.setImageResource(R.drawable.Play_icon);
                    playbut = 0;
                    new Getinfo().execute();
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

        new Getinfo().execute();
        progress.setMax(duration);
        int test = duration;

        progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            int prog = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                prog = progresValue;
                mPlayer.seekToPosition(prog);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                new Getinfo().execute();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
                        mPlayer.play("spotify:track:" + trackid);
                        new Getinfo().execute();


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
        Log.d("MainActivity", "Playback event received: " + eventType.name());
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
        String Forgot_URL = "https://api.spotify.com/v1/tracks/" + trackid;
        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // here Check for success tag



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




}
