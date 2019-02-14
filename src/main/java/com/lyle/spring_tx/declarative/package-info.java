/**
 * 声明式事务<br>
 * <p>1.@Transactional 注解应该只被应用到 public 方法上，这是由 Spring AOP 的本质决定的。如果你在 protected、private
 * 或者默认可见性的方法上使用 @Transactional 注解，这将被忽略，也不会抛出任何异常</p>
 * <p>2.事务回滚异常只能为RuntimeException异常，而Checked Exception异常不回滚，捕获异常不抛出也不会回滚，但可以强制事务回滚：
 * TransactionAspectSupport.currentTransactionStatus().isRollbackOnly();</p>
 * <p>3.如果在接口、实现类或方法上都指定了@Transactional 注解，则优先级顺序为方法>实现类>接口</p>
 * <p>4.建议只在实现类或实现类的方法上使用@Transactional，而不要在接口上使用，这是因为如果使用JDK代理机制（基于接口的代理）是没问题；
 * 而使用使用CGLIB代理（继承）机制时就会遇到问题，因为其使用基于类的代理而不是接口，这是因为接口上的@Transactional注解是“不能继承的”</p>
 * <p>5.基于TransactionProxyFactoryBean,img/transactionProxyFactoryBean.png,img/transactionProxyFactoryBean_code.png
 * <code><prop key="transfer">PROPAGETION_REQUIRED,+java.lang.ArithmeticException</prop></code>
 * </p><br>
 * <p>6.基于AspectJ的配置方式,img/aspectJ.png</p><br>
 * <p>7.基于注解的方式,img/annotation_transaction.png</p><br>
 */
package com.lyle.spring_tx.declarative;