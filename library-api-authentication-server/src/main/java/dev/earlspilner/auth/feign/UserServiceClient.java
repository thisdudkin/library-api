package dev.earlspilner.auth.feign;

import dev.earlspilner.auth.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Alexander Dudkin
 */
@FeignClient(
        value = "users-service",
        url = "http://localhost:9091/api/",
        configuration = FeignConfig.class
)
public interface UserServiceClient {

    /**
     * Synchronized request to User Service to collect User Details
     * @param  username is a unique identifier of user
     * @return {@code UserDto} data transfer object
     */
    @RequestMapping(method = RequestMethod.GET, value = "/users/{username}")
    UserDto getUser(@PathVariable String username);

}
