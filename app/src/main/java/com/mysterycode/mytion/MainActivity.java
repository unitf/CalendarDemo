package com.mysterycode.mytion;

import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager vp = findViewById(R.id.vp);


        List<LevelTag> list = new ArrayList<>();
        List<String> list1 = new ArrayList<>();
        list1.add("2019-03-01");
        list1.add("2019-03-11");
        list1.add("2019-03-21");
        list1.add("2019-04-15");
        list1.add("2019-04-25");
        list1.add("2019-04-05");
        list1.add("2019-05-15");
        List<String> list2 = new ArrayList<>();
        list2.add("2019-02-28");
        list2.add("2019-03-02");
        list2.add("2019-03-12");
        list2.add("2019-03-23");
        list2.add("2019-04-14");
        list2.add("2019-04-24");
        list2.add("2019-04-04");
        list2.add("2019-05-14");

        list.add(new LevelTag("回城", R.drawable.d2, R.drawable.d1, list1));
        list.add(new LevelTag("出战", R.drawable.c2, R.drawable.c1, list2));
        String startDate = "2018-01-01";
        String endDate = "2019-10-31";
        final CalendarPagerAdapter calendarPagerAdapter = new CalendarPagerAdapter(this,startDate, endDate, list);
        vp.setAdapter(calendarPagerAdapter);
        vp.setCurrentItem(calendarPagerAdapter.getMonthSpace(startDate, getCurrentDate())-1);
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                calendarPagerAdapter.clearCache(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private String getCurrentDate() {
        Calendar c=Calendar.getInstance();
        SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd");
        return f.format(c.getTime());
    }
}
