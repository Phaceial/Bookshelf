package temple.edu;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BookListFragment extends Fragment {
    private BookSelectedInterface mCallback;
    ArrayList<Book> books;
    BookAdapter bookAdapter;

    public BookListFragment() {
        // Required empty public constructor
    }

    public static BookListFragment newInstance(ArrayList<Book> books) {
        BookListFragment fragment = new BookListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Keys.BOOK, books);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ListView listView = (ListView) inflater.inflate(R.layout.fragment_booklist, container, false);
        books = (ArrayList<Book>) getArguments().getSerializable(Keys.BOOK);
        listView.setAdapter(bookAdapter = new BookAdapter(getActivity(), books));
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCallback.bookSelected(books.get(position));
            }
        });
        return listView;
    }

    public interface BookSelectedInterface{
        public void bookSelected(Book book);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mCallback = (BookSelectedInterface) context;
        } catch (ClassCastException e){
            throw new ClassCastException (context.toString() + " must implement BookSelectedInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    public void searchUpdated(ArrayList<Book> bookshelf){
        bookAdapter.notifyDataSetChanged();
    }
}
