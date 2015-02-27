pg_prove
============

A Java replacement of [pg_prove from pgtap](http://pgtap.org/)

Why
---

It's not (easy) possible to export pg_prove results as JUnit result files on windows. cygwins Perl version (and dependend modules) is far behind the requiered version by `TAP::Harness::JUnit` and `TAP::Formatter::JUnit`

Usage
-----
It's not 100% compatible with pg_prove(http://pgtap.org/pg_prove.html) but this should work:

    pg_prove --d my_datbase test_file.sql
    pg_prove --dbname my_datbase test_file.sql

TODO:
-----

Test file handling:
 
1. passing a directory in the last argument
2. passing multiple files
3. passing wildcards as files

Database connection

1. passing credentials 

