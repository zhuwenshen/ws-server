package com.zhuwenshen.ws.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.n3r.idworker.IdWorker;
import org.n3r.idworker.WorkerIdStrategy;
import org.n3r.idworker.strategy.DefaultWorkerIdStrategy;
import org.n3r.idworker.utils.Utils;

public class MySid {

	private static WorkerIdStrategy workerIdStrategy;
    private static IdWorker idWorker;

    static {
        configure(DefaultWorkerIdStrategy.instance);
    }


    public static synchronized void configure(WorkerIdStrategy custom) {
        if (workerIdStrategy != null) workerIdStrategy.release();
        workerIdStrategy = custom;
        idWorker = new IdWorker(workerIdStrategy.availableWorkerId()) {
            @Override
            public long getEpoch() {
                return Utils.midnightMillis();
            }
        };
    }

    /**
     * 一天最大毫秒86400000，最大占用27比特
     * 27+10+11=48位 最大值281474976710655(15字)，YK0XXHZ827(10字)
     * 14位(yyyyMMddHHmmss)+10位，共24位
     *
     * @return 固定24位字符串
     */

    public static String next() {
        long id = idWorker.nextId();
        String yyMMdd = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
       // return yyMMdd + String.format("%014d", id);
        return yyMMdd + Utils.padLeft(Utils.encode(id), 10, '0');
    }
    
    public static String nextInt() {
        long id = idWorker.nextId();        
        return String.format("%014d", id);
    }


    /**
     * 返回固定16位的字母数字混编的字符串。
     */
    public static String nextShort() {
        long id = idWorker.nextId();
        String yyMMdd = new SimpleDateFormat("yyMMdd").format(new Date());
        return yyMMdd + Utils.padLeft(Utils.encode(id), 10, '0');
    }
    
    /**
     * 返回固定22位的字母数字混编的字符串。
     */
    public static String nextLong() {
        long id = idWorker.nextId();
        String yyyyMMdd = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
        return yyyyMMdd + Utils.padLeft(Utils.encode(id), 10, '0');
    }
}
