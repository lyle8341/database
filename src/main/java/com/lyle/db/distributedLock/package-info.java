/**
 * 1.分布式与单机情况下最大的不同在于其不是多线程而是多进程
 * 2.多线程由于可以共享堆内存，因此可以简单的采取内存作为标记存储位置。
 * 而进程之间甚至可能都不在同一台物理机上，因此需要将标记存储在一个所有进程都能看到的地方
 */
package com.lyle.db.distributedLock;