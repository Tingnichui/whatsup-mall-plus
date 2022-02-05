package com.chunhui;

import com.chunhui.common.Constants;
import com.chunhui.common.NewBeeMallCategoryLevelEnum;
import com.chunhui.dao.AdminUserMapper;
import com.chunhui.dao.GoodsCategoryMapper;
import com.chunhui.dao.NewBeeMallOrderMapper;
import com.chunhui.dao.NewBeeMallSeckillSuccessMapper;
import com.chunhui.entity.GoodsCategory;
import com.chunhui.entity.NewBeeMallOrder;
import com.chunhui.redis.RedisCache;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class WhatsupMallPlusApplicationTests {

    @Autowired
    private GoodsCategoryMapper goodsCategoryMapper;
    @Autowired
    private NewBeeMallSeckillSuccessMapper newBeeMallSeckillSuccessMapper;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private NewBeeMallOrderMapper newBeeMallOrderMapper;

    @Test
    void test01() {
        List<Integer> numbers = Arrays.asList(3, 2, 2, 3, 7, 3, 5);
// 获取对应的平方数
        List<Integer> squaresList = numbers.stream().map( i -> i*i).distinct().collect(Collectors.toList());
        System.out.println(squaresList);
    }

    @Test
    void test02() {

        List<GoodsCategory> firstLevelCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), NewBeeMallCategoryLevelEnum.LEVEL_ONE.getLevel(), Constants.INDEX_CATEGORY_NUMBER);

        List<Long> firstLevelCategoryIds = firstLevelCategories.stream().map(GoodsCategory::getCategoryId).collect(Collectors.toList());
        System.err.println(firstLevelCategoryIds);

    }

    @Test
    void test03() {
        List<Long> list = newBeeMallSeckillSuccessMapper.selectBySeckillId(1L);
        list.forEach(i -> System.out.println(i));
        list.forEach(System.out::println);
        redisCache.setCacheSet("test", list);

    }

    @Test
    void test04() {
        NewBeeMallOrder newBeeMallOrder = new NewBeeMallOrder();
        newBeeMallOrder.setExtraInfo("11111");
        int i = newBeeMallOrderMapper.insertSelective(newBeeMallOrder);
        System.out.println(i);
    }

    @Test
    void test05() {
        System.out.println(new Date());
    }
}
