package com.btw.leetcode.medium.q355;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Twitter {

    public List<Tweet> tweetList; 
    public Map<Integer, Set<Integer>> followMap;
	
    /** Initialize your data structure here. */
    public Twitter() {
        tweetList = new ArrayList<Twitter.Tweet>(); 
        followMap = new HashMap<Integer, Set<Integer>>();
    }
    
    /** Compose a new tweet. */
    public void postTweet(int userId, int tweetId) {
    	tweetList.add(new Tweet(userId, tweetId));
    }
    
    /** Retrieve the 10 most recent tweet ids in the user's news feed. Each item in the news feed must be posted by users who the user followed or by the user herself. Tweets must be ordered from most recent to least recent. */
    public List<Integer> getNewsFeed(int userId) {
    	List<Integer> list = new ArrayList<Integer>();
    	Set<Integer> set = new HashSet<Integer>();
    	set.add(userId);
    	if(followMap.get(userId)!=null){
    		set.addAll(followMap.get(userId));
    	}
    	for(int i=tweetList.size()-1;i>=0;i--){
    		if(set.contains(tweetList.get(i).userId)){
    			list.add(tweetList.get(i).tweetId);
    		}
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
    
	public class Tweet {
		int userId;
		int tweetId;
		Tweet(int userId, int tweetId){
			this.userId = userId;
			this.tweetId = tweetId;
		}
	}
}