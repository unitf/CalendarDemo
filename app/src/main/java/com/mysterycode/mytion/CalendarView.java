package com.mysterycode.mytion;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by cretin on 16/9/21.
 */
public class CalendarView extends LinearLayout {
    private Context mContext;
    private int currYear;
    private int currMonth;

    public void clearCache(String currentDate) {
        int[] ints = CalendarPagerAdapter.map.get(currentDate);
        if (null!=ints){
            TextView textView = days[ints[0]][ints[1]];
            WeekEntity entity= (WeekEntity) days[ints[0]][ints[1]].getTag();
            days[ints[0]][ints[1]].setBackgroundResource(entity.unId);
            days[ints[0]][ints[1]].setTextColor(entity.txtColor);
            CalendarPagerAdapter.map.put(currentDate, null);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, String date);
    }

    private OnItemClickListener listener;

    public OnItemClickListener getListener() {
        return listener;
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * 承载控件
     */
    //记录存放日期控件的LinearLayout
    private LinearLayout[] layouts = new LinearLayout[6];
    //单个日期
    private LinearLayout[][] containers = new LinearLayout[6][7];
    //创建一个二维数组来存放显示日期的控件
    private TextView[][] days = new TextView[6][7];
    //日期的标记
    private TextView[][] dayType = new TextView[6][7];

    //今日年份 今日月份 和 今日日期
    private int tYear;
    private int tMonth;
    private int tDay;

    private int preColor;//前一个月
    private int nowColor;//现在
    private int nextColor;//下一个月
    private int todayUnSelectDrawable;//今天未选中ResId
    private int todaySelectedDrawable;//今天选中ResId

    private String minDate;
    private String maxDate;
    private String yearAndMonth;

    public void setYearAndMonth(String yearAndMonth) {
        if (!yearAndMonth.contains("-")) {
            throw new IllegalArgumentException("传入的参数不是\"YYYY-MM\"格式");
        }
        this.yearAndMonth = yearAndMonth;
        String[] split = yearAndMonth.split("-");
        currYear = Integer.parseInt(split[0]);
        currMonth = Integer.parseInt(split[1]);
    }

    public void setMinDate(String minDate, String maxDate) {
        if (!minDate.contains("-") || !maxDate.contains("-")) {
            throw new IllegalArgumentException("传入的参数不是\"YYYY-MM-dd\"格式");
        }
        this.minDate = minDate;
        this.maxDate = maxDate;
    }

    public CalendarView(Context context) {
        super(context);
        init(context);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CalendarView);
        preColor = ta.getColor(R.styleable.CalendarView_preColor, Color.parseColor("#999999"));
        nowColor = ta.getColor(R.styleable.CalendarView_nowColor, Color.parseColor("#333333"));
        nextColor = ta.getColor(R.styleable.CalendarView_nextColor, Color.parseColor("#999999"));
        todaySelectedDrawable = ta.getResourceId(R.styleable.CalendarView_todaySelectDrawable, 0);
        todayUnSelectDrawable = ta.getResourceId(R.styleable.CalendarView_todayUnSelectDrawable, 0);
        init(context);
    }

    public CalendarView setColor(int preColor, int nowColor, int nextColor) {
        this.preColor = preColor;
        this.nowColor = nowColor;
        this.nextColor = nextColor;
        return this;
    }

    public CalendarView setTodayResId(int todaySelectedDrawable, int todayUnSelectDrawable) {
        this.todaySelectedDrawable = todaySelectedDrawable;
        this.todayUnSelectDrawable = todayUnSelectDrawable;
        return this;
    }

    //初始化
    private void init(Context context) {
        mContext = context;
        setOrientation(VERTICAL);
        if (0 == tYear) {
            Calendar calendar = Calendar.getInstance();
            tYear = calendar.get(Calendar.YEAR);
            tMonth = calendar.get(Calendar.MONTH) + 1;
            tDay = calendar.get(Calendar.DAY_OF_MONTH);
        }

        initBodyView(mContext);
        initDay();
    }

