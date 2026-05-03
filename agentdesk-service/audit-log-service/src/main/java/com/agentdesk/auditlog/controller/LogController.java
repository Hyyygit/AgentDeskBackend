package com.agentdesk.auditlog.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hyyy
 * @date 2026/5/3 21:07
 * @description 日志控制类
 */
@Tag(name = "日志控制类")
@RestController
@RequestMapping("/log")
@RequiredArgsConstructor
public class LogController {
}
