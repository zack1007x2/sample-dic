package com.example.zack.timelinepage;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.zack.timeinepage.message.messageAdapter;
import com.example.zack.timeinepage.message.messageData;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements
        android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener {
    private ListView lvMessage;
    private ProgressBar progressbar;
    private SwipeRefreshLayout swipe;
    private CountDownTimer timer;
    private messageAdapter adapter;
    private List<messageData> messageList = new ArrayList<messageData>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for (int i = 0; i < 10; i++) {
            messageData mData = new messageData();
            mData.setTitle("Title" + i);
            messageList.add(mData);

        }


        initView();


    }

    private void initView() {
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        lvMessage = (ListView) findViewById(R.id.lvMessage);
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipe.setOnRefreshListener(this);
        // 頂部刷新的樣式
        swipe.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light);
        if (messageList.size() > 0) {
            adapter = new messageAdapter(MainActivity.this);
            adapter.sethomeList(messageList);
            lvMessage.setAdapter(adapter);
        } else {
            progressbar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRefresh() {
    }


}
