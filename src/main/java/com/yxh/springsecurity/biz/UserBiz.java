package com.yxh.springsecurity.biz;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.yxh.springsecurity.bean.Result;
import com.yxh.springsecurity.bean.Users;
import com.yxh.springsecurity.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Wrapper;

@Service
public class UserBiz {

    @Autowired
    public UserMapper userMapper;

    public Result signin(Users users){
        Result result=new Result();
        QueryWrapper<Users> wrapper=new QueryWrapper();
        wrapper.eq("uname",users.getUname());
        Users user=userMapper.selectOne(wrapper);
        if(user!=null){
            result.setCode(0);
            result.setData("该用户名已注册");
        }else{
            users.setUpwd(new BCryptPasswordEncoder().encode(users.getUpwd()));
            users.setRole("ROLE_USER");
            result.setCode(userMapper.insert(users));
            result.setData(users);
        }
        return result;
    }

}
