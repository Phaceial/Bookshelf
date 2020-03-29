package temple.edu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements BookListFragment.BookSelectedInterface {

    boolean twoViews;

    private ArrayList<HashMap<String, String>> bookshelf() {
        ArrayList<HashMap<String, String>> bookshelf = new ArrayList<>();
        String title, author;
        for (int i = 0; i < 21; i++) {
            HashMap<String, String> book = new HashMap<>();
            title = "Title" + i;
            author = "Author" + i;
            book.put(Keys.TITLE, title);
            book.put(Keys.AUTHOR, author);
            bookshelf.add(book);
        }
        return bookshelf;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        twoViews = (findViewById(R.id.bookdetails_fragment) != null);
        ArrayList<HashMap<String, String>> bookshelf = bookshelf();
        BookListFragment booklist = BookListFragment.newInstance(bookshelf);

        getSupportFragmentManager().beginTransaction().add(R.id.booklist_fragment, booklist).addToBackStack(null).commit();
        if (twoViews) {
            BookDetailsFragment books = BookDetailsFragment.newInstance(bookshelf.get(0));
            getSupportFragmentManager().beginTransaction().add(R.id.bookdetails_fragment, books).addToBackStack(null).commit();
        }


    }

    @Override
    public void bookSelected(HashMap<String, String> book) {
        if (twoViews) {
            BookDetailsFragment books = BookDetailsFragment.newInstance(book);
            getSupportFragmentManager().beginTransaction().replace(R.id.bookdetails_fragment, books).addToBackStack(null).commit();
        } else {
            BookDetailsFragment books = BookDetailsFragment.newInstance(book);
            getSupportFragmentManager().beginTransaction().replace(R.id.booklist_fragment, books).addToBackStack(null).commit();
        }
    }
}
