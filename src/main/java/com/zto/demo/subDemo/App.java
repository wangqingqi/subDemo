package com.zto.demo.subDemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.zto.demo.subDemo.callBack.SubCallBack;
import com.zto.demo.subDemo.entity.CallbackEntity;
import com.zto.demo.subDemo.entity.TraceEntity;
import com.zto.demo.subDemo.util.DigestUtil;

/**
 * Hello world!
 *
 */
@EnableAutoConfiguration
@Controller
public class App 
{
    
    private final static Logger logger = LoggerFactory.getLogger(SubCallBack.class);

    public static void main(String[] args) throws Exception {
        SpringApplication.run(App.class, args);
    }
    
    
    @RequestMapping(value="/billCallBack",method = RequestMethod.POST)
    @ResponseBody
    public String CallBack(String data, String msg_type, String company_id, String data_digest)
    {
        CallbackEntity callbackEntity = new CallbackEntity();
        TraceEntity resData = JSON.parseObject(data, TraceEntity.class); // 消息内容
        String msgType = msg_type; // 消息类型
        String companyId = company_id; // 合作商ID（为调用订阅接口时传递的createBy）
        String dataDigest = data_digest; // 消息签名
        String callBackKkey = "789789"; // 中通回调合作商接口时使用的加密KEY，在上线前分配。不是订阅时使用的key
        try
        {
            // 获取加密签名
            String key = DigestUtil.digest(data + "", callBackKkey, DigestUtil.UTF8);
            // 验签
            key.equals(dataDigest);

            String billCode = resData.getBillCode();// 运单号
            String scanType = resData.getScanType();// 扫描类型，事件/操作，详情参见scanType编码规范
            String scanSite = resData.getScanSite();// 扫描网点
            String scanCity = resData.getScanCity();// 扫描城市
            String scanDate = resData.getScanDate();// 扫描时间（yyyy-MM-dd HH:mm:ss）
            String desc = resData.getDesc();// 物流信息描述
            String contacts = resData.getContacts();// 收\派件业务，签收客户名
            String contactsTel = resData.getContactsTel();// 收\派件业务电话
            // 回调返回设置
            callbackEntity.setMessage("回调成功");
            callbackEntity.setResult("success");
            callbackEntity.setStatus(true);
            callbackEntity.setStatusCode("0");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        logger.info(data + msg_type + company_id + data_digest);
        logger.info(JSON.toJSON(callbackEntity).toString());
        return JSON.toJSON(callbackEntity).toString();
    }
    
}
