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

--��ѯ���б��
SELECT name FROM sqlite_master WHERE type='table' ORDER BY name;

--��ѯ����е��ֶ�
PRAGMA  table_info("members");

*******************************************************
���ݿ���Ƶ�һЩ����
*******************************************************
1 ������ͬ����֮����»��ߣ�ȫСд
2 �ֶ�����ͬ����֮�䲻���»��ߣ�ȫСд



*******************************************************
��Ա��صı�
*******************************************************
--��Ա��
members

--��Ա��Ч�ڱ�
memeber_validates


*******************************************************
ԤԼ������صı�
*******************************************************
--bookings ԤԼ�����
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

placetimeid --simpletypes���
whereknowusid --simpletypes���

--simpletypes �����ͱ� 
--id,code,name,typecode(�����͵�����),description(����������),1
create table simpletypes(
id integer primary key not null,
code text key not null,
name ntext not null,
typecode text not null,
description text,
enable int
);

insert into simpletypes values(null,'FT01PM','������� ��һ��19:30--21:30','IntroLessonPlaceTime','���Ŀγ��Ͽγ���',1);
insert into simpletypes values(null,'FT03PM','������� ������19:30--21:30','IntroLessonPlaceTime','���Ŀγ��Ͽγ���',1);
insert into simpletypes values(null,'FT05PM','������� ������19:30--21:30','IntroLessonPlaceTime','���Ŀγ��Ͽγ���',1);
insert into simpletypes values(null,'FT06PM','��������ٶ����� �������� 14:00--16:00','IntroLessonPlaceTime','���Ŀγ��Ͽγ���',1);
insert into simpletypes values(null,'FT06PM2','������� �������� 16:00--18:00','IntroLessonPlaceTime','���Ŀγ��Ͽγ���',1);
insert into simpletypes values(null,'FT07AM','������� ��������9:30--12:30 ���޹�Ħ','IntroLessonPlaceTime','���Ŀγ��Ͽγ���',1);
insert into simpletypes values(null,'NS02PM','��ɽ���� �ܶ���20:00--22:00','IntroLessonPlaceTime','���Ŀγ��Ͽγ���',1);
insert into simpletypes values(null,'NS04PM','��ɽ���� ������20:00--22:00','IntroLessonPlaceTime','���Ŀγ��Ͽγ���',1);
insert into simpletypes values(null,'DZMT','�������ſ�����','WhereKnowUs','������֪�����ǵ�',1);
insert into simpletypes values(null,'NETSEARCH','�����ѵ���','WhereKnowUs','������֪�����ǵ�',1);
insert into simpletypes values(null,'FRIEND','�����Ѹ�������','WhereKnowUs','������֪�����ǵ�',1);
insert into simpletypes values(null,'MOMENTS','����Ȧ������','WhereKnowUs','������֪�����ǵ�',1);