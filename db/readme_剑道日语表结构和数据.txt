--命令行加载数据库：
sqlite3 mkendo201811.db

--帮助
.help

--查询所有表格
.tables

--格式化查询结果
.header on
.mode column
.timer on

--dicusers 注册用户表：
--id,name,mobile,password,wxopenid,commitdatetime,description,enable

create table dicusers(
id integer primary key not null,
name ntext not null,
mobile text,
password text,
wxopenid text,
wxunionid text,
wximgurl ntext,
wxname text,
commitdatetime datetime not null,
description ntext,
enable int
);

--dicentries表 存放词条--
create table dicentries(
id integer primary key not null,
code not null,
title ntext,
content ntext,
language ntext,
initial text, --首字母用于快速索引
description ntext,
enable int
);

--comments表 评论（存放大家的翻译和评论）--
create table comments(
id integer primary key not null,
forentryid integer not null,
forentrycode text not null,
dicuserid integer not null,
language text,
title ntext,
content ntext,
description ntext,
commitdatetime datetime not null,
enable int
);

--执行增加词条sql
文件《导入剑道词典数据sql》

--更新索引字母为大写
update dicentries set initial = UPPER(initial) ;

--增加一条翻译
insert into comments(forentryid,forentrycode,dicuserid,language,title,content,description,commitdatetime,enable) 
values(83,'choyakusuburin',1,'CN','跳跃摆振','跳跃摆振中文翻译内容','','2020-03-05 16:42:00',1);

-----一些查询sql--------------
--查询可索引的字符，转换为大写 升序排列
select ini from (select upper(initial) ini from dicentries order by initial asc) group by ini;
--------------------------------
select count(*) from dicentries;
---------------------------------
 select *,u.wxname dicUserWxname, u.wximgurl  dicUserWximgurl from comments c 
left join dicusers u on c.dicuserid=u.id 
where forentrycode='choyakusuburin';
------------------------------------
--随机获取一条
SELECT code FROM dicentries ORDER BY RANDOM() limit 1;
----------------------------------------

--查重
select * from (select code,count(code) cc from dicentries group by code) where cc>2;

