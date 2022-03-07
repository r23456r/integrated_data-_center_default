CREATE TABLE IF NOT EXISTS idc_node_info_xhcountrydata(
   id VARCHAR(64) NOT NULL COMMENT 'ID',
   fatherId VARCHAR(64) COMMENT '父ID',
   nodeName VARCHAR(256)  COMMENT '节点名称',
   iDCAttributei TEXT  COMMENT '参数信息',
   iDCType INT  COMMENT '节点类型',
   createDate DATE  COMMENT '创建时间',
   updateDate DATE  COMMENT '更新时间',
   PRIMARY KEY ( `id` )
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='新华网节点数据';

CREATE TABLE IF NOT EXISTS idc_node_data_xhcountrydata(
   id INT PRIMARY KEY AUTO_INCREMENT  COMMENT 'ID',
   nodeId VARCHAR(64) COMMENT '节点ID',
   paramKey VARCHAR(256)  COMMENT 'key',
   paramValue TEXT  COMMENT '数据',
   createDate DATE  COMMENT '创建时间',
   updateDate DATE  COMMENT '更新时间'
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='新华网详情数据';

CREATE TABLE IF NOT EXISTS data_source_info(
   id INT PRIMARY KEY AUTO_INCREMENT  COMMENT 'ID',
   dataSource VARCHAR(64) COMMENT '数据源',
   dataName  VARCHAR(256)  COMMENT '数据名称',
   nodeInfoTblName VARCHAR(256)  COMMENT '节点表名称',
   nodeDataTblName VARCHAR(256)  COMMENT '数据表名称',
   iconPath VARCHAR(512)  COMMENT '数据展示图片',
   datacomment TEXT  COMMENT '数据描述',
   createDate DATE  COMMENT '创建时间',
   updateDate DATE  COMMENT '更新时间'
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='数据源表';
