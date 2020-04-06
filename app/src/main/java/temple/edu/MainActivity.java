package temple.edu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements BookListFragment.BookSelectedInterface {
    EditText titleSearch;
    ArrayList<Book> books = new ArrayList<>();
    Book book;
    RequestQueue requestQueue;
    BookListFragment bookList;
    BookDetailsFragment bookDetails;
    boolean twoViews;

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
        twoViews = (findViewById(R.id.container2) != null);
        titleSearch = findViewById(R.id.titleSearch);
        requestQueue = Volley.newRequestQueue(this);
        if(savedInstanceState != null){
            if (twoViews) {
                book = (Book) savedInstanceState.getSerializable(Keys.BOOK);
                books = (ArrayList<Book>) savedInstanceState.getSerializable(Keys.LIST);
                getSupportFragmentManager().beginTransaction().replace(R.id.container1, BookListFragment.newInstance(books)).addToBackStack(null).commit();
                getSupportFragmentManager().beginTransaction().replace(R.id.container2, BookDetailsFragment.newInstance(book)).addToBackStack(null).commit();
                System.out.println(book);
            } else {
                book = (Book) savedInstanceState.getSerializable(Keys.BOOK);
                books = (ArrayList<Book>) savedInstanceState.getSerializable(Keys.LIST);
                getSupportFragmentManager().beginTransaction().replace(R.id.container1, BookListFragment.newInstance(books)).addToBackStack(null).commit();
                getSupportFragmentManager().beginTransaction().replace(R.id.container1, BookDetailsFragment.newInstance(book)).addToBackStack(null).commit();
                System.out.println(book);
            }
        }
        if (savedInstanceState == null && twoViews) {
            bookList = BookListFragment.newInstance(books);
            getSupportFragmentManager().beginTransaction().replace(R.id.container1, bookList, "list").addToBackStack(null).commit();
            bookDetails = BookDetailsFragment.newInstance(book);
            getSupportFragmentManager().beginTransaction().replace(R.id.container2, bookDetails, "details").addToBackStack(null).commit();
        } else {
            bookList = BookListFragment.newInstance(books);
            getSupportFragmentManager().beginTransaction().replace(R.id.container1, bookList, "list").addToBackStack(null).commit();
        }

        findViewById(R.id.searchButton).setOnClickListener(new View.OnClickListener() {
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
                                    Book entry = new Book(book.getInt("book_id"), book.getString("title"), book.getString("author"), book.getString("cover_url"));
                                    books.add(entry);
                                }
                            } else {
                                for (int i = 0; i < response.length(); i++) {
                                    book = response.getJSONObject(i);
                                    if (book.getString("title").toLowerCase().contains(titleSearch.getText().toString().toLowerCase()) ||
                                            book.getString("author").toLowerCase().contains(titleSearch.getText().toString().toLowerCase())) {
                                        Book entry = new Book(book.getInt("book_id"), book.getString("title"), book.getString("author"), book.getString("cover_url"));
                                        books.add(entry);
                                    }
                                }
                            }
                            bookList.searchUpdated();
                            if (!twoViews)
                                getSupportFragmentManager().beginTransaction().replace(R.id.container1, bookList, "list").addToBackStack(null).commit();
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
    }

    @Override
    public void bookSelected(Book book) {
        if (twoViews) {
            ((BookDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.container2)).displayBook(book);
        } else {
            bookDetails = BookDetailsFragment.newInstance(book);
            getSupportFragmentManager().beginTransaction().replace(R.id.container1, bookDetails, "list").addToBackStack(null).commit();
        }
    }
}
