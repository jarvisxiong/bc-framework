package cn.bc.template.dao.hibernate.jpa;

import cn.bc.BCConstants;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.NotEqualsCondition;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;
import cn.bc.template.dao.TemplateDao;
import cn.bc.template.domain.Template;

/**
 * DAO接口的实现
 * 
 * @author lbj
 * 
 */
public class TemplateDaoImpl extends HibernateCrudJpaDao<Template> implements
		TemplateDao {

	public Template loadByCode(String code) {
		if (code == null)
			return null;
		int i = code.indexOf(":");
		String version = null;
		if (i != -1) {
			version = code.substring(i + 1);
			code = code.substring(0, i);
		}
		AndCondition c = new AndCondition();
		c.add(new EqualsCondition("code", code));
		if (version != null) {
			c.add(new EqualsCondition("version", version));// 获取指定版本
		} else {
			c.add(new EqualsCondition("status", BCConstants.STATUS_ENABLED));// 获取最新版本
		}
		return this.createQuery().condition(c).singleResult();
	}

	public boolean isUniqueCodeAndVersion(Long currentId, String code,
			String version) {
		Condition c;
		if (currentId == null) {
			c = new AndCondition().add(new EqualsCondition("code", code)).add(
					new EqualsCondition("version", version));

		} else {
			c = new AndCondition().add(new EqualsCondition("code", code))
					.add(new NotEqualsCondition("id", currentId))
					.add(new EqualsCondition("version", version));
		}
		return this.createQuery().condition(c).count() > 0;
	}
}
