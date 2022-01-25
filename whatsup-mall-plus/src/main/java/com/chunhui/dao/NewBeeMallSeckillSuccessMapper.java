package com.chunhui.dao;


import com.chunhui.entity.NewBeeMallSeckillSuccess;

public interface NewBeeMallSeckillSuccessMapper {
    int deleteByPrimaryKey(Integer secId);

    int insert(NewBeeMallSeckillSuccess record);

    int insertSelective(NewBeeMallSeckillSuccess record);

    NewBeeMallSeckillSuccess selectByPrimaryKey(Long secId);

    int updateByPrimaryKeySelective(NewBeeMallSeckillSuccess record);

    int updateByPrimaryKey(NewBeeMallSeckillSuccess record);

    NewBeeMallSeckillSuccess getSeckillSuccessByUserIdAndSeckillId(Long userId, Long seckillId);
}
