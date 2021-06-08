package com.yxh.springsecurity.bean;

import lombok.Data;

@Data
public class Users {
    private Integer uid;
    private String uname;
    private String upwd;
    private String role;

}
