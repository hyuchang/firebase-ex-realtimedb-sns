package com.hucloud.fbex.chat.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.google.firebase.database.*;
import com.hucloud.fbex.chat.R;
import com.hucloud.fbex.chat.firebase.FirebaseWrapp;
import com.hucloud.fbex.chat.model.Feed;
import com.hucloud.fbex.chat.model.Reply;

import java.text.SimpleDateFormat;
import java.util.*;

public class FeedActivity extends AppCompatActivity {

    private RecyclerView feedListRv;

    /**
     * 피드 아답터
     */
    private FeedListAdapter feedListAdapter;
    private static FirebaseDatabase dbInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        setView();
        dbInstance = FirebaseWrapp.getDbInstance();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        feedListRv.setLayoutManager(layoutManager);
        feedListAdapter = new FeedListAdapter();
        feedListRv.setAdapter(feedListAdapter);
        feedListRv.setItemAnimator(new DefaultItemAnimator());
        feedListRv.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feed_write, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(FeedActivity.this, FeedWriteActivity.class));
        return super.onOptionsItemSelected(item);
    }

    private void setView(){
        feedListRv = (RecyclerView) findViewById(R.id.feedListRv);
    }

    class FeedListAdapter extends RecyclerView.Adapter<FeedListAdapter.FeedListHolder> {

        /**
         *  피드목록
         **/
        private List<Feed> mFeedList;

        public FeedListAdapter() {

            this.mFeedList = new ArrayList<Feed>();

            Query queryRef = dbInstance.getReference("feeds");

            queryRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Feed feed = dataSnapshot.getValue(Feed.class);
                    List<Reply> replyList = new ArrayList<Reply>();
                    try{
                        HashMap map = (HashMap) dataSnapshot.child("replies").getValue();
                        if ( map != null ) {
                            Collection<HashMap> replies = map.values();
                            for ( HashMap data : replies ) {
                                String email = data.get("email").toString();
                                String text = data.get("text").toString();
                                Reply reply = new Reply();
                                reply.setEmail(email);
                                reply.setText(text);
                                replyList.add(reply);
                            }
                            feed.setReplyList(replyList);
                        }

                        mFeedList.add(feed);
                        notifyItemInserted(mFeedList.size() - 1);
                    }catch (Exception e){
                        FirebaseWrapp.getCrashInstance().logcat(Log.ERROR, getClass().getName(), e.getMessage());
                        e.printStackTrace();
                    }

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Feed changedFeed = dataSnapshot.getValue(Feed.class);
                    int changedIndex = -1;
                    int i = 0;
                    for ( Feed feed : mFeedList ) {
                        if ( feed.getFeedId().equals(changedFeed.getFeedId())) {
                            changedIndex = i;
                            break;
                        }
                        i++;
                    }
                    try{
                        List<Reply> replyList = new ArrayList<Reply>();
                        HashMap map = (HashMap) dataSnapshot.child("replies").getValue();
                        if ( map != null ) {
                            Collection<HashMap> replies = map.values();
                            for (HashMap data : replies) {
                                String email = data.get("email").toString();
                                String text = data.get("text").toString();
                                Reply reply = new Reply();
                                reply.setEmail(email);
                                reply.setText(text);
                                replyList.add(reply);
                            }
                            changedFeed.setReplyList(replyList);
                        }
                        mFeedList.set(changedIndex, changedFeed);
                        notifyItemChanged(changedIndex);

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Feed feed = dataSnapshot.getValue(Feed.class);
                    int removedItemIndex = mFeedList.indexOf(feed);
                    if (removedItemIndex > -1) {
                        mFeedList.set(removedItemIndex, feed);
                        notifyItemRemoved(removedItemIndex);
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return mFeedList.size();
        }

        public Feed getItem(int position){
            return this.mFeedList.get(position);
        }

        @Override
        public FeedListHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_feed_item, viewGroup, false);
            FeedListHolder viewHolder = new FeedListHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final FeedListHolder viewHolder,final int i) {
            final Feed feed = getItem(i);
            HorizontalScrollView scrollView = viewHolder.photo_scroll_area;

            if ( feed.getPhotos() != null && feed.getPhotos().size() > 0 ) {
                LinearLayout photoLayout = viewHolder.photo_area;
                scrollView.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(500, 500);
                if ( photoLayout.getChildCount() > 0 ) {
                    photoLayout.removeAllViews();
                }
                for ( String photo : feed.getPhotos()) {
                    ImageView feedImageView = new ImageView(getApplication());
                    feedImageView.setPadding(10, 10, 10, 10);
                    feedImageView.setLayoutParams(layoutParams);
                    Glide.with(getApplication())
                            .load(photo)
                            .thumbnail(0.1f)
                            .override(300, 300)
                            .centerCrop()
                            .crossFade()
                            .into(feedImageView);
                    photoLayout.addView(feedImageView);
                }
            } else {
                scrollView.setVisibility(View.GONE);
            }

            if ( feed.getReplyList() != null && feed.getReplyList().size() > 0 ) {
                for ( Reply reply : feed.getReplyList() ) {
                    LinearLayout layout = new LinearLayout(getApplication());
                    layout.setOrientation(LinearLayout.HORIZONTAL);
                    layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    TextView tv = new TextView(getApplicationContext());
                    tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tv.setTextColor(Color.BLACK);
                    tv.setText( reply.getEmail().split("@")[0] + " : " + reply.getText());
                    layout.addView(tv);
                    viewHolder.replies_area.addView(layout);
                }
            }
            viewHolder.writer.setText(feed.getUserName());
            viewHolder.content.setText(feed.getText());
            viewHolder.txtRegdate.setText(new SimpleDateFormat("yyyy.MM.dd a hh:mm").format(feed.getRegdate()));
            viewHolder.addReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String replyTxt = viewHolder.msg.getText().toString();
                    if ( !replyTxt.isEmpty() ) {
                        DatabaseReference ref = dbInstance.getReference("feeds").child(feed.getFeedId());
                        Reply reply = new Reply();
                        reply.setEmail(FirebaseWrapp.getAuthInstance().getCurrentUser().getEmail());
                        reply.setText(replyTxt);
                        ref.child("replies").push().setValue(reply);
                        viewHolder.msg.setText("");
                    }
                }
            });
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        public class FeedListHolder extends RecyclerView.ViewHolder {

            private TextView writer;
            private TextView content;
            private TextView txtRegdate;
            private HorizontalScrollView photo_scroll_area;
            private LinearLayout photo_area,replies_area;
            private Button addReply;
            private EditText msg;

            private FeedListHolder(View v){
                super(v);
                writer = (TextView) v.findViewById(R.id.txtName);
                content = (TextView) v.findViewById(R.id.txtContent);
                txtRegdate = (TextView) v.findViewById(R.id.txtRegdate);
                photo_scroll_area = (HorizontalScrollView) v.findViewById(R.id.photo_scroll_area);
                photo_area = (LinearLayout) v.findViewById(R.id.photo_area);
                addReply = (Button) v.findViewById(R.id.addReply);
                msg = (EditText) v.findViewById(R.id.msg);
                replies_area = (LinearLayout) v.findViewById(R.id.replies_area);

            }
        }

    }
}
