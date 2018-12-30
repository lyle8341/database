# databases

* 本工程软件版本

|  mysql  |  redis  |  mongo  |
|:-------:|:-------:|:-------:|
|  8.0.13 |         |         |

><a href="#variables">variables</a>  
<a href="#not_null_default">not_null_default</a>  
<a href="#three">three </a>

<a name="variables">variables</a>
+ 查看mysql的datadir
  + mysql> show variables like 'datadir';

+ mysql console清屏
  + mysql> system clear


https://www.cnblogs.com/liujiacai/p/7753324.html


  
  
段落的前后要有空行，所谓的空行是指没有文字内容。若想在段内强制换行的方式是使用**两个以上**空格加上回车（引用中换行省略回车）。


> xx

> xx

> xx
>> xx


    void main()    
    {    
        printf("Hello, Markdown.");    
}  


> *斜体*，_斜体_    
> **粗体**，__粗体__


+
+
+

* `*`
* \*

- `-`
- \-

- + this

- + - + - + this

1. g



1 . d

分割线
***

><a name="not_null_default">not_null_default</a>

![not null default](/img/not-null-default.png)
```sql
mysql> insert into lyle(sex)values('男');
#ERROR 1364 (HY000): Field 'name' doesn't have a default value
mysql> insert into lyle(name)values('gg');
#Query OK, 1 row affected (0.29 sec)
mysql> insert into lyle(name,sex)values('ggg',null);
#ERROR 1048 (23000): Column 'sex' cannot be null
```


分割线
---

|xx

___
分割线


