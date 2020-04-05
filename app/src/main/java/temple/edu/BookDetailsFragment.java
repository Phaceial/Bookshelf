package temple.edu;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class BookDetailsFragment extends Fragment {

    TextView title, author;
    ImageView cover = null;
    Book book;
    View v;

    public BookDetailsFragment() {
        // Required empty public constructor
    }


    public static BookDetailsFragment newInstance(Book book) {
        BookDetailsFragment fragment = new BookDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Keys.BOOK, book);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Keys.BOOK, book);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
            book = (Book) getArguments().getSerializable(Keys.BOOK);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_bookdetails, container, false);
        author = v.findViewById(R.id.Author);
        title = v.findViewById(R.id.Title);
        cover = v.findViewById(R.id.Cover);
        cover.setImageResource(android.R.color.transparent);

        if (book != null)
            displayBook(book);

        return v;
    }

    public void displayBook(Book book) {
        title.setText(book.getTitle());
        author.setText(book.getAuthor());
        Picasso.get().load(book.getCoverURL()).into(cover);
    }
}