    //初始化整体布局
    private void initBodyView(Context context) {
        //添加日期的数据 6行
        for (int i = 0; i < 6; i++) {
            layouts[i] = new LinearLayout(context);
            layouts[i].setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            layouts[i].setOrientation(HORIZONTAL);

            for (int j = 0; j < 7; j++) {
                containers[i][j] = new LinearLayout(context);
                containers[i][j].setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(46f), 1));
                containers[i][j].setOrientation(LinearLayout.VERTICAL);
                containers[i][j].setGravity(Gravity.CENTER_HORIZONTAL);


                //日期
                days[i][j] = new TextView(context);
                days[i][j].setLayoutParams(new LayoutParams(DensityUtil.dp2px(27f), DensityUtil.dp2px(27f)));
                days[i][j].setTextColor(Color.parseColor("#333333"));
                days[i][j].setIncludeFontPadding(false);
                days[i][j].setTextSize(13);
                days[i][j].setGravity(Gravity.CENTER);
                containers[i][j].addView(days[i][j]);


                //标记
                LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, DensityUtil.dp2px(3f), 0, DensityUtil.dp2px(1f));
                dayType[i][j] = new TextView(context);
                dayType[i][j].setIncludeFontPadding(false);
                dayType[i][j].setLayoutParams(layoutParams);
                dayType[i][j].setTextColor(Color.parseColor("#BDBDBD"));
                dayType[i][j].setTextSize(8);
                dayType[i][j].setGravity(Gravity.CENTER);
                containers[i][j].addView(dayType[i][j]);

