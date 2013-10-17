package com.sanbo.common;

public class Constants {
	public static final String FORMATE_STYLE_DATA_SHORT = "yyyy-MM-dd";

	public static final String FORMATE_STYLE_DATA_LONG = "yyyy-MM-dd HH:mm:ss";

	// ======模板,静态文件根目录 ==begin=====
	public static final String TEMPLATE_PATH = "template";

	public static final String STYLE_DEFAULT = "default";

	public static final String STYLE_CLASSIC = "classic";

	public static final String BRANDING_STATIC_PATH = "branding";

	public static final String STATIC_PATH = "static";
	// =====模板,静态文件根目录 ==end=========

	public static final String SESSION_KEY_USER_INFO = "session.key.userInfo";

	public static final String SESSION_KEY_USER_INFO_SUFFIX = "session.key.userInfo.suffix";

	public static final String SESSION_KEY_USER_CURRENT_PROJECT = "session.key.user.current.project";// 当前项目的KEY

	public static final String SESSION_KEY_USER_CURRENT_MODULE = "session.key.user.current.module";// 当前模块的KEY

	public static final String SESSION_KEY_TOP_MENU_ID = "session.key.topMenuId";

	public static final String SESSION_KEY_MENU_NAME = "session.key.menuname";

	public static final String SESSION_KEY_CURRENT_PROJECT = "session.key.current.project";

	public static final String UPLOAD_FILE_SAVE_PATH = "file.store.path"; // 上传付件的配置信息key

	public static final int TREE_ROOT_ID = 1; // 根节点ID,约定为1

	public static final String WEB_CONFIG = "WEB_CONFIG_HOME"; // 环境变量名称

	public static final String[] PROPERTIES_FILES = { "elearning-admin.properties" };// 配置文件

	public static final String IDS_SEPARATOR_COMMA = ",";

	/**
	 * default page size
	 */
	public static final Integer DEFAULT_PAGE_SIZE = 8;

	public static final String JSON_DATA_KEY_MSG = "msg";

	/**
	 * project id request parameter name & database column name
	 * 
	 */
	public static final String PROJECT_ID_PARA_NAME = "project";
	public static final String PROJECT_ID_COLUMN_NAME = "project_id";
	/**
	 * 默认编码长度
	 */
	public static final Integer CODE_DEFAULT_LENGTH = 2;
	/**
	 * 父节点最大长度
	 */
	public static final Integer PARENT_CODE_LIMIT_LENGTH = 100;

	/**
	 * 会议集成key
	 */
	public static final String ELEARNING_CONFERENCE_KEY = "elearning.conference.key"; // token加密key
	public static final String ELEARNING_CONFERENCE_ACCOUNT = "elearning.conference.account";
	public static final String ELEARNING_CONFERENCE_TICKET = "elearning.conference.ticket";
	public static final String ELEARNING_CONFERENCE_SITENAME = "elearning.conference.siteName";
	public static final String ELEARNING_CONFERENCE_SITEURL = "elearning.conference.siteUrl";
	public static final String ELEARNING_CONFERENCE_SITEADMIN = "elearning.conference.siteAdmin";
	public static final String ELEARNING_CONFERENCE_PASSWORD = "elearning.conference.password";

	/**
	 * war url key
	 */
	public static final String ELEARNING_ADMIN_URL = "elearning.admin.url";
	public static final String ELEARNING_PORTAL_URL = "elearning.portal.url";
	public static final String ELEARNING_PROJECT_URL = "elearning.project.url";
	public static final String ELEARNING_CMS_URL = "elearning.cms.url";
	public static final String ELEARNING_EXAM_URL = "elearning.exam.url";

	/**
	 * ## the upload file's storage folder ## sa/portal/project的附件存储物理路径目录
	 */
	public static final String FILE_STORE_PATH = "file.store.path";
	/**
	 * ##CMS生成的文件存储物理路径
	 */
	public static final String FILE_STORE_CMS_PATH = "file.store.cms.path";
	/**
	 * ##sa/portal/project访问附件的相对路径
	 */
	public static final String ATTACHFILE_WEB_RELA_PATH = "attachfile.web.rela.path";
	/**
	 * ##CMS访问附件的相对路径
	 */
	public static final String ATTACHFILE_CMS_RELA_PATH = "attachfile.cms.rela.path";
	/**
	 * ##访问sa/portal/project附件的http路径
	 */
	public static final String ATTACHFILE_HTTPROOT_PATH = "attachfile.httproot.path";

