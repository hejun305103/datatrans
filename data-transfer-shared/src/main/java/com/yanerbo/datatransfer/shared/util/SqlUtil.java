package com.yanerbo.datatransfer.shared.util;

import java.util.Arrays;

import com.yanerbo.datatransfer.shared.domain.DataTrans;

/**
 * 
 * @author jihaibo
 *
 */
public class SqlUtil {
	
	/**
	 * 获取指定表名总数据量
	 */
	private final static String sql_all_count = "select count(1) from /%s";
	
	/**
	 * 获取指定表名总数据量(分片)
	 */
	private final static String sql_all_count_sharding = "select count(1) from /%s/ where mod(/%s/,/%s/) = /%s";
	
	/**
	 * 删除指定表数据
	 */
	
	private final static String sql_delete = "delete from /%s";
	/**
	 * 清空指定表数据
	 */
	
	private final static String sql_truncate = "truncate table /%s";
	
	/**
	 * 标记位置
	 */
	private final static String sql_signpagepost = "select min(/%s/) pageStart, max(/%s/) pageEnd, count(1) totalCount from /%s/";
	/**
	 * 分页位置
	 */
	private final static String sql_pagepost = "select min(/%s/) pageStart, max(/%s/) pageEnd, count(1) totalCount from /%s/ where /%s/ >= /%s/ and rownum<= /%s/";

	/**
	 * 标记位置（分片）
	 */
	private final static String sql_signpagepost_sharding = "select min(/%s/) pageStart, max(/%s/) pageEnd, count(1) totalCount from /%s/ where mod(/%s/,/%s/) = /%s";

	/**
	 * 分页位置（分片）
	 */
	private final static String sql_pagepost_sharding = "select min(/%s/) pageStart, max(/%s/) pageEnd, count(1) totalCount from /%s/ where mod(/%s/,/%s/) = /%s and /%s/ >= /%s/ and rownum<= /%s/";

	
	/**
	 * 全表
	 * @param tableName
	 * @return
	 */
	public static String allCount(String tableName) {
		return String.format(sql_all_count, tableName);
	}
	
	/**
	 * 分片
	 * @param tableName
	 * @param key
	 * @param shardingItem
	 * @param shardingTotal
	 * @return
	 */
	public static String allCountSharding(String tableName, String key, int shardingItem, int shardingTotal) {
		return String.format(sql_all_count_sharding, tableName, key, shardingItem, shardingTotal);
	}
	
	/**
	 * 全表
	 * @param tableName
	 * @return
	 */
	public static String delete(String tableName) {
		return String.format(sql_delete, tableName);
	}
	
	/**
	 * 全表
	 * @param tableName
	 * @return
	 */
	public static String truncate(String tableName) {
		return String.format(sql_truncate, tableName);
	}
	
	/**
	 * 分页
	 * @param tableName
	 * @param key
	 * @param start
	 * @param pageCount
	 * @return
	 */
	public static String getSignPagePost(String tableName, String key) {
		return String.format(sql_signpagepost, key, key, tableName);
	}
	/**
	 * 分页
	 * @param tableName
	 * @param key
	 * @param start
	 * @param pageCount
	 * @return
	 */
	public static String getPagePost(String tableName, String key, int start, int pageCount) {
		return String.format(sql_pagepost, key, key, tableName, key, start, pageCount);
	}
	/**
	 * 
	 * @param tableName
	 * @param key
	 * @param shardingItem
	 * @param shardingTotal
	 * @return
	 */
	public static String getSignPagePostSharding(String tableName, String key, int shardingItem, int shardingTotal) {
		return String.format(sql_signpagepost_sharding, key, key, tableName, key, shardingItem, shardingTotal);
	}
	
	
	
	public static String getStartPagePost(String tableName, String key, int shardingItem, int shardingTotal,int start, int pageCount) {
		return "select min("+ key+ ") pageStart, max(" + key + ") pageEnd, count(1) totalCount from " 
				+ tableName + " where mod(" + key + "," + shardingTotal + ") = " + shardingItem
				+ " and "+ key+ " >= " + start + " and rownum<= " + pageCount;
	}
	/**
	 * 分页（分片）
	 * @param tableName
	 * @param key
	 * @param shardingItem
	 * @param shardingTotal
	 * @param start
	 * @param pageCount
	 * @return
	 */
	public static String getPagePostSharding(String tableName, String key, int shardingItem, int shardingTotal,int start, int pageCount) {
		return String.format(sql_pagepost_sharding, key, key, tableName, key, shardingItem, shardingTotal, key, start, pageCount);
	}
	
	
	
	
	public static String getInitPagePost(String tableName, String key, int shardingItem, int shardingTotal,int start, int pageCount) {
		return "select min("+ key+ ") pageStart, max(" + key + ") pageEnd, count(1) totalCount from " 
				+ tableName + " where mod(" + key + "," + shardingTotal + ") = " + shardingItem
				+ " and "+ key+ " >= " + start + " and rownum<= " + pageCount;
	}
	
