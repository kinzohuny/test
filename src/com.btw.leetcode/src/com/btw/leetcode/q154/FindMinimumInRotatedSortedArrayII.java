package com.btw.leetcode.q154;


/**
 * Suppose a sorted array is rotated at some pivot unknown to you beforehand.
 * 
 * (i.e., 0 1 2 4 5 6 7 might become 4 5 6 7 0 1 2).
 * 
 * Find the minimum element.
 * 
 * The array may contain duplicates.
 *
 * @author Kinzo
 *
 */
public class FindMinimumInRotatedSortedArrayII {

	public static void main(String[] args) {
		Solution solution = new Solution();
		int[] nums = {1,2,3};
		
		solution.findMin(nums);
	}
	
}