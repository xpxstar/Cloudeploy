package cn.ac.iscas.cloudeploy.v2.model.entity.topology;

public enum DataType {
	INTEGER("integer"),BOOLEAN("boolean"),STRING("string"),LIST("list"),MAP("map");
	
	private final String value;
	
	DataType(String value){
		this.value = value;
	}
	
	public String getValue(){
		return this.value;
	}
	
	public DataType valueof(String type){
		for(final DataType cur : DataType.values()){
			if(cur.value.equals(type)){
				return cur;
			}
		}
		throw new IllegalArgumentException(type + "is not a legal property type");
	}
}
