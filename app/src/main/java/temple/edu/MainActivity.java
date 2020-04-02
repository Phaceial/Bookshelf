package temple.edu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements BookListFragment.BookSelectedInterface {
    boolean twoViews;
    EditText titleSearch;
    ArrayList<Book> bookshelf = new ArrayList<>();
    Book book;
    RequestQueue requestQueue;
    BookDetailsFragment bookdetails;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        titleSearch = findViewById(R.id.titleSearch);
        requestQueue = Volley.newRequestQueue(this);
        final BookListFragment booklist = BookListFragment.newInstance(bookshelf);
        getSupportFragmentManager().beginTransaction().replace(R.id.booklist_fragment, booklist).addToBackStack(null).commit();
        twoViews = (findViewById(R.id.bookdetails_fragment) != null);

        if (twoViews) {
            BookDetailsFragment books = BookDetailsFragment.newInstance(book);
            getSupportFragmentManager().beginTransaction().replace(R.id.bookdetails_fragment, books).addToBackStack(null).commit();
        }

        findViewById(R.id.searchButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://kamorris.com/lab/abp/booksearch.php?search";
                JsonArrayRequest searchResults = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSONObject book;
                        bookshelf.clear();
                        try {
                            if (titleSearch.getText().toString().isEmpty()) {
                                for (int i = 0; i < response.length(); i++) {
                                    book = response.getJSONObject(i);
                                    Book entry = new Book(book.getInt("book_id"), book.getString("title"), book.getString("author"), book.getString("cover_url"));
                                    bookshelf.add(entry);
                                }
                            } else{
                                for (int i = 0; i < response.length(); i++){
                                    book = response.getJSONObject(i);
                                    if(book.getString("title").toLowerCase().contains(titleSearch.getText().toString().toLowerCase()) ||
                                       book.getString("author").toLowerCase().contains(titleSearch.getText().toString().toLowerCase()) ){
                                        Book entry = new Book(book.getInt("book_id"), book.getString("title"), book.getString("author"), book.getString("cover_url"));
                                        bookshelf.add(entry);
                                    }
                                }
                            }
                            if(bookshelf.size() == 1) {
                                if(twoViews){
                                    getSupportFragmentManager().beginTransaction().replace(R.id.bookdetails_fragment, bookdetails).addToBackStack(null).commit();
                                }else {
                                    bookdetails = BookDetailsFragment.newInstance(bookshelf.get(0));
                                    getSupportFragmentManager().beginTransaction().replace(R.id.booklist_fragment, bookdetails).addToBackStack(null).commit();
                                }
                            } else
                                getSupportFragmentManager().beginTransaction().replace(R.id.booklist_fragment, booklist).addToBackStack(null).commit();

                            booklist.searchUpdated(bookshelf);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Doesn't work", Toast.LENGTH_SHORT).show();
                        /*  added internet permission after installing, must have been working from cache
                            uninstalling and reinstalling app resolved issue
                        */
                        error.printStackTrace();
                    }
                });
                requestQueue.add(searchResults);
            }
        });



    }

    @Override
    public void bookSelected(Book book) {
        if (twoViews) {
            //editing the current fragment doesn't allow me to go back.
            ((BookDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.bookdetails_fragment)).displayBook(book);
        } else {
            BookDetailsFragment books = BookDetailsFragment.newInstance(book);
            getSupportFragmentManager().beginTransaction().replace(R.id.booklist_fragment, books).addToBackStack(null).commit();
        }
    }
}
