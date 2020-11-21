package com.wcy.rhapsody.admin.controller.api;

import com.wcy.rhapsody.admin.core.R;
import com.wcy.rhapsody.admin.modules.entity.Ad;
import com.wcy.rhapsody.admin.service.AdService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 广告金主控制器
 *
 * @author Yeeep 2020/11/7
 */
@Api(tags = "广告控制器")
@RestController
public class AdController {

    @Autowired
    private AdService adService;

    /**
     * 广告列表
     *
     * @return
     */
    @GetMapping("/ads")
    public R list() {
        List<Ad> list = adService.list();
        return R.ok().data(list);
    }

}