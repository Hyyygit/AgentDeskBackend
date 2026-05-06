package com.agentdesk.opsadmin.controller;

import com.agentdesk.common.web.controller.BaseController;
import com.agentdesk.common.web.result.AjaxResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/config")
public class SystemConfigController extends BaseController {

    @GetMapping("/")
    public AjaxResult list() {
        Map<String, Object> configs = new LinkedHashMap<>();
        configs.put("maxUploadSize", "10MB");
        configs.put("sessionTimeout", 30);
        configs.put("maxLoginAttempts", 5);
        configs.put("enableRegistration", true);
        configs.put("defaultLanguage", "zh-CN");
        return success(configs);
    }

    @PutMapping("/")
    public AjaxResult update(@RequestBody Map<String, Object> config) {
        return success("配置更新成功", config);
    }
}
