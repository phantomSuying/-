# 参赛作品
1.注册使用signUp函数<br>
2.登陆使用logIn函数，返回结果为JSONObject（阿尔山api返回值），需判断其中errcode是否为0<br>
3.获取用户自己信息请使用getUserInformation函数，包括key_pair和数字资产存证（存储个人信息）的did<br>
4.上传个人信息使用uploadPOE函数，个人信息请存储在JSON数据结构中<br>
5.更改个人信息使用uodatePOE函数，同上<br>
6.获取（谁要请求访问你的信息）列表，请使用getPermissionFromWho函数，返回JSONArray结构，均以OFF/ON开头，OFF代表禁止访问，ON代表允许访问，可更改<br>
7.更改（谁要请求访问你的信息）列表，请使用updatePermissionBox函数，可更改开头的OFF/ON，将结果存为JSONArray传入参数<br>
8.updateMessageBox函数更改MessageBox中的数据，其中传入的JSONObject参数为key_pair和POEdid，如无必要，请勿使用<br>
9.getMessageBoxInformation函数获取MessageBox中的数据，可获取用户的key_pair和POEdid，如无必要，请勿使用<br>
10.请求访问他人信息使用askPermissionBox函数，返回结果代表可以/不可以访问<br>
11.getPermissionBoxInformation函数查询PermissionBox中的数据，如无必要，请勿使用<br>
12.analyzeMetadata/JSONObjectToMetadata（解码/编码）metadata<br>
## 返回值请尽量判断是否为null
