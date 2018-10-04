
package com.tcg.admin.controller;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.MediaType;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcg.admin.model.BehaviorLog;
import com.tcg.admin.service.BehaviorLogService;
import com.tcg.admin.to.response.JsonResponseT;

/**
 * Created by ian.r on 5/22/2017.
 */

@RestController
@RequestMapping(value = "/resources/behaviorlog", produces = MediaType.APPLICATION_JSON_VALUE)
public class BehaviorLogResource {
    
    @Autowired
    private BehaviorLogService behaviorLogService;
    
    @GetMapping
    public JsonResponseT<Page<BehaviorLog>> queryBehaviorLog(@RequestParam(value = "merchant", required = false)String merchant,
            @RequestParam(value = "username", required = false)String username,
            @RequestParam(value = "actionType", required = false)int actionType,
            @RequestParam(value = "resourceId", required = false)String resourceId,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "startDateTime", required = false) String startDateTime,
            @RequestParam(value = "endDateTime", required = false) String endDateTime,
            @RequestParam(value = "system", required = false) boolean isSystem,
            @RequestParam(value = "pageNo", required = false) int pageNo,
            @RequestParam(value = "pageSize", required = false) int pageSize) {
            JsonResponseT<Page<BehaviorLog>> response = new JsonResponseT<>(true);
        Page<BehaviorLog> queryResultresponse;
        if(isSystem){
            queryResultresponse = behaviorLogService.queryBehaviorLogOfSystem(merchant,username, actionType , this.convertIntegerList(resourceId), keyword, startDateTime, endDateTime ,pageNo,pageSize);
        }else{
            queryResultresponse = behaviorLogService.queryBehaviorLog(merchant,username, actionType , this.convertIntegerList(resourceId), keyword, startDateTime, endDateTime ,pageNo,pageSize);
        }
            response.setValue(queryResultresponse);
            return response;
    }

    /**
     * StringArray (String) ex: 3,4,5
     * returnList: <List>: [3,4,5]
     * @param stringArray
     * @return
     */
    private List<Integer> convertIntegerList (String stringArray){
        List<Integer> returnList = new ArrayList<>();
        if(StringUtils.isNotEmpty(stringArray)) {
            for (String str : Arrays.asList(stringArray.split(","))) {
                returnList.add(Integer.parseInt(str));
            }
        }
        return returnList;
    }
}
