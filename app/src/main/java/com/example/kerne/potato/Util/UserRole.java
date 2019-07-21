package com.example.kerne.potato.Util;

public class UserRole {
    private static String userRole = "farmer";

    public static String getUserRole(){
        return userRole;
    }

    public static void setUserRole(String userRole){
        UserRole.userRole = userRole;
    }
}
