DROP TABLE IF EXISTS BC_WORK_SOURCE;
DROP TABLE IF EXISTS BC_WORK_DONE;
DROP TABLE IF EXISTS BC_WORK_TODO;
DROP TABLE IF EXISTS BC_WORK;

-- 事项来源
CREATE TABLE BC_WORK_SOURCE(
	ID INT NOT NULL,
	TYPE_ VARCHAR(255) NOT NULL,
	URL_ VARCHAR(1000) NOT NULL,
	CONSTRAINT BCPK_WORK_SOURCE PRIMARY KEY (ID)
);
COMMENT ON TABLE BC_WORK_SOURCE IS '事项来源';
COMMENT ON COLUMN BC_WORK_SOURCE.ID IS 'ID';
COMMENT ON COLUMN BC_WORK_SOURCE.TYPE_ IS '来源类型';
COMMENT ON COLUMN BC_WORK_SOURCE.URL_ IS '访问地址';

-- 工作事项
CREATE TABLE BC_WORK(
	ID INT NOT NULL,
	CATEGORY VARCHAR(500) NULL,
	CODE VARCHAR(255),
	SUBJECT VARCHAR(500) NOT NULL,
	DETAIL VARCHAR(1000) NOT NULL,
	SOURCE_TYPE VARCHAR(255) NOT NULL,
	SOURCE_ID INT NOT NULL,
	FILE_DATE TIMESTAMP,
	AUTHOR_ID INT,
	CONSTRAINT BCPK_WORK PRIMARY KEY (ID)
);
COMMENT ON TABLE BC_WORK IS '工作事项';
COMMENT ON COLUMN BC_WORK.ID IS 'ID';
COMMENT ON COLUMN BC_WORK.CATEGORY IS '分类 : 可以多级分类，使用"/"连接，如"通知/内部通告"';
COMMENT ON COLUMN BC_WORK.CODE IS '单号';
COMMENT ON COLUMN BC_WORK.SUBJECT IS '标题';
COMMENT ON COLUMN BC_WORK.DETAIL IS '详细说明';
COMMENT ON COLUMN BC_WORK.SOURCE_TYPE IS '来源类型';
COMMENT ON COLUMN BC_WORK.SOURCE_ID IS '来源标识';
COMMENT ON COLUMN BC_WORK.FILE_DATE IS '创建时间';
COMMENT ON COLUMN BC_WORK.AUTHOR_ID IS '创建人ID';
ALTER TABLE BC_WORK ADD CONSTRAINT BCFK_WORK_AUTHOR FOREIGN KEY (AUTHOR_ID)
	REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);

-- 经办事项
CREATE TABLE BC_WORK_DONE(
	ID INT NOT NULL,
	TYPE_ INT NOT NULL,
	WID INT NOT NULL,
	PRIORITY INT NOT NULL,
	SEND_DATE TIMESTAMP NOT NULL,
	SENDER_ID INT NOT NULL,
	WORKER_ID INT NOT NULL,
	SUBJECT VARCHAR(500) NOT NULL,
	DETAIL VARCHAR(1000),
	DEADLINE TIMESTAMP,
	CONFIRM_DATE TIMESTAMP,
	TEAM_ID INT,
	DONE_DATE TIMESTAMP NOT NULL,
	EXECUTOR_ID INT,
	CONSTRAINT BCPK_WORK_DONE PRIMARY KEY (ID)
);
COMMENT ON TABLE BC_WORK_DONE IS '经办事项';
COMMENT ON COLUMN BC_WORK_DONE.ID IS 'ID';
COMMENT ON COLUMN BC_WORK_DONE.TYPE_ IS '经办类型 : 0-指派任务,1-个人经办,2-岗位经办,3-部门经办,4-单位经办';
COMMENT ON COLUMN BC_WORK_DONE.WID IS '工作事项ID';
COMMENT ON COLUMN BC_WORK_DONE.PRIORITY IS '优先级';
COMMENT ON COLUMN BC_WORK_DONE.SEND_DATE IS '发送时间';
COMMENT ON COLUMN BC_WORK_DONE.SENDER_ID IS '发送人ID';
COMMENT ON COLUMN BC_WORK_DONE.WORKER_ID IS '经办人ID';
COMMENT ON COLUMN BC_WORK_DONE.SUBJECT IS '标题';
COMMENT ON COLUMN BC_WORK_DONE.DETAIL IS '详细说明';
COMMENT ON COLUMN BC_WORK_DONE.DEADLINE IS '办理期限';
COMMENT ON COLUMN BC_WORK_DONE.CONFIRM_DATE IS '确认时间';
COMMENT ON COLUMN BC_WORK_DONE.TEAM_ID IS '源组织ID : 仅用于指派待办人的经办类型，记录源组织类待办的待办人ID';
COMMENT ON COLUMN BC_WORK_DONE.DONE_DATE IS '完成时间';
COMMENT ON COLUMN BC_WORK_DONE.EXECUTOR_ID IS '执行人ID : 仅用于指派待办人的经办类型，记录被指派到的待办人ID';
ALTER TABLE BC_WORK_DONE ADD CONSTRAINT BCFK_WORK_DONE_WID FOREIGN KEY (WID)
	REFERENCES BC_WORK (ID);
