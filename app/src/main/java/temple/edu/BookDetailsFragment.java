package temple.edu;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.HashMap;


public class BookDetailsFragment extends Fragment {

    TextView title, author;
    ImageView cover;
    Book book;
    View v;

    public BookDetailsFragment() {
        // Required empty public constructor
    }


    public static BookDetailsFragment newInstance(Book book) {
        BookDetailsFragment fragment = new BookDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Keys.BOOK, (Serializable) book);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null)
            book =(Book) bundle.getSerializable(Keys.BOOK);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_bookdetails, container, false);
        if(book != null)
            displayBook(book);
        return v;
    }

    public void displayBook (Book book){
        author = v.findViewById(R.id.Author);
        title = v.findViewById(R.id.Title);
        cover = v.findViewById(R.id.Cover);
        title.setText(book.getTitle());
        author.setText(book.getAuthor());
    }
}
