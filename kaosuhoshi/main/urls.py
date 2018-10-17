from django.conf.urls import url,include
from main import views
urlpatterns=[
    url(r'^$',views.first_page),
    url(r'^/search',views.search),
    url(r'^/update',views.update),
    url(r'^/permissionDecide',views.permissionDecide),

]