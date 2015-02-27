-- header
begin;
-- # of tests
select plan(4);

------------------------------------------------------------
-- tests
------------------------------------------------------------
select has_table('Person');
select has_column('Person', 'ID');
select has_table('Abteilung');
select has_column('Abteilung', 'ID');

------------------------------------------------------------
-- get results
------------------------------------------------------------
select * from finish();

-- final rollback
rollback;