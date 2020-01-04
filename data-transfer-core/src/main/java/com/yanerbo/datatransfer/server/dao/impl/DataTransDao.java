package com.yanerbo.datatransfer.server.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.yanerbo.datatransfer.entity.DataType;
import com.yanerbo.datatransfer.entity.Page;
import com.yanerbo.datatransfer.server.dao.IDataTransDao;
import com.yanerbo.datatransfer.support.util.DataSourceUtil;
import com.yanerbo.datatransfer.support.util.SqlUtil;

/**
 * 
 * @author jihaibo
 *
 */
@Repository
public class DataTransDao implements IDataTransDao{
	
	
	
	@Autowired
	private DataSourceUtil dataSourceUtil;
	/**
	 * 获取条数
	 * @param sql
	 * @param type
	 * @return
	 */
	@Override
	public Page pageInfo(DataType type, String sql) {
		
		return dataSourceUtil.getJdbcTemplate(type).queryForObject(sql, new RowMapper<Page>(){
			@Override
			public Page mapRow(ResultSet rs, int rowNum) throws SQLException {
				Page page = new Page();  
				page.setTotalCount(rs.getInt("totalCount"));
				page.setPageStart(rs.getInt("pageStart"));
				page.setPageEnd(rs.getInt("pageEnd"));
				return page;
			}
		});
	}
	
	/**
	 * 获取条数
	 * @param sql
	 * @param type
	 * @return
	 */
	@Override
	public int count(DataType type, String sql) {
		return dataSourceUtil.getJdbcTemplate(type).queryForObject(sql, Integer.class);
	}
	
	
	/**
	 * 获取数据
	 * @param sql
	 * @param type
	 * @return
	 */
	@Override
	public List<Map<String, Object>>  select(DataType type, String sql) {
		return dataSourceUtil.getJdbcTemplate(type).queryForList(sql);
	}
	
	
	/**
	 * 获取数据
	 * @param sql
	 * @param type
	 * @return
	 */
	@Override
	public void insertBatch(DataType type, String sql, List<Map<String, Object>> datas) {
		//批量保存
		long startTime = System.currentTimeMillis();
		dataSourceUtil.getJdbcTemplate(type).batchUpdate(sql, new BatchPreparedStatementSetter() {
			//获取字段列表
			private String[] fields = SqlUtil.getFields(sql);
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				 try{
					 Map<String, Object> data = datas.get(i);
					 for(int j = 0; j<fields.length;j++) {
						 ps.setObject(j+1, data.get(fields[j]));
					 }
				 }catch(Exception e){
			        e.printStackTrace();
			     }
			}
			@Override
			public int getBatchSize() {
				return datas.size();
			}
		});
		System.out.println("insertBatch 耗时：" + (System.currentTimeMillis() - startTime));
	}
	
	/**
	 * 获取数据
	 * @param sql
	 * @param type
	 * @return
	 */
	@Override
	public void insert(DataType type, String sql, Map<String, Object> data) {
		
		String[] fields = SqlUtil.getFields(sql);
		long startTime = System.currentTimeMillis();
		dataSourceUtil.getJdbcTemplate(type).update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				for(int j = 0; j<fields.length;j++) {
					 ps.setObject(j+1, data.get(fields[j]));
				 }
			}
		});
		System.out.println("insert 耗时：" + (System.currentTimeMillis() - startTime) + "value: " + data);
	}

	@Override
	public void delete(DataType type, String sql) {
		dataSourceUtil.getJdbcTemplate(type).execute(sql);
	}

	@Override
	public void truncate(DataType type, String sql) {
		dataSourceUtil.getJdbcTemplate(type).execute(sql);
	}


}