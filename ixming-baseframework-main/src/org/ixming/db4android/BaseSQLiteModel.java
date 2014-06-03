package org.ixming.db4android;

/**
 * 这是一个标识接口，定义该类是与SQLite数据库中的某个表相关的一个Bean对象，
 * 在本框架的解析过程中，会针对实现该接口的具体类，一直扫描到其顶层的Class（最先实现该标识接口的类），以寻找列字段；
 * 
 * <p>
 * 在应用中，我们可能有些Model具有继承关系，而有继承关系的Model对应的表是不一样的，
 * 设计者既然以此处理，我们认为需要层级查找列（从最外层子类，到最先实现该接口的祖先类），
 * 以确保没有遗漏;
 * </p>
 * @author Yin Yong
 * @version 1.0
 */
public interface BaseSQLiteModel { }
