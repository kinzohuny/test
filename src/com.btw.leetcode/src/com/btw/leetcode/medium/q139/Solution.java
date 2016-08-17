package com.btw.leetcode.medium.q139;

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
    	//len+1长度的数组，can[x]表示s(0,x)能否被拆分
    	boolean[] can = new boolean[len+1];
    	can[0]=true;
    	for(int i=1;i<len+1;i++){
    		for(int j=i-1;j>=0;j--){
    			times++;
    			//如果s(0,j)和s(j,i)能被拆分，则s(0,i)能被拆分
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
