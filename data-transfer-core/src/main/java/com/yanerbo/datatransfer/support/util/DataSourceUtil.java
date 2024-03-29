package com.yanerbo.datatransfer.support.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import com.yanerbo.datatransfer.shared.domain.DataType;
import com.yanerbo.datatransfer.exception.DataTransRuntimeException;

/**
 * 
 * 多数据源管理类
 * @author jihaibo
 *
 */
@Component
public class DataSourceUtil {
	
	@Autowired
	@Qualifier("sourceJdbcTemplate")
	private JdbcTemplate sourceJdbcTemplate;
	

	@Autowired
	@Qualifier("targetJdbcTemplate")
	private JdbcTemplate targetJdbcTemplate;
	
	
	/**
	 * 
	 * @param type
	 * @return
	 */
	public JdbcTemplate getJdbcTemplate(DataType type) {
		
		switch(type) {
			case source:return sourceJdbcTemplate;
			case target:return targetJdbcTemplate;
		}
		throw new DataTransRuntimeException("没有找到对应数据源配置");
	}
}