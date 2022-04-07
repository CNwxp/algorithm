 **selenium实现百度搜索**
 ``` python
 from time import sleep
from selenium import webdriver
from selenium.webdriver.common.by import By

driver = webdriver.Chrome()
driver.get("https://www.baidu.com")

input = driver.find_element(By.ID,'kw')
input.send_keys("selenium 从入门到精通")

button = driver.find_element(By.ID,'su')
sleep(1)
button.click()
 ```
