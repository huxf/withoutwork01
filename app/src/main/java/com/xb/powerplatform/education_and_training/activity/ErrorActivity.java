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
import com.xb.powerplatform.education_and_training.adapter.EducationErrorAdapter;
import com.xb.powerplatform.education_and_training.bean.Question;
import com.xb.powerplatform.education_and_training.myview.VoteSubmitViewPager;
import com.xb.powerplatform.education_and_training.util.BaseRequestAssessLisenter;
import com.xb.powerplatform.thread.GetDataThread;
import com.xb.powerplatform.utilsclass.base.AlertDialogCallBack;
import com.xb.powerplatform.utilsclass.base.Constant;
import com.xb.powerplatform.utilsclass.myViews.Header;
import com.xb.powerplatform.utilsclass.myViews.StatusBarUtils;
import com.xb.powerplatform.utilsclass.utils.AlertDialogUtil;
import com.xb.powerplatform.utilsclass.utils.ProgressDialogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ErrorActivity extends AppCompatActivity {
    List<Question.BodyBean.ListBean> beanList = new ArrayList<Question.BodyBean.ListBean>();
    List<View> viewItems = new ArrayList<View>();
    //    @Bind(R.id.left)
//    ImageView left;
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
    EducationErrorAdapter adapter;

    MyDatabaseHelper helper;
    String classId = null;
    @Bind(R.id.header)
    Header header;
    @Bind(R.id.left)
    TextView left;
    @Bind(R.id.activity_educationmo_ni)
    LinearLayout activityEducationmoNi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_educationmo_ni);
        ButterKnife.bind(this);
        new StatusBarUtils().setWindowStatusBarColor(ErrorActivity.this, R.color.color_bg_selected);
        left.setVisibility(View.GONE);
        title.setText(getResources().getString(R.string.error));
        helper = DbManager.getInstance(ErrorActivity.this);
        Intent intent = getIntent();
        classId = intent.getStringExtra("classId");
        ProgressDialogUtil.startLoad(this, Constant.GETDATA);
        //查询数据
        getAssessData();
    }

    //子线程获取数据
    private void getAssessData() {
        GetDataThread.getErrorData(this, classId, helper, beanList, viewItems, new BaseRequestAssessLisenter() {
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
            if (beanList.size()==0){
                new AlertDialogUtil(ErrorActivity.this).showDialog(getResources().getString(R.string.no_assess),
                        new AlertDialogCallBack() {
                            @Override
                            public void confirm() {
                                finish();
                            }

                            @Override
                            public void cancel() {
                                finish();
                            }
                        });
                //new AlertDialogUtil(ErrorActivity.this).showSmallDialog(getResources().getString(R.string.no_assess));
            }else {
                adapter = new EducationErrorAdapter(ErrorActivity.this, viewItems, beanList);
                voteSubmitViewpager.setAdapter(adapter);
                voteSubmitViewpager.getParent()
                        .requestDisallowInterceptTouchEvent(false);
                ProgressDialogUtil.stopLoad();
            }
        }
    };

    /**
     * @param index 根据索引值切换页面
     */
    public void setCurrentView(int index) {
        voteSubmitViewpager.setCurrentItem(index);
    }
}
