/**
 * 
 */
package cn.bc.report.web.struts2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.core.query.Query;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.docs.domain.Attach;
import cn.bc.orm.hibernate.jpa.HibernateJpaNativeQuery;
import cn.bc.report.domain.ReportTemplate;
import cn.bc.report.service.ReportTemplateService;
import cn.bc.template.domain.Template;
import cn.bc.template.service.TemplateService;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.GridExporter;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;

/**
 * 报表执行Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class RunReportAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	private static Log logger = LogFactory.getLog(RunReportAction.class);
	public String code;// 报表模板的编码
	private ReportTemplateService reportTemplateService;// 报表模板服务
	private TemplateService templateService;
	private ReportTemplate tpl;// 报表模板
	private JSONObject config;// 报表模板的详细配置

	@Autowired
	public void setReportTemplateService(
			ReportTemplateService reportTemplateService) {
		this.reportTemplateService = reportTemplateService;
	}

	@Autowired
	public void setTemplateService(TemplateService templateService) {
		this.templateService = templateService;
	}

	private JSONObject getConfig() {
		if (config != null)
			return config;

		this.initConfig();
		return config;
	}

	private void initConfig() {
		// 初始化配置信息
		// TODO 加载模板配置
		// this.tpl = this.reportTemplateService.loadByCode(this.code);
		this.tpl = new ReportTemplate();
		this.tpl.setName("报表测试标题");

		// TODO 获取详细配置信息
		this.tpl.setConfig("{type: 'sql',"
				+ "columns: ["
				+ "    {type:'id',id: 'a.id', width: 40, el:'name'},"
				+ "    {id: 'a.name', label: '名称', width: 100, el:'name'},"
				+ "    {id: 'a.pname', label: '上级', el:'pname'}"
				+ "],"
				+ "sql: 'select a.id as id,a.name as name,a.pname as pname from bc_identity_actor a order by a.code',"
				+ "conditionForm: 'tpl:testConditionForm',"
				+ "exportTpl: 'tpl:testExportTemplate',"
				+ "ui: 'data',width: 600,height: 400}");
		this.config = tpl.getConfigJson();

		// 避免空指针引用
		if (this.config == null)
			this.config = new JSONObject();
	}

	@Override
	protected Toolbar getHtmlPageToolbar(boolean useDisabledReplaceDelete) {
		Toolbar tb = new Toolbar();

		// TODO 添加额外的工具条按钮
		tb.addButton(Toolbar.getDefaultEmptyToolbarButton());

		// 搜索按钮
		tb.addButton(getDefaultSearchToolbarButton());
		return tb;
	}

	@Override
	protected String getHtmlPageNamespace() {
		return this.getContextPath() + BCConstants.NAMESPACE;
	}

	@Override
	protected String getViewActionName() {
		return "runReport";
	}

	@Override
	protected String getHtmlPageTitle() {
		return this.tpl.getName();
	}

	@Override
	protected PageOption getHtmlPageOption() {
		PageOption po = super.getHtmlPageOption();
		try {
			if (this.getConfig().has("width")) {
				po.setWidth(this.config.getInt("width"));
			}
			if (this.getConfig().has("height")) {
				po.setHeight(this.config.getInt("height"));
			}
		} catch (JSONException e) {
			logger.error(e.getMessage());
		}

		return po;
	}

	@Override
	protected Query<Map<String, Object>> getQuery() {
		return new HibernateJpaNativeQuery<Map<String, Object>>(jpaTemplate,
				getSqlObject());
	}

	private List<Column> columns;

	@Override
	protected List<Column> getGridColumns() {
		if (columns != null)
			return columns;

		columns = new ArrayList<Column>();

		// 初始化列的配置信息
		JSONArray jColumns = null;
		try {
			jColumns = this.getConfig().getJSONArray("columns");
		} catch (JSONException e) {
			logger.error(e.getMessage());
		}
		JSONObject jcolumn = null;
		for (int i = 0; i < jColumns.length(); i++) {
			try {
				jcolumn = jColumns.getJSONObject(i);
				if (jcolumn.has("type")
						&& "id".equals(jcolumn.getString("type"))) {// IdColumn4MapKey列
					columns.add(new IdColumn4MapKey(jcolumn.getString("id"),
							jcolumn.getString("el")));
				} else {// 默认使用TextColumn4MapKey列
					columns.add(new TextColumn4MapKey(jcolumn.getString("id"),
							jcolumn.getString("el"),
							jcolumn.getString("label"),
							jcolumn.has("width") ? jcolumn.getInt("width") : 0));
				}
			} catch (JSONException e) {
				logger.error(e.getMessage());
				jcolumn = null;
			}
		}

		// columns.add(new IdColumn4MapKey("a.id", "id"));
		// columns.add(new TextColumn4MapKey("a.name", "name", "姓名", 100));
		// columns.add(new TextColumn4MapKey("a.pname", "pname", "上级"));
		return columns;
	}

	@Override
	public String list() throws Exception {
		this.initConfig();
		return super.list();
	}

	@Override
	public String export() throws Exception {
		this.initConfig();
		return super.export();
	}

	@Override
	protected GridExporter buileGridExporter(String title, String idLabel) {
		GridExporter exporter = super.buileGridExporter(title, idLabel);

		// 设置导出模板
		if (this.getConfig().has("exportTpl")) {
			try {
				String exportTpl = this.getConfig().getString("exportTpl");
				if (exportTpl.startsWith("tpl:")) {
					Template t = this.templateService.loadByCode(exportTpl
							.substring(4));
					if (t != null && t.getType() == Template.TYPE_EXCEL) {
						exporter.setTemplateFile(t.getInputStream());
					} else {
						logger.error("指定的模板不存在或不是Excel模板:exportTpl="
								+ exportTpl);
					}
				} else {
					File file = new File(Attach.DATA_REAL_PATH + "/"
							+ exportTpl);
					try {
						exporter.setTemplateFile(new FileInputStream(file));
					} catch (FileNotFoundException e) {
						logger.error("指定的模板文件不存在:file="
								+ file.getAbsolutePath());
					}
				}
			} catch (JSONException e) {
				logger.error(e.getMessage());
			}
		}

		return exporter;
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		try {
			sqlObject.setSql(this.getConfig().getString("sql"));
		} catch (JSONException e) {
			logger.error(e.getMessage());
		}

		// 注入参数
		sqlObject.setArgs(null);

		// 获取所有列的值表达式
		final List<String> mapKeys = new ArrayList<String>();
		for (Column column : this.getGridColumns()) {
			if (column instanceof TextColumn4MapKey)
				mapKeys.add(((TextColumn4MapKey) column)
						.getOriginValueExpression());
			else if (column instanceof IdColumn4MapKey)
				mapKeys.add(((IdColumn4MapKey) column)
						.getOriginValueExpression());
			else
				mapKeys.add(column.getValueExpression());
		}

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				for (String key : mapKeys) {
					map.put(key, rs[i]);
					i++;
				}
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected String getDefaultExportFileName() {
		return this.tpl.getName();
	}

	@Override
	protected String[] getGridSearchFields() {
		// 不处理
		return null;
	}

	@Override
	protected String getGridRowLabelExpression() {
		// 不处理
		return null;
	}

	@Override
	protected String getFormActionName() {
		// 没有表单处理
		return null;
	}

	@Override
	protected String getGridDblRowMethod() {
		// 取消双击处理函数
		return null;
	}
}