from django.conf.urls import url,include
from login import views
urlpatterns=[
    url(r'^$',views.first_page),
    url(r'^/loginCheck',views.loginCheck),
]