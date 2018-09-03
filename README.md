# 参赛作品
## 如何查询自己信息？
使用query_personal_message函数，返回值是json结构的  
`{＜/br＞
   "name":"xx"＜/br＞
   "sex":"xx"＜/br＞
   ...＜/br＞
 }`  
 ## 如何修改自己信息？
 使用update_personal_message函数，参数中的message如上述json结构所说
 ## 如何查询其他人的信息？
 使用ask_other_message函数，返回值为bool型，再调用query_personal_message即可
 ## 如何修改允许查看自己信息的人的列表和不允许查看自己信息的人的列表？
 使用update_permission_list，permit_list和reject_list分别是允许和禁止列表，使用的是python的list数据结构