	public static String getPageInfo(String tableName, String key, int shardingItem, int shardingTotal) {
		return "select min("+ key+ ") startPostPage, max(" + key + ") endPostPage, count(1) totalCount from " + tableName + " where mod(" + key + "," + shardingTotal + ") = " + shardingItem;
	}
	
	/**
	 * 分片
	 * @param tableName
	 * @param key
	 * @param shardingItem
	 * @param shardingTotal
	 * @return
	 */
	public static String getMaxKey(DataTrans entity,int shardingItem, int shardingTotal, int currentPage) {
		
		/**
		 * 如果表名和字段名称为null，那么就从sql进行构建
		 */
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("select max(").append(entity.getSourceKey()).append(") from ").append(entity.getSourceTable());
		//添加分片分页信息
		sqlBuilder.append(" where mod(" + entity.getSourceKey() + "," + shardingTotal + ") = " + shardingItem);
		sqlBuilder.append(" and ").append(entity.getSourceKey()).append(" >= ").append(currentPage);
		sqlBuilder.append(" and rownum<= ").append(entity.getPageCount());
		System.out.println(sqlBuilder.toString());
		return sqlBuilder.toString();
	}
	
	private static boolean isNotEmpty(String str) {
		if(str == null || str.isEmpty()) {
			return false;
		}
		return true;
	}
	
	public static String builderSelect(DataTrans entity,int shardingItem, int shardingTotal, int start, int end) {
		/**
		 * 如果表名和字段名称为null，那么就从sql进行构建
		 */
		StringBuilder sqlBuilder = new StringBuilder();
		if(isNotEmpty(entity.getSourceTable()) && isNotEmpty(entity.getSourceColumns())){
			sqlBuilder.append("select ").append(entity.getSourceColumns()).append(" from ").append(entity.getSourceTable());
		}else {
			sqlBuilder.append(entity.getSourceSql());
		}
		if(entity.getSourceSql().contains("where")) {
			//添加分片分页信息
			sqlBuilder.append(" and mod(" + entity.getSourceKey() + "," + shardingTotal + ") = " + shardingItem);
		}else {
			//添加分片分页信息
			sqlBuilder.append(" where mod(" + entity.getSourceKey() + "," + shardingTotal + ") = " + shardingItem);
		}
		sqlBuilder.append(" and ").append(entity.getSourceKey()).append(" >= ").append(start);
		sqlBuilder.append(" and ").append(entity.getSourceKey()).append(" < ").append(end);
//		sqlBuilder.append(" and rownum<= ").append(entity.getPageCount());
		System.out.println(sqlBuilder.toString());
		return sqlBuilder.toString();
	}
	
	public static String builderInsert(DataTrans entity) {
		if(isNotEmpty(entity.getTargetTable()) && isNotEmpty(entity.getTargetColumns())){
			return builderInsert(entity.getTargetTable(), entity.getTargetColumns());
		}
		return entity.getTargetSql();
	}
	
	public static String builderInsert(String table, String columns){
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("insert  into ").append(table).append(" (").append(columns).append(") values (");
		String[] fields = columns.split(",");
		for(int i=1; i<=fields.length; i++) {
			if(i<fields.length) {
				sqlBuilder.append("?,");
			}else {
				sqlBuilder.append("?)");
			}
		}
		return sqlBuilder.toString();
	}
	/**
	 * 
	 * @param sql
	 * @return
	 */
	public static String[] getFields(String sql){
		
		int startIndex = sql.toUpperCase().indexOf("(");
		int endIndex = sql.toUpperCase().indexOf(")");
		String[] fields = sql.substring(startIndex+"(".length(), endIndex).split(",");
		return fields;
		
	}
	
	
	public static void main(String[] args) {
		
		String sql = "insert into t_cmc_cust_account (createtime, createuserid, lastupdatetime, lastupdateuserid, custnumber, custname, bankaccount, acountname, relation, accountnature, accounttype, accountuse, isdefaultaccount, linkmanmobile, linkmanphone, bankname, bankid, bankcode, subbankid, subbankcode, subbankname, bankprovinceid, bankprovicecode, bankprovincename, bankcityid, bankcitycode, bankcityname, bankareaid, bankareacode, bankareaname, financelinkman, financelinkmanid, status) values()";
		System.out.println(Arrays.toString(getFields(sql)));
		
	}
	
}