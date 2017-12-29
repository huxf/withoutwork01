package com.xb.powerplatform.education_and_training.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jude.rollviewpager.RollPagerView;
import com.xb.powerplatform.DB.Constant;
import com.xb.powerplatform.DB.DbManager;
import com.xb.powerplatform.DB.MyDatabaseHelper;
import com.xb.powerplatform.R;
import com.xb.powerplatform.SharedPreferencesHelper;
import com.xb.powerplatform.education_and_training.adapter.TestNormalAdapter;
import com.xb.powerplatform.education_and_training.bean.ClassName;
import com.xb.powerplatform.education_and_training.bean.assess;
import com.xb.powerplatform.education_and_training.presenter.IPresenter;
import com.xb.powerplatform.education_and_training.presenter.impl.Presenterimpl;
import com.xb.powerplatform.education_and_training.view.IView;
import com.xb.powerplatform.utilsclass.myViews.StatusBarUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EducationActivity extends AppCompatActivity implements IView {

    //教育培训
    @Bind(R.id.rellPagerView)
    RollPagerView rellPagerView;
    @Bind(R.id.btn1)
    Button btn1;
    @Bind(R.id.btn2)
    Button btn2;
    @Bind(R.id.btn3)
    Button btn3;
    @Bind(R.id.left)
    ImageView left;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.right)
    TextView right;
    private MyDatabaseHelper helper;
    SQLiteDatabase db;
    List<String> list = new ArrayList<String>();
    List<assess.BodyBean.ListBean> beanListR = new ArrayList<assess.BodyBean.ListBean>();
    List<assess.BodyBean.ListBean> beanListM = new ArrayList<assess.BodyBean.ListBean>();
    List<assess.BodyBean.ListBean> beanListJ = new ArrayList<assess.BodyBean.ListBean>();
    List<assess.BodyBean.ListBean> beanList = new ArrayList<assess.BodyBean.ListBean>();
    List<ClassName> classNamesbeanList = new ArrayList<ClassName>();
    List<ClassName> dwoClassNamesbeanList = new ArrayList<ClassName>();

    IPresenter presenter;
    Intent intent;

    List<String> listName = new ArrayList<>();
    List<String> listId = new ArrayList<>();

    List<String> listNamed = new ArrayList<>();
    List<String> listIdd = new ArrayList<>();

    List<assess.BodyBean.RuleBean> listrule = new ArrayList<>();

    SharedPreferencesHelper preference;
    private String classId;
    String classId1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education);
        ButterKnife.bind(this);
        new StatusBarUtils().setWindowStatusBarColor(EducationActivity.this, R.color.color_bg_selected);

        helper = DbManager.getInstance(EducationActivity.this);
        db = helper.getReadableDatabase();

        left.setVisibility(View.VISIBLE);
        title.setText(getResources().getString(R.string.education_and_training));
        right.setText(getResources().getString(R.string.downlod));

        preference = new SharedPreferencesHelper(this, "login");

        list.add("https://pic4.zhimg.com/03b2d57be62b30f158f48f388c8f3f33_b.png");
        list.add("https://pic1.zhimg.com/4373a4f045e5e9ae16ebd6a624bf6228_b.png");
        //设置播放时间间隔
        rellPagerView.setPlayDelay(3000);
        //设置透明度
        rellPagerView.setAnimationDurtion(500);
        //设置适配器
        rellPagerView.setAdapter(new TestNormalAdapter(rellPagerView, list));

        presenter = new Presenterimpl(this, this);

        getDwoClassNameData();
        if (listNamed.size()!=0){
            title.setText(listNamed.get(0));
            classId=listIdd.get(0);
        }
    }

    //查询数据库获得试题
    private void getData() {
        beanListR.clear();
        beanListM.clear();
        beanListJ.clear();
        beanList.clear();
        Cursor cursor;
        String sql1 = "select * from rule where classid='" + classId + "'";
        cursor = DbManager.queryBySQL(db, sql1, null);
        listrule = DbManager.cursorTorule(cursor);
        int raNum=listrule.get(0).getRadioNum();
        int muNum=listrule.get(0).getMultiNum();
        int juNum=listrule.get(0).getJudgeNum();

        String sqlR = "select * from moni where classid='" + classId + "' and quType='0' order by random() limit '"+raNum+"'";// order by random() limit 100
        cursor = DbManager.queryBySQL(db, sqlR, null);
        beanListR = DbManager.cursorToPerson(cursor);

        String sqlM = "select * from moni where classid='" + classId + "' and quType='1' order by random() limit '"+muNum+"'";// order by random() limit 100
        cursor = DbManager.queryBySQL(db, sqlM, null);
        beanListM = DbManager.cursorToPerson(cursor);

        String sqlJ = "select * from moni where classid='" + classId + "' and quType='2' order by random() limit '"+juNum+"'";// order by random() limit 100
        cursor = DbManager.queryBySQL(db, sqlJ, null);
        beanListJ = DbManager.cursorToPerson(cursor);

        for (int i=0;i<beanListR.size();i++){
            beanList.add(beanListR.get(i));
        }
        for (int i=0;i<beanListM.size();i++){
            beanList.add(beanListM.get(i));
        }
        for (int i=0;i<beanListJ.size();i++){
            beanList.add(beanListJ.get(i));
        }
    }

    //查询数据库获得试题
    private void getClassNameData() {
        Cursor cursor;
        String sql = "select * from className";
        cursor = DbManager.queryBySQL(db, sql, null);
        classNamesbeanList = DbManager.cursorToClassName(cursor);
    }

    //获取已经下载的班级
    private void getDwoClassNameData() {
        listIdd.clear();
        listNamed.clear();
        Cursor cursor;
        String sql = "select * from dwoClassName";
        cursor = DbManager.queryBySQL(db, sql, null);
        dwoClassNamesbeanList = DbManager.cursorToClassName(cursor);
        for (int i = 0; i < dwoClassNamesbeanList.size(); i++) {
            String className = dwoClassNamesbeanList.get(i).getClassName();
            listNamed.add(className);
            String classId = dwoClassNamesbeanList.get(i).getClassId();
            listIdd.add(classId);
        }
    }

    @OnClick({R.id.btn1, R.id.btn2, R.id.btn3, R.id.left, R.id.title, R.id.right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn1:
                getData();
                if (beanList.size() != 0) {
                    intent = new Intent(EducationActivity.this, EducationMoNiActivity.class);
                    intent.putExtra("list", (Serializable) beanList);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, getResources().getString(R.string.first_download), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn2:
                getData();
                if (beanList.size() != 0) {
                    intent = new Intent(EducationActivity.this, EducationZaiXianActivity.class);
                    intent.putExtra("classId", classId);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, getResources().getString(R.string.first_download), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn3:
                classId1 = preference.getData(this, "classId", "");
                if (!classId1.equals("")) {
                    intent = new Intent(EducationActivity.this, RegularAssessActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "暂无考试班级", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.left:
                finish();
                break;
            case R.id.title:
                getDwoClassNameData();
                if (listNamed.size() == 0) {
                    Toast.makeText(EducationActivity.this, getResources().getString(R.string.first_download), Toast.LENGTH_SHORT).show();
                } else {
                    final String[] arrName = (String[]) listNamed.toArray(new String[listNamed.size()]);
                    final String[] arrId = (String[]) listIdd.toArray(new String[listIdd.size()]);
                    new AlertDialog.Builder(EducationActivity.this)
                            .setTitle(getResources().getString(R.string.pelease_select))
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setSingleChoiceItems(arrName,
                                    0, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            dialog.dismiss();
                                            title.setText(arrName[which]);
                                            classId=arrId[which];
                                        }
                                    }).show();
                }
                break;
            case R.id.right:
                //将下载个人信息
                getGetList();
                //list转数组
                final String[] arrName = (String[]) listName.toArray(new String[listName.size()]);
                final String[] arrId = (String[]) listId.toArray(new String[listId.size()]);
                if (listName.size() == 0) {
                    Toast.makeText(this, getResources().getString(R.string.no_question), Toast.LENGTH_SHORT).show();
                } else {
                    //dialog
                    new AlertDialog.Builder(this)
                            .setTitle(getResources().getString(R.string.pelease_select))
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setSingleChoiceItems(arrName,
                                    0, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            dialog.dismiss();
                                            title.setText(arrName[which]);
                                            classId=arrId[which];
                                            getDwoClassNameData();
                                            if (listNamed.contains(arrName[which])) {
                                                Toast.makeText(EducationActivity.this,
                                                        getResources().getString(R.string.download_old),
                                                        Toast.LENGTH_SHORT).show();
                                            } else {
                                                classId=arrId[which];
                                                String dwoStatic = "Yes";
                                                ContentValues values = new ContentValues();
                                                values.put(Constant.CLASSID, arrId[which]);
                                                values.put(Constant.CLASSNAME, arrName[which]);
                                                values.put(Constant.DWOSTATIC, dwoStatic);
                                                db.insert(Constant.TABBLE_DOW_CLASS_NAME, null, values);
                                                values.clear();
                                                //下载试题
                                                presenter.getPresenteerData(arrId[which]);
                                            }
                                        }
                                    }).show();
                }
                break;
        }
    }

    @Override
    public void getViewData(assess assess) {
        int num = assess.getBody().getList().size();
        if (num != 0) {
            for (int i = 0; i < assess.getBody().getList().size(); i++) {
                String ID = assess.getBody().getList().get(i).getId();
                String quContent = assess.getBody().getList().get(i).getQuContent();//题目
                String quAnalyze = assess.getBody().getList().get(i).getQuAnalyze();//题目解析
                String quAnswer = assess.getBody().getList().get(i).getQuAnswer();//答案
                String quA = assess.getBody().getList().get(i).getQuA();
                String quB = assess.getBody().getList().get(i).getQuB();
                String quC = assess.getBody().getList().get(i).getQuC();
                String quD = assess.getBody().getList().get(i).getQuD();
                String quE = assess.getBody().getList().get(i).getQuE();
                String quF = assess.getBody().getList().get(i).getQuF();
                String quType = assess.getBody().getList().get(i).getQuType();//类型 0单选 1多选 2判断
                ContentValues values = new ContentValues();
                values.put(Constant.ID, ID);
                values.put(Constant.QUTYPE, quType);
                values.put(Constant.QUCONTENT, quContent);
                values.put(Constant.QUA, quA);
                values.put(Constant.QUB, quB);
                values.put(Constant.QUC, quC);
                values.put(Constant.QUD, quD);
                values.put(Constant.QUE, quE);
                values.put(Constant.QUF, quF);
                values.put(Constant.QUANSWER, quAnswer);
                values.put(Constant.QUANALYZE, quAnalyze);
                values.put(Constant.CLASSID, classId);
                db.insert(Constant.TABBLE_NAME, null, values);
                values.clear();
            }
            ContentValues values = new ContentValues();
            int radioNum=assess.getBody().getRule().getRadioNum();
            int multiNum=assess.getBody().getRule().getMultiNum();
            int judgeNum=assess.getBody().getRule().getJudgeNum();
            values.put(Constant.RADIONUM, radioNum);
            values.put(Constant.MULTINUM, multiNum);
            values.put(Constant.JUDGENUM, judgeNum);
            values.put(Constant.CLASSID, classId);
            db.insert(Constant.TABBLE_NAME_RULE, null, values);
            values.clear();

            Toast.makeText(this, getResources().getString(R.string.download_ok), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getResources().getString(R.string.no_question), Toast.LENGTH_SHORT).show();
        }
    }

    public void getGetList() {
        getClassNameData();
        listName.clear();
        listId.clear();
        if (classNamesbeanList.size() != 0) {
            for (int i = 0; i < classNamesbeanList.size(); i++) {
                listName.add(classNamesbeanList.get(i).getClassName());
                listId.add(classNamesbeanList.get(i).getClassId());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}