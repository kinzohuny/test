package com.btw.leetcode.q355;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Twitter1 {

    public Map<Integer, Map<Integer, Integer>> tweetMap; 
    public Map<Integer, Set<Integer>> followMap;
	public int sort=0;
    
    /** Initialize your data structure here. */
    public Twitter1() {
        tweetMap = new HashMap<Integer, Map<Integer, Integer>>(); 
        followMap = new HashMap<Integer, Set<Integer>>();
    }
    
    /** Compose a new tweet. */
    public void postTweet(int userId, int tweetId) {
    	if(tweetMap.get(userId)==null){
    		tweetMap.put(userId, new HashMap<Integer, Integer>());
    	}
    	tweetMap.get(userId).put(getSort(), tweetId);
    }
    
    /** Retrieve the 10 most recent tweet ids in the user's news feed. Each item in the news feed must be posted by users who the user followed or by the user herself. Tweets must be ordered from most recent to least recent. */
    public List<Integer> getNewsFeed(int userId) {
    	List<Integer> list = new ArrayList<Integer>();
    	Set<Integer> set = new HashSet<Integer>();
    	set.add(userId);
    	if(followMap.get(userId)!=null){
    		set.addAll(followMap.get(userId));
    	}
    	Map<Integer, Integer> map = new HashMap<Integer, Integer>();
    	for(Integer uid : set){
    		if(tweetMap.get(uid)!=null){
    			map.putAll(tweetMap.get(uid));
    		}
    	}
    	List<Integer> keys = new ArrayList<Integer>(map.keySet());
    	Collections.sort(keys);
    	for(int i=keys.size()-1;i>=0;i--){
    		list.add(map.get(keys.get(i)));
    		if(list.size()>=10){
    			break;
    		}
    	}
    	return list;
    }
    
    /** Follower follows a followee. If the operation is invalid, it should be a no-op. */
    public void follow(int followerId, int followeeId) {
        if(followMap.get(followerId)==null){
        	followMap.put(followerId, new HashSet<Integer>());
        }
       	followMap.get(followerId).add(followeeId);
    }
    
    /** Follower unfollows a followee. If the operation is invalid, it should be a no-op. */
    public void unfollow(int followerId, int followeeId) {
        if(followMap.get(followerId)==null){
        	followMap.put(followerId, new HashSet<Integer>());
        }
       	followMap.get(followerId).remove(followeeId);
    }
    
    public int getSort(){
    	return sort++;
    }
}