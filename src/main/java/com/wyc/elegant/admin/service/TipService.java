package com.wyc.elegant.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wyc.elegant.admin.model.entity.Tip;

/**
 * Tip
 *
 * @author Yeeep 2020/11/7
 */
public interface TipService extends IService<Tip> {
    /**
     * 获取每日赠言
     *
     * @param type 类型
     * @return {@link Tip}
     */
    Tip getRandomTip(Integer type);
}