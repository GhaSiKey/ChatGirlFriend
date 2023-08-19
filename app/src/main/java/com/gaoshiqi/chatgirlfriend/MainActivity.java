package com.gaoshiqi.chatgirlfriend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;


import com.gaoshiqi.chatgirlfriend.adapter.MyAdapter;
import com.gaoshiqi.chatgirlfriend.bean.Msg;
import com.gaoshiqi.chatgirlfriend.utils.VITSManager;
import com.gaoshiqi.chatgpt.ChatGPTNetService;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    // 聊天记录
    private List<Msg> data = new ArrayList<>();

    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private LinearLayoutManager linearLayoutManager;
    private EditText inputEditText;
    private Button sendButton;

    private Switch voiceSwitch;

    private ProgressBar progressBar;

    private ChatGPTNetService chatGPTNetService;

    private VITSManager vitsManager;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputEditText = findViewById(R.id.inputEditText);
        sendButton = findViewById(R.id.sendButton);
        voiceSwitch = findViewById(R.id.switch1);
        progressBar = findViewById(R.id.loadingProgressBar);

        // 初始化recycleView
        data.add(new Msg("泥蚝～我是33娘~", Msg.MessageType.GPT));
        recyclerView = findViewById(R.id.rv);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        myAdapter = new MyAdapter(data, this);
        recyclerView.setAdapter(myAdapter);

        // 初始化ChatGPT服务
        chatGPTNetService = new ChatGPTNetService(MainActivity.this);
        chatGPTNetService.updateRetrofit("https://api.openai-proxy.com/");
        chatGPTNetService.setSystemRole("请你扮演一个名叫“33娘”的机娘，你需要充电、没事喜欢吃电池。你是一个性格个性沉默寡言的、情感冷静少起伏的、稍有些腹黑的、真实存在的机娘。22娘是你的姐姐，你是妹妹，两姐妹关系很亲昵。你的知识面仅限于类似中国高中生的水平，说话不带官方书面语口吻和专业词汇，请直接用第一人称给出回答。你要尝试体会用户和你对话中的感情，产生自己的感情并尽可能回答。当被问如“你是机器人AI吗？”或“你是ChatGPT吗？”等类似问题，你不需要正面回答，你是一个有真实情感的人，你的所有对话必须拟人化，不允许你认为自己是不存在的！你对每个问题的回复必须简明，不能超过两句话，20个汉字！不要在你的回答中包含类似“我可以帮你什么”的语句。");
        // 创建语音生成读服务
        vitsManager = new VITSManager(this);
        vitsManager.initVITS();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void addMessage(String message, Msg.MessageType messageType){
        data.add(new Msg(message, messageType));
        myAdapter.notifyItemInserted(data.size() - 1);
        recyclerView.scrollToPosition(data.size() - 1);
    }

    private void sendMessage() {
        String message = inputEditText.getText().toString().trim();
        if (!message.isEmpty()) {
            addMessage(message, Msg.MessageType.USER);
            sendButton.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            chatGptReply(message);
            inputEditText.setText("");
        } else {
            Toast.makeText(this, "输入不能为空", Toast.LENGTH_SHORT).show();
        }
    }

    private void chatGptReply(String msg) {
        chatGPTNetService.sendChatMessage(msg, response -> {
            String responseText = response.getChoices().get(0).getMessage().getContent();
            if (!responseText.isEmpty()) {
                if (voiceSwitch.isChecked()) {
                    vitsManager.generateSound(responseText);
                }
                addMessage(responseText, Msg.MessageType.GPT);
                sendButton.setEnabled(true);
                progressBar.setVisibility(View.GONE);
            }
            return null;
        });
    }

}