package com.xb.powerplatform.education_and_training.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xb.powerplatform.DB.DbManager;
import com.xb.powerplatform.DB.MyDatabaseHelper;
import com.xb.powerplatform.R;
import com.xb.powerplatform.education_and_training.adapter.EducationMoreSelectAdapter;
import com.xb.powerplatform.education_and_training.bean.Question;
import com.xb.powerplatform.education_and_training.myview.VoteSubmitViewPager;
import com.xb.powerplatform.education_and_training.util.BaseRequestAssessLisenter;
import com.xb.powerplatform.thread.GetDataThread;
import com.xb.powerplatform.utilsclass.base.Constant;
import com.xb.powerplatform.utilsclass.myViews.StatusBarUtils;
import com.xb.powerplatform.utilsclass.utils.ProgressDialogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MoreSelectActivity extends AppCompatActivity {

    List<Question.BodyBean.ListBean> beanList = new ArrayList<Question.BodyBean.ListBean>();
    List<View> viewItems = new ArrayList<View>();
    @Bind(R.id.left)
    TextView left;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.right)
    TextView right;
    @Bind(R.id.vote_submit_viewpager)
    VoteSubmitViewPager voteSubmitViewpager;
    @Bind(R.id.vote_submit_linear_dot)
    LinearLayout voteSubmitLinearDot;
    @Bind(R.id.vote_submit_relative)
    RelativeLayout voteSubmitRelative;
    EducationMoreSelectAdapter adapter;

    MyDatabaseHelper helper;
    String classId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_educationmo_ni);
        ButterKnife.bind(this);
        left.setVisibility(View.GONE);
        new StatusBarUtils().setWindowStatusBarColor(MoreSelectActivity.this, R.color.color_bg_selected);
        title.setText(getResources().getString(R.string.moselect));
        helper = DbManager.getInstance(MoreSelectActivity.this);
        Intent intent = getIntent();
        classId = intent.getStringExtra("classId");
        ProgressDialogUtil.startLoad(this, Constant.GETDATA);
        //查询数据
        getAssessData();
    }

    //子线程获取数据
    private void getAssessData() {
        GetDataThread.getMoreSelectData(this, classId, helper, beanList, viewItems, new BaseRequestAssessLisenter() {
            @Override
            public void success(Object o, Object o2) {
                beanList = (List<Question.BodyBean.ListBean>) o2;
                viewItems = (List<View>) o;
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }

            @Override
            public void fail(String message) {

            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adapter = new EducationMoreSelectAdapter(MoreSelectActivity.this, viewItems, beanList);
            voteSubmitViewpager.setAdapter(adapter);
            voteSubmitViewpager.getParent()
                    .requestDisallowInterceptTouchEvent(false);
            ProgressDialogUtil.stopLoad();
        }
    };

    /**
     * @param index 根据索引值切换页面
     */
    public void setCurrentView(int index) {
        voteSubmitViewpager.setCurrentItem(index);
    }
}
