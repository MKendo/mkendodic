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