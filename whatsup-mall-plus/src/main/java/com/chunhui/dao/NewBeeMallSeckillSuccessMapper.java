package com.chunhui.dao;


import com.chunhui.entity.NewBeeMallSeckillSuccess;

import java.util.List;

public interface NewBeeMallSeckillSuccessMapper {
    int deleteByPrimaryKey(Integer secId);

    int insert(NewBeeMallSeckillSuccess record);

    int insertSelective(NewBeeMallSeckillSuccess record);

    NewBeeMallSeckillSuccess selectByPrimaryKey(Long secId);

    int updateByPrimaryKeySelective(NewBeeMallSeckillSuccess record);

    int updateByPrimaryKey(NewBeeMallSeckillSuccess record);

    NewBeeMallSeckillSuccess getSeckillSuccessByUserIdAndSeckillId(Long userId, Long seckillId);

    List<Long> selectBySeckillId(Long seckillId);
}
