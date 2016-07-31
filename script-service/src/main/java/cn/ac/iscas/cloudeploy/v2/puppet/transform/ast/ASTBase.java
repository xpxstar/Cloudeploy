package cn.ac.iscas.cloudeploy.v2.puppet.transform.ast;

public class ASTBase {
	private int line;
	private Object file;
	public int getLine() {
		return line;
	}
	public void setLine(int line) {
		this.line = line;
	}
	public Object getFile() {
		return file;
	}
	public void setFile(Object file) {
		this.file = file;
	} 
    
}
