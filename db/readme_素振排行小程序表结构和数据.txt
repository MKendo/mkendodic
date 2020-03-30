*******************************************************
注册登录相关的表
*******************************************************
--users 注册用户表：
--id,name,mobile,password,wxopenid,commitdatetime,description,enable

create table users(
id integer primary key not null,
name ntext not null,
mobile text,
password text,
wxopenid text,--素振suburi小程序openid
wximgurl ntext,
wxname text,
commitdatetime datetime not null,
description ntext,
enable int
);

alter table users add wxdicopenid text;

--增加wxunionid列
alter table users add wxunionid text;
--一定要更新为空串，否则程序查询时会出错因为找不到这个列
update users set wxunionid='';


--configs表
create table configs(
id integer primary key not null,
code text not null,
name text not null,
value ntext not null,
description ntext,
enable int
);

*******************************************************
素振数量排名相关的表
*******************************************************
create table user_suburis(
id integer primary key not null,
userid int,
amount int,
commitdatetime datetime not null,
description ntext,
enable int
);

--前三名
 select userid,sum(amount) total from user_suburis group by userid order by total desc limit 3;

 select u.id,u.wxname,sum(amount) total from user_suburis us left join users u on us.userid=u.id group by userid order by total desc limit 3;

 select u.id,u.wxname,sum(amount) total,us.commitdatetime from user_suburis us left join users u on us.userid=u.id group by userid having us.commitdatetime=max(us.commitdatetime) order by total desc limit 3;

select  u.id,u.wxname,sum(amount) total,us.commitdatetime from user_suburis us left join users u on us.userid=u.id group by userid having us.commitdatetime=max(us.commitdatetime) order by total desc;

select * from (
select  u.id uid,u.wxname,sum(amount) total,us.commitdatetime from user_suburis us left join users u on us.userid=u.id group by userid having us.commitdatetime=max(us.commitdatetime) order by total desc
) where uid=1;

select ifnull(sum(amount),0) total from user_suburis where userid=1;
select sum(amount) total from user_suburis where userid=1;

--指定userid的排名
 select userid,sum(amount) total from user_suburis group by userid order by total;

*******************************************************
点赞相关的表
*******************************************************
create table user_zans(
id integer primary key not null,
userid int,
zanuserid int,
count int,
commitdatetime datetime not null,
enable int
);

--点赞
insert into user_zans(userid,zanuserid,count,commitdatetime,enable) 
values(1,1,1,'2020-02-25 17:50:42',1);

--查某用户被点多少赞
select zanuserid,sum(count) from user_zans group by zanuserid;

select *,sum(uz.count) from (
select  u.id uuserid ,u.wxname,sum(amount) total,us.commitdatetime 
from users u
left join user_suburis us on u.id =us.userid
group by u.id having us.commitdatetime=max(us.commitdatetime) 
order by total desc) 
left join user_zans uz on uz.zanuserid = uuserid;

--查用户信息和获得了多少个赞
select *,ifnull(sum(uz.count),0) from (
select u.id uid, u.wxname ,ifnull(sum(us.amount),0) total
from users u 
left join user_suburis us on u.id=us.userid
group by u.id having us.commitdatetime=max(us.commitdatetime)
)
left join user_zans uz on uz.zanuserid = uid 
group by uid 
order by total desc;

--跳过0行取10行 用于分页
select id,wxname,wxunionid from users limit 10 offset 0;

