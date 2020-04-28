package temple.edu;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import edu.temple.audiobookplayer.AudiobookService;

public class MainActivity extends AppCompatActivity implements BookListFragment.BookSelectedInterface, BookDetailsFragment.PlayBookInterface {
    EditText titleSearch;
    ArrayList<Book> books = new ArrayList<>();
    Book book;
    View searchButton;
    RequestQueue requestQueue;
    BookListFragment bookList;
    BookDetailsFragment bookDetails;
    boolean twoViews, connected;
    Intent libIntent;
    AudiobookService.MediaControlBinder playback;
    Handler playing;
    AudiobookService.BookProgress bookProgress;
    int id, progress;

    ServiceConnection libraryConnect = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            connected = true;
            playback = (AudiobookService.MediaControlBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            connected = false;
            playback = null;
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (book != null)
            outState.putSerializable(Keys.BOOK, book);
        if (books != null)
            outState.putSerializable(Keys.LIST, books);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchButton = findViewById(R.id.searchButton);
        twoViews = (findViewById(R.id.container2) != null);
        titleSearch = findViewById(R.id.titleSearch);
        requestQueue = Volley.newRequestQueue(this);
        libIntent = new Intent(MainActivity.this, AudiobookService.class);
        bindService(libIntent, libraryConnect, BIND_AUTO_CREATE);

        if (savedInstanceState != null) {
            book = (Book) savedInstanceState.getSerializable(Keys.BOOK);
            books = (ArrayList<Book>) savedInstanceState.getSerializable(Keys.LIST);
            bookList = BookListFragment.newInstance(books);
            if (twoViews) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container1, bookList).addToBackStack(null).commit();
                getSupportFragmentManager().beginTransaction().replace(R.id.container2, BookDetailsFragment.newInstance(book)).addToBackStack(null).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.container1, bookList).addToBackStack(null).commit();
                getSupportFragmentManager().beginTransaction().replace(R.id.container1, BookDetailsFragment.newInstance(book)).addToBackStack(null).commit();
            }
        } else if (savedInstanceState == null) {
            if (twoViews) {
                bookList = BookListFragment.newInstance(books);
                getSupportFragmentManager().beginTransaction().replace(R.id.container1, bookList).addToBackStack(null).commit();
                bookDetails = BookDetailsFragment.newInstance(book);
                getSupportFragmentManager().beginTransaction().replace(R.id.container2, bookDetails).addToBackStack(null).commit();
            } else {
                bookList = BookListFragment.newInstance(books);
                getSupportFragmentManager().beginTransaction().replace(R.id.container1, bookList).addToBackStack(null).commit();
            }
        }

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://kamorris.com/lab/abp/booksearch.php?search";
                JsonArrayRequest searchResults = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSONObject book;
                        books.clear();
                        try {
                            if (titleSearch.getText().toString().isEmpty()) {
                                for (int i = 0; i < response.length(); i++) {
                                    book = response.getJSONObject(i);
                                    Book entry = new Book(book.getInt("book_id"), book.getString("title"), book.getString("author"), book.getString("cover_url"), book.getInt("duration"));
                                    books.add(entry);
                                }
                            } else {
                                for (int i = 0; i < response.length(); i++) {
                                    book = response.getJSONObject(i);
                                    if (book.getString("title").toLowerCase().contains(titleSearch.getText().toString().toLowerCase()) ||
                                            book.getString("author").toLowerCase().contains(titleSearch.getText().toString().toLowerCase())) {
                                        Book entry = new Book(book.getInt("book_id"), book.getString("title"), book.getString("author"), book.getString("cover_url"), book.getInt("duration"));
                                        books.add(entry);
                                    }
                                }
                            }
                            bookList.searchUpdated();
                            if (!twoViews)
                                getSupportFragmentManager().beginTransaction().replace(R.id.container1, bookList).addToBackStack(null).commit();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        /*  added internet permission after installing, must have been working from cache
                            uninstalling and reinstalling app resolved issue
                        */
                        error.printStackTrace();
                    }
                });
                requestQueue.add(searchResults);
            }
        });
//pausing worked before I used a handler. Get null reference.
        findViewById(R.id.pauseButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress = bookProgress.getProgress();
                id = bookProgress.getBookId();
                    if (playback.isPlaying()) {
                        playback.pause();
                        setTitle(R.string.app_name);
                    }
                    else
                        playback.play(id, progress);
                    System.out.println("The error is here");
            }
        });

        findViewById(R.id.stopButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connected) {
                    playback.stop();
                    stopService(libIntent);
                    setTitle(R.string.app_name);
                }
            }
        });


        final SeekBar playBar = findViewById(R.id.bookSeek);
        playing = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                bookProgress = (AudiobookService.BookProgress) msg.obj;
                playBar.setMax(books.get(bookProgress.getBookId()-1).getDuration());

                //looks like it goes below 0 faster than the check can be performed added < 0 to stop memory leak
                if (bookProgress.getProgress() == books.get(bookProgress.getBookId()-1).getDuration() || bookProgress.getProgress() < 0) {
                    playback.stop();
                    unbindService(libraryConnect);
                    playBar.setProgress(0);
                }
                if (bookProgress.getProgress() < books.get(bookProgress.getBookId()-1).getDuration()) {
                    playBar.setProgress(bookProgress.getProgress());
                }

                playBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if(fromUser) {
                            playback.seekTo(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
                return false;

            }
        });
    }

    @Override
    public void bookSelected(Book book) {
        this.book = book;
        if (twoViews) {
            ((BookDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.container2)).displayBook(book);
        } else {
            bookDetails = BookDetailsFragment.newInstance(book);
            getSupportFragmentManager().beginTransaction().replace(R.id.container1, bookDetails).addToBackStack(null).commit();
        }

    }
//only works if I start in portrait. Doesn't seem to connect if I am in twoViews
    @Override
    public void play(int id) {
        if(connected) {
            startService(libIntent);
            playback.play(id);
            playback.setProgressHandler(playing);
            setTitle("Now Playing: "+ books.get(id-1).getTitle());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(libIntent);
    }
}


