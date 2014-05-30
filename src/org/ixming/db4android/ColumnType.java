package org.ixming.db4android;

/**
 * 所有的数据类型，根据Cursor中提供的方法一一对应，没有具体到复杂数据类型。
 * 因为以下的一些现实：
 * <p>
 * 存储在 SQLite 数据库中的每个值（或是由数据库引擎所操作的值）都有一个以下的存储类型(storage classes)：
 * 	<ul>
 * 		<li>NULL. 值是空值。</li>
 * 		<li>INTEGER. 值是有符号整数，根据值的大小以1，2，3，4，6 或8字节存储。（即byte,short,smallint,int,long）</li>
 * 		<li>REAL. 值是浮点数，以8字节 IEEE 浮点数存储。</li>
 * 		<li>TEXT. 值是文本字符串，使用数据库编码（UTF-8, UTF-16BE 或 UTF-16LE）进行存储。</li>
 * 		<li>BLOB. 值是一个数据块，按它的输入原样存储。</li>
 * 	</ul>
 * </p>
 * 
 * <p>
 * 每个 SQLite 3 数据库中的列都归于以下的类型亲和性中的一种：
 * 	<ul>
 * 		<li>TEXT</li>
 * 		<li>NUMERIC</li>
 * 		<li>INTEGER</li>
 * 		<li>REAL</li>
 * 		<li>NONE</li>
 * 	</ul>
 * 一个具有 TEXT 亲和性的列使用存储类型 NULL、 TEXT 或 BLOB 存储所有数据。如果数值数据被插入到一个具有 TEXT 亲和性的列，则数据在存储前被转换为文本形式。
 * </p>
 * 
 * <p>
 * 列的亲和性是由它的声明类型决定的，按照以下顺序所示的规则：
 * 	<ul>
 * 		<li>1. 如果声明类型包含字符串“INT”，那它被指定为整型亲和性；</li>
 * 		<li>2. 如果列的声明类型包含任何“CHAR”、“CLOB”或“TEXT”字符串，那么该列具有文本亲和性。注意：VARCHAR类型包含“CHAR”并且被指定为文本亲和性；</li>
 * 		<li>3. 如果列的声明类型包含“BLOB”或者没有指定类型，那这列具有NONE亲和性；</li>
 * 		<li>4. 如果列的声明类型包含任何“REAL”、“FLOA”或“DOUB”字符串，则该列具有实数亲和性；</li>
 * 		<li>5. 否则，它将具有数值亲和性。</li>
 * 	</ul>
 * 注意：判定列亲和性规则的顺序是很重要的。一个具有“CHARINT”声明类型的列将匹配规则1和2，但是规则1优先所有该列具有整型亲和性。
 * </p>
 * 
 * 
 * @author Yin Yong
 * @version 1.0
 */
public enum ColumnType implements SQLDataType {
	
	/**
	 * column type of SHORT
	 */
	SHORT {
		private final Class<?>[] CLZ = new Class[]{ short.class, Short.class };
		@Override
		public String getSQLTypeName() {
			return "INTEGER";
		}

		@Override
		public Class<?>[] getSQLRelatedClasses() {
			return CLZ;
		}
		
	},
	
	/**
	 * column type of INTEGER
	 */
	INTEGER {
		private final Class<?>[] CLZ = new Class[]{ int.class, Integer.class };
		@Override
		public String getSQLTypeName() {
			return "INTEGER";
		}

		@Override
		public Class<?>[] getSQLRelatedClasses() {
			return CLZ;
		}
		
	},
	
	/**
	 * column type of LONG
	 */
	LONG {
		private final Class<?>[] CLZ = new Class[]{ long.class, Long.class };
		@Override
		public String getSQLTypeName() {
			return "INTEGER";
		}

		@Override
		public Class<?>[] getSQLRelatedClasses() {
			return CLZ;
		}
		
	},
	
	/**
	 * column type of FLOAT
	 */
	FLOAT {
		private final Class<?>[] CLZ = new Class[]{ float.class, Float.class };
		@Override
		public String getSQLTypeName() {
			return "REAL";
		}

		@Override
		public Class<?>[] getSQLRelatedClasses() {
			return CLZ;
		}
		
	},
	
	/**
	 * column type of DOUBLE
	 */
	DOUBLE {
		private final Class<?>[] CLZ = new Class[]{ double.class, Double.class };
		@Override
		public String getSQLTypeName() {
			return "REAL";
		}

		@Override
		public Class<?>[] getSQLRelatedClasses() {
			return CLZ;
		}
		
	},
	
	/**
	 * column type of VARCHAR（v1.0暂时为TEXT的替代）
	 */
	VARCHAR {
		private final Class<?>[] CLZ = new Class[]{ String.class };
		@Override
		public String getSQLTypeName() {
			return "TEXT";
		}

		@Override
		public Class<?>[] getSQLRelatedClasses() {
			return CLZ;
		}
		
	},
	
	/**
	 * column type of BLOB
	 */
	BLOB {
		private final Class<?>[] CLZ = new Class[]{ byte[].class, Byte[].class };
		@Override
		public String getSQLTypeName() {
			return "BLOB";
		}

		@Override
		public Class<?>[] getSQLRelatedClasses() {
			return CLZ;
		}
		
	};

}