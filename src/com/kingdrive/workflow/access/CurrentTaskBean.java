package com.kingdrive.workflow.access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.kingdrive.workflow.db.ConnectionFactory;
import com.kingdrive.workflow.db.DBHelper;
import com.kingdrive.workflow.model.CurrentTaskModel;
import com.kingdrive.workflow.util.StringUtil;

public class CurrentTaskBean{
	// log4j for log
	final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(CurrentTaskBean.class);

	public CurrentTaskBean(){
	}

	public void delete(int currentTaskId) throws SQLException{
		String sql = "delete from WF_CURRENT_TASK where CURRENT_TASK_ID=?";
		PreparedStatement st = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
			st = conn.prepareStatement(sql);
			st.setInt(1, currentTaskId);
			st.executeUpdate();
			logger.info(sql);
		}catch(SQLException e){
			e.printStackTrace();
			throw new SQLException(e.getMessage());
		}finally{
			DBHelper.closeConnection(conn,st,null);
		}
	}

	public ArrayList find() throws SQLException{
		return find(-1, -1);
	}

	public ArrayList find(int theBegin, int theEnd) throws SQLException{
		ArrayList list = new ArrayList();
		String sql = "select CURRENT_TASK_ID, INSTANCE_ID, NODE_ID, EXECUTOR, DELEGATION_ID, OWNER, CREATOR, CREATE_TIME, LIMIT_EXECUTE_TIME, RESPONSIBILITY ,PARENT_TASK_ID from WF_CURRENT_TASK";
		PreparedStatement st = null;
		ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
			st = conn.prepareStatement(sql);
			if(theEnd > 0)
				st.setFetchSize(theEnd);

			rs = st.executeQuery();
			if(theBegin > 1)
				rs.absolute(theBegin - 1);
			while(rs.next()){
				list.add(parseResultSet(rs));
				if(rs.getRow() == theEnd)
					break;
			}
		}catch(SQLException e){
			e.printStackTrace();
			throw new SQLException(e.getMessage());
		}finally{
			DBHelper.closeConnection(conn,st,rs);
		}

		return list;
	}

	public CurrentTaskModel findByKey(int currentTaskId) throws SQLException{
		CurrentTaskModel model = new CurrentTaskModel();
		String sql = "select CURRENT_TASK_ID, INSTANCE_ID, NODE_ID, EXECUTOR, DELEGATION_ID, OWNER, CREATOR, CREATE_TIME, LIMIT_EXECUTE_TIME, RESPONSIBILITY, PARENT_TASK_ID from WF_CURRENT_TASK where CURRENT_TASK_ID=?";
		PreparedStatement st = null;
		ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
			st = conn.prepareStatement(sql);
			st.setInt(1, currentTaskId);
			rs = st.executeQuery();
			if(rs.next()){
				model = parseResultSet(rs);
			}
		}catch(SQLException e){
			e.printStackTrace();
			throw new SQLException(e.getMessage());
		}finally{
			DBHelper.closeConnection(conn,st,rs);
		}
		return model;
	}

	public int insert(CurrentTaskModel model) throws SQLException{
		String sql = null;
		ArrayList strList = new ArrayList();
		ArrayList valList = new ArrayList();
		if(model.getCurrentTaskIdModifyFlag()){
			StringUtil.makeDynaParam("CURRENT_TASK_ID", model.getCurrentTaskId(), strList, valList);
		}
		if(model.getInstanceIdModifyFlag()){
			StringUtil.makeDynaParam("INSTANCE_ID", model.getInstanceId(), strList, valList);
		}
		if(model.getNodeIdModifyFlag()){
			StringUtil.makeDynaParam("NODE_ID", model.getNodeId(), strList, valList);
		}
		if(model.getExecutorModifyFlag()){
			StringUtil.makeDynaParam("EXECUTOR", convertSQL(model.getExecutor()), strList, valList);
		}
		if(model.getDelegationIdModifyFlag()){
			StringUtil.makeDynaParam("DELEGATION_ID", model.getDelegationId(), strList, valList);

		}
		if(model.getOwnerModifyFlag()){
			StringUtil.makeDynaParam("OWNER", convertSQL(model.getOwner()), strList, valList);
		}
		if(model.getCreatorModifyFlag()){
			StringUtil.makeDynaParam("CREATOR", convertSQL(model.getCreator()), strList, valList);
		}
		if(model.getCreateTimeModifyFlag()){
			StringUtil.makeDynaParam("CREATE_TIME", convertSQL(model.getCreateTime()), strList, valList);
		}
		if(model.getLimitExecuteTimeModifyFlag()){
			StringUtil.makeDynaParam("LIMIT_EXECUTE_TIME", convertSQL(model.getLimitExecuteTime()), strList, valList);

		}
		if(model.getResponsibilityModifyFlag()){
			StringUtil.makeDynaParam("RESPONSIBILITY", model.getResponsibility(), strList, valList);

		}
		if(model.getParentTaskIdModifyFlag()){
			StringUtil.makeDynaParam("PARENT_TASK_ID", model.getParentTaskId(), strList, valList);
		}
		String insertString = "";
		String valsString = "";
		int length = strList.size();
		if(length == 0){
			insertString = null;
			valsString = null;
		}else{
			for(int i = 0; i <= length - 1; i++){
				insertString += strList.get(i) + ",";
				valsString += "?,";
			}
			insertString = insertString.substring(0, insertString.length() - 1);
			valsString = valsString.substring(0, valsString.length() - 1);
		}
		sql = "insert into WF_CURRENT_TASK(" + insertString + ") values(" + valsString + ")";
		DBHelper.executeUpdate(sql, valList.toArray());
		logger.info(sql);
		return 1;
	}

	public int update(CurrentTaskModel model) throws SQLException{
		String sql = null;
		String updateString = "";
		ArrayList strList = new ArrayList();
		ArrayList valList = new ArrayList();
		if(model.getInstanceIdModifyFlag()){
			StringUtil.makeDynaParam("INSTANCE_ID=", model.getInstanceId(), strList, valList);
		}
		if(model.getNodeIdModifyFlag()){
			StringUtil.makeDynaParam("NODE_ID=", model.getNodeId(), strList, valList);
		}
		if(model.getExecutorModifyFlag()){
			StringUtil.makeDynaParam("EXECUTOR=", convertSQL(model.getExecutor()), strList, valList);
		}
		if(model.getDelegationIdModifyFlag()){
			StringUtil.makeDynaParam("DELEGATION_ID=", model.getDelegationId(), strList, valList);
		}
		if(model.getOwnerModifyFlag()){
			StringUtil.makeDynaParam("OWNER=", convertSQL(model.getOwner()), strList, valList);
		}
		if(model.getCreatorModifyFlag()){
			StringUtil.makeDynaParam("CREATOR=", convertSQL(model.getCreator()), strList, valList);
		}
		if(model.getCreateTimeModifyFlag()){
			StringUtil.makeDynaParam("CREATE_TIME=", convertSQL(model.getCreateTime()), strList, valList);
		}
		if(model.getLimitExecuteTimeModifyFlag()){
			StringUtil.makeDynaParam("LIMIT_EXECUTE_TIME=", convertSQL(model.getLimitExecuteTime()), strList, valList);
		}
		if(model.getResponsibilityModifyFlag()){
			StringUtil.makeDynaParam("RESPONSIBILITY=", model.getResponsibility(), strList, valList);
		}
		if(model.getParentTaskIdModifyFlag()){
			StringUtil.makeDynaParam("PARENT_TASK_ID=", model.getParentTaskId(), strList, valList);
		}
		if(strList.size() == 0)
			return 0;
		valList.add(model.getCurrentTaskId());
		int length = strList.size();
		if(length == 0){
			updateString = null;
		}else{
			for(int i = 0; i <= length - 1; i++){
				updateString += strList.get(i) + "?,";
			}
			updateString = updateString.substring(0, updateString.length() - 1);
		}
		sql = "update WF_CURRENT_TASK set " + updateString + " where CURRENT_TASK_ID=?";
		DBHelper.executeUpdate(sql, valList.toArray());
		logger.info(sql);
		return 1;
	}

	public void removeByInstance(int instanceId) throws SQLException{
		String sql = "delete from wf_current_task where instance_id = ?";
		PreparedStatement st = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
			st = conn.prepareStatement(sql);
			st.setInt(1, instanceId);
			st.executeUpdate();
			logger.info(sql);
		}catch(SQLException e){
			e.printStackTrace();
			throw new SQLException(e.getMessage());
		}finally{
			DBHelper.closeConnection(conn,st,null);
		}
	}

	public void removeByNodeId(int instanceId, int nodeId) throws SQLException{
		String sql = "delete from wf_current_task where instance_id = ? and node_id = ?";
		PreparedStatement st = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
			st = conn.prepareStatement(sql);
			st.setInt(1, instanceId);
			st.setInt(2, nodeId);
			st.executeUpdate();
			logger.info(sql);
		}catch(SQLException e){
			e.printStackTrace();
			throw new SQLException(e.getMessage());
		}finally{
			DBHelper.closeConnection(conn,st,null);
		}
	}

	public void removeMainTaskByNodeId(int instanceId, int nodeId) throws SQLException{
		// 只删除该节点的主办任务，保留辅办任务
		String sql = "delete from wf_current_task where instance_id = ? and node_id = ?" + " and responsibility = '1'";
		PreparedStatement st = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
			st = conn.prepareStatement(sql);
			st.setInt(1, instanceId);
			st.setInt(2, nodeId);
			st.executeUpdate();
			logger.info(sql);
		}catch(SQLException e){
			e.printStackTrace();
			throw new SQLException(e.getMessage());
		}finally{
			DBHelper.closeConnection(conn,st,null);
		}
	}

	public ArrayList hasCurrentTask(int instanceId, int nodeId) throws SQLException{
		return hasCurrentTask(-1, -1, instanceId, nodeId);
	}

	public ArrayList hasCurrentTask(int theBegin, int theEnd, int instanceId, int nodeId) throws SQLException{
		ArrayList list = new ArrayList();
		String sql = "select * from wf_current_task where instance_id = ? and node_id = ?";
		PreparedStatement st = null;
		ResultSet rs = null;
    Connection conn = null;
    try {
    	conn = ConnectionFactory.getConnection();
			if(theEnd > 0)
				st.setFetchSize(theEnd);
			st = conn.prepareStatement(sql);
			st.setInt(1, instanceId);
			st.setInt(2, nodeId);
			rs = st.executeQuery();
			if(theBegin > 1)
				rs.absolute(theBegin - 1);
			while(rs.next()){
				list.add(parseResultSet(rs));
				if(rs.getRow() == theEnd)
					break;
			}
		}catch(SQLException e){
			e.printStackTrace();
			throw new SQLException(e.getMessage());
		}finally{
			DBHelper.closeConnection(conn,st,rs);
		}

		return list;
	}

	private CurrentTaskModel parseResultSet(ResultSet rs) throws SQLException{
		CurrentTaskModel model = new CurrentTaskModel();
		try{
			model.setCurrentTaskId(rs.getInt("CURRENT_TASK_ID"));
			if(rs.wasNull())
				model.setCurrentTaskId(null);
		}catch(Exception e){
			model.setCurrentTaskId(null);
		}

		try{
			model.setInstanceId(rs.getInt("INSTANCE_ID"));
			if(rs.wasNull())
				model.setInstanceId(null);
		}catch(Exception e){
			model.setInstanceId(null);
		}

		try{
			model.setNodeId(rs.getInt("NODE_ID"));
			if(rs.wasNull())
				model.setNodeId(null);
		}catch(Exception e){
			model.setNodeId(null);
		}

		try{
			model.setExecutor(rs.getString("EXECUTOR"));
			if(rs.wasNull())
				model.setExecutor(null);
		}catch(Exception e){
			model.setExecutor(null);
		}

		try{
			model.setDelegationId(rs.getInt("DELEGATION_ID"));
			if(rs.wasNull())
				model.setDelegationId(null);
		}catch(Exception e){
			model.setDelegationId(null);
		}

		try{
			model.setOwner(rs.getString("OWNER"));
			if(rs.wasNull())
				model.setOwner(null);
		}catch(Exception e){
			model.setOwner(null);
		}

		try{
			model.setCreator(rs.getString("CREATOR"));
			if(rs.wasNull())
				model.setCreator(null);
		}catch(Exception e){
			model.setCreator(null);
		}

		try{
			model.setCreateTime(rs.getString("CREATE_TIME"));
			if(rs.wasNull())
				model.setCreateTime(null);
		}catch(Exception e){
			model.setCreateTime(null);
		}

		try{
			model.setLimitExecuteTime(rs.getString("LIMIT_EXECUTE_TIME"));
			if(rs.wasNull())
				model.setLimitExecuteTime(null);
		}catch(Exception e){
			model.setLimitExecuteTime(null);
		}

		try{
			model.setResponsibility(rs.getInt("RESPONSIBILITY"));
			if(rs.wasNull())
				model.setResponsibility(null);
		}catch(Exception e){
			model.setResponsibility(null);
		}

		try{
			model.setParentTaskId(rs.getInt("PARENT_TASK_ID"));
			if(rs.wasNull())
				model.setParentTaskId(null);
		}catch(Exception e){
			model.setParentTaskId(null);
		}

		return model;
	}

	private String convertSQL(String input){
		String temp1, temp2;
		int tempIndex = 0, curIndex = 0;
		temp1 = input;
		if(input != null){
			while(true){
				curIndex = temp1.indexOf('\'', tempIndex);
				if(curIndex == -1)
					break;
				temp2 = temp1;
				temp1 = temp2.substring(0, curIndex) + "'" + temp2.substring(curIndex);
				tempIndex = curIndex + 2;
			}
		}
		return temp1;
	}
	
	public void removeByExecutor (int instanceId, int nodeId, String executor) throws SQLException{
		String sql = "delete from wf_current_task where instance_id = ? and node_id=? and executor=?";
		Connection conn = null;
		PreparedStatement st = null;
		try{
			conn = ConnectionFactory.getConnection();
			st = conn.prepareStatement(sql);
			st.setInt(1, instanceId);
			st.setInt(2, nodeId);
			st.setString(3, executor);
			st.executeUpdate();
			logger.info(sql);
		}catch(SQLException e){
			e.printStackTrace();
			throw new SQLException(e.getMessage());
		}finally{
			DBHelper.closeConnection(conn, st, null);
		}
	}

}