package com.btw.leetcode.q139;

import java.util.Arrays;
import java.util.Set;

public class Solution {
	
    public boolean wordBreak(String s, Set<String> wordDict) {
    	if(s==null||s.length()==0||wordDict==null||wordDict.isEmpty()){
    		return false;
    	}
    	System.out.println("=============");
    	System.out.println("s="+s);
    	System.out.println("wordDict="+Arrays.toString(wordDict.toArray()));
    	long times = 0;
    	int len = s.length();
    	boolean[] can = new boolean[len+1];
    	can[0]=true;
    	for(int i=1;i<len+1;i++){
    		for(int j=i-1;j>=0;j--){
    			times++;
    			if(can[j]&&wordDict.contains(s.substring(j, i))){
    				can[i]=true;
    				break;
    			}
    		}
    	}
        System.out.println("times="+times);
        System.out.println("result="+can[len]);
        return can[len];
    }
}
