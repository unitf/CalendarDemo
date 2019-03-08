package com.mysterycode.mytion;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarPagerAdapter extends PagerAdapter {
    public static Map<String, int[]> map = new HashMap<>();
    /**
     * 记录需要展示的数量
     */
    private int count;

    /**
     * 初始化事件监听
     */
    private String startDate;
    private List<LevelTag> list;
    private Context context;
    private Map<Integer, CalendarView> cache = new HashMap<>();

    public CalendarPagerAdapter(Context context, String startDate, String endDate, List<LevelTag> list) {
        this.context = context;
        this.startDate = startDate;
        this.count = getMonthSpace(startDate, endDate);
        this.list = list;
    }

    /***
     * @comments 计算两个时间的时间差
     */
    public int getMonthSpace(String StartDate, String endDate) {
        int nMonth = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar bef = Calendar.getInstance();
        Calendar aft = Calendar.getInstance();
        try {
            bef.setTime(sdf.parse(StartDate));
            aft.setTime(sdf.parse(endDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int result = aft.get(Calendar.MONTH) - bef.get(Calendar.MONTH) + 1;
        int month = (aft.get(Calendar.YEAR) - bef.get(Calendar.YEAR)) * 12;
        nMonth = Math.abs((month + result));
        return nMonth == 0 ? 1 : nMonth;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    private CalendarView view;

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        view = new CalendarView(container.getContext());
        view.setColor(Color.parseColor("#999999"), Color.parseColor("#333333"), Color.parseColor("#999999"));
        view.setTodayResId(R.drawable.c1, R.drawable.d1);
        view.setYearAndMonth(view.getCurrentDate(startDate, position));
        view.setCollections(list);
        view.setOnItemClick(new CalendarView.OnItemClick() {
            @Override
            public void click(String date) {
                Toast.makeText(context, date, Toast.LENGTH_SHORT).show();
            }
        });
        cache.put(position, view);
        container.addView(view);
        return view;
    }


//    //根据position获取到此页面需要展示年月份的数据
//    public int[] getYearAndMonth(int position) {
//        int cMonth = startMonth + position;
//        int cYear = startYear + cMonth / 12;
//        if (cMonth % 12 == 1) {
//            //增加一年
//            cMonth = 1;
//        } else if (cMonth % 12 == 0) {
//            //正好12月
//            cMonth = 12;
//            cYear--;
//        } else {
//            cMonth = cMonth % 12;
//        }
//        return new int[]{cYear, cMonth};
//    }

    //根据年月反推position
//    public int getPosition(int[] yearAndMonth) {
//        int year = yearAndMonth[0];
//        int month = yearAndMonth[1];
//        //计算需要展示的所有月数
//        if (year == startYear) {
//            if (month > startMonth) {
//                return month - startMonth;
//            } else {
//                return 0;
//            }
//        }
//        return (year - startYear - 1) * 12 + (12 - startMonth) + month;
//    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (container != null) {
            ViewGroup parent = (ViewGroup) container.getParent();
            if (parent != null) {
                parent.removeView((CalendarView) object);
            }
        }
    }

    public void clearCache(int position) {
        CalendarView calendarView = cache.get(position);
        if (null != calendarView)
            calendarView.clearCache(calendarView.getCurrentDate(startDate, position));
    }
}
