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


public class BookListFragment extends Fragment {
    private BookSelectedInterface mCallback;
    ArrayList<HashMap<String, String>> books;

    public BookListFragment() {
        // Required empty public constructor
    }

    public static BookListFragment newInstance(ArrayList<HashMap<String, String>> books) {
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
        View v = inflater.inflate(R.layout.fragment_booklist, container, false);
        final ListView listview = (ListView) v.findViewById(R.id.BookList);
        books = (ArrayList<HashMap<String, String>>) getArguments().getSerializable(Keys.BOOK);
        listview.setAdapter(new SimpleAdapter(getActivity(), books, android.R.layout.simple_list_item_2, new String[]{Keys.TITLE, Keys.AUTHOR}, new int[]{android.R.id.text1, android.R.id.text2}));
        listview.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCallback.bookSelected(books.get(position));
            }
        });

        return v;
    }

    public interface BookSelectedInterface{
        public void bookSelected(HashMap<String, String> book);
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
}
