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

insert into configs(code,name,value,enable)  values('wxgetopenid','΢�Ż�ȡopenid�Ľӿ�url','https://api.weixin.qq.com/sns/jscode2session?appid=wxe6a6249f535bcf1c&secret=3693fe2c3d3a7ae45d3f4d40d0f5e68d&js_code=__js_code__&grant_type=authorization_code',1);
