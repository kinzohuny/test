package com.btw.leetcode;

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
		Set<String> set = new HashSet<String>();
		set.add("leet");
		set.add("code");
		set.add("le");
		boolean result = new Solution().wordBreak("leetcode", set);
		System.out.println("result="+result);
	}
}


class Solution {
    public boolean wordBreak(String s, Set<String> wordDict) {
        if(s==null || s.length()==0){
            return true;
        }
        boolean result = false;
        
        return result;
    }
}