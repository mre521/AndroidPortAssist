package analysis;

public class MethodReference {
	public MethodReference(String name, String desc) {
		this.name = name;
		this.desc = desc;
	}
	
	
	public String getName() {
		return name;
	}
	public String getDesc() {
		return desc;
	}


	private String name;
	private String desc;
}
