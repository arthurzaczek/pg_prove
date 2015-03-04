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

Database connection:

1. passing credentials 

Copyright and License
---------------------

The MIT License (MIT)

Copyright (c) 2015 Arthur Zaczek

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.