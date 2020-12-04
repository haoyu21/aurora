package com.wyc.elegant.admin.controller.auth;

import com.wyc.elegant.admin.common.R;
import com.wyc.elegant.admin.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 注销 控制器
 *
 * @author Knox 2020/11/7
 */
@Api(tags = "注销控制器")
@RestController
@RequestMapping("/auth")
public class LogoutController extends BaseController {

    /**
     * 退出登录
     *
     * @return
     */
    @ApiOperation(value = "退出登录", notes = "退出登录状态", httpMethod = "POST")
    @PostMapping("/logout")
    public R logout() {
        getSubject().logout();
        return R.ok().message("账号退出成功");
    }
}