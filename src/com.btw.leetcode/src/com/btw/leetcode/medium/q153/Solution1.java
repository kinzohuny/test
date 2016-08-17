package com.btw.leetcode.medium.q153;


public class Solution1 {
    public int findMin(int[] nums) {
    	return findMin(nums, 0, nums.length-1);
    }
    
    public int findMin(int[] nums, int left, int right){
    	if(left == right){
    		return nums[left];
    	}
    	if(right - left == 1){
    		return Math.min(nums[left], nums[right]);
    	}
    	int mid = left+(right-left)/2;
    	if(nums[left]<nums[right]){
    		return nums[left];
    	}else if(nums[left]>nums[mid]){
    		return findMin(nums, left, mid);
    	}else{
    		return findMin(nums, mid, right);
    	}
    }
}
