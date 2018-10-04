package com.tcg.admin.controller.core.filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.controller.core.BehaviorRequestWrapper;
import com.tcg.admin.controller.core.session.TcgIgnoreListService;
import com.tcg.admin.model.MenuItem;
import com.tcg.admin.persistence.springdata.IMenuItemRepository;
import com.tcg.admin.service.BehaviorLogService;
import com.tcg.admin.service.CommonMenuService;
import com.tcg.admin.service.impl.OperatorLoginService;

@WebFilter(filterName = "behaviorLogFilter", urlPatterns = "/resources/*")
@Component
public class A0BehaviorLogFilter implements Filter  {

	/**
	 * Token key
	 */
    public static  final String MERCHANT_HEADER = "Merchant";
	public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String NOT_LOGIN = "not-login";
	/**
	 * 格式化時間樣式
	 */

	public static final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss");

	private static final Logger LOGGER = LoggerFactory.getLogger(A0BehaviorLogFilter.class);

    private static final ConcurrentHashMap<String, Object> customerDoingMap = new ConcurrentHashMap<>();

    @Autowired
    private TcgIgnoreListService tcgIgnoreListService;

    @Autowired
    private BehaviorLogService behaviorLogService;

    @Autowired
    private OperatorLoginService operatorLoginService;

    @Autowired
    private CommonMenuService menuProxyService;

    @Autowired
    private IMenuItemRepository menuItemRepository;

    private Boolean isAutowired = false;
    
	@Override
	public void destroy() {
		// do nothing
	}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        
        if(!isAutowired) {
            SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
            isAutowired = true;
        }
        
        request.setCharacterEncoding("UTF-8");
        
