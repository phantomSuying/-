# encoding:  utf-8
from django.shortcuts import render
from login import main
from django.http import HttpResponse
from django.http import HttpResponseRedirect


def first_page(request):
    return render(request, 'sign.html')


def signCheck(request):
    access = request.GET['signAccess']
    secrect = request.GET['signSecret']
    secrect2 = request.GET['signSecret2']
    entity_type = request.GET['signEntity_type']
    name = request.GET['signName']
    country = request.GET['signCountry']
    if secrect != secrect2:
        return render(request, 'sign.html')
    else:
        ClientPart = main.ClientPart()
        print ClientPart.sign_up(access, secrect, entity_type, name, country)
        return render(request, 'login.html')
