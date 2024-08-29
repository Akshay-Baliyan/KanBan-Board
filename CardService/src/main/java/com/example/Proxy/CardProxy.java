package com.example.Proxy;

import com.example.Domain.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "UserAuthenticationService", url = "localhost:8083")
public interface CardProxy {
    @PostMapping("api/v1/saveUser")
    public ResponseEntity<?>saveUser(@RequestBody User user);
}