        BehaviorRequestWrapper behaviorRequestWrapper = new BehaviorRequestWrapper((HttpServletRequest) request);
        HttpServletRequest req = (HttpServletRequest) request;
        final String token = req.getHeader(AUTHORIZATION_HEADER);
        final String merchantCode = req.getHeader(MERCHANT_HEADER);
        String customerName = null;
        Date startTime = null;
        try {
            // # check user is login
            boolean isLogin = operatorLoginService.isLogin(token);
            boolean logining = req.getRequestURI().contains("/operator_sessions");

            if (isLogin || logining) { //login
                customerName = isLogin ? operatorLoginService.getSessionUser(token).getUserName() : "not-login";
                startTime = new Date();
                //Process the request and save into database(menu_type = merchant_view (1), system_view (0) ,login and logout)
                this.writeLogOrSaveBehaviorLog(req, behaviorRequestWrapper, customerName, startTime, new Date(), merchantCode);
            }
        } catch (AdminServiceBaseException e) {
            LOGGER.error("token[" + customerName + "]", e);
        }finally {
            //do the request
            filterChain.doFilter(behaviorRequestWrapper, response);
        }
    }

    private void writeLogOrSaveBehaviorLog( HttpServletRequest req, BehaviorRequestWrapper behaviorRequestWrapper, String customerName,Date startTime,Date endTime,String merchantCode){
        //save into files, if not saveBehaviorLog
        if(!this.saveBehaviorLog(req, behaviorRequestWrapper, customerName, startTime , endTime, merchantCode)) {
            boolean ignore = true;
            //TODO: Or try to get all the uri which need to log
            List<String> ignoreList = Arrays.asList("announcement/activeAnnouncement","merchants/getMerchantList","permissions","workflow","roles/all",
                    "categories", "operators/activityLog","operator_sessions","operators/profile"
                    ,"transfer","behaviorlog","subsystem/verification");
            int size = ignoreList.size();
            String uri = req.getRequestURI();
            for(int i=0; i < size ; i++){
                ignore = uri.contains(ignoreList.get(i));
                if(ignore){
                    break;
                }
            }

            if(!ignore){
                behaviorLogService.writeBehaviorLog(req, behaviorRequestWrapper, customerName, startTime , endTime, merchantCode);
            }
        }
    }


    private Boolean saveBehaviorLog(final HttpServletRequest req, final BehaviorRequestWrapper behaviorRequestWrapper, final String customerName, final Date startTime, final Date endTime, final String merchantCode){
        Boolean save = false;
        final String token = req.getHeader(AUTHORIZATION_HEADER);
        final String requestUri = req.getRequestURI();
        final String reqURIKey = getURIKey(requestUri);
        final String doingMapKey = token + "_" + reqURIKey;
        if(!checkUserCanUse(reqURIKey, doingMapKey)){
            MenuItem menuItem = null;
            String uri = req.getRequestURI();
            /**
             *
             *  1. /operator_sessions (action type login 4, logout 41) & /tcg-admin/resources/subsystem
             *  2. not include get method (menu Type system view 0, merchant view 1)
             */

            if (!(uri.contains("/operator_sessions") || uri.startsWith("/tcg-admin/resources/subsystem/"))){
               menuItem = this.findMenuItemForBehaviorLogByBlurUrl(req.getRequestURI(),req.getMethod());
            }

            boolean isLoginOrLogout =  uri.contains("/operator_sessions");
            boolean isSubSystem =  uri.startsWith("/tcg-admin/resources/subsystem/");
            final boolean menuItemNull = menuItem != null;
            if (menuItemNull || (isLoginOrLogout || isSubSystem )){
                String pathString = !menuItemNull || isLoginOrLogout || isSubSystem ? "" : this.getPathStrings(req, menuItem);
                if(!(menuItemNull && StringUtils.equals(menuItem.getAccessType(),"9")) && !uri.startsWith("/tcg-admin/resources/subsystem/workflow/task")){
                    behaviorLogService.saveBehaviorLog(req, behaviorRequestWrapper, customerName, startTime, endTime, merchantCode, menuItem, pathString);
                }
                save = true;
            }

        }
        return save;
    }


    private MenuItem findMenuItemForBehaviorLogByBlurUrl(String requestUrl, String method){
        MenuItem menuItem = null;
        if(requestUrl.startsWith("/tcg-admin/resources")) {
            menuItem = menuItemRepository.findMenuItemForBehaviorLogByBlurUrl(requestUrl.replace("/tcg-admin/resources", ""), this.getAccessTypeByHttpMethod(method, requestUrl));
        }
        
        if(menuItem != null) {
            return menuItem;
        }
        
        menuItem = menuItemRepository.findMenuItemForBehaviorLogByBlurUrl(this.getUrl(requestUrl), this.getAccessTypeByHttpMethod(method, requestUrl));

        return menuItem;
    }


    private String getPathStrings(HttpServletRequest req, MenuItem menuItem ){
        List<String> pathParameters = this.getPathParameters(req.getRequestURI());
        StringBuilder pathString= new StringBuilder();
        if(!pathParameters.isEmpty()){
            List<String> urlList = Arrays.asList(menuItem.getUrl().split("/"));
            int size = urlList.size();
            int j = 0 ;
            for(int i=0; i < size ; i++ ){
                if((urlList.get(i).startsWith("{")||urlList.get(i).startsWith("${")) && urlList.get(i).endsWith("}") && j < size){
                    pathString.append(urlList.get(i)+":"+ pathParameters.get(j) +";");
                    j++;
                }
            }
        }
        return pathString.toString();
    }

    /**
     *
     * @param requestUrl
     * @return
     */
    private List<String> getPathParameters(String requestUrl){
        List<String> urlList = Arrays.asList(requestUrl.split("/"));
        //get keywords in menuItems
        List<String> dirList = this.getMenuItemKeyWords();
        //keywords will be removed
        List<String> removeList = Arrays.asList("","tcg-admin", "resources");
        //remove keywords and concat url
        return this.getPathParameters(urlList, dirList, removeList );
    }

    /**
     * remove keywords in menuItems ,in ("","tcg-admin", "resources") and concate url
     * @param urlList
     * @param dirList
     * @param removeList
     * @return
     */
    private List<String> getPathParameters(List<String> urlList, List<String> dirList, List<String> removeList ){
        int urlSize = urlList.size();
        List<String> finalList = new ArrayList<>();
        for(int i = 0 ; i < urlSize; i++ ){
            String url = urlList.get(i);
            Boolean isInclude = dirList.contains(url);
            Boolean remove = removeList.contains(url);

            if(!isInclude && !remove){
                finalList.add(url);
            }
        }
        return finalList;
    }

    /**
     * remove useless keywords=
     * @param requestUrl
     * @return
     */
    private String getUrl(String requestUrl){
        List<String> urlList = Arrays.asList(requestUrl.split("/"));
        //get keywords in menuItems
        List<String> dirList = this.getMenuItemKeyWords();
        //keywords will be removed
        List<String> removeList = Arrays.asList("","tcg-admin", "resources");
        //remove keywords and concat url
        return this.concatUrl(urlList, dirList, removeList );
    }

    /**
     *  get keywords in menuItems
     *  @return
     */
    private List<String> getMenuItemKeyWords(){
        //Get all menu url , split into Array
        List<String> dirList = new ArrayList<>();
        Map<Integer, List<MenuItem>> menuItemMap = menuProxyService.getWholeMenuTree();

        List<MenuItem> menuItemlist = new ArrayList<>();
        for(Map.Entry entry:menuItemMap.entrySet()){
            menuItemlist.addAll((List<MenuItem>) entry.getValue());
        }
        for(MenuItem menuItem: menuItemlist){
            dirList.addAll(Arrays.asList(menuItem.getUrl().split("/")));
        }
        return dirList;
    }

    /**
     * remove keywords in menuItems ,in ("","tcg-admin", "resources") and concate url
     * @param urlList
     * @param dirList
     * @param removeList
     * @return
     */
    private String concatUrl(List<String> urlList, List<String> dirList, List<String> removeList ){
        int urlSize = urlList.size();
        String blurUrl = "%";
        int lastOne;
        for(int i = 0 ; i < urlSize; i++ ){
            String url = urlList.get(i);
            Boolean isInclude = dirList.contains(url);
            Boolean remove = removeList.contains(url);
            lastOne = urlSize -1;

            if(isInclude && !remove){
                blurUrl = (i == lastOne) ? blurUrl.concat("/"+url) : blurUrl.concat("/"+url+"%");
            }
        }

        /**
         * data all remove, means no path parameter
         */
        if(StringUtils.equals(blurUrl,"%")){
            for(int i = 0 ; i < urlSize; i++ ){
                String url = urlList.get(i);
                Boolean remove = removeList.contains(url);
                lastOne = urlSize -1;

                if(!remove){
                    blurUrl = (i == lastOne) ? blurUrl.concat("/"+url) : blurUrl.concat("/"+url+"%");
                }
            }
        }

        return blurUrl;
    }


    /**
     * get accessType by httpMethod
     * @param httpMethod
     * @return
     */
    private String getAccessTypeByHttpMethod(String httpMethod, String requestUrl){
        Integer accessType;
        switch (httpMethod) {
            case "POST":
                accessType = requestUrl.contains("/export") ? BehaviorLogService.EXPORT : BehaviorLogService.ADD;
                break;
            case "PUT":
                accessType = BehaviorLogService.EDIT;
                if(requestUrl.contains("/reject")){
                    accessType = BehaviorLogService.REJECT;
                }else if (requestUrl.contains("/approve")){
                    accessType = BehaviorLogService.APPROVE;
                }
                break;
            case "GET":
                accessType = BehaviorLogService.CHECK; //if access type is check, save behavior log
                break;
            case "DELETE":
                accessType = BehaviorLogService.REMOVE;
                break;
            default:
                accessType = BehaviorLogService.OTHER;
                break;
        }
        return accessType.toString();
    }

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// do nothing
	}

    private String getURIKey(String requestUri) {
        return requestUri.replace("/tcg-admin/resources/", "");
    }
    /**
     * Check user can use reqURIKey resource.
     *
     * @param token user token
     * @param reqURIKey uri key
     * @param doingMapKey map key
     * @return
     */
    private boolean checkUserCanUse(String reqURIKey, String doingMapKey) {
        Object lockObj = new Object();
        Object afterPut = customerDoingMap.putIfAbsent(doingMapKey, lockObj);
        if (afterPut != null) {
            lockObj = afterPut;
        }

        synchronized (lockObj) {
        	return tcgIgnoreListService.checkRequestURIExist(reqURIKey);
        }
    }

}
