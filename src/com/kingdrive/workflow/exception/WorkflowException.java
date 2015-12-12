package com.kingdrive.workflow.exception;

public class WorkflowException extends Exception {

  int code;

  String message;

  Object obj[];

  public WorkflowException(int code) {
    message = "";
    this.code = code;
    setMessage(code);
  }

  public WorkflowException(int code, String message) {
    this.message = "";
    this.code = code;
    setMessage(code);
    if (message != null)
      this.message = this.message + "(" + message + ")";
  }

  public WorkflowException(int code, String message, Object obj[]) {
    this.message = "";
    this.code = code;
    setMessage(code);
    this.message = this.message + "(" + message + ")";
    this.obj = obj;
  }

  public int getCode() {
    return code;
  }

  public Object getObj(int index) {
    if (index > obj.length)
      return null;
    return obj[index];
  }

  public String toString() {
    return String.valueOf((new StringBuffer("工作流引擎错误[")).append(code).append("]：")
      .append(message));
  }

  public String getMessage() {
    return this.message;
  }

  private void setMessage(int code) {
    switch (code) {
    case 99801:
      this.message = "没有选择需要移交的任务。";
      break;

    case 999:
      this.message = "找不到相应的功能方法。";
      break;

    case 1001:
      this.message = "获取数据库连接时出错。";
      break;

    case 1002:
      this.message = "释放数据库连接时出错。";
      break;

    case 1003:
      this.message = "提交或回退事务时出错。";
      break;

    case 1010:
      this.message = "获取流程列表时出错。";
      break;

    case 1011:
      this.message = "获取流程列表时出错（取单条记录时）。";
      break;

    case 1015:
      this.message = "创建流程时出错（插入的记录数不为1）。";
      break;

    case 1016:
      this.message = "创建流程时出错（数据库存取错误）。";
      break;

    case 1020:
      this.message = "获取流程信息时出错。";
      break;

    case 1021:
      this.message = "获取流程信息时出错（取单条记录时）。";
      break;

    case 1025:
      this.message = "删除流程出错（删除的记录数不为1）。";
      break;

    case 1026:
      this.message = "删除流程时出错，流程原“是否可用”设置错误。";
      break;

    case 1027:
      this.message = "删除流程时出错（数据库存取错误）。";
      break;

    case 10251:
      this.message = "不能删除已经使用的流程";
      break;

    case 1030:
      this.message = "修改流程时出错（更新的记录数不为1）。";
      break;

    case 1033:
      this.message = "修改流程时出错（数据库存取错误）。";
      break;

    case 1031:
      this.message = "修改流程时出错，流程的“是否可用”设置错误。";
      break;

    case 1032:
      this.message = "修改流程时出错，流程原“是否可用”设置错误。";
      break;

    case 10301:
      this.message = "流程存在正在执行的实例，不能把流程设置为“不可用”。";
      break;

    case 10302:
      this.message = "流程没有设置完整，不能使其生效。";
      break;

    case 1035:
      this.message = "按执行者获取流程列表时出错。";
      break;

    case 1036:
      this.message = "按执行者获取流程列表时出错（取单条记录时）。";
      break;

    case 1040:
      this.message = "复制流程时，获取节点操作者执行顺序列表时出错。";
      break;

    case 1041:
      this.message = "复制流程时，复制节点操作者执行顺序时出错（每次插入的记录数不为1）。";
      break;

    case 1042:
      this.message = "复制流程时，复制节点操作者执行顺序时出错（数据库存取错误）。";
      break;

    case 1100:
      this.message = "判断是否有正在执行中的当前流程实例时出错。";
      break;

    case 1101:
      this.message = "判断是否有正在执行中的当前流程实例时出错（取单条记录时）。";
      break;

    case 1102:
      this.message = "创建工作流实例（实例id等于-1）时，当前任务存在（任务id不等于-1）。";
      break;

    case 1105:
      this.message = "创建新工作流实例时出错（插入的记录数不为1）。";
      break;

    case 1106:
      this.message = "创建新工作流实例时出错（数据库存取错误）。";
      break;

    case 1107:
      this.message = "工作流实例已经结束或中止，不能激活/挂起。";
      break;

    case 1108:
      this.message = "工作流实例已经结束，不能中止。";
      break;

    case 1110:
      this.message = "修改工作流实例时出错（更新的记录数不为1）。";
      break;

    case 1111:
      this.message = "修改工作流实例时出错（数据库存取错误）。";
      break;

    case 1112:
      this.message = "开始任务节点不能执行退回处理。";
      break;

    case 1113:
      this.message = "后续节点没有待办任务，不能回收。";
      break;

    case 1115:
      this.message = "创建工作流实例中的新动作时出错（插入的记录数不为1）。";
      break;

    case 1116:
      this.message = "创建工作流实例中的新动作时出错（数据库存取错误）。";
      break;

    case 1117:
      this.message = "授权或移交任务时，必须指定任务执行者。";
      break;

    case 1118:
      this.message = "授权或移交任务时，不能指定自己为任务执行者。";
      break;

    case 1120:
      this.message = "创建新当前任务时出错（插入的记录数不为1）。";
      break;

    case 1121:
      this.message = "创建新当前任务时出错（数据库存取错误）。";
      break;

    case 1122:
      this.message = "创建后续任务节点任务时出错，任务节点没有预置执行者或指派任务执行者顺序错误，请正确指派任务执行者。";
      break;

    case 1123:
      this.message = "创建当前任务节点任务时出错，节点没有后续的顺签执行者，请正确指派任务执行者。";
      break;

    case 1125:
      this.message = "删除当前任务时出错（删除的记录数不为1）。";
      break;

    case 1126:
      this.message = "删除当前任务时出错（数据库存取错误）。";
      break;

    case 1130:
      this.message = "判断是否有当前任务记录时出错。";
      break;

    case 1131:
      this.message = "判断是否有当前任务记录时出错（取单条记录时）。";
      break;

    case 1135:
      this.message = "获取工作流监控列表时出错。";
      break;

    case 1136:
      this.message = "获取工作流监控列表时出错（取单条记录时）。";
      break;

    case 1140:
      this.message = "获取工作流历史列表时出错。";
      break;

    case 1141:
      this.message = "获取工作流历史列表时出错（取单条记录时）。";
      break;

    case 1145:
      this.message = "获取待执行任务列表时出错。";
      break;

    case 1146:
      this.message = "获取待执行任务列表时出错（取单条记录时）。";
      break;

    case 1150:
      this.message = "创建工作流动作历史时出错（插入的记录数不为1）。";
      break;

    case 1151:
      this.message = "创建工作流动作历史时出错（数据库存取错误）。";
      break;

    case 1155:
      this.message = "获取节点未选择动作的人数时出错。";
      break;

    case 1156:
      this.message = "获取节点未选择动作的人数时出错（取单条记录时）。";
      break;

    case 1160:
      this.message = "获取下一顺序操作者时出错。";
      break;

    case 1161:
      this.message = "获取下一顺序操作者时出错（取单条记录时）。";
      break;

    case 1162:
      this.message = "获取第一顺序操作者时出错。";
      break;

    case 1163:
      this.message = "获取第一顺序操作者时出错（取单条记录时）。";
      break;

    case 1165:
      this.message = "获取当前操作者的直接上级时出错。";
      break;

    case 1166:
      this.message = "获取当前操作者的直接上级时出错（取单条记录时）。";
      break;

    case 1170:
      this.message = "修改当前任务时出错（更新的记录数不为1）。";
      break;

    case 1171:
      this.message = "修改当前任务时出错（数据库存取错误）。";
      break;

    case 1175:
      this.message = "获取上一次操作时出错。";
      break;

    case 1176:
      this.message = "获取上一次操作时出错（取单条记录时）。";
      break;

    case 1177:
      this.message = "没有取到任何动作记录。";
      break;

    case 1180:
      this.message = "删除工作流动作时出错（删除的记录数不为1）。";
      break;

    case 1181:
      this.message = "删除工作流动作时出错（数据库存取错误）。";
      break;

    case 1185:
      this.message = "获取当前任务信息时出错。";
      break;

    case 1186:
      this.message = "获取当前任务信息时出错（取单条记录时）。";
      break;

    case 1190:
      this.message = "获取备注列表时出错。";
      break;

    case 1191:
      this.message = "获取备注列表时出错（取单条记录时）。";
      break;

    case 1195:
      this.message = "获取实例信息时出错。";
      break;

    case 1196:
      this.message = "获取实例信息时出错（取单条记录时）。";
      break;

    case 1200:
      this.message = "获取流程的开始任务节点时出错。";
      break;

    case 1201:
      this.message = "获取流程的开始任务节点时出错（取单条记录时）。";
      break;

    case 1205:
      this.message = "获取流程的当前任务节点时出错。";
      break;

    case 1206:
      this.message = "获取流程的当前任务节点时出错（取单条记录时）。";
      break;

    case 1207:
      this.message = "获取流程的开始任务节点时出错，流程没有配置开始结点与任务节点间的流向。";
      break;

    case 1210:
      this.message = "获取TaskListener时出错。";
      break;

    case 1214:
      this.message = "执行任务节点时出错（开始任务节点的执行者方式只能是独签）。";
      break;

    case 1215:
      this.message = "执行任务节点时出错（执行者方式标志不正常）。";
      break;

    case 1216:
      this.message = "执行任务节点时出错（数量或百分比标志不正常）。";
      break;

    case 1217:
      this.message = "执行任务节点时出错（执行者关系标志不正常）。";
      break;

    case 1218:
      this.message = "执行任务节点时出错（节点流向类型标志不正常）。";
      break;

    case 1220:
      this.message = "获取后续节点的类型时出错（节点类型标志不正常）。";
      break;

    case 1221:
      this.message = "获取前置节点的类型时出错（节点类型标志不正常）。";
      break;

    case 1225:
      this.message = "没有找到任何符合条件的流程方向。";
      break;

    case 1230:
      this.message = "表达式解析错误。";
      break;

    case 1235:
      this.message = "当前环节是并签环节，可以操作的总人数为0"
        + "（取wf_tab_executor_order表的数据）。可能的原因是数据库引擎中有脏数据。 ";
      break;

    case 1300:
      this.message = "创建移交历史记录时出错（插入的记录数不为1）。";
      break;

    case 1301:
      this.message = "创建移交历史记录时出错（数据库存取错误）。";
      break;

    case 1305:
      this.message = "创建移交控制记录时出错（插入的记录数不为1）。";
      break;

    case 1306:
      this.message = "创建移交控制记录时出错（数据库存取错误）。";
      break;

    case 1310:
      this.message = "创建变量值记录时出错（插入的记录数不为1）。";
      break;

    case 1311:
      this.message = "创建变量值记录时出错（数据库存取错误）。";
      break;

    case 1312:
      this.message = "变量值赋值错误，变量值 = ";
      break;

    case 1313:
      this.message = "变量值的变量（variableId)没有定义。变量值 = ";
      break;

    case 1315:
      this.message = "修改变量值记录时出错（更新的记录数不为1）。";
      break;

    case 1316:
      this.message = "修改变量值记录时出错（数据库存取错误）。";
      break;

    case 1320:
      this.message = "获取变量值列表时出错。";
      break;

    case 1321:
      this.message = "获取变量值列表时出错（取单条记录时）。";
      break;

    case 1325:
      this.message = "修改移交控制记录时出错（更新的记录数不为1）。";
      break;

    case 1326:
      this.message = "修改移交控制记录时出错（数据库存取错误）。";
      break;

    case 1330:
      this.message = "获取移交列表时出错。";
      break;

    case 1331:
      this.message = "获取移交列表时出错（取单条记录时）。";
      break;

    case 1335:
      this.message = "判断是否有当前任务的移交记录时出错。";
      break;

    case 1336:
      this.message = "判断是否有当前任务的移交记录时出错（取单条记录时）。";
      break;

    case 1340:
      this.message = "删除移交控制记录时出错（删除的记录数不为1）。";
      break;

    case 1341:
      this.message = "删除移交控制记录时出错（数据库存取错误）。";
      break;

    case 1345:
      this.message = "创建通过数表记录时出错（插入的记录数不为1）。";
      break;

    case 1346:
      this.message = "创建通过数表记录时出错（数据库存取错误）。";
      break;

    case 1351:
      this.message = "删除通过数表记录时出错（数据库存取错误）。";
      break;

    case 1355:
      this.message = "获取节点已通过人数时出错。";
      break;

    case 1356:
      this.message = "获取节点已通过人数时出错（取单条记录时）。";
      break;

    case 1360:
      this.message = "创建任务操作者顺序时出错（插入的记录数不为1）。";
      break;

    case 1361:
      this.message = "创建任务操作者顺序时出错（数据库存取错误）。";
      break;

    case 1365:
      this.message = "获取操作者顺序列表时出错。";
      break;

    case 1366:
      this.message = "获取操作者顺序时出错（取单条记录时）。";
      break;

    case 1370:
      this.message = "获取可对某串签节点进行操作的人数时出错。";
      break;

    case 1371:
      this.message = "获取可对某串签节点进行操作的人数时出错（取单条记录时）。";
      break;

    case 1375:
      this.message = "获取最大执行顺序号时出错。";
      break;

    case 1376:
      this.message = "获取最大执行顺序号时出错（取单条记录时）。";
      break;

    case 1380:
      this.message = "创建节点操作者顺序时出错（插入的记录数不为1）。";
      break;

    case 1381:
      this.message = "创建节点操作者顺序时出错（数据库存取错误）。";
      break;

    case 1385:
      this.message = "删除节点操作者顺序时出错（删除的记录数不为1）。";
      break;

    case 1386:
      this.message = "删除节点操作者顺序时出错（数据库存取错误）。";
      break;

    case 1390:
      this.message = "删除任务操作者顺序时出错（删除的记录数不为1）。";
      break;

    case 1391:
      this.message = "删除任务操作者顺序时出错（数据库存取错误）。";
      break;

    case 1395:
      this.message = "获取任务操作者顺序列表时出错。";
      break;

    case 1396:
      this.message = "获取任务操作者顺序列表时出错（取单条记录时）。";
      break;

    case 1400:
      this.message = "删除任务操作者顺序时出错（删除的记录数不为1）。";
      break;

    case 1401:
      this.message = "删除任务操作者顺序时出错（数据库存取错误）。";
      break;

    case 1405:
      this.message = "修改任务操作者顺序时出错（更新的记录数不为1）。";
      break;

    case 1406:
      this.message = "修改任务操作者顺序时出错（数据库存取错误）。";
      break;

    case 1410:
      this.message = "更新节点操作者顺序时出错（删除的记录数不为1）。";
      break;

    case 1411:
      this.message = "更新节点操作者顺序时出错（数据库存取错误）。";
      break;

    case 1415:
      this.message = "获取具有相同顺序的人数时出错。";
      break;

    case 1416:
      this.message = "获取具有相同顺序的人数时出错（取单条记录时）。";
      break;

    case 2000:
      this.message = "操作数据库错误";
      break;

    case 2001:
      this.message = "获取流程节点列表错误";
      break;

    case 2002:
      this.message = "增加节点错误";
      break;

    case 2003:
      this.message = "删除节点错误";
      break;

    case 2004:
      this.message = "修改节点错误";
      break;

    case 2005:
      this.message = "增加节点执行者错误";
      break;

    case 2006:
      this.message = "获取员工组列表错误";
      break;

    case 2007:
      this.message = "获取节点执行者列表错误";
      break;

    case 2008:
      this.message = "获取员工组员工列表错误";
      break;

    case 2009:
      this.message = "获取节点后续流向列表错误";
      break;

    case 2010:
      this.message = "删除节点流向错误";
      break;

    case 2011:
      this.message = "获取节点前置流向列表错误";
      break;

    case 2012:
      this.message = "增加节点流向错误";
      break;

    case 2013:
      this.message = "更新节点流向错误";
      break;

    case 2014:
      this.message = "设置流向关系错误";
      break;

    case 2015:
      this.message = "设置节点执行方式错误";
      break;

    case 2016:
      this.message = "设置完成条件错误";
      break;

    case 2017:
      this.message = "设置完成顺序错误";
      break;

    case 2018:
      this.message = "获取节点执行顺序错误";
      break;

    case 2019:
      this.message = "获取流程的流向列表错误";
      break;

    case 2020:
      this.message = "流程的流程变量名重复";
      break;

    case 2021:
      this.message = "增加流程变量错误";
      break;

    case 2022:
      this.message = "删除流程变量错误";
      break;

    case 2023:
      this.message = "更新流程变量错误";
      break;

    case 2024:
      this.message = "设置流转表达式错误";
      break;

    case 2025:
      this.message = "数字类型变量不能设置为字符类型";
      break;

    case 2030:
      this.message = "同一流程状态名重复";
      break;

    case 2050:
      this.message = "添加代理历史记录错误";
      break;

    case 2051:
      this.message = "添加代理错误";
      break;

    case 2052:
      this.message = "更新代理错误";
      break;

    case 2053:
      this.message = "删除代理错误";
      break;

    case 2054:
      this.message = "根据接受者获取有效代理列表错误";
      break;

    case 2055:
      this.message = "根据员工获取可执行节点列表错误";
      break;

    case 2056:
      this.message = "不能代理给自己";
      break;

    case 2057:
      this.message = "代理时间错误";
      break;

    case 2058:
      this.message = "获取前代理记录列表错误";
      break;

    case 2059:
      this.message = "超出可代理时间范围";
      break;

    case 2060:
      this.message = "获取员工已代理记录错误";
      break;

    case 2061:
      this.message = "获取后代理记录列表错误";
      break;

    case 2062:
      this.message = "不能代理给拥有者";
      break;

    case 2063:
      this.message = "根据所有者节点获取代理列表错误";
      break;

    case 2100:
      this.message = "创建组织错误";
      break;

    case 2101:
      this.message = "更新组织错误";
      break;

    case 2102:
      this.message = "创建职位错误";
      break;

    case 2103:
      this.message = "更新职位错误";
      break;

    case 2104:
      this.message = "创建员工错误";
      break;

    case 2105:
      this.message = "更新员工错误";
      break;

    case 2106:
      this.message = "创建员工组错误";
      break;

    case 2107:
      this.message = "更新员工组错误";
      break;

    case 2200:
      this.message = "登陆失败，用户不存在!";
      break;

    case 2201:
      this.message = "登陆失败，用户状态无效!";
      break;

    case 2202:
      this.message = "登陆失败，密码错误!";
      break;
    case 2203:
      this.message = "";
      break;
    case 2204:
      this.message = "无法获取汇总子任务!请使用流程监控检查流程是否正常流转!";
      break;
    case 2205:
      this.message = "无法获取汇总节点，请检查流程是否已经流经了汇总节点！";
      break;
    case 2206:
      this.message = "环节参数无效";
      break;
    case 2207:
      this.message = "字符串类型的流向表达式只支持＝＝符号";
      break;
    case 2208:
        this.message = "执行listener的回调出错!";
        break;
    case 2209:
        this.message = "获取工作流变量有误!";
    	break;
    default:
      this.message = "未知错误。";
      break;
    }
  }
}
