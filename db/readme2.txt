*******************************************************
ע���¼��صı�
*******************************************************
--users ע���û���
--id,name,mobile,password,wxopenid,commitdatetime,description,enable

create table users(
id integer primary key not null,
name ntext not null,
mobile text,
password text,
wxopenid text,
wximgurl ntext,
wxname text,
commitdatetime datetime not null,
description ntext,
enable int
);


--configs��
create table configs(
id integer primary key not null,
code text not null,
name text not null,
value ntext not null,
description ntext,
enable int
);

*******************************************************
��������������صı�
*******************************************************
create table user_suburis(
id integer primary key not null,
userid int,
amount int,
commitdatetime datetime not null,
description ntext,
enable int
);

--ǰ����
 select userid,sum(amount) total from user_suburis group by userid order by total desc limit 3;

 select u.id,u.wxname,sum(amount) total from user_suburis us left join users u on us.userid=u.id group by userid order by total desc limit 3;

 select u.id,u.wxname,sum(amount) total,us.commitdatetime from user_suburis us left join users u on us.userid=u.id group by userid having us.commitdatetime=max(us.commitdatetime) order by total desc limit 3;

select  u.id,u.wxname,sum(amount) total,us.commitdatetime from user_suburis us left join users u on us.userid=u.id group by userid having us.commitdatetime=max(us.commitdatetime) order by total desc limit 3;



--ָ��userid������
 select userid,sum(amount) total from user_suburis group by userid order by total;