ALTER TABLE BC_WORK_DONE ADD CONSTRAINT BCFK_WORK_DONE_SENDER FOREIGN KEY (SENDER_ID)
	REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
ALTER TABLE BC_WORK_DONE ADD CONSTRAINT BCFK_WORK_DONE_WORKER FOREIGN KEY (WORKER_ID)
	REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
ALTER TABLE BC_WORK_DONE ADD CONSTRAINT BCFK_WORK_DONE_TEAM FOREIGN KEY (TEAM_ID)
	REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
ALTER TABLE BC_WORK_DONE ADD CONSTRAINT BCFK_WORK_DONE_EXECUTOR FOREIGN KEY (EXECUTOR_ID)
	REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);

-- 待办事项
CREATE TABLE BC_WORK_TODO(
	ID INT NOT NULL,
	TYPE_ INT NOT NULL,
	WID INT NOT NULL,
	PRIORITY INT NOT NULL,
	SEND_DATE TIMESTAMP NOT NULL,
	SENDER_ID INT NOT NULL,
	WORKER_ID INT NOT NULL,
	SUBJECT VARCHAR(500) NOT NULL,
	DETAIL VARCHAR(1000),
	DEADLINE TIMESTAMP,
	CONFIRM_DATE TIMESTAMP,
	TEAM_ID INT,
	CONSTRAINT BCPK_WORK_TODO PRIMARY KEY (ID)
);
COMMENT ON TABLE BC_WORK_TODO IS '待办事项';
COMMENT ON COLUMN BC_WORK_TODO.ID IS 'ID';
COMMENT ON COLUMN BC_WORK_TODO.TYPE_ IS '待办类型 : 1-个人待办,2-岗位待办,3-部门待办,4-单位待办';
COMMENT ON COLUMN BC_WORK_TODO.WID IS '工作事项ID';
COMMENT ON COLUMN BC_WORK_TODO.PRIORITY IS '优先级';
COMMENT ON COLUMN BC_WORK_TODO.SEND_DATE IS '发送时间';
COMMENT ON COLUMN BC_WORK_TODO.SENDER_ID IS '发送人ID';
COMMENT ON COLUMN BC_WORK_TODO.WORKER_ID IS '待办人ID';
COMMENT ON COLUMN BC_WORK_TODO.SUBJECT IS '标题';
COMMENT ON COLUMN BC_WORK_TODO.DETAIL IS '详细说明';
COMMENT ON COLUMN BC_WORK_TODO.DEADLINE IS '办理期限';
COMMENT ON COLUMN BC_WORK_TODO.CONFIRM_DATE IS '确认时间';
COMMENT ON COLUMN BC_WORK_TODO.TEAM_ID IS '源组织ID : 指用户所认领的组织类待办的待办人ID，或者被指派的组织类待办的ID';
ALTER TABLE BC_WORK_TODO ADD CONSTRAINT BCFK_WORK_TODO_WID FOREIGN KEY (WID)
	REFERENCES BC_WORK (ID);
ALTER TABLE BC_WORK_TODO ADD CONSTRAINT BCFK_WORK_TODO_SENDER FOREIGN KEY (SENDER_ID)
	REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
ALTER TABLE BC_WORK_TODO ADD CONSTRAINT BCFK_WORK_TODO_WORKER FOREIGN KEY (WORKER_ID)
	REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
ALTER TABLE BC_WORK_TODO ADD CONSTRAINT BCFK_WORK_TODO_TEAM FOREIGN KEY (TEAM_ID)
	REFERENCES BC_IDENTITY_ACTOR_HISTORY (ID);
