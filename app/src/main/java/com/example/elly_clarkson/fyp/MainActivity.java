package com.example.elly_clarkson.fyp;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.*;
import android.widget.*;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.widget.Toolbar;



import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends FragmentActivity implements
        ViewPager.OnPageChangeListener, TabHost.OnTabChangeListener {

    public static FragmentTabHost mTabHost;
    private LayoutInflater layoutInflater;
    private Class fragmentArray[] = { Fragment1.class, Fragment2.class, Fragment3.class };
    private int imageViewArray[] = { R.drawable.tab_home_btn, R.drawable.tab_search_btn,R.drawable.tab_explore_btn};
    private String textViewArray[] = { "Home", "Search","Explore"};
    private List<Fragment> list = new ArrayList<Fragment>();
    private ViewPager vp;
    public Display display;
    public static int screenWidth;
    public static int screenHeight;
    public static String studentID;
    public static boolean booked=false;
    public ImageButton logout;
    public static WifiManager mWifiManager;
    //public List<ScanResult> srList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPermission();
        setContentView(R.layout.activity_main);
        initService(this);
        logout=(ImageButton)findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        initView();//初始化控件
        initPage();//初始化页面
        Display display = getWindowManager(). getDefaultDisplay();
        Point size = new Point();
        display. getSize(size);
        screenWidth = size. x;
        screenHeight = size. y;

        Intent intent = this.getIntent();
        studentID = intent.getStringExtra("ID");

    }

    //    控件初始化控件
    private void initView() {
        vp = (ViewPager) findViewById(R.id.pager);

        /*实现OnPageChangeListener接口,目的是监听Tab选项卡的变化，然后通知ViewPager适配器切换界面*/
        /*简单来说,是为了让ViewPager滑动的时候能够带着底部菜单联动*/

        vp.addOnPageChangeListener(this);//设置页面切换时的监听器
        layoutInflater = LayoutInflater.from(this);//加载布局管理器

        /*实例化FragmentTabHost对象并进行绑定*/
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);//绑定tahost
        mTabHost.setup(this, getSupportFragmentManager(), R.id.pager);//绑定viewpager

        /*实现setOnTabChangedListener接口,目的是为监听界面切换），然后实现TabHost里面图片文字的选中状态切换*/
        /*简单来说,是为了当点击下面菜单时,上面的ViewPager能滑动到对应的Fragment*/
        mTabHost.setOnTabChangedListener(this);

        int count = textViewArray.length;

        /*新建Tabspec选项卡并设置Tab菜单栏的内容和绑定对应的Fragment*/
        for (int i = 0; i < count; i++) {
            // 给每个Tab按钮设置标签、图标和文字
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(textViewArray[i])
                    .setIndicator(getTabItemView(i));
            // 将Tab按钮添加进Tab选项卡中，并绑定Fragment
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
            mTabHost.setTag(i);
            mTabHost.getTabWidget().getChildAt(i)
                    .setBackgroundResource(R.drawable.selector_tab_background);//设置Tab被选中的时候颜色改变
        }
    }

    /*初始化Fragment*/
    private void initPage() {
        Fragment1 fragment1 = new Fragment1();
        Fragment2 fragment2 = new Fragment2();
        Fragment3 fragment3 = new Fragment3();

        list.add(fragment1);
        list.add(fragment2);
        list.add(fragment3);
        //绑定Fragment适配器
        vp.setAdapter(new MyFragmentAdapter(getSupportFragmentManager(), list));
        mTabHost.getTabWidget().setDividerDrawable(null);
    }

    private View getTabItemView(int i) {
        //将xml布局转换为view对象
        View view = layoutInflater.inflate(R.layout.tab_content, null);
        //利用view对象，找到布局中的组件,并设置内容，然后返回视图
        ImageView mImageView = (ImageView) view
                .findViewById(R.id.tab_imageview);
        TextView mTextView = (TextView) view.findViewById(R.id.tab_textview);
        mImageView.setBackgroundResource(imageViewArray[i]);
        mTextView.setText(textViewArray[i]);
        return view;
    }


    @Override
    public void onPageScrollStateChanged(int arg0) {

    }//arg0 ==1的时候表示正在滑动，arg0==2的时候表示滑动完毕了，arg0==0的时候表示什么都没做，就是停在那。

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }//表示在前一个页面滑动到后一个页面的时候，在前一个页面滑动前调用的方法

    @Override
    public void onPageSelected(int arg0) {//arg0是表示你当前选中的页面位置Postion，这事件是在你页面跳转完毕的时候调用的。
        //if(arg0==1)
            //scanWifi();
        TabWidget widget = mTabHost.getTabWidget();
        int oldFocusability = widget.getDescendantFocusability();
        widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);//设置View覆盖子类控件而直接获得焦点
        mTabHost.setCurrentTab(arg0);//根据位置Postion设置当前的Tab
        widget.setDescendantFocusability(oldFocusability);//设置取消分割线
        //if(arg0==2)Fragment3.xxx();
    }

    @Override
    public void onTabChanged(String tabId) {//Tab改变的时候调用
        int position = mTabHost.getCurrentTab();
        vp.setCurrentItem(position);//把选中的Tab的位置赋给适配器，让它控制页面切换

    }

    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void getPermission(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }
    }
//Create an instance of WifiMAnager
    private void initService(Context context){
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }
//scanWifi function
public void scanWifi(){
    mWifiManager.startScan();
    if(mWifiManager.isWifiEnabled()){
        Fragment2.srList = mWifiManager.getScanResults();

    }
}

}
