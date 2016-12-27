package com.toe.shareyourcuisine.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.toe.shareyourcuisine.R;
import com.toe.shareyourcuisine.activity.CreatePostActivity;
import com.toe.shareyourcuisine.activity.MainActivity;
import com.toe.shareyourcuisine.adapter.PostRecyclerViewAdapter;
import com.toe.shareyourcuisine.model.Post;
import com.toe.shareyourcuisine.service.PostService;

import org.parceler.Parcels;

import java.util.List;

/**
 * Created by HQu on 12/27/2016.
 */

public class PostFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, PostService.GetAllPostsListener {

    private RecyclerView mPostRV;
    private PostRecyclerViewAdapter mAdapter;
    private SwipeRefreshLayout mPostSRL;
    private FloatingActionButton mCreatePostFAB;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_post, container, false);
        mPostRV = (RecyclerView)rootView.findViewById(R.id.post_rv);
        mPostSRL = (SwipeRefreshLayout)rootView.findViewById(R.id.post_srl);

        mCreatePostFAB = (FloatingActionButton) rootView.findViewById(R.id.create_post_fab);
        mCreatePostFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((MainActivity)getActivity()).getAuth().getCurrentUser() != null) {
                    Intent intent = new Intent(getActivity(), CreatePostActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "Please log in!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        mPostSRL.setOnRefreshListener(this);
        mPostSRL.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.colorRed), ContextCompat.getColor(getActivity(), R.color.colorAccent), ContextCompat.getColor(getActivity(), R.color.colorOrange));
        getAllPosts();
        return rootView;
    }

    @Override
    public void onRefresh() {
        getAllPosts();
    }

    public void getAllPosts() {
        mPostSRL.setRefreshing(true);
        PostService postService = new PostService(getActivity());
        postService.setGetAllPostsListener(this);
        postService.getAllPosts();
    }

    @Override
    public void getAllPostsSucceed(List<Post> posts) {
        mAdapter = new PostRecyclerViewAdapter(getActivity(), posts);
        mAdapter.setPostItemClickListener(new PostRecyclerViewAdapter.PostItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
//                Intent intent = new Intent(getActivity(), OneRecipeActivity.class);
//                Parcelable wrapped = Parcels.wrap(recipes.get(position));
//                intent.putExtra("recipe", wrapped);
//                startActivity(intent);
            }
        });
        mPostRV.setAdapter(mAdapter);
        mPostSRL.setRefreshing(false);
    }

    @Override
    public void getAllPostsFail(String errorMsg) {
        mPostSRL.setRefreshing(false);
        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
    }
}