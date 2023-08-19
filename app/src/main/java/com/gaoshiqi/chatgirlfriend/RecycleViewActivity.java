package com.gaoshiqi.chatgirlfriend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.gaoshiqi.chatgirlfriend.adapter.MyAdapter;
import com.gaoshiqi.chatgirlfriend.bean.Msg;

import java.util.ArrayList;
import java.util.List;

public class RecycleViewActivity extends AppCompatActivity {

    private List<Msg> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_view);

        for (int i = 0; i < 1000; i++) {
            Msg msg = new Msg("test" + i, Msg.MessageType.GPT);
            data.add(msg);
        }

        // 初始化recycleview
        RecyclerView recyclerView = findViewById(R.id.rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        MyAdapter myAdapter = new MyAdapter(data, this);
        recyclerView.setAdapter(myAdapter);
    }
}