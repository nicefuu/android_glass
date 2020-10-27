package com.example.GuangMing128.ui;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;

public class TestActivity{
    private static String  getResult(int[] data){
        int L = data.length;
        String[] res = new String[L];
        for (int i = 0; i < L; i++){
            int[] tmp = transform_bit(data[i]);
            res[i] = transform_position(tmp);
        }
        String pre = "";
        for (int i = 0; i<L; i++){
            String dcz = res[i];
            int need_l = dcz.length();
            String tmp = dcz.substring(need_l-2, need_l);
            String cur = dcz.substring(0, need_l-2);
            cur = pre + cur;
            res[i] = cur;
            pre = tmp;
        }
        res[0] = pre + res[0];
        StringBuffer res_s = new StringBuffer();
        for (int i = 0; i<L; i++){
            BigInteger decimal = new BigInteger(res[i],2);
            res_s.append(decimal);
            res_s.append(" ");
        }
        return  res_s.toString();
    }

    private  static int[] transform_bit(int a){
        int[] help = new int[32];
        for (int i = 31; i >= 0; i--){
            help[31-i] = a >>> i & 1;
        }
        return help;
//        StringBuffer r = new StringBuffer();
//        for (int i = 0; i < 32; i++){
//            r.append(help[i]);
//        }
//        return r.toString();
    }

    public static void main(String args[]) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line;
        line = br.readLine();
        String[] strings =line.trim().split(" ");
        int[] data = new int[strings.length];
        for (int i = 0 ; i < strings.length; i++){
            data[i] = Integer.parseInt(strings[i]);
        }
        System.out.print(getResult(data));
    }

    private static  String transform_position(int[] a){
        for (int i = 31; i >=0 ; i =i-2){
            int l = i;
            int r = i-1;
            int tmp = a[l];
            a[l] = a[r];
            a[r] = tmp;
        }
        StringBuffer r = new StringBuffer();
        for (int i = 0; i < 32; i++){
            r.append(a[i]);
        }
        return r.toString();
    }
}

