package com.sicong.smartstore.main;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import com.sicong.smartstore.R;
import com.sicong.smartstore.stock_search.view.SearchFragment;
import com.sicong.smartstore.stock_change.view.ChangeFragment;
import com.sicong.smartstore.stock_check.view.CheckFragment;
import com.sicong.smartstore.stock_in.data.model.Statistic;
import com.sicong.smartstore.stock_in.view.InFragment;
import com.sicong.smartstore.stock_out.view.OutFragment;
import com.sicong.smartstore.stock_user.view.UserFragment;
import com.sicong.smartstore.util.network.NetBroadcastReceiver;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //常量
    private final static String TAG = "MainActivity";

    //数据
    private String username;//操作人员的id
    private String check;//校验码
    private String company;//公司
    private List<Statistic> statisticList;//统计结果


    //视图
    private ViewPager pagers;//分页
    private BottomNavigationView bottomNav;//底部导航栏
    private List<Fragment> fragments;
    private InFragment inFragment;
    private OutFragment outFragment;
    private ChangeFragment changeFragment;
    private CheckFragment checkFragment;
    private SearchFragment searchFragment;
    private UserFragment userFragment;

    //广播
    private NetBroadcastReceiver netBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();//初始化控件
        initPagers();//初始化分页
        initBottomNav();//初始化底部导航栏

        initNetBoardcastReceiver();//初始化广播
    }


    /**
     * 接收数据
     */
    private void initRecevicerFromScan() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("statisticList")) {
            statisticList = (List<Statistic>) intent.getSerializableExtra("statisticList");
        }
        if (intent != null && intent.hasExtra("check")) {
            check = intent.getStringExtra("check");
        }
        if (intent != null && intent.hasExtra("username")) {
            username = intent.getStringExtra("username");
        }
        if (intent != null && intent.hasExtra("companyId")) {
            company = intent.getStringExtra("companyId");
            Log.e(TAG, "initRecevicerFromScan: " + company, null);
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        pagers = (ViewPager) findViewById(R.id.main_pagers);
        bottomNav = (BottomNavigationView) findViewById(R.id.main_bottom_nav);
    }

    /**
     * 初始化分页
     */
    private void initPagers() {
        //设置屏幕外分页数量
        pagers.setOffscreenPageLimit(4);

        inFragment = new InFragment();
        outFragment = new OutFragment();
        changeFragment = new ChangeFragment();
        checkFragment = new CheckFragment();
        userFragment = new UserFragment();

        fragments = new ArrayList<Fragment>();
        fragments.add(inFragment);
        fragments.add(outFragment);
        fragments.add(changeFragment);
        fragments.add(checkFragment);
        fragments.add(userFragment);

        //适配器
        pagers.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });

        //页面切换事件
        pagers.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            //图标Id
            int[] itemIds = {
                    R.id.stock_in,
                    R.id.stock_out,
                    R.id.stock_change,
                    R.id.stock_check,
                    R.id.stock_user
            };

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                bottomNav.setSelectedItemId(itemIds[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 初始化底部导航栏
     */
    private void initBottomNav() {

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //初始化当前位置
                int position = 0;
                //根据item获取当前位置的页码
                switch (item.getItemId()) {
                    case R.id.stock_in:
                        position = 0;
                        break;
                    case R.id.stock_out:
                        position = 1;
                        break;
                    case R.id.stock_change:
                        position = 2;
                        break;
                    case R.id.stock_check:
                        position = 3;
                        break;
                    case R.id.stock_user:
                        position = 4;
                        break;

                }
                //选择分页的页码
                pagers.setCurrentItem(position);
                //设置当前图标为选中状态
                item.setChecked(true);
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initRecevicerFromScan();
        initNetBoardcastReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(netBroadcastReceiver);
    }


    public String getUsername() {
        return username;
    }


    public String getCheck() {
        return check;
    }

    public String getCompany() {
        return company;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    public List<Statistic> getStatisticList() {
        return statisticList;
    }

    /**
     * 初始化网络广播
     */
    private void initNetBoardcastReceiver() {
        if (netBroadcastReceiver == null) {
            netBroadcastReceiver = new NetBroadcastReceiver();
            netBroadcastReceiver.setNetChangeListern(new NetBroadcastReceiver.NetChangeListener() {
                @Override
                public void onChangeListener(boolean status) {
                    if (status) {
                        Log.e(TAG, "onChangeListener: 可行", null);

                        InFragment inFragment = (InFragment)fragments.get(0);
                        inFragment.startRequestDataThread();

                        OutFragment outFragmentTmp = (OutFragment) fragments.get(1);
                        outFragmentTmp.startRequestDataThread();

                        ChangeFragment changeFragment = (ChangeFragment) fragments.get(2);
                        changeFragment.startRequestDataThread();

                        CheckFragment checkFragment = (CheckFragment) fragments.get(3);
                        checkFragment.startRequestDataThread();



                    } else {
                        Toast.makeText(MainActivity.this, "无可用的网络，请连接网络", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netBroadcastReceiver, filter);
    }

    //重写物理返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}
