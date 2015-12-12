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
    return String.valueOf((new StringBuffer("�������������[")).append(code).append("]��")
      .append(message));
  }

  public String getMessage() {
    return this.message;
  }

  private void setMessage(int code) {
    switch (code) {
    case 99801:
      this.message = "û��ѡ����Ҫ�ƽ�������";
      break;

    case 999:
      this.message = "�Ҳ�����Ӧ�Ĺ��ܷ�����";
      break;

    case 1001:
      this.message = "��ȡ���ݿ�����ʱ����";
      break;

    case 1002:
      this.message = "�ͷ����ݿ�����ʱ����";
      break;

    case 1003:
      this.message = "�ύ���������ʱ����";
      break;

    case 1010:
      this.message = "��ȡ�����б�ʱ����";
      break;

    case 1011:
      this.message = "��ȡ�����б�ʱ����ȡ������¼ʱ����";
      break;

    case 1015:
      this.message = "��������ʱ��������ļ�¼����Ϊ1����";
      break;

    case 1016:
      this.message = "��������ʱ�������ݿ��ȡ���󣩡�";
      break;

    case 1020:
      this.message = "��ȡ������Ϣʱ����";
      break;

    case 1021:
      this.message = "��ȡ������Ϣʱ����ȡ������¼ʱ����";
      break;

    case 1025:
      this.message = "ɾ�����̳���ɾ���ļ�¼����Ϊ1����";
      break;

    case 1026:
      this.message = "ɾ������ʱ��������ԭ���Ƿ���á����ô���";
      break;

    case 1027:
      this.message = "ɾ������ʱ�������ݿ��ȡ���󣩡�";
      break;

    case 10251:
      this.message = "����ɾ���Ѿ�ʹ�õ�����";
      break;

    case 1030:
      this.message = "�޸�����ʱ�������µļ�¼����Ϊ1����";
      break;

    case 1033:
      this.message = "�޸�����ʱ�������ݿ��ȡ���󣩡�";
      break;

    case 1031:
      this.message = "�޸�����ʱ�������̵ġ��Ƿ���á����ô���";
      break;

    case 1032:
      this.message = "�޸�����ʱ��������ԭ���Ƿ���á����ô���";
      break;

    case 10301:
      this.message = "���̴�������ִ�е�ʵ�������ܰ���������Ϊ�������á���";
      break;

    case 10302:
      this.message = "����û����������������ʹ����Ч��";
      break;

    case 1035:
      this.message = "��ִ���߻�ȡ�����б�ʱ����";
      break;

    case 1036:
      this.message = "��ִ���߻�ȡ�����б�ʱ����ȡ������¼ʱ����";
      break;

    case 1040:
      this.message = "��������ʱ����ȡ�ڵ������ִ��˳���б�ʱ����";
      break;

    case 1041:
      this.message = "��������ʱ�����ƽڵ������ִ��˳��ʱ����ÿ�β���ļ�¼����Ϊ1����";
      break;

    case 1042:
      this.message = "��������ʱ�����ƽڵ������ִ��˳��ʱ�������ݿ��ȡ���󣩡�";
      break;

    case 1100:
      this.message = "�ж��Ƿ�������ִ���еĵ�ǰ����ʵ��ʱ����";
      break;

    case 1101:
      this.message = "�ж��Ƿ�������ִ���еĵ�ǰ����ʵ��ʱ����ȡ������¼ʱ����";
      break;

    case 1102:
      this.message = "����������ʵ����ʵ��id����-1��ʱ����ǰ������ڣ�����id������-1����";
      break;

    case 1105:
      this.message = "�����¹�����ʵ��ʱ��������ļ�¼����Ϊ1����";
      break;

    case 1106:
      this.message = "�����¹�����ʵ��ʱ�������ݿ��ȡ���󣩡�";
      break;

    case 1107:
      this.message = "������ʵ���Ѿ���������ֹ�����ܼ���/����";
      break;

    case 1108:
      this.message = "������ʵ���Ѿ�������������ֹ��";
      break;

    case 1110:
      this.message = "�޸Ĺ�����ʵ��ʱ�������µļ�¼����Ϊ1����";
      break;

    case 1111:
      this.message = "�޸Ĺ�����ʵ��ʱ�������ݿ��ȡ���󣩡�";
      break;

    case 1112:
      this.message = "��ʼ����ڵ㲻��ִ���˻ش���";
      break;

    case 1113:
      this.message = "�����ڵ�û�д������񣬲��ܻ��ա�";
      break;

    case 1115:
      this.message = "����������ʵ���е��¶���ʱ��������ļ�¼����Ϊ1����";
      break;

    case 1116:
      this.message = "����������ʵ���е��¶���ʱ�������ݿ��ȡ���󣩡�";
      break;

    case 1117:
      this.message = "��Ȩ���ƽ�����ʱ������ָ������ִ���ߡ�";
      break;

    case 1118:
      this.message = "��Ȩ���ƽ�����ʱ������ָ���Լ�Ϊ����ִ���ߡ�";
      break;

    case 1120:
      this.message = "�����µ�ǰ����ʱ��������ļ�¼����Ϊ1����";
      break;

    case 1121:
      this.message = "�����µ�ǰ����ʱ�������ݿ��ȡ���󣩡�";
      break;

    case 1122:
      this.message = "������������ڵ�����ʱ��������ڵ�û��Ԥ��ִ���߻�ָ������ִ����˳���������ȷָ������ִ���ߡ�";
      break;

    case 1123:
      this.message = "������ǰ����ڵ�����ʱ�����ڵ�û�к�����˳ǩִ���ߣ�����ȷָ������ִ���ߡ�";
      break;

    case 1125:
      this.message = "ɾ����ǰ����ʱ����ɾ���ļ�¼����Ϊ1����";
      break;

    case 1126:
      this.message = "ɾ����ǰ����ʱ�������ݿ��ȡ���󣩡�";
      break;

    case 1130:
      this.message = "�ж��Ƿ��е�ǰ�����¼ʱ����";
      break;

    case 1131:
      this.message = "�ж��Ƿ��е�ǰ�����¼ʱ����ȡ������¼ʱ����";
      break;

    case 1135:
      this.message = "��ȡ����������б�ʱ����";
      break;

    case 1136:
      this.message = "��ȡ����������б�ʱ����ȡ������¼ʱ����";
      break;

    case 1140:
      this.message = "��ȡ��������ʷ�б�ʱ����";
      break;

    case 1141:
      this.message = "��ȡ��������ʷ�б�ʱ����ȡ������¼ʱ����";
      break;

    case 1145:
      this.message = "��ȡ��ִ�������б�ʱ����";
      break;

    case 1146:
      this.message = "��ȡ��ִ�������б�ʱ����ȡ������¼ʱ����";
      break;

    case 1150:
      this.message = "����������������ʷʱ��������ļ�¼����Ϊ1����";
      break;

    case 1151:
      this.message = "����������������ʷʱ�������ݿ��ȡ���󣩡�";
      break;

    case 1155:
      this.message = "��ȡ�ڵ�δѡ����������ʱ����";
      break;

    case 1156:
      this.message = "��ȡ�ڵ�δѡ����������ʱ����ȡ������¼ʱ����";
      break;

    case 1160:
      this.message = "��ȡ��һ˳�������ʱ����";
      break;

    case 1161:
      this.message = "��ȡ��һ˳�������ʱ����ȡ������¼ʱ����";
      break;

    case 1162:
      this.message = "��ȡ��һ˳�������ʱ����";
      break;

    case 1163:
      this.message = "��ȡ��һ˳�������ʱ����ȡ������¼ʱ����";
      break;

    case 1165:
      this.message = "��ȡ��ǰ�����ߵ�ֱ���ϼ�ʱ����";
      break;

    case 1166:
      this.message = "��ȡ��ǰ�����ߵ�ֱ���ϼ�ʱ����ȡ������¼ʱ����";
      break;

    case 1170:
      this.message = "�޸ĵ�ǰ����ʱ�������µļ�¼����Ϊ1����";
      break;

    case 1171:
      this.message = "�޸ĵ�ǰ����ʱ�������ݿ��ȡ���󣩡�";
      break;

    case 1175:
      this.message = "��ȡ��һ�β���ʱ����";
      break;

    case 1176:
      this.message = "��ȡ��һ�β���ʱ����ȡ������¼ʱ����";
      break;

    case 1177:
      this.message = "û��ȡ���κζ�����¼��";
      break;

    case 1180:
      this.message = "ɾ������������ʱ����ɾ���ļ�¼����Ϊ1����";
      break;

    case 1181:
      this.message = "ɾ������������ʱ�������ݿ��ȡ���󣩡�";
      break;

    case 1185:
      this.message = "��ȡ��ǰ������Ϣʱ����";
      break;

    case 1186:
      this.message = "��ȡ��ǰ������Ϣʱ����ȡ������¼ʱ����";
      break;

    case 1190:
      this.message = "��ȡ��ע�б�ʱ����";
      break;

    case 1191:
      this.message = "��ȡ��ע�б�ʱ����ȡ������¼ʱ����";
      break;

    case 1195:
      this.message = "��ȡʵ����Ϣʱ����";
      break;

    case 1196:
      this.message = "��ȡʵ����Ϣʱ����ȡ������¼ʱ����";
      break;

    case 1200:
      this.message = "��ȡ���̵Ŀ�ʼ����ڵ�ʱ����";
      break;

    case 1201:
      this.message = "��ȡ���̵Ŀ�ʼ����ڵ�ʱ����ȡ������¼ʱ����";
      break;

    case 1205:
      this.message = "��ȡ���̵ĵ�ǰ����ڵ�ʱ����";
      break;

    case 1206:
      this.message = "��ȡ���̵ĵ�ǰ����ڵ�ʱ����ȡ������¼ʱ����";
      break;

    case 1207:
      this.message = "��ȡ���̵Ŀ�ʼ����ڵ�ʱ��������û�����ÿ�ʼ���������ڵ�������";
      break;

    case 1210:
      this.message = "��ȡTaskListenerʱ����";
      break;

    case 1214:
      this.message = "ִ������ڵ�ʱ������ʼ����ڵ��ִ���߷�ʽֻ���Ƕ�ǩ����";
      break;

    case 1215:
      this.message = "ִ������ڵ�ʱ����ִ���߷�ʽ��־����������";
      break;

    case 1216:
      this.message = "ִ������ڵ�ʱ����������ٷֱȱ�־����������";
      break;

    case 1217:
      this.message = "ִ������ڵ�ʱ����ִ���߹�ϵ��־����������";
      break;

    case 1218:
      this.message = "ִ������ڵ�ʱ�����ڵ��������ͱ�־����������";
      break;

    case 1220:
      this.message = "��ȡ�����ڵ������ʱ�����ڵ����ͱ�־����������";
      break;

    case 1221:
      this.message = "��ȡǰ�ýڵ������ʱ�����ڵ����ͱ�־����������";
      break;

    case 1225:
      this.message = "û���ҵ��κη������������̷���";
      break;

    case 1230:
      this.message = "���ʽ��������";
      break;

    case 1235:
      this.message = "��ǰ�����ǲ�ǩ���ڣ����Բ�����������Ϊ0"
        + "��ȡwf_tab_executor_order������ݣ������ܵ�ԭ�������ݿ��������������ݡ� ";
      break;

    case 1300:
      this.message = "�����ƽ���ʷ��¼ʱ��������ļ�¼����Ϊ1����";
      break;

    case 1301:
      this.message = "�����ƽ���ʷ��¼ʱ�������ݿ��ȡ���󣩡�";
      break;

    case 1305:
      this.message = "�����ƽ����Ƽ�¼ʱ��������ļ�¼����Ϊ1����";
      break;

    case 1306:
      this.message = "�����ƽ����Ƽ�¼ʱ�������ݿ��ȡ���󣩡�";
      break;

    case 1310:
      this.message = "��������ֵ��¼ʱ��������ļ�¼����Ϊ1����";
      break;

    case 1311:
      this.message = "��������ֵ��¼ʱ�������ݿ��ȡ���󣩡�";
      break;

    case 1312:
      this.message = "����ֵ��ֵ���󣬱���ֵ = ";
      break;

    case 1313:
      this.message = "����ֵ�ı�����variableId)û�ж��塣����ֵ = ";
      break;

    case 1315:
      this.message = "�޸ı���ֵ��¼ʱ�������µļ�¼����Ϊ1����";
      break;

    case 1316:
      this.message = "�޸ı���ֵ��¼ʱ�������ݿ��ȡ���󣩡�";
      break;

    case 1320:
      this.message = "��ȡ����ֵ�б�ʱ����";
      break;

    case 1321:
      this.message = "��ȡ����ֵ�б�ʱ����ȡ������¼ʱ����";
      break;

    case 1325:
      this.message = "�޸��ƽ����Ƽ�¼ʱ�������µļ�¼����Ϊ1����";
      break;

    case 1326:
      this.message = "�޸��ƽ����Ƽ�¼ʱ�������ݿ��ȡ���󣩡�";
      break;

    case 1330:
      this.message = "��ȡ�ƽ��б�ʱ����";
      break;

    case 1331:
      this.message = "��ȡ�ƽ��б�ʱ����ȡ������¼ʱ����";
      break;

    case 1335:
      this.message = "�ж��Ƿ��е�ǰ������ƽ���¼ʱ����";
      break;

    case 1336:
      this.message = "�ж��Ƿ��е�ǰ������ƽ���¼ʱ����ȡ������¼ʱ����";
      break;

    case 1340:
      this.message = "ɾ���ƽ����Ƽ�¼ʱ����ɾ���ļ�¼����Ϊ1����";
      break;

    case 1341:
      this.message = "ɾ���ƽ����Ƽ�¼ʱ�������ݿ��ȡ���󣩡�";
      break;

    case 1345:
      this.message = "����ͨ�������¼ʱ��������ļ�¼����Ϊ1����";
      break;

    case 1346:
      this.message = "����ͨ�������¼ʱ�������ݿ��ȡ���󣩡�";
      break;

    case 1351:
      this.message = "ɾ��ͨ�������¼ʱ�������ݿ��ȡ���󣩡�";
      break;

    case 1355:
      this.message = "��ȡ�ڵ���ͨ������ʱ����";
      break;

    case 1356:
      this.message = "��ȡ�ڵ���ͨ������ʱ����ȡ������¼ʱ����";
      break;

    case 1360:
      this.message = "�������������˳��ʱ��������ļ�¼����Ϊ1����";
      break;

    case 1361:
      this.message = "�������������˳��ʱ�������ݿ��ȡ���󣩡�";
      break;

    case 1365:
      this.message = "��ȡ������˳���б�ʱ����";
      break;

    case 1366:
      this.message = "��ȡ������˳��ʱ����ȡ������¼ʱ����";
      break;

    case 1370:
      this.message = "��ȡ�ɶ�ĳ��ǩ�ڵ���в���������ʱ����";
      break;

    case 1371:
      this.message = "��ȡ�ɶ�ĳ��ǩ�ڵ���в���������ʱ����ȡ������¼ʱ����";
      break;

    case 1375:
      this.message = "��ȡ���ִ��˳���ʱ����";
      break;

    case 1376:
      this.message = "��ȡ���ִ��˳���ʱ����ȡ������¼ʱ����";
      break;

    case 1380:
      this.message = "�����ڵ������˳��ʱ��������ļ�¼����Ϊ1����";
      break;

    case 1381:
      this.message = "�����ڵ������˳��ʱ�������ݿ��ȡ���󣩡�";
      break;

    case 1385:
      this.message = "ɾ���ڵ������˳��ʱ����ɾ���ļ�¼����Ϊ1����";
      break;

    case 1386:
      this.message = "ɾ���ڵ������˳��ʱ�������ݿ��ȡ���󣩡�";
      break;

    case 1390:
      this.message = "ɾ�����������˳��ʱ����ɾ���ļ�¼����Ϊ1����";
      break;

    case 1391:
      this.message = "ɾ�����������˳��ʱ�������ݿ��ȡ���󣩡�";
      break;

    case 1395:
      this.message = "��ȡ���������˳���б�ʱ����";
      break;

    case 1396:
      this.message = "��ȡ���������˳���б�ʱ����ȡ������¼ʱ����";
      break;

    case 1400:
      this.message = "ɾ�����������˳��ʱ����ɾ���ļ�¼����Ϊ1����";
      break;

    case 1401:
      this.message = "ɾ�����������˳��ʱ�������ݿ��ȡ���󣩡�";
      break;

    case 1405:
      this.message = "�޸����������˳��ʱ�������µļ�¼����Ϊ1����";
      break;

    case 1406:
      this.message = "�޸����������˳��ʱ�������ݿ��ȡ���󣩡�";
      break;

    case 1410:
      this.message = "���½ڵ������˳��ʱ����ɾ���ļ�¼����Ϊ1����";
      break;

    case 1411:
      this.message = "���½ڵ������˳��ʱ�������ݿ��ȡ���󣩡�";
      break;

    case 1415:
      this.message = "��ȡ������ͬ˳�������ʱ����";
      break;

    case 1416:
      this.message = "��ȡ������ͬ˳�������ʱ����ȡ������¼ʱ����";
      break;

    case 2000:
      this.message = "�������ݿ����";
      break;

    case 2001:
      this.message = "��ȡ���̽ڵ��б����";
      break;

    case 2002:
      this.message = "���ӽڵ����";
      break;

    case 2003:
      this.message = "ɾ���ڵ����";
      break;

    case 2004:
      this.message = "�޸Ľڵ����";
      break;

    case 2005:
      this.message = "���ӽڵ�ִ���ߴ���";
      break;

    case 2006:
      this.message = "��ȡԱ�����б����";
      break;

    case 2007:
      this.message = "��ȡ�ڵ�ִ�����б����";
      break;

    case 2008:
      this.message = "��ȡԱ����Ա���б����";
      break;

    case 2009:
      this.message = "��ȡ�ڵ���������б����";
      break;

    case 2010:
      this.message = "ɾ���ڵ��������";
      break;

    case 2011:
      this.message = "��ȡ�ڵ�ǰ�������б����";
      break;

    case 2012:
      this.message = "���ӽڵ��������";
      break;

    case 2013:
      this.message = "���½ڵ��������";
      break;

    case 2014:
      this.message = "���������ϵ����";
      break;

    case 2015:
      this.message = "���ýڵ�ִ�з�ʽ����";
      break;

    case 2016:
      this.message = "���������������";
      break;

    case 2017:
      this.message = "�������˳�����";
      break;

    case 2018:
      this.message = "��ȡ�ڵ�ִ��˳�����";
      break;

    case 2019:
      this.message = "��ȡ���̵������б����";
      break;

    case 2020:
      this.message = "���̵����̱������ظ�";
      break;

    case 2021:
      this.message = "�������̱�������";
      break;

    case 2022:
      this.message = "ɾ�����̱�������";
      break;

    case 2023:
      this.message = "�������̱�������";
      break;

    case 2024:
      this.message = "������ת���ʽ����";
      break;

    case 2025:
      this.message = "�������ͱ�����������Ϊ�ַ�����";
      break;

    case 2030:
      this.message = "ͬһ����״̬���ظ�";
      break;

    case 2050:
      this.message = "��Ӵ�����ʷ��¼����";
      break;

    case 2051:
      this.message = "��Ӵ������";
      break;

    case 2052:
      this.message = "���´������";
      break;

    case 2053:
      this.message = "ɾ���������";
      break;

    case 2054:
      this.message = "���ݽ����߻�ȡ��Ч�����б����";
      break;

    case 2055:
      this.message = "����Ա����ȡ��ִ�нڵ��б����";
      break;

    case 2056:
      this.message = "���ܴ�����Լ�";
      break;

    case 2057:
      this.message = "����ʱ�����";
      break;

    case 2058:
      this.message = "��ȡǰ�����¼�б����";
      break;

    case 2059:
      this.message = "�����ɴ���ʱ�䷶Χ";
      break;

    case 2060:
      this.message = "��ȡԱ���Ѵ����¼����";
      break;

    case 2061:
      this.message = "��ȡ������¼�б����";
      break;

    case 2062:
      this.message = "���ܴ����ӵ����";
      break;

    case 2063:
      this.message = "���������߽ڵ��ȡ�����б����";
      break;

    case 2100:
      this.message = "������֯����";
      break;

    case 2101:
      this.message = "������֯����";
      break;

    case 2102:
      this.message = "����ְλ����";
      break;

    case 2103:
      this.message = "����ְλ����";
      break;

    case 2104:
      this.message = "����Ա������";
      break;

    case 2105:
      this.message = "����Ա������";
      break;

    case 2106:
      this.message = "����Ա�������";
      break;

    case 2107:
      this.message = "����Ա�������";
      break;

    case 2200:
      this.message = "��½ʧ�ܣ��û�������!";
      break;

    case 2201:
      this.message = "��½ʧ�ܣ��û�״̬��Ч!";
      break;

    case 2202:
      this.message = "��½ʧ�ܣ��������!";
      break;
    case 2203:
      this.message = "";
      break;
    case 2204:
      this.message = "�޷���ȡ����������!��ʹ�����̼�ؼ�������Ƿ�������ת!";
      break;
    case 2205:
      this.message = "�޷���ȡ���ܽڵ㣬���������Ƿ��Ѿ������˻��ܽڵ㣡";
      break;
    case 2206:
      this.message = "���ڲ�����Ч";
      break;
    case 2207:
      this.message = "�ַ������͵�������ʽֻ֧�֣�������";
      break;
    case 2208:
        this.message = "ִ��listener�Ļص�����!";
        break;
    case 2209:
        this.message = "��ȡ��������������!";
    	break;
    default:
      this.message = "δ֪����";
      break;
    }
  }
}
