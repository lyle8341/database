#!/bin/sh

#mysqldump --column-statistics=0  -hlocalhost -P3306 -uroot -p123456 databases > db-`date +%Y-%m-%d`.sql
mysqldump --column-statistics=0  -hlocalhost -P3306 -uroot -p123456 --databases databases databases2 > db-`date +%Y-%m-%d`.sql