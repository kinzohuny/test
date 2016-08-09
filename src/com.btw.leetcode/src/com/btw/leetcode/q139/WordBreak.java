package com.btw.leetcode.q139;

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
public class WordBreak {

	public static void main(String[] args) {
		String s;
		Set<String> set = new HashSet<String>();
		Solution solution = new Solution();
		
		
		s = "leetcode";
		set = new HashSet<String>(Arrays.asList(new String[]{
				"leet",
				"code",
				"le",
				"leetco",
				"etco"
				}));
		solution.wordBreak(s, set);
		
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
		solution.wordBreak(s, set);
		
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
		solution.wordBreak(s, set);
		
	}

}

