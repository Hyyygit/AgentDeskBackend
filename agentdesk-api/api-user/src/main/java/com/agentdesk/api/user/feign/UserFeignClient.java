package com.agentdesk.api.user.feign;

import com.agentdesk.api.user.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", path = "/internal/users")
public interface UserFeignClient {

    @GetMapping("/{userId}")
    UserDTO getUserById(@PathVariable Long userId);

    @GetMapping("/by-username/{username}")
    UserDTO getUserByUsername(@PathVariable String username);
}
