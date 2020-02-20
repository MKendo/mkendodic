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

--查询所有表格
SELECT name FROM sqlite_master WHERE type='table' ORDER BY name;

--查询表格中的字段
PRAGMA  table_info("members");

*******************************************************
数据库设计的一些规则：
*******************************************************
1 表名不同单词之间带下划线，全小写
2 字段名不同单词之间不带下划线，全小写



*******************************************************
会员相关的表
*******************************************************
--会员表
members

--会员有效期表
memeber_validates


*******************************************************
预约体验相关的表
*******************************************************
--bookings 预约体验表：
--id,name,mobile,wxopenid,placetime,classdate,whereknowus,commitdatetime,enable

create table bookings(
id integer primary key not null,
name ntext not null,
mobile text not null,
wxopenid text,
placetimeid integer not null, 
classdate date not null,
whereknowusid integer,
commitdatetime datetime not null,
description ntext,
enable int
);

placetimeid --simpletypes外键
whereknowusid --simpletypes外键

--simpletypes 简单类型表 
--id,code,name,typecode(简单类型的类型),description(简单类型描述),1
create table simpletypes(
id integer primary key not null,
code text key not null,
name ntext not null,
typecode text not null,
description text,
enable int
);

insert into simpletypes values(null,'FT01PM','福田道场 周一晚19:30--21:30','IntroLessonPlaceTime','初心课程上课场次',1);
insert into simpletypes values(null,'FT03PM','福田道场 周三晚19:30--21:30','IntroLessonPlaceTime','初心课程上课场次',1);
insert into simpletypes values(null,'FT05PM','福田道场 周五晚19:30--21:30','IntroLessonPlaceTime','初心课程上课场次',1);
insert into simpletypes values(null,'FT06PM','福田道场少儿剑道 周六下午 14:00--16:00','IntroLessonPlaceTime','初心课程上课场次',1);
insert into simpletypes values(null,'FT06PM2','福田道场 周六下午 16:00--18:00','IntroLessonPlaceTime','初心课程上课场次',1);
insert into simpletypes values(null,'FT07AM','福田道场 周日上午9:30--12:30 仅限观摩','IntroLessonPlaceTime','初心课程上课场次',1);
insert into simpletypes values(null,'NS02PM','南山道场 周二晚20:00--22:00','IntroLessonPlaceTime','初心课程上课场次',1);
insert into simpletypes values(null,'NS04PM','南山道场 周四晚20:00--22:00','IntroLessonPlaceTime','初心课程上课场次',1);
insert into simpletypes values(null,'DZMT','大众美团看到哒','WhereKnowUs','从哪里知道我们的',1);
insert into simpletypes values(null,'NETSEARCH','网上搜到哒','WhereKnowUs','从哪里知道我们的',1);
insert into simpletypes values(null,'FRIEND','好朋友告诉我哒','WhereKnowUs','从哪里知道我们的',1);
insert into simpletypes values(null,'MOMENTS','朋友圈看到哒','WhereKnowUs','从哪里知道我们的',1);