/**
 *
 */
package cn.bc.core.query.condition.impl;

import cn.bc.core.query.QueryOperator;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.util.StringUtils;

import javax.json.JsonObject;

/**
 * 条件配置接口的默认实现
 *
 * @author dragon
 */
public class AdvanceCondition implements cn.bc.core.query.condition.AdvanceCondition {
	private String id;
	private QueryOperator operator;
	private Object value;
	private String mapper;


	public AdvanceCondition(String id, QueryOperator operator, Object value, String mapper) {
		this.id = id;
		this.operator = operator;
		this.value = value;
		this.mapper = mapper;
	}

	public AdvanceCondition(String id, QueryOperator operator, Object value) {
		this(id, operator, value, null);
	}

	public AdvanceCondition(JsonObject json) {
		// 验证json结构的有效性
		if (json == null || !(json.containsKey("id") && json.containsKey("operator") && json.containsKey("value")))
			throw new IllegalArgumentException("missing 'id', 'value' or 'operator' mapping, json=" + json);

		this.id = json.getString("id");
		this.operator = QueryOperator.symbolOf(json.getString("operator"));

		// value 处理
		String value = json.getString("value");
		String type = json.containsKey("type") ? json.getString("type") : null;
		this.value = StringUtils.convertValueByType(type, value);

		if (json.containsKey("mapper")) this.mapper = json.getString("mapper");
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getMapper() {
		return mapper;
	}

	@Override
	public QueryOperator getOperator() {
		return operator;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public Condition toCondition(String mapper) {
		// TODO
		return null;
	}
}
