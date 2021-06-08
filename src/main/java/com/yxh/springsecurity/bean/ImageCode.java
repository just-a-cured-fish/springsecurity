package com.yxh.springsecurity.bean;

import lombok.Data;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;

@Data
public class ImageCode {

    /** 验证码 */
    private String code;

    /** 判断过期时间 */
    private LocalDateTime expireTime;

    /** 生成的图片验证码 */
    private BufferedImage image;


    public ImageCode(String code, int expireIn, BufferedImage image) {
        this.code = code;
        this.expireTime = LocalDateTime.now().plusSeconds(expireIn);
        this.image = image;
    }

    //判断验证码是否过期
    public boolean isExpried() {
        return LocalDateTime.now().isAfter(expireTime);
    }

    //省略get/set方法
}
