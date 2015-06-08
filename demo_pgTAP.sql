-- header
begin;
-- # of tests
select plan(5);

------------------------------------------------------------
-- tests
------------------------------------------------------------
select has_table('Person');
select ok(count(*) = 0, '0 Personen') from "Person2";
select has_column('Person', 'ID');
select has_table('Abteilung');
select has_column('Abteilung', 'ID');

------------------------------------------------------------
-- get results
------------------------------------------------------------
select * from finish();

-- final rollback
rollback;