package com.tcg.admin.common.interceptor;

import com.tcg.admin.common.error.AdminErrorCode;
import com.tcg.admin.common.helper.RequestHelper;
import com.tcg.admin.model.Operator;
import com.tcg.admin.service.OperatorAuthenticationService;
import com.tcg.admin.to.UserInfo;
import com.tcg.admin.to.response.JsonResponseT;
import com.tcg.admin.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * com.tcg.admin.common.interceptor
 *
 * @author lyndon.j
 * @version 1.0
 * @date 2019/6/17 16:00
 */
@Component
public class TacInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(TacInterceptor.class);

    @Autowired
    private OperatorAuthenticationService operatorAuthenticationService;




    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
//        String currentIp = RequestHelper.getIp();
//        UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
//        String lastLoginIp = userInfo.getLoginIp();
//        if (!currentIp.equals(lastLoginIp)) {
//            logger.info("check request ip error, they ip not equal user info : {}",userInfo);
//            operatorAuthenticationService.logout(RequestHelper.getToken());
//            buildErrorInfo(response);
//            return false;
//        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }

    private void buildErrorInfo(HttpServletResponse response)throws IOException {
        response.reset();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter pw = response.getWriter();
        pw.write(JsonUtils.toJson(new JsonResponseT<String>(false, AdminErrorCode.IP_HAS_CHANGED)));
        pw.flush();
        pw.close();

    }
}
