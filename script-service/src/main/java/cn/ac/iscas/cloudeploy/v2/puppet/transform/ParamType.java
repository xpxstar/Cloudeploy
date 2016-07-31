package cn.ac.iscas.cloudeploy.v2.puppet.transform;

import java.lang.reflect.Array;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

import cn.ac.iscas.cloudeploy.v2.puppet.transform.ast.ASTASTArray;
import cn.ac.iscas.cloudeploy.v2.puppet.transform.ast.ASTASTHash;
import cn.ac.iscas.cloudeploy.v2.puppet.transform.ast.ASTBoolean;
import cn.ac.iscas.cloudeploy.v2.puppet.transform.ast.ASTConcat;
import cn.ac.iscas.cloudeploy.v2.puppet.transform.ast.ASTFunction;
import cn.ac.iscas.cloudeploy.v2.puppet.transform.ast.ASTString;
import cn.ac.iscas.cloudeploy.v2.puppet.transform.ast.ASTUndef;
import cn.ac.iscas.cloudeploy.v2.puppet.transform.ast.ASTVariable;

public enum ParamType {
	STRING {
		@Override
		public String transform(Object value) {
			Preconditions.checkArgument(value instanceof String, "ParamType STRING can't transform a value which is not a instance of String");
			//TODO verify values
			String valueString = (String)value;
			valueString = StringUtils.strip(valueString, "\"'");
			return "\"" + valueString + "\"";
		}
		@Override
		public boolean verify(Object value) {
			// TODO Auto-generated method stub
			return false;
		}
	},BOOLEAN {
		@Override
		public String transform(Object value) {
			Preconditions.checkArgument(verify(value), value + "is not a verified boolen");
			return value + "";
		}
		public boolean verify(Object value){
			if(value instanceof Boolean) return true;
			if(value instanceof String){
				String regex ="true|false";
				return verify(((String) value).trim(), regex);
			}
			return false;
		}
	},ARRAY {
		@Override
		public String transform(Object value) {
			Preconditions.checkArgument(verify(value), value + "is not a legal Array");
			if(value instanceof String){
				return value + "";
			}else{
				StringBuilder builder = new StringBuilder();
				builder.append("[");
				int length = Array.getLength(value);
				for(int i = 0; i < length; i++){
					//may exist a bug
					//TODO
					builder.append(Array.get(value, i)).append(",");
				}
				builder.append("]");
				return builder.toString();
			}
		}
		public boolean verify(Object value){
			String regex = "^\\[.*\\]$";
			if(value instanceof String) 
				return verify((String) value, regex);
			if(value.getClass().isArray()) return true;
			return false;
		}
	},HASH {
		@Override
		public String transform(Object value) {
			return value + "";
		}

		@Override
		public boolean verify(Object value) {
			return false;
		}
	},UNKNOWN {
		@Override
		public String transform(Object value) {
			return value + "";
		}

		@Override
		public boolean verify(Object value) {
			// TODO Auto-generated method stub
			return false;
		}
	}, UNDEF {
		@Override
		public String transform(Object value) {
			return value + "";
		}

		@Override
		public boolean verify(Object value) {
			// TODO Auto-generated method stub
			return false;
		}
	}, VARIABLE {
		@Override
		public String transform(Object value) {
			String regex = "^\"?\\$\\{(.*)\\}\"?$";
			if(value instanceof String && verify((String) value, regex)){
//				return "\"" + value + "\"";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher((String)value);
				if(matcher.find()){
					String temp = matcher.group(0);
					String temp1 = matcher.group(1);
					return matcher.group(1);
				}
			}
			return value + "";
		}

		@Override
		public boolean verify(Object value) {
			// TODO Auto-generated method stub
			return true;
		}
	}, FUNCTION {
		@Override
		public boolean verify(Object value) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public String transform(Object value) {
			// TODO Auto-generated method stub
			return "undef";
		}
	};
	
	protected boolean verify(String value, String regex){
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(value);
		return matcher.matches();
	}
	
	public abstract boolean verify(Object value);
	public abstract String transform(Object value);
	
	private static final String ASTBOOLEAN;
	private static final String ASTSTRING;
	private static final String ASTARRAY;
	private static final String ASTHASH;
	private static final String ASTUNDEF;
	private static final String ASTVARIABLE;
	private static final String ASTCONCAT;
	private static final String ASTFUNCTION;
	static{
		ASTBOOLEAN=ASTBoolean.class.getSimpleName();
		ASTSTRING=ASTString.class.getSimpleName();
		ASTARRAY=ASTASTArray.class.getSimpleName();
		ASTHASH=ASTASTHash.class.getSimpleName();
		ASTUNDEF=ASTUndef.class.getSimpleName();
		ASTVARIABLE=ASTVariable.class.getSimpleName();
		ASTCONCAT=ASTConcat.class.getSimpleName();
		ASTFUNCTION=ASTFunction.class.getSimpleName();
	}
	
	
	public static ParamType typeOf(Object typeObject){
		if(typeObject==null) return UNKNOWN;
		String type=typeObject.getClass().getSimpleName();
		if (ASTBOOLEAN.equals(type)) {
			return BOOLEAN;
		} else if (ASTSTRING.equals(type) || ASTCONCAT.equals(type) ) {
			return STRING;
		} else if (ASTARRAY.equals(type)) {
			return ARRAY;
		} else if (ASTHASH.equals(type)) {
			return HASH;
		} else if(ASTUNDEF.equals(type)){
			return UNDEF;
		} else if(ASTVARIABLE.equals(type)){
			return VARIABLE;
		} else if(ASTFUNCTION.equals(type)){
			return FUNCTION;
		}else {
			return UNKNOWN;
		}
	}
	
	public static ParamType typeByString(String paramKey) {
		switch(paramKey){
		case "STRING":
			return STRING;
		case "BOOLEAN":
			return BOOLEAN;
		case "ARRAY":
			return ARRAY;
		case "HASH":
			return HASH;
		case "UNDEF":
			return UNDEF;
		case "VARIABLE":
			return VARIABLE;
		default:
			return UNKNOWN;
		}
	}
	
}
