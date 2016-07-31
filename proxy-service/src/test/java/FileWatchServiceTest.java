import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import static java.nio.file.StandardWatchEventKinds.*;

public class FileWatchServiceTest {
	public static void main(String[] args) {
		try {
			WatchService watcher = FileSystems.getDefault().newWatchService();
			Path file = Paths.get("E:\\test");
			WatchKey key = file.register(watcher,ENTRY_CREATE, ENTRY_MODIFY);
			while(true){
				key = watcher.take();
				for(WatchEvent<?> event : key.pollEvents()){
					if(event.kind() == ENTRY_MODIFY){
						System.out.println("modify: " + event.context());
					}
					if(event.kind() == ENTRY_CREATE){
						System.out.println("create: " + event.context());
					}
				}
				key.reset();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
