package com.codegym.task.task39.task3913;

import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

public class Solution {
    public static void main(String[] args) {
        LogParser logParser = new LogParser(Paths.get("C:\\Users\\Admin\\Desktop\\temp"));
        //System.out.println(logParser.getNumberOfUniqueIPs(null, new Date()));
        //System.out.println(logParser.getAllUsers());
        //System.out.println(logParser.getNumberOfUsers(new Date(), null));
        //System.out.println(logParser.getIPsForEvent(Event.LOGIN, null, null));
        //System.out.println(logParser.getDatesForUserAndEvent("Amigo" ,Event.ATTEMPT_TASK, null, null));


//        System.out.println(logParser.execute("get ip for date = \"29.2.2028 5:4:7\""));
//        System.out.println(logParser.execute("get user for date = \"29.2.2028 5:4:7\""));
//        System.out.println(logParser.execute("get event for date = \"29.2.2028 5:4:7\""));
//        System.out.println(logParser.execute("get status for date = \"29.2.2028 5:4:7\""));
//
//        System.out.println("----------------------------");
//
//        System.out.println(logParser.execute("get user for ip = \"192.168.100.2\""));
//        System.out.println(logParser.execute("get event for ip = \"192.168.100.2\""));
//        System.out.println(logParser.execute("get status for ip = \"192.168.100.2\""));
//        System.out.println(logParser.execute("get date for ip = \"192.168.100.2\""));
//
//        System.out.println("----------------------------");
//
//        System.out.println(logParser.execute("get ip for user = \"Eduard Bentley\""));
//        System.out.println(logParser.execute("get event for user = \"Eduard Bentley\""));
//        System.out.println(logParser.execute("get status for user = \"Eduard Bentley\""));
//        System.out.println(logParser.execute("get date for user = \"Eduard Bentley\""));
//
//        System.out.println("----------------------------");
//
//        System.out.println(logParser.execute("get ip for event = \"COMPLETE_TASK\""));
//        System.out.println(logParser.execute("get user for event = \"COMPLETE_TASK\""));
//        System.out.println(logParser.execute("get status for event = \"COMPLETE_TASK\""));
//        System.out.println(logParser.execute("get date for event = \"COMPLETE_TASK\""));
//
//        System.out.println("----------------------------");
//
//        System.out.println(logParser.execute("get ip for status = \"ERROR\""));
//        System.out.println(logParser.execute("get user for status = \"ERROR\""));
//        System.out.println(logParser.execute("get event for status = \"ERROR\""));
//        System.out.println(logParser.execute("get date for status = \"ERROR\""));

        System.out.println(logParser.execute("get ip for user = \"Eduard Bentley\" and date between \"11.12.2013 0:00:00\" and \"03.01.2014 23:59:59\""));
    }
}