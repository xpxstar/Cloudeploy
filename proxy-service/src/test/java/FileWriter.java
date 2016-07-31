import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class FileWriter {
	public static void main(String[] args) throws IOException  {
		
		try(FileOutputStream out = new FileOutputStream(new File("E://test/test.txt"))){
			while(true){
				Thread.sleep(3000);
				out.write(91);
				System.out.println("has Writed");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
