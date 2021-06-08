package com.yxh.springsecurity.Controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yxh.springsecurity.bean.Result;
import com.yxh.springsecurity.bean.Users;
import com.yxh.springsecurity.biz.UserBiz;
import com.yxh.springsecurity.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@RequestMapping("/users")
public class UsersController {
    @Autowired
    private UserBiz userBiz;


    @GetMapping(value = "/sign")
    public Result signin( Users users){
        return userBiz.signin(users);
    }

}
