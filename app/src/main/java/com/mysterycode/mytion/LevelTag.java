package com.mysterycode.mytion;

import java.util.List;

/**
 * Describe:
 * Author:MysteryCode
 * Data:2019/3/7
 * Change:
 */
public class LevelTag {
    public String levelTag;//
    public int unSelectDrawable;
    public int SelectedDrawable;
    public List<String> list;

    public LevelTag(String levelTag, int unSelectDrawable, int selectedDrawable, List<String> list) {
        this.levelTag = levelTag;
        this.unSelectDrawable = unSelectDrawable;
        SelectedDrawable = selectedDrawable;
        this.list = list;
    }
}
