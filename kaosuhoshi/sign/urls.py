from django.conf.urls import url,include
from sign import views
urlpatterns=[
    url(r'^$',views.first_page),
    url(r'^/signCheck', views.signCheck),

]