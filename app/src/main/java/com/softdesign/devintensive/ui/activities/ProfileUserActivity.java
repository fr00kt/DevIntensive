package com.softdesign.devintensive.ui.activities;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.storage.models.UserDTO;
import com.softdesign.devintensive.ui.adapters.RepositoriesAdapter;
import com.softdesign.devintensive.utils.ConstantManager;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProfileUserActivity extends BaseActivity {

    private Toolbar mToolbar;
    private ImageView mProfileImage;
    private EditText mUserBio;
    private TextView mUserRating, mUserCodeLines, mUserProjects;

    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    private ListView mRepoListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mProfileImage = (ImageView) findViewById(R.id.user_photo_img);
        mUserBio = (EditText) findViewById(R.id.about_me_et);
        mUserRating = (TextView) findViewById(R.id.user_info_rait_txt);
        mUserCodeLines = (TextView) findViewById(R.id.user_info_code_line_txt);
        mUserProjects = (TextView) findViewById(R.id.user_info_project_txt);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mRepoListView = (ListView) findViewById(R.id.repositories_list);

        setupToolBar();
        initProfileData();
    }

    private void setupToolBar(){
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar !=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    private void initProfileData(){
        UserDTO userDTO = getIntent().getParcelableExtra(ConstantManager.PARCELABLE_KEY);

        final List<String> repositories = userDTO.getmRepositories();
        final RepositoriesAdapter repositoriesAdapter = new RepositoriesAdapter(this, repositories);
        mRepoListView.setAdapter(repositoriesAdapter);

        mUserBio.setText(userDTO.getmBio());
        mUserRating.setText(userDTO.getmRating());
        mUserCodeLines.setText(userDTO.getmCodeLines());
        mUserProjects.setText(userDTO.getmProjects());

        mCollapsingToolbarLayout.setTitle(userDTO.getmFullName());

        Picasso.with(this)
                .load(userDTO.getmPhoto())
                .placeholder(R.drawable.user_photo)
                .error(R.drawable.user_photo)
                .into(mProfileImage);

    }
}
