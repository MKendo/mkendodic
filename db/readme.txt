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