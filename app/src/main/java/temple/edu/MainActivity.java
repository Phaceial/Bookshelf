package temple.edu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements BookListFragment.BookSelectedInterface {
    boolean twoViews;
    EditText titleSearch;
    ArrayList<Book> bookshelf = new ArrayList<>();
    Book book;
    RequestQueue requestQueue;
    BookListFragment bookList;
    BookDetailsFragment bookDetails;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Keys.LIST, bookshelf);
        outState.putSerializable(Keys.BOOK, book);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        twoViews = (findViewById(R.id.bookdetails_container) != null);
        titleSearch = findViewById(R.id.titleSearch);
        requestQueue = Volley.newRequestQueue(this);

        if (savedInstanceState != null) {

            if (twoViews) {
                getSupportFragmentManager().beginTransaction().replace(R.id.booklist_container, bookList.newInstance((ArrayList<Book>) savedInstanceState.getSerializable(Keys.LIST))).addToBackStack(null).commit();
                getSupportFragmentManager().beginTransaction().replace(R.id.bookdetails_container, bookDetails.newInstance((Book) savedInstanceState.getSerializable(Keys.BOOK))).commitNow();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.booklist_container, bookList.newInstance((ArrayList<Book>) savedInstanceState.getSerializable(Keys.LIST))).commitNow();
                getSupportFragmentManager().beginTransaction().replace(R.id.booklist_container, bookDetails.newInstance((Book) savedInstanceState.getSerializable(Keys.BOOK))).addToBackStack(null).commit();
            }
        } else {
            bookList = BookListFragment.newInstance(bookshelf);

            getSupportFragmentManager().beginTransaction().replace(R.id.booklist_container, bookList).addToBackStack(null).commit();

            if (twoViews) {
                bookDetails = BookDetailsFragment.newInstance(book);
                getSupportFragmentManager().beginTransaction().replace(R.id.bookdetails_container, bookDetails).addToBackStack(null).commit();
            }
        }
            findViewById(R.id.searchButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = "https://kamorris.com/lab/abp/booksearch.php?search";
                    JsonArrayRequest searchResults = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
                        @SuppressLint("ResourceType")
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
                                } else {
                                    for (int i = 0; i < response.length(); i++) {
                                        book = response.getJSONObject(i);
                                        if (book.getString("title").toLowerCase().contains(titleSearch.getText().toString().toLowerCase()) ||
                                                book.getString("author").toLowerCase().contains(titleSearch.getText().toString().toLowerCase())) {
                                            Book entry = new Book(book.getInt("book_id"), book.getString("title"), book.getString("author"), book.getString("cover_url"));
                                            bookshelf.add(entry);
                                        }
                                    }
                                }
//                            If bookList contains one book, immediately load bookDetails for book
//                            if(bookshelf.size() == 1) {
//                                if (twoViews) {
//                                    bookDetails.displayBook(bookshelf.get(0));
//                                    getSupportFragmentManager().beginTransaction().replace(R.id.bookdetails_fragment, bookDetails).addToBackStack(null).commit();
//                                } else {
//                                    bookDetails = BookDetailsFragment.newInstance(bookshelf.get(0));
//                                    getSupportFragmentManager().beginTransaction().replace(R.id.booklist_fragment, bookDetails).addToBackStack(null).commit();
//                                }
//                            }
                                bookList.searchUpdated();
                                if (getSupportFragmentManager().findFragmentById(R.id.booklist_container).getArguments().containsKey(Keys.BOOK))
                                    getSupportFragmentManager().beginTransaction().replace(R.id.booklist_container, bookList).addToBackStack(null).commit();
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
            ((BookDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.bookdetails_container)).displayBook(book);
        } else {
            bookDetails = BookDetailsFragment.newInstance(book);
            getSupportFragmentManager().beginTransaction().replace(R.id.booklist_container, bookDetails).addToBackStack(null).commit();
        }
    }
}
