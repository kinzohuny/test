package com.btw.leetcode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *	Given a string s and a dictionary of words dict, determine if s can be segmented into a space-separated sequence of one or more dictionary words.
 *
 *	For example, given
 *	s = "leetcode",
 *	dict = ["leet", "code"].
 *
 *	Return true because "leetcode" can be segmented as "leet code".
 *
 * @author Kinzo
 *
 */
public class Q139WordBreak {

	public static void main(String[] args) {
		String s;
		Set<String> set = new HashSet<String>();
		Solution solution = new Solution();
//		Solution_Get solution = new Solution_Get();
		
		
		s = "leetcode";
		set = new HashSet<String>(Arrays.asList(new String[]{
				"leet",
				"code",
				"le",
				"leetco",
				"etco"
				}));
		boolean result = solution.wordBreak(s, set);
		System.out.println("result="+result);
		
		s = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab";
		set = new HashSet<String>(Arrays.asList(new String[]{
				"a",
				"aa",
				"aaa",
				"aaaa",
				"aaaaa",
				"aaaaaa",
				"aaaaaaa",
				"aaaaaaaa",
				"aaaaaaaaa",
				"aaaaaaaaaa"}));
		result = solution.wordBreak(s, set);
		System.out.println("result="+result);
		
		s = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaabaabaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
		set = new HashSet<String>(Arrays.asList(new String[]{
				"aa",
				"aaa",
				"aaaa",
				"aaaaa",
				"aaaaaa",
				"aaaaaaa",
				"aaaaaaaa",
				"aaaaaaaaa",
				"aaaaaaaaaa",
				"ba"
				}));
		result = solution.wordBreak(s, set);
		System.out.println("result="+result);
		
	}
}


class Solution {
	
	public static long times = 0;
	
    public boolean wordBreak(String s, Set<String> wordDict) {
    	if(s==null||s.length()==0||wordDict==null||wordDict.isEmpty()){
    		return false;
    	}
    	int len = s.length();
    	boolean[] can = new boolean[len+1];
    	for(int i=0;i<len;i++){
    		for(int j=i-1;j>0;j--){
    			times++;
    			String x = s.substring(j, i);
    			if(can[j]&&wordDict.contains(s.substring(j, i))){
    				can[i]=true;
    				break;
    			}
    		}
    	}
        System.out.println("times="+times);
        return can[len];
    }
}

class Solution_Get {
	
	private long times;
	
	public boolean wordBreak(String s, Set<String> dict) {
		if (s == null || s.length() == 0 || dict == null || dict.size() == 0)
			return false;
		int len = s.length();
		boolean[] can = new boolean[len + 1];
		can[0] = true;
		times = 0;
		for (int i = 1; i <= len; i++) {
			for (int j = 0; j < i; j++) {
				times++;
				if (can[j] && dict.contains(s.substring(j, i))) {
					can[i] = true;
					break;
				}
			}
		}
		System.out.println(times);
		return can[len];
	}
}