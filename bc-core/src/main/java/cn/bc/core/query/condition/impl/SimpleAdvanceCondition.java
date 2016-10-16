/**
 *
 */
package cn.bc.core.query.condition.impl;

import cn.bc.core.query.QueryOperator;
import cn.bc.core.query.condition.AdvanceCondition;
import cn.bc.core.util.StringUtils;

import javax.json.JsonObject;

/**
 * 高级条件配置接口的默认实现
 *
 * @author dragon
 */
public class SimpleAdvanceCondition implements AdvanceCondition {
	private String id;
	private QueryOperator operator;
	private Object value;


	public SimpleAdvanceCondition(String id, QueryOperator operator, Object value) {
		this.id = id;
		this.operator = operator;
		this.value = value;
	}

	public SimpleAdvanceCondition(JsonObject json) {
		// 验证json结构的有效性
		if (json == null || !(json.containsKey("id") && json.containsKey("operator") && json.containsKey("value")))
			throw new IllegalArgumentException("missing 'id', 'value' or 'operator' mapping, json=" + json);

		this.id = json.getString("id");
		this.operator = QueryOperator.symbolOf(json.getString("operator"));

		// value 处理
		String value = json.getString("value");
		String type = json.containsKey("type") ? json.getString("type") : null;
		this.value = StringUtils.convertValueByType(type, value);
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public QueryOperator getOperator() {
		return operator;
	}

	@Override
	public Object getValue() {
		return value;
	}
}
