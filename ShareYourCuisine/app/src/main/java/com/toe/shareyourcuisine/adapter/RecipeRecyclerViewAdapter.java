package com.toe.shareyourcuisine.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.squareup.picasso.Picasso;
import com.toe.shareyourcuisine.R;
import com.toe.shareyourcuisine.activity.CreateRecipeActivity;
import com.toe.shareyourcuisine.model.Recipe;

import java.io.File;
import java.util.List;

/**
 * Created by HQu on 12/8/2016.
 */

public class RecipeRecyclerViewAdapter extends RecyclerView.Adapter<RecipeRecyclerViewAdapter.RecipeViewHolder>{

    private static final String TAG = "ToeRecipeRVD:";
    private Context mContext;
    private List<Recipe> mRecipes;
    private static RecipeItemClickListener mRecipeItemClickListener;

    public RecipeRecyclerViewAdapter(Context context, List<Recipe> recipes) {
        mContext = context;
        mRecipes = recipes;
    }

    public interface RecipeItemClickListener {
        public void onItemClick(int position, View v);
    }

    public void setRecipeItemClickListener(RecipeItemClickListener recipeItemClickListener) {
        mRecipeItemClickListener = recipeItemClickListener;
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView mDisplayImgIV;
        public TextView mTitleTV;
        public SimpleRatingBar mRatingSRB;
        public TextView mFlavorTV;
        public TextView mTimeTV;
        public RecipeViewHolder(View itemView) {
            super(itemView);
            mDisplayImgIV = (ImageView)itemView.findViewById(R.id.display_img_iv);
            mTitleTV = (TextView)itemView.findViewById(R.id.title_tv);
            mRatingSRB = (SimpleRatingBar)itemView.findViewById(R.id.rating_srb);
            mFlavorTV = (TextView)itemView.findViewById(R.id.flavor_tv);
            mTimeTV = (TextView)itemView.findViewById(R.id.time_tv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mRecipeItemClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        RecipeViewHolder recipeViewHolder = new RecipeViewHolder(view);
        return recipeViewHolder;
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        Picasso.with(mContext).load(mRecipes.get(position).getDisplayImgUrl()).fit().centerCrop().into(holder.mDisplayImgIV);
        holder.mTitleTV.setText(mRecipes.get(position).getTitle());
        if(mRecipes.get(position).getRatedBy().size() == 0)
            holder.mRatingSRB.setRating(0);
        else
            holder.mRatingSRB.setRating(mRecipes.get(position).getTotalRates()/mRecipes.get(position).getRatedBy().size());
        holder.mFlavorTV.setText(mRecipes.get(position).getFlavorTypes());
        holder.mTimeTV.setText(mRecipes.get(position).getCookingTime());
    }

    @Override
    public int getItemCount() {
        return mRecipes.size();
    }
}