	/**
	 * 用户默认头像
	 */
	public static final String USER_PHOTO_DEFAULT_PATH = "photo.jpg";

	/**
	 * 项目主页面
	 */
	public static final String PROJECT_ADMIN = "projectAdmin";
	public static final String PROJECT_INDEX = "projectIndex";
	public static final String PROJECT_MY_CLASS = "myClass";

	/**
	 * 随机编码字符源31个字符
	 */
	public final static String CARD_NO_CHAR_SOURCE = "23456789abcdefghijkmnpqrstuvwxy";
	// 每次导出文件的最大记录数-注册卡导出
	public final static Integer MAX_EXPORT_CARDS = 1000;

	// ++++++++++++++++++积分相关设置+++++++++++++++++

	/**
	 * 答疑积分-提问
	 */
	public static final String INTEGRAL_ANSWERING_QUESTION = "elearning.project.integral.answering.question";

	/**
	 * 答疑积分-回答别人问题
	 */
	public static final String INTEGRAL_ANSWERING_ANSWER = "elearning.project.integral.answering.answer";

	/**
	 * 答疑积分-设置为最满意答案的回答
	 */
	public static final String INTEGRAL_ANSWERING_BEST_REPLIER = "elearning.project.integral.answering.best.replier";

	/**
	 * 答疑积分-置顶问题的所有积分*2
	 */
	public static final String INTEGRAL_ANSWERING_TOP_QUESTION = "elearning.project.integral.answering.top.question";

	/**
	 * 文章积分-发文
	 */
	public static final String INTEGRAL_ARTICLE_WRITE = "elearning.project.integral.article.write";

	/**
	 * 文章积分-评论别人
	 */
	public static final String INTEGRAL_ARTICLE_COMMENTS = "elearning.project.integral.article.comments";

	/**
	 * 文章积分-被评
	 */
	public static final String INTEGRAL_ARTICLE_BYCOMMENTS = "elearning.project.integral.article.bycomments";

	/**
	 * 文章积分-推优
	 */
	public static final String INTEGRAL_ARTICLE_TOP = "elearning.project.integral.article.top";

	/**
	 * 考场容量-默认值
	 */
	public static final String EXAMINATIONROOM_CAPACITY_DEFAULT = "elearning.examinationroom.capacity.default";

	/**
	 * 考场容量-最大值
	 */
	public static final String EXAMINATIONROOM_CAPACITY_MAX = "elearning.examinationroom.capacity.max";
	/**
	 * 进入课堂时的url
	 */
	public static final String ELEARNING_PROJECT_CLASSROOM_URL = "elearning.enter.classroom.url";
	/**
	 * 当前课堂ID
	 */
	public static final String ELEARNING_PROJECT_CLASSROOM_ID_KEY = "elearning.enter.classroom.id.ke";
	public static final String ELEARNING_PROJECT_CLASSROOM_ID_KEY_VALUE = "elearning.enter.classroom.id.key.value";
//	public static final String ELEARNING_PROJECT_CLASSROOM_ID = "elearning.enter.classroom.id";
	
	public static final String ELEARNING_PAGE_TITLE="elearning.common.title";
	public static final String ELEARNING_SHOW_HEAD_BLOG = "elearning.show.head.blog";
	public static final String ELEARNING_SHOW_HEAD_FORUM = "elearning.show.head.forum";

	public static final String ELEARNING_SHOW_PROJECT_HOMEPAGE = "elearning.project.show.homepage";
	public static final String ELEARNING_SHOW_PROJECT_FORUM = "elearning.project.show.forum";
	public static final String ELEARNING_SHOW_MY_BLOG = "elearning.portal.show.my.blog";
	public static final String ELEARNING_SHOW_MY_FORUM = "elearning.portal.show.my.forum";
	public static final String ELEARNING_SHOW_MY_WIKI = "elearning.portal.show.my.wiki";
	public static final String ELEARNING_COPYWRITE = "elearning.common.footer.copywrite";
	public static final String ELEARNING_COMPANY_NAME="elearning.company.name";
}
