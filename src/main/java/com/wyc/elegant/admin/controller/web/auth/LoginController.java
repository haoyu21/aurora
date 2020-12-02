package com.wyc.elegant.admin.controller.web.auth;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wyc.elegant.admin.common.MyHttpCode;
import com.wyc.elegant.admin.common.R;
import com.wyc.elegant.admin.controller.BaseController;
import com.wyc.elegant.admin.exception.TokenException;
import com.wyc.elegant.admin.model.dto.LoginDTO;
import com.wyc.elegant.admin.model.entity.User;
import com.wyc.elegant.admin.service.UserService;
import com.wyc.elegant.admin.utils.TokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录控制器
 *
 * @author Yeeep 2020/11/7
 */
@Api(tags = "登录控制器")
@RestController
@RequestMapping("/auth")
public class LoginController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenUtil jwtTokenUtil;

    /**
     * 前台登录
     *
     * @param dto 登录请求对象
     * @return
     */
    @ApiOperation(value = "前台登录", notes = "前台用户登录接口", httpMethod = "POST")
    @PostMapping("/login")
    public R login(@Valid @RequestBody LoginDTO dto, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Map<String, Object> map = new HashMap<>(16);
        String username = dto.getUsername();
        String password = dto.getPassword();
        // 验证数据库用户
        User dbUser = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));

        if (!StringUtils.isEmpty(dbUser)) {
            // 验证密码
            if (BCrypt.checkpw(password, dbUser.getPassword())) {
                String token = jwtTokenUtil.generateToken(dbUser);
                dbUser.setToken(token);
                userService.updateById(dbUser);

                map.put("token", token);
                map.put("user", dbUser);
                session.setAttribute("user", dbUser);
                session.setAttribute("token", token);
                return R.ok().message("登录成功").data(map);
            }
        }
        return R.error().code(MyHttpCode.USER_NAME_PASS_ERROR).message("用户名或密码错误");
    }

    /**
     * 登录后跳转首页获取用户信息
     *
     * @param token
     * @return
     */
    @ApiOperation(value = "登录后，获取用户信息", notes = "登录成功，请求用户信息，需要携带token", httpMethod = "POST")
    @PostMapping("/login/profile")
    public R getUserInfoByToken(@ApiParam(name = "token", value = "用户登录Token", required = true)
                                @RequestParam String token) {
        String username = jwtTokenUtil.parseToken(token).getSubject();
        if (StringUtils.isEmpty(username)) {
            throw new TokenException();
        }
        User user = userService.selectByUsername(username);
        Assert.notNull(user, "用户不存在");
        return R.ok().data(user);
    }
}