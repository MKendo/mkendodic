--�����м������ݿ⣺
sqlite3 mkendo201811.db

--����
.help

--��ѯ���б��
.tables

--��ʽ����ѯ���
.header on
.mode column
.timer on

--dicusers ע���û���
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

--dicentries�� ��Ŵ���--
create table dicentries(
id integer primary key not null,
code not null,
title ntext,
content ntext,
language ntext,
initial text, --����ĸ���ڿ�������
description ntext,
enable int
);

--comments�� ���ۣ���Ŵ�ҵķ�������ۣ�--
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

--ִ�����Ӵ���sql
�ļ������뽣���ʵ�����sql��

--����������ĸΪ��д
update dicentries set initial = UPPER(initial) ;

--����һ������
insert into comments(forentryid,forentrycode,dicuserid,language,title,content,description,commitdatetime,enable) 
values(83,'choyakusuburin',1,'CN','��Ծ����','��Ծ�������ķ�������','','2020-03-05 16:42:00',1);

-----һЩ��ѯsql--------------
--��ѯ���������ַ���ת��Ϊ��д ��������
select ini from (select upper(initial) ini from dicentries order by initial asc) group by ini;
--------------------------------
select count(*) from dicentries;
---------------------------------
 select *,u.wxname dicUserWxname, u.wximgurl  dicUserWximgurl from comments c 
left join dicusers u on c.dicuserid=u.id 
where forentrycode='choyakusuburin';
------------------------------------
--�����ȡһ��
SELECT code FROM dicentries ORDER BY RANDOM() limit 1;
----------------------------------------

--����
select * from (select code,count(code) cc from dicentries group by code) where cc>2;

