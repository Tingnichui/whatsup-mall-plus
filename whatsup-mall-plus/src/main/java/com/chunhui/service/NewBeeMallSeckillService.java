package com.chunhui.service;


import com.chunhui.controller.vo.ExposerVO;
import com.chunhui.controller.vo.SeckillSuccessVO;
import com.chunhui.entity.NewBeeMallSeckill;
import com.chunhui.util.PageQueryUtil;
import com.chunhui.util.PageResult;

import java.util.List;

public interface NewBeeMallSeckillService {

    PageResult getSeckillPage(PageQueryUtil pageUtil);

    boolean saveSeckill(NewBeeMallSeckill newBeeMallSeckill);

    boolean updateSeckill(NewBeeMallSeckill newBeeMallSeckill);

    NewBeeMallSeckill getSeckillById(Long id);

    boolean deleteSeckillById(Long id);

    List<NewBeeMallSeckill> getHomeSeckillPage();

    ExposerVO exposerUrl(Long seckillId);

    SeckillSuccessVO executeSeckill(Long seckillId, Long userId);
}