                //设置点击日期
                final int finalI = i;
                final int finalJ = j;
                containers[i][j].setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView selectTxt = days[finalI][finalJ];
                        WeekEntity weekEntity = (WeekEntity) selectTxt.getTag();
                        selectTxt.setBackgroundResource(weekEntity.inId);
                        selectTxt.setTextColor(Color.parseColor("#FFFFFF"));
                        int[] coordinate = CalendarPagerAdapter.map.get(yearAndMonth);
                        if (null != coordinate) {
                            if (finalI==coordinate[0]&&finalJ==coordinate[1]){
                                return;
                            }
                            TextView preTxt = days[coordinate[0]][coordinate[1]];
                            WeekEntity preEntity = (WeekEntity) preTxt.getTag();
                            preTxt.setBackgroundResource(preEntity.unId);
                            preTxt.setTextColor(preEntity.txtColor);
                            if (null!=onItemClick) onItemClick.click(getCurrentDay(yearAndMonth + "-" + cover(weekEntity.day), weekEntity.preOrNext));
                        }
                        CalendarPagerAdapter.map.put(yearAndMonth, new int[]{finalI, finalJ});
                    }
                });
                layouts[i].addView(containers[i][j]);
            }
            addView(layouts[i]);
        }
    }

    /**
     * 根据位置获取年月
     * @param startDate
     * @param position
     * @return
     */
    public String getCurrentDate(String startDate,int position) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            calendar.setTime(sdf.parse(startDate));
        } catch (ParseException e) {

        }
        if (position==-1){
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)-1);
        }else {
            calendar.add(Calendar.MONTH, position);
        }
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM");
        return sdf1.format(calendar.getTime());
    }

    /**
     * 获取当前日期
     * @param startDate
     * @param position
     * @return
     */
    public String getCurrentDay(String startDate,int position) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            calendar.setTime(sdf.parse(startDate));
        } catch (ParseException e) {

        }
        if (position==-1){
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)-1);
        }else {
            calendar.add(Calendar.MONTH, position);
        }
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        return sdf1.format(calendar.getTime());
    }

    public interface OnItemClick {
        void click(String date);
    }

    private OnItemClick onItemClick;

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }


    private List<WeekEntity> list;
    private Calendar mCalendar;

    /**
     * 获取当月第一天周几
     *
     * @param year
     * @param month
     * @return
     */
    private int getFirstDayOfWeek(int year, int month) {
        Calendar calendar = setCurrentMonth(year, month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }

    /**
     * 获取当月天数
     *
     * @param year
     * @param month
     * @return
     */
    private int getMaxMonthDay(int year, int month) {
        Calendar calendar = setCurrentMonth(year, month);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取当前月份的信息
     *
     * @param year
     * @param month
     * @return
     */
    private Calendar setCurrentMonth(int year, int month) {
        if (null == mCalendar)
            mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.YEAR, year);
        //注意月份-1
        mCalendar.set(Calendar.MONTH, month - 1);
        return mCalendar;
    }

    //获取上一个月的最后一天
    private int getLastDayPreMonth(int year, int month) {
        if (null == mCalendar)
            mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month - 1);
        mCalendar.roll(Calendar.MONTH, -1);//12个月循环，减少1个月，区别add
        return mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);//当月最大值
    }

    private class WeekEntity {
        int day;
        int preOrNext;
        String tag;
        int unId;
        int inId;
        int txtColor;

        public WeekEntity(int day, int preOrNext, String tag, int unId, int inId, int txtColor) {
            this.day = day;
            this.preOrNext = preOrNext;
            this.tag = tag;
            this.unId = unId;
            this.inId = inId;
            this.txtColor = txtColor;
        }
    }

    public static final int PRE_MONTH = -1;
    public static final int NOW_MONTH = 0;
    public static final int NEXT_MONTH = 1;


    private void initDay() {
        if (null == dateList) return;
        list = new ArrayList<>();
        //加入上个月数据
        int firstDayOfWeek = getFirstDayOfWeek(currYear, currMonth);
        int lastDayPreMonth = getLastDayPreMonth(currYear, currMonth);
        int limit = 0 == firstDayOfWeek ? 7 : firstDayOfWeek;

        for (int i = limit; i > 0; i--) {
            int preMonthDay = lastDayPreMonth - i + 1;
            list.add(judgeDay(currYear, currMonth - 1, preMonthDay, PRE_MONTH));
        }
        //添加当月数据
        int maxMonthDay = getMaxMonthDay(currYear, currMonth);
        for (int nowMonthDay = 1; nowMonthDay < maxMonthDay + 1; nowMonthDay++) {
            //低于当前日期不可点击
            list.add(judgeDay(currYear, currMonth, nowMonthDay, NOW_MONTH));
        }
        //添加下个月数据
        for (int nextMonthDay = 1; nextMonthDay < 14; nextMonthDay++) {
            list.add(judgeDay(currYear, currMonth + 1, nextMonthDay, NEXT_MONTH));
        }


        //绘制日历
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                int position = i * 7 + j;
                WeekEntity weekEntity = list.get(position);
                int day = weekEntity.day;
                days[i][j].setText(String.valueOf(day));
                if (!TextUtils.isEmpty(weekEntity.tag)) {
                    dayType[i][j].setText(weekEntity.tag);
                    dayType[i][j].setTextColor(Color.parseColor("#9ea2a3"));
                }
                //设置文字颜色
                days[i][j].setTextColor(weekEntity.txtColor);
                days[i][j].setTag(weekEntity);
                days[i][j].setBackgroundResource(weekEntity.unId);
                if (days[i][j].equals("今天")) {
                    days[i][j].setBackgroundResource(todaySelectedDrawable);
                }
            }
        }
    }

    private WeekEntity judgeDay(int year, int month, int day, int preOrNext) {
        if (month == 0) {
            year -= 1;
            month = 12;
        }

        String cDay = year + "-" + cover(month) + "-" + cover(day);
        if ((tYear + "-" + cover(tMonth) + "-" + cover(tDay)).equals(cDay)) {
            return new WeekEntity(day, preOrNext, "今日", todayUnSelectDrawable, todaySelectedDrawable, getTxtColor(preOrNext));
        }
        for (int i = 0; i < dateList.size(); i++) {
            LevelTag levelTag = dateList.get(i);
            List<String> list = levelTag.list;
            if (list.contains(cDay)) {
                return new WeekEntity(day, preOrNext, levelTag.levelTag, levelTag.unSelectDrawable, levelTag.SelectedDrawable, getTxtColor(preOrNext));
            }
        }
        return new WeekEntity(day, preOrNext, "", R.drawable.white, todayUnSelectDrawable, getTxtColor(preOrNext));
    }

    private int getTxtColor(int preOrNext) {
        switch (preOrNext) {
            case -1:
                return preColor;
            case 0:
                return nowColor;
            case 1:
                return nextColor;
        }
        return 0;
    }

    private String cover(int num) {
        if (num <= 9) return "0" + num;
        return String.valueOf(num);
    }

    private List<LevelTag> dateList;

    /**
     * 设置基础数据
     *
     * @param dateList
     */
    public void setCollections(List<LevelTag> dateList) {
        this.dateList = dateList;
        initDay();
    }
}
