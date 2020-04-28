package temple.edu;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class BookDetailsFragment extends Fragment {
    private PlayBookInterface mCallback;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null)
            book = (Book) savedInstanceState.getSerializable(Keys.BOOK);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (book != null)
        outState.putSerializable(Keys.BOOK,book);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_bookdetails, container, false);
        author = v.findViewById(R.id.Author);
        title = v.findViewById(R.id.Title);
        cover = v.findViewById(R.id.Cover);
        v.findViewById(R.id.playButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.play(book.getId());
            }
        });
        cover.setImageResource(android.R.color.transparent);
        if(book == null)
            book = (Book) getArguments().getSerializable(Keys.BOOK);

        if (book != null)
            displayBook(book);

        return v;
    }

    public void displayBook(Book book) {
        title.setText(book.getTitle());
        author.setText(book.getAuthor());
        Picasso.get().load(book.getCoverURL()).into(cover);
    }

    public interface PlayBookInterface{
        public void play(int id);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mCallback = (PlayBookInterface) context;
        } catch (ClassCastException e){
            throw new ClassCastException (context.toString() + " must implement PlayBookInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    public void searchUpdated(){
    }
}
