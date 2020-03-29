package temple.edu;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;


public class BookDetailsFragment extends Fragment {

    public BookDetailsFragment() {
        // Required empty public constructor
    }


    public static BookDetailsFragment newInstance(HashMap<String, String> book) {
        BookDetailsFragment fragment = new BookDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Keys.BOOK, book);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bookdetails, container, false);
        TextView author = v.findViewById(R.id.Author);
        TextView title = v.findViewById(R.id.Title);
        HashMap<String, String> book = (HashMap<String, String>) getArguments().getSerializable(Keys.BOOK);
        author.setText(book.get(Keys.AUTHOR));
        title.setText(book.get(Keys.TITLE));
        // Inflate the layout for this fragment
        return v;
    }

    public void displayBook (HashMap<String, String> book){

    }
}
