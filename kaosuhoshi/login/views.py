# encoding: utf-8
from django.shortcuts import render
from django.http import HttpResponse
from django.http import HttpResponseRedirect
from login import main
import json

def first_page(request):
    return render(request,'login.html')

def loginCheck(request):
    access = request.GET['access']
    password = request.GET['password']
    print("access = ", access)
    print("password = ", password)

    #检测用户的access与password
    ClientPart=main.ClientPart()
    jsonstr=ClientPart.log_in(access,password)
    context={}
    #errorcode是0则为已经注册用户
    if(jsonstr['ErrCode']==0):
        #获取该用户的信息
        jsonPersonalInf=ClientPart.query_personal_message(access)
        context['dicts']=jsonPersonalInf
        print jsonPersonalInf
        context['labelUserName']=access
        request.session['msg']=context
        return HttpResponseRedirect('/main')
        #非注册用户
    else:
        return HttpResponseRedirect('/login')


    # #仅做测试用 #以下代码用来 测试跳转至main并传递参数
    # context={}
    # context['labelUserName']=access
    # testlist={"1":"2","2":"3","3":"3"}
    # context['dicts']=testlist
    # request.session['msg']=context
    # return HttpResponseRedirect('/main')
