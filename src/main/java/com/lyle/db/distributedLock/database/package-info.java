/**
 * <b>
 *   BUG：insertLock 关闭了连接，但是unlock 忘记关闭连接，导致连接池用完后程序获取不到可用的连接
 * </b>
 * <p>
 *   mysql engine <b>MyISAM</b>
 * </p>
 *
 */
package com.lyle.db.distributedLock.database;