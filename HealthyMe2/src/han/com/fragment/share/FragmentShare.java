package han.com.fragment.share;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import han.com.R;
import han.com.resources.ResourceGetter;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.Header;

public class FragmentShare extends Fragment implements OnScrollListener {

    private static final String className = FragmentShare.class.getName();
    private static Handler reloadListHandler;

    public static Handler getReloadListHandler() {
        return reloadListHandler;
    }
    private final AsyncHttpClient http = new AsyncHttpClient();
    private TextView shareButton;
    private ListView listView;
    private ArrayList<Object[]> listItems;
    private ListAdapterPostItem listAdapter;
    private int page;
    private final int pageSize = 10;
    private boolean loadingMore;
    private boolean listReady;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(className, "onCreate");

        reloadListHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0) {
                    reloadList();
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View myFragmentView = inflater.inflate(R.layout.fragment_share, container, false);

        shareButton = (TextView) myFragmentView.findViewById(R.id.frag_share_text1);
        shareButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                Intent i = new Intent(getActivity(), ActivityShare.class);
                startActivity(i);
            }
        });

        listView = (ListView) myFragmentView.findViewById(R.id.frag_share_list1);
        listReady = false;
        listItems = new ArrayList<Object[]>(0);
        listAdapter = new ListAdapterPostItem(getActivity(), listItems);
        listView.setAdapter(listAdapter);
        listView.setOnScrollListener(this);

        return myFragmentView;
    }

    private void loadData() {
        page = 1;
        http.get(ResourceGetter.getInstance(null).getServiceAddressGetPosts() + "?page=" + page + "&size=" + pageSize, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                GetResult r = new Gson().fromJson(responseBody, GetResult.class);
                List<Post> posts = r.getPosts();
                listItems = new ArrayList<Object[]>(posts.size());
                for (Post p : posts) {
                    listItems.add(new Object[]{p});
                }
                listAdapter.clear();
                listAdapter.addAll(listItems);
                listAdapter.notifyDataSetChanged();
                loadingMore = false;
                listReady = true;
            }

            @Override
            public void onFailure(String responseBody, Throwable error) {
                Toast.makeText(getActivity(), "Cannot read from server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void reloadList() {
        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(className, "onResume");
        loadData();
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //do nothing
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (!listReady) {
            return;
        }

        int lastInScreen = firstVisibleItem + visibleItemCount;
        if ((lastInScreen == totalItemCount) && !(loadingMore)) {
            loadingMore = true;

            page++;
            http.get(ResourceGetter.getInstance(null).getServiceAddressGetPosts() + "?page=" + page + "&size=" + pageSize, new TextHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                    GetResult r = new Gson().fromJson(responseBody, GetResult.class);
                    List<Post> posts = r.getPosts();
                    if (posts.isEmpty()) {
                        return;
                    }
                    for (Post p : posts) {
                        listAdapter.add(new Object[]{p});
                    }
                    listAdapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(), "More posts are loaded", Toast.LENGTH_SHORT).show();
                    loadingMore = false;
                }

                @Override
                public void onFailure(String responseBody, Throwable error) {
                    Toast.makeText(getActivity(), "Cannot read from server", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private class GetResult {

        private List<Post> posts;

        public List<Post> getPosts() {
            return posts;
        }
    }
}
