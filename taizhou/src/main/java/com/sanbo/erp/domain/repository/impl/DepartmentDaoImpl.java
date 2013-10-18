package com.sanbo.erp.domain.repository.impl;

import org.mybatis.spring.support.SqlSessionDaoSupport;

import com.sanbo.erp.domain.model.Department;
import com.sanbo.erp.domain.repository.DepartmentDao;

public class DepartmentDaoImpl extends SqlSessionDaoSupport implements DepartmentDao {

	public Department getDepartmentById(Integer id) {
		return (Department) getSqlSession().selectOne("com.sanbo.erp.domain.model.DepartmentMapper.getDepartmentById",
				id);
	}

}
