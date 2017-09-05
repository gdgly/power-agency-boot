package com.power.controller.ex;

import com.alibaba.fastjson.JSON;
import com.google.code.kaptcha.Constants;
import com.mchange.lang.IntegerUtils;
import com.power.cache.RedisRepository;
import com.power.yuneng.sms.api.ISMSService;
import com.power.yuneng.sms.domain.SMSCheckCode;
import io.renren.utils.R;
import io.renren.utils.RRException;
import io.renren.utils.ShiroUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;


/**
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017-06-21 23:02:38
 */
@RestController
@RequestMapping("sms")
public class SMSController {
    private static final Logger logger = LoggerFactory.getLogger(SMSController.class);
    @Autowired
    private ISMSService smsService;
    @Autowired
    private RedisRepository<String, String> repository;
    /**
     * 用户发短信标识
     **/
    private final static String USER_SMS = "USER_SMS_";
    private final static String USER_SMS_IN_USE_TAG = "USER_SMS_IN_USE_TAG_";

    /**
     * 每次发送间隔
     **/
    private final static int TIME_OUT = 5 * 60;
    private final static int TIME_OUT_LOCK = 60;

    /**
     * 发送次数上限
     **/
    private final static String SMS_BY_PHONE = "SMS_BY_PHONE_";
    private final static long SMS_COUNT_BY_PHONE_TIME = 24;
    private final static int SMS_BY_PHONE_COUNT = 5;

    private final static TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    private final static Pattern PHONE_CHECK = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$"); // 验证手机号

    /**
     * 列表
     */
    @RequestMapping("/captcha/sendSMS")
    public R list(@RequestParam("phone") String phone,@RequestParam("captcha") String captcha) {
        String kaptcha = ShiroUtils.getKaptcha(Constants.KAPTCHA_SESSION_KEY);
        if(!captcha.equalsIgnoreCase(kaptcha)){
            return R.error("验证码不正确");
        }
        ShiroUtils.getSession().removeAttribute(Constants.KAPTCHA_SESSION_KEY);
        if (!PHONE_CHECK.matcher(phone).matches()) {
            return R.error("不是标准的手机号码，请输入正确的手机号码！");
        }
        int count = IntegerUtils.parseInt(repository.get(SMS_BY_PHONE + phone), 0);
        if (count < SMS_BY_PHONE_COUNT) {
            repository.set(SMS_BY_PHONE + phone, "" + (count++), SMS_COUNT_BY_PHONE_TIME, TimeUnit.HOURS);
        } else {
            return R.error("您当日短信验证码超过次数限制(5次)，请24小时后再发！");
        }
        String redisKey = USER_SMS + phone;
        String redisLockKey = USER_SMS_IN_USE_TAG + phone;
        //redis存储验证码
        if (!repository.exists(redisLockKey)) {

            SMSCheckCode smsCheckCode = new SMSCheckCode();
            smsCheckCode.setBizTarget("宇能共享");
            smsCheckCode.setPhone(phone);
            String code = String.valueOf((int) ((Math.random() * 9 + 1) * 1000));
            smsCheckCode.setCheckCode(code);
            boolean flag = smsService.sendSMS(smsCheckCode);
            if (flag) {
                //存储值
                repository.set(redisKey, code);
                repository.expire(redisKey, TIME_OUT, TIME_UNIT);
                //存储锁
                repository.set(redisLockKey, code);
                repository.expire(redisLockKey, TIME_OUT_LOCK, TIME_UNIT);
                return R.ok();
            } else {
                return R.error("验证码发送失败，请稍后再试！");
            }
        } else {
            return R.error("验证码已经发出，请60秒后重试！");
        }

    }

    /**
     * 校验短信验证码
     *
     * @param phone     手机号码
     * @param checkCode 校验码
     * @return 成功/失败
     */
    @RequestMapping(value = "/captcha/checkSms")
    @ResponseBody
    public R checkSms(
            @RequestParam String phone,
            @RequestParam String checkCode) {
        if (!PHONE_CHECK.matcher(phone).matches()) {
            return R.error("不是标准的手机号码，请输入正确的手机号码！");
        }
        String redisKey = USER_SMS + phone;
        if (repository.exists(redisKey)) {
            String code = repository.get(redisKey);
            if (code.equals(checkCode)) {
                //TODO 待优化至拦截器
                return R.ok();
            }
            return R.error("验证码校验失败，请重新输入！");
        }
        return R.error("验证码校验失败，请重新输入！");
    }
}
