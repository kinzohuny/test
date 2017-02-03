package com.btw.hackerrank.JavaLoopsII;

import java.util.Scanner;

public class Solution {
    public static void main(String []argh){
        Scanner in = new Scanner(System.in);
        int t=in.nextInt();
        for(int i=0;i<t;i++){
            int a = in.nextInt();
            int b = in.nextInt();
            int n = in.nextInt();
            test(a, b, n);
        }
        in.close();
    }
    
    public static void test(int a, int b, int n){
    	int temp = a;
    	for(int i=0;i<n;i++){
    		temp = (int) (temp+Math.pow(2, i)*b);
    		System.out.print(temp);
    		System.out.print(" ");
    	}
    	System.out.println();
    }
    
}
