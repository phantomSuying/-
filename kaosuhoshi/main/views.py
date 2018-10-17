# encoding:  utf-8
from django.shortcuts import render
from login import main
import json
from django.http import HttpResponse
from django.http import HttpResponseRedirect


# 返回main.html
def first_page(request):
    context = request.session.get('msg')

    ClientPart = main.ClientPart()
    access = context['labelUserName']

    context['userList'] = ClientPart.query_personal_permission(access)
    # print context['userList']
    # print context['dicts']
    # #测试
    # test={1,2,3,4}
    # context['userList'] =test

    return render(request, 'main.html', context)


# 更新个人数据
def update(request):
    context = request.session.get('msg')
    access = context['labelUserName']
    otherKey = request.GET["otherKey"]
    otherValue = request.GET['otherValue']
    curDicts=context['dicts']  # [otherKey]=otherValue
    if curDicts is None:
        curDicts={}
    curDicts[otherKey]=otherValue
    context['dicts']=curDicts
    print("otherKey = ", otherKey)
    print("otherValue = ", otherValue)
    message = curDicts
    request.session['msg'] = context
    ClientPart = main.ClientPart()
    ClientPart.update_personal_message(access, message)
    return HttpResponseRedirect('/main')


# 搜索他人数据
def search(request):
    context = request.session.get('msg')
    self_access = context['labelUserName']
    other_access = request.GET['other']

    ClientPart = main.ClientPart()
    print self_access
    print other_access
    # 获取permission list
    permissionList = ClientPart.ask_other_message(self_access, other_access)
    print permissionList
    # 获取查询对象所有的信息
    allOtherInfList = ClientPart.query_personal_message(other_access)

    # 过滤
    temp = {}
    if permissionList is None:
        permissionList = []
    for allows in permissionList:
        temp[allows] = allOtherInfList[allows]
    context['otherDicts'] = temp
    request.session['msg'] = context
    return HttpResponseRedirect('/main')

    # # 仅做测试用
    # temp={"12":"34","22":"34"}
    # context['otherDicts']=temp
    # request.session['msg'] = context
    # return HttpResponseRedirect('/main')


# 勾选可以公开的信息
def permissionDecide(request):
    context = request.session.get('msg')
    self_access = context['labelUserName']
    check_box_list = request.GET.getlist('check_box_lists')
    print("check box here", check_box_list)

    selected = request.GET.get('dropdown')
    ClientPart = main.ClientPart()
    ClientPart.update_permission_list(self_access, check_box_list, selected)

    # test=request.GET.get('dropdown')
    # print(test)
    return HttpResponseRedirect('/main')
