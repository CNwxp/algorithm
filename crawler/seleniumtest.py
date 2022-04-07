#!/usr/bin/env python
# -*- coding: utf-8 -*-

# @Author : CZW
# B站登录-图鉴代码平台-点选验证码

import time
from selenium import webdriver
from selenium.webdriver import ActionChains  # 动作链
from tu_jian_api import base64_api  # 图鉴

# 哔哩哔哩账号密码
user_name = "15937523795"
user_pwd = "love.u2"

# 浏览器对象
driver = webdriver.Chrome()

# 访问哔哩哔哩登录页
driver.get("https://passport.bilibili.com/login")

# 输入账号
driver.find_element_by_xpath('//input[@id="login-username"]').send_keys(user_name)
time.sleep(1)

# 输入密码
driver.find_element_by_xpath('//input[@id="login-passwd"]').send_keys(user_pwd)
time.sleep(1)

# 点击登录
driver.find_element_by_xpath('//a[text()="登录"]').click()
time.sleep(3)

# 找到弹出验证码框的节点元素
yzm_element = driver.find_element_by_xpath('//div[@class="geetest_panel_next"]')
time.sleep(1)

# 保存截取验证码框的图片
yzm_element.screenshot("bilibili_yzm.png")

# 图鉴打码
code_result = base64_api(r"./bilibili_yzm.png", 27)
print("坐标", code_result)

# 根据坐标点击
for item in code_result.split("|"):
    x = item.split(",")[0]
    y = item.split(",")[-1]
    print(x, y)
    time.sleep(1)
    ActionChains(driver).move_to_element_with_offset(yzm_element, int(x), int(y)).click().perform()
time.sleep(1)

# 点击确认
driver.find_element_by_xpath('//div[text()="确认"]').click()
time.sleep(3)

# 进入个人主页
driver.get("https://space.bilibili.com/")

input("回车关闭>>>")
driver.quit()
driver.close()
print("执行完毕")
