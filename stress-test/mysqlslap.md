- mysqlslap -uroot -p123456 -c500 --create-schema=miaosha -q "update test set count = count - 1 where id = 1 and count > 0;"

- xx