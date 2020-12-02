package com.wyc.elegant.admin.controller.web.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wyc.elegant.admin.common.R;
import com.wyc.elegant.admin.component.RedisService;
import com.wyc.elegant.admin.controller.BaseController;
import com.wyc.elegant.admin.model.dto.ActiveDTO;
import com.wyc.elegant.admin.model.entity.Topic;
import com.wyc.elegant.admin.model.entity.User;
import com.wyc.elegant.admin.service.TopicService;
import com.wyc.elegant.admin.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 账户控制器
 *
 * @author Yeeep 2020/11/7
 */
@Api(tags = "账户控制器")
@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private RedisService redisService;

    /**
     * 用户主页：根据用户名查询
     *
     * @param username
     * @return
     */
    @ApiOperation(value = "根据用户名查询", notes = "username访问用户主页")
    @ApiImplicitParam(name = "username", value = "username用户名", required = true, paramType = "path")
    @GetMapping("/{username}")
    public R getUserByName(@PathVariable("username") String username,
                           @ApiParam(value = "pageNo", name = "页码") @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
                           @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Map<String, Object> map = new HashMap<>(16);
        User user = userService.selectByUsername(username);
        Assert.notNull(user, "用户不存在");
        Page<Topic> page = topicService.page(new Page<>(pageNo, size),
                new LambdaQueryWrapper<Topic>().eq(Topic::getUserId, user.getId()));
        map.put("user", user);
        map.put("topics", page);
        return R.ok().data(map);
    }

    /**
     * 账号激活
     * <p>
     * user/active?name=${active.user}&code=${active.code}
     */
    @PostMapping("/active")
    public R active(@RequestBody @Valid ActiveDTO activeDTO) {
        User user = userService.selectByUsername(activeDTO.getUser());
        Assert.notNull(user, "用户不存在");
        Assert.isTrue(!user.getActive(), "账号已激活");

        String activeCode = (String) redisService.get("activeCode[" + activeDTO.getUser() + "]");
        Assert.isTrue(activeCode.equals(activeDTO.getCode()), "激活码错误");

        user.setActive(true);
        boolean b = userService.updateById(user);
        if (b) {
            redisService.del("activeCode[" + activeDTO.getUser() + "]");
            return R.ok().message("恭喜你，账号激活成功!");
        }
        return R.error().message("恭喜你，账号激活成功!").code(10000);
    }
}