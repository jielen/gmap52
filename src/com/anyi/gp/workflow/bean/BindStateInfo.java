/*
 * Created on 2005-6-26
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.anyi.gp.workflow.bean;


/**
 * @author   leidaohong
 * @time  2005-6-26
 */
public class BindStateInfo extends AbstractWFEntity {
    private int state_id;
		//private String stateValue;
		private String stateName;
		//private String template_id;
		private String tabName;
		private	String fieldName;
        /**
		 * @return   Returns the name.
		 * @uml.property   name="stateName"
		 */
        public String getStateName() {
            return stateName;
        }
        /**
		 * @param name   The name to set.
		 * @uml.property   name="stateName"
		 */
        public void setStateName(String name) {
            this.stateName = name;
        }
    /**
	 * @return   Returns the state_id.
	 * @uml.property   name="state_id"
	 */
    public int getState_id() {
        return state_id;
    }
    /**
	 * @param state_id   The state_id to set.
	 * @uml.property   name="state_id"
	 */
    public void setState_id(int state_id) {
        this.state_id = state_id;
    }
        /**
         * @return Returns the template_id.
         */
 
        /**
		 * @return   Returns the template_id.
		 * @uml.property   name="fieldName"
		 */
        public String getFieldName() {
            return fieldName;
        }
        /**
		 * @param fieldName   The fieldName to set.
		 * @uml.property   name="fieldName"
		 */
        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }
        /**
		 * @return   Returns the tabName.
		 * @uml.property   name="tabName"
		 */
        public String getTabName() {
            return tabName;
        }
        /**
		 * @param tabName   The tabName to set.
		 * @uml.property   name="tabName"
		 */
        public void setTabName(String tabName) {
            this.tabName = tabName;
        }
}
