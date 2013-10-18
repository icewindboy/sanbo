package engine.common;

import java.io.Serializable;

@SuppressWarnings("serial")
public final class OtherUser implements Serializable {

	private String dwdm = "";
	private String name;
	private String loginName = "";
	private String loginPassword;
	private String dwmc;
	private String dwtxId;

	private Boolean isSupply;
	private Boolean isSales;

	// 用户id
	private String userId = "";
	// 是否登录
	private boolean isLogin = false;
	// 是否可用
	private boolean isValid = true;
	// 用户浏览器分辨率
	private int screen = 1;
	// 分公司id
	private String filialeId = "";

	// 公司所属部门id
	private String deptId = "";

	private String types = "";

	public String getTypes() {
		return types;
	}

	public void setTypes(String types) {
		this.types = types;
	}

	public int getScreen() {
		return screen;
	}

	/**
	 * 设置分公司id
	 * 
	 * @param filialeId
	 *            分公司id
	 */
	void setFilialeId(String filialeId) {
		this.filialeId = filialeId;
	}

	/**
	 * 得到登录用户的分公司序号
	 * 
	 * @return 返回登录用户的分公司序号
	 */
	public String getFilialeId() {
		return filialeId;
	}

	/**
	 * 在登录页面上检测用户的屏幕分辨率
	 * 
	 * @param screen
	 *            分辨率类型。0 - 640 * 480，1 - 800 * 600，2 - 1024 * 768，3 - 1280 *
	 *            720，4 - higher
	 */
	void setScreen(int screen) {
		this.screen = screen;
	}

	/**
	 * 是否可用
	 * 
	 * @return 返回是否可用
	 */
	public boolean isValid() {
		return isValid;
	}

	/**
	 * 将用户的session为invalid
	 */
	void invalid() {
		this.isValid = false;
	}

	/**
	 * 登录
	 */
	void login() {
		this.isLogin = true;
		// this.loginCount++;
	}

	/**
	 * 退出
	 */
	void logout() {
		this.isLogin = false;
		// this.loginCount --;
	}

	public boolean isLogin() {
		return isLogin;
	}

	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLoginPassword() {
		return loginPassword;
	}

	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}

	public String getDwdm() {
		return dwdm;
	}

	public void setDwdm(String dwdm) {
		this.dwdm = dwdm;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getDwmc() {
		return dwmc;
	}

	public void setDwmc(String dwmc) {
		this.dwmc = dwmc;
	}

	public String getDwtxId() {
		return dwtxId;
	}

	public void setDwtxId(String dwtxId) {
		this.dwtxId = dwtxId;
	}

	public Boolean getIsSupply() {
		return isSupply;
	}

	public void setIsSupply(Boolean isSupply) {
		this.isSupply = isSupply;
	}

	public Boolean getIsSales() {
		return isSales;
	}

	public void setIsSales(Boolean isSales) {
		this.isSales = isSales;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

}
