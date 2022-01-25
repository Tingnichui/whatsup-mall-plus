package com.chunhui;

import com.chunhui.dao.AdminUserMapper;
import com.chunhui.entity.AdminUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WhatsupMallPlusApplicationTests {

    @Autowired
    private AdminUserMapper adminUserMapper;

    @Test
    void test00() {
        AdminUser adminUser = adminUserMapper.selectByPrimaryKey(1);
        System.out.println(adminUser);
    }

}
