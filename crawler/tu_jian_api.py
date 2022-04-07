#!/usr/bin/env python
# -*- coding: utf-8 -*-

# @Author : CZW

import base64
import json
import requests

# 图鉴账号密码
uname = "cnwxp"
pwd = "123456"


def base64_api(img, typeid):
    with open(img, 'rb') as f:
        base64_data = base64.b64encode(f.read())
        b64 = base64_data.decode()
    data = {"username": uname, "password": pwd, "typeid": typeid, "image": b64}
    result = json.loads(requests.post("http://api.ttshitu.com/predict", json=data).text)
    if result['success']:
        return result["data"]["result"]
    else:
        return result["message"]
    return ""
