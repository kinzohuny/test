package com.btw.leetcode.hard.q315;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    public List<Integer> countSmaller(int[] nums) {
    	List<Integer> list = new ArrayList<Integer>();
    	int len = nums.length;
    	int[] counts = new int[len];
        for(int i=0; i<len; i++){
        	for(int j=0; j<i; j++){
        		if(nums[i]<nums[j]){
        			counts[j]++;
        		}
        	}
        }
        for(int c : counts){
        	list.add(c);
        }
        return list;
    }
}
