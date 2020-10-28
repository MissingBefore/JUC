package current;
/**
 * 枚举本质上是单例
 * @author 仙缘一梦
 *
 */
public enum ClassEnum {
	ONE(1, "英语"), TWO(2, "数学"), THREE(3, "语文"), 
	FOUR(4, "体育"), FIVE(5, "美术"), SIX(6, "编程");
	private Integer retCode;
	private String retMessage;
	private ClassEnum(Integer retCode, String retMessage) {
		this.retCode = retCode;
		this.retMessage = retMessage;
	}
	public static ClassEnum forEach(int index) {
		ClassEnum[] values = ClassEnum.values();
		for (ClassEnum element : values) {
			if(index==element.retCode) {
				return element;
			}
		}
		return null;
	}
	public Integer getRetCode() {
		return retCode;
	}
	public String getRetMessage() {
		return retMessage;
	}
}
