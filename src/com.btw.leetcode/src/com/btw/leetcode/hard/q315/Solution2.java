package com.btw.leetcode.hard.q315;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution2 {
    public List<Integer> countSmaller(int[] nums) {
    	List<Integer> list = new ArrayList<Integer>();
    	int len = nums.length;
    	Map<Integer,Integer> map = new HashMap<Integer, Integer>();
    	for(int i=0;i<len;i++){
    		map.put(nums[i], i);
    	}
        return list;
    }
}
