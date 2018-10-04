package com.tcg.admin.controller.subsystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tcg.admin.service.MenuItemService;
import com.tcg.admin.to.ApiLabelTo;
import com.tcg.admin.to.response.JsonResponseT;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "/resources/subsystem/translate", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Subsystem")
public class LangController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LangController.class);
	
	@Autowired
	private MenuItemService menuItemService;
	
	@GetMapping("/menu")
	@ApiOperation(value = "Menu Translate")
    public JsonResponseT<ApiLabelTo> menu() {
        JsonResponseT<ApiLabelTo> jp = new JsonResponseT<>(true);
        ApiLabelTo labels = menuItemService.getAllApiLabelTo();
        jp.setValue(labels);
        return jp;
    }
	
}
