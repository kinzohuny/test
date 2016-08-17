package com.btw.leetcode.medium.q153;

/**
 * https://leetcode.com/problems/Find-Minimum-in-Rotated-Sorted-Array/
 * 
 * Suppose a sorted array is rotated at some pivot unknown to you beforehand.
 * 
 * (i.e., 0 1 2 4 5 6 7 might become 4 5 6 7 0 1 2).
 * 
 * Find the minimum element.
 * 
 * You may assume no duplicate exists in the array.
 *
 * @author Kinzo
 *
 */
public class FindMinimumInRotatedSortedArray {
	public static void main(String[] args) {
		Solution1 solution = new Solution1();
		int[] nums = {6,7,1,2,3,4,5};
		
		System.out.println(solution.findMin(nums));
	}
}
