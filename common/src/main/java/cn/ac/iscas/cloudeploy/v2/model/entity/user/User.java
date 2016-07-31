package cn.ac.iscas.cloudeploy.v2.model.entity.user;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.springside.modules.security.utils.Digests;
import org.springside.modules.utils.Encodes;

import cn.ac.iscas.cloudeploy.v2.model.entity.IdEntity;

@Entity
@Table(name = "d_user")
public class User extends IdEntity {
	private String name;
	private String email;
	private String password;
	private String salt;
	
	public User(){
		this.salt = Encodes.encodeHex(PasswordEncryptor.genrateSalt());
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = encryptPwd(password);
	}
	
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}
	public boolean verifyPassword(String plainPassword) {
		return encryptPwd(plainPassword).equals(this.password);
	}
	
	private String encryptPwd(String plainPassword){
		return PasswordEncryptor.encriptPassword(plainPassword, salt);
	}
	
	private static class PasswordEncryptor {

		public static int SALT_SIZE = 8;
		public static int ITERATION_TIME = 1024;

		static byte[] genrateSalt() {
			return Digests.generateSalt(SALT_SIZE);
		}

		static String encriptPassword(String plainPassword, String salt) {
			byte[] password = Digests.sha1(plainPassword.getBytes(),
					Encodes.decodeHex(salt), ITERATION_TIME);
			return Encodes.encodeHex(password);
		}
	}
	
	public static void main(String[] args){
		User user = new User();
		user.setPassword("111111");
		System.out.println(user.getSalt());
		System.out.println(user.getPassword());
	}
}
