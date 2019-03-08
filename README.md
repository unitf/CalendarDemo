# CalendarDemo
简单的日历控件绘制

原理：
    1、通过组合控件的方式初始化日期；
    2、对应状态覆盖特定日期；
    3、通过传入特定的‘分类’区分日期LevelTag为X分类的日期集合，再通过List<LevelTag>.add(X)将多种分类传入
    Bug：cache的方式清除缓存时存在未能覆盖的可能，但可以采取特殊drawable去覆盖达到效果；
        
