/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本系统已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2020 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package com.chunhui.service.impl;

import com.chunhui.common.Constants;
import com.chunhui.common.ServiceResultEnum;
import com.chunhui.controller.vo.NewBeeMallShoppingCartItemVO;
import com.chunhui.dao.NewBeeMallGoodsMapper;
import com.chunhui.dao.NewBeeMallShoppingCartItemMapper;
import com.chunhui.entity.NewBeeMallGoods;
import com.chunhui.entity.NewBeeMallShoppingCartItem;
import com.chunhui.service.NewBeeMallShoppingCartService;
import com.chunhui.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class NewBeeMallShoppingCartServiceImpl implements NewBeeMallShoppingCartService {

    @Autowired
    private NewBeeMallShoppingCartItemMapper newBeeMallShoppingCartItemMapper;

    @Autowired
    private NewBeeMallGoodsMapper newBeeMallGoodsMapper;

    @Override
    public String saveNewBeeMallCartItem(NewBeeMallShoppingCartItem newBeeMallShoppingCartItem) {
        NewBeeMallShoppingCartItem temp = newBeeMallShoppingCartItemMapper.selectByUserIdAndGoodsId(newBeeMallShoppingCartItem.getUserId(), newBeeMallShoppingCartItem.getGoodsId());
        if (temp != null) {
            //已存在则修改该记录
            temp.setGoodsCount(newBeeMallShoppingCartItem.getGoodsCount());
            return updateNewBeeMallCartItem(temp);
        }
        NewBeeMallGoods newBeeMallGoods = newBeeMallGoodsMapper.selectByPrimaryKey(newBeeMallShoppingCartItem.getGoodsId());
        //商品为空
        if (newBeeMallGoods == null) {
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }
        int totalItem = newBeeMallShoppingCartItemMapper.selectCountByUserId(newBeeMallShoppingCartItem.getUserId()) + 1;
        //超出单个商品的最大数量
        if (newBeeMallShoppingCartItem.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        //超出最大数量
        if (totalItem > Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_TOTAL_NUMBER_ERROR.getResult();
        }
        //保存记录
        if (newBeeMallShoppingCartItemMapper.insertSelective(newBeeMallShoppingCartItem) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateNewBeeMallCartItem(NewBeeMallShoppingCartItem newBeeMallShoppingCartItem) {
        NewBeeMallShoppingCartItem newBeeMallShoppingCartItemUpdate = newBeeMallShoppingCartItemMapper.selectByPrimaryKey(newBeeMallShoppingCartItem.getCartItemId());
        if (newBeeMallShoppingCartItemUpdate == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        //超出单个商品的最大数量
        if (newBeeMallShoppingCartItem.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        // 数量相同不会进行修改
        if (newBeeMallShoppingCartItemUpdate.getGoodsCount().equals(newBeeMallShoppingCartItem.getGoodsCount())) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        // userId不同不能修改
        if (!newBeeMallShoppingCartItem.getUserId().equals(newBeeMallShoppingCartItemUpdate.getUserId())) {
            return ServiceResultEnum.NO_PERMISSION_ERROR.getResult();
        }
        newBeeMallShoppingCartItemUpdate.setGoodsCount(newBeeMallShoppingCartItem.getGoodsCount());
        newBeeMallShoppingCartItemUpdate.setUpdateTime(new Date());
        //修改记录
        if (newBeeMallShoppingCartItemMapper.updateByPrimaryKeySelective(newBeeMallShoppingCartItemUpdate) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public NewBeeMallShoppingCartItem getNewBeeMallCartItemById(Long newBeeMallShoppingCartItemId) {
        return newBeeMallShoppingCartItemMapper.selectByPrimaryKey(newBeeMallShoppingCartItemId);
    }

    @Override
    public Boolean deleteById(Long shoppingCartItemId, Long userId) {
        NewBeeMallShoppingCartItem newBeeMallShoppingCartItem = newBeeMallShoppingCartItemMapper.selectByPrimaryKey(shoppingCartItemId);
        if (newBeeMallShoppingCartItem == null) {
            return false;
        }
        //userId不同不能删除
        if (!userId.equals(newBeeMallShoppingCartItem.getUserId())) {
            return false;
        }
        return newBeeMallShoppingCartItemMapper.deleteByPrimaryKey(shoppingCartItemId) > 0;
    }

    @Override
    public List<NewBeeMallShoppingCartItemVO> getMyShoppingCartItems(Long newBeeMallUserId) {
        List<NewBeeMallShoppingCartItemVO> newBeeMallShoppingCartItemVOS = new ArrayList<>();
        List<NewBeeMallShoppingCartItem> newBeeMallShoppingCartItems = newBeeMallShoppingCartItemMapper.selectByUserId(newBeeMallUserId, Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER);
        if (!CollectionUtils.isEmpty(newBeeMallShoppingCartItems)) {
            //查询商品信息并做数据转换
            List<Long> newBeeMallGoodsIds = newBeeMallShoppingCartItems.stream().map(NewBeeMallShoppingCartItem::getGoodsId).collect(Collectors.toList());
            List<NewBeeMallGoods> newBeeMallGoods = newBeeMallGoodsMapper.selectByPrimaryKeys(newBeeMallGoodsIds);
            Map<Long, NewBeeMallGoods> newBeeMallGoodsMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(newBeeMallGoods)) {
                newBeeMallGoodsMap = newBeeMallGoods.stream().collect(Collectors.toMap(NewBeeMallGoods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
            }
            for (NewBeeMallShoppingCartItem newBeeMallShoppingCartItem : newBeeMallShoppingCartItems) {
                NewBeeMallShoppingCartItemVO newBeeMallShoppingCartItemVO = new NewBeeMallShoppingCartItemVO();
                BeanUtil.copyProperties(newBeeMallShoppingCartItem, newBeeMallShoppingCartItemVO);
                if (newBeeMallGoodsMap.containsKey(newBeeMallShoppingCartItem.getGoodsId())) {
                    NewBeeMallGoods newBeeMallGoodsTemp = newBeeMallGoodsMap.get(newBeeMallShoppingCartItem.getGoodsId());
                    newBeeMallShoppingCartItemVO.setGoodsCoverImg(newBeeMallGoodsTemp.getGoodsCoverImg());
                    String goodsName = newBeeMallGoodsTemp.getGoodsName();
                    // 字符串过长导致文字超出的问题
                    if (goodsName.length() > 28) {
                        goodsName = goodsName.substring(0, 28) + "...";
                    }
                    newBeeMallShoppingCartItemVO.setGoodsName(goodsName);
                    newBeeMallShoppingCartItemVO.setSellingPrice(newBeeMallGoodsTemp.getSellingPrice());
                    newBeeMallShoppingCartItemVOS.add(newBeeMallShoppingCartItemVO);
                }
            }
        }
        return newBeeMallShoppingCartItemVOS;
    }
}